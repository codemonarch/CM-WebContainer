//
//  WebContainer.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/5.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit
import WebKit
import JavaScriptCore
import WebViewJavascriptBridge
import sfunctional

let filteredKey = "FilteredKey"
var pool: WKProcessPool? = nil

class NetworkIntercept: URLProtocol, NSURLConnectionDataDelegate {
    static var isRegisted = false
    private var conn: NSURLConnection?
    
    private static let extList = ["txt", "js", "css", "zip", "mp3", "wav", "gif", "jpg", "html", "mp4", "3gp", "pdf", "png", "svg", "ttf"]
    override class func canInit(with request: URLRequest) -> Bool {
        var ext = request.url?.pathExtension
        if (ext == nil) {
            ext = ""
        } else {
            ext = ext?.lowercased()
        }
        let isSource = extList.contains(ext!)
        return URLProtocol.property(forKey: filteredKey, in: request) == nil && isSource
    }
    
    override class func canonicalRequest(for request: URLRequest) -> URLRequest {
        return request
    }
    override func startLoading() {
        var filename = super.request.url!.absoluteString
        if (filename.contains("/")) {
            filename = filename.sub(start: filename.lastIndexOf(sub: "/") + 1)
        }
        print(filename)
        let resPath = Bundle.main.path(forResource: filename, ofType: "", inDirectory: JsLocalResources.basePath)
        if (resPath == nil) {
            // 没有本地资源，从网络加载
            let req = (self.request as NSURLRequest).mutableCopy() as! NSMutableURLRequest
            req.allHTTPHeaderFields = self.request.allHTTPHeaderFields
            URLProtocol.setProperty(true, forKey: filteredKey, in: req)
            self.conn = NSURLConnection(request: req as URLRequest, delegate: self)
            return
        }
        let dataPath = Bundle.main.path(forResource: filename, ofType: "", inDirectory: JsLocalResources.basePath)!
        let data = NSData(contentsOfFile: dataPath)!
        let mime = JsLocalResources.getMimeType(filename)
        sendResponseWithData(data as Data, mime)
    }
    override func stopLoading() {
        self.conn?.cancel()
    }
    
    private func sendResponseWithData(_ data: Data, _ mimeType: String) {
        var header = [String: String]()
        header["Content-Type"] = mimeType + ";charset=UTF-8"
        header["Content-Length"] = "\(data.count)"
        let resp = HTTPURLResponse(url: self.request.url!, statusCode: 200, httpVersion: "1.1", headerFields: header)!
        self.client?.urlProtocol(self, didReceive: resp, cacheStoragePolicy: URLCache.StoragePolicy.notAllowed)
        self.client?.urlProtocol(self, didLoad: data)
        self.client?.urlProtocolDidFinishLoading(self)
    }
    
    func connection(_ connection: NSURLConnection, didReceive response: URLResponse) {
        self.client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: URLCache.StoragePolicy.allowed)
    }
    func connection(_ connection: NSURLConnection, didReceive data: Data) {
        self.client?.urlProtocol(self, didLoad: data)
    }
    func connectionDidFinishLoading(_ connection: NSURLConnection) {
        self.client?.urlProtocolDidFinishLoading(self)
    }
    func connection(_ connection: NSURLConnection, didFailWithError error: Error) {
        self.client?.urlProtocol(self, didFailWithError: error)
    }
}

public class WebContainer: UIView, UIScrollViewDelegate, WKNavigationDelegate, WKUIDelegate {

    private var wv: WKWebView!
    private var bridge: WebViewJavascriptBridge!
    private var cookie: [String: Any]? = nil
    private var meta: [String: String]? = nil
    public var delegate: WebDelegate? = nil
    
    var acceptCookies = true

    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        let cfg = WKWebViewConfiguration()
        let pref = WKPreferences()
        pref.javaScriptEnabled = true
        cfg.preferences = pref
        cfg.selectionGranularity = WKSelectionGranularity.character
        cfg.userContentController = WKUserContentController()
        cfg.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        cfg.processPool = singleSharePool()
        JsInjection.injectJs(cfg: cfg)
        
        wv = WKWebView(frame: frame, configuration: cfg)
        wv.scrollView.bounces = false
        wv.scrollView.delegate = self
        wv.scrollView.showsVerticalScrollIndicator = false
        wv.scrollView.showsHorizontalScrollIndicator = false
        wv.navigationDelegate = self
        wv.uiDelegate = self
        
        bridge = WebViewJavascriptBridge(wv)
        bridge.setWebViewDelegate(self)
        bridge.disableJavscriptAlertBoxSafetyTimeout()

        bridge.registerHandler("js2device", handler: { (data, responseCallback) in
            var p = data as! [String: Any]
            let routing = p["__routing__"] as! String
            p.removeValue(forKey: "__routing__")
            let executor = JsRouting.find(route: routing)
            var d: [String: Any]? = nil
            if (executor != nil) {
                d = executor!.execute(p)
            }
            if (responseCallback != nil) {
                responseCallback!(d)
            }
        })
        
        self.addSubview(wv)
        
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    public func load(_ url: String) {
        if (acceptCookies) {
            if (cookie == nil) {
                wv.load(URLRequest(url: URL(string: url)!))
            } else {
                wv.configuration.websiteDataStore.httpCookieStore.setCookie(httpCookie()) {
                    self.wv.load(URLRequest(url: URL(string: url)!))
                }
            }
        } else {
            wv.load(URLRequest(url: URL(string: url)!))
        }
    }

    public func loadLocal(_ file: String, _ path: String) {
        let p = Bundle.main.path(forResource: file, ofType: "", inDirectory: path)!
        let pathURL = URL(fileURLWithPath: p)
        let bundleURL = URL(fileURLWithPath: Bundle.main.bundlePath)
        if (acceptCookies) {
            if (cookie == nil) {
                wv.loadFileURL(pathURL, allowingReadAccessTo: bundleURL)
            } else {
                wv.configuration.websiteDataStore.httpCookieStore.setCookie(httpCookie()) {
                    self.wv.loadFileURL(pathURL, allowingReadAccessTo: bundleURL)
                }
            }
        } else {
            wv.loadFileURL(pathURL, allowingReadAccessTo: bundleURL)
        }
    }
    
    public func callJs(_ routing:String, _ data: [String: Any]?, callback:@escaping ([String: Any]?) -> Void) {
        var p = [String: Any]()
        p["__routing__"] = routing
        data?.forEach { k, v in
            p[k] = v
        }
        bridge.callHandler("device2js", data: p) { resp in
            let retData = resp as? [String: Any]
            callback(retData)
        }
    }
    
    public func runJs(_ js: String, callback: @escaping (String?) -> Void) {
        wv.evaluateJavaScript(js) { (data, _) in
            callback(data as? String)
        }
    }
    
    public func loadLocalResource(_ resourcePath: String) {
        JsLocalResources.load(resourcePath)
        if (!NetworkIntercept.isRegisted) {
            NetworkIntercept.isRegisted = true
            URLProtocol.registerClass(NetworkIntercept.classForCoder())
            let cls = NSClassFromString("WKBrowsingContextController")
            let sel = NSSelectorFromString("registerSchemeForCustomProtocol:")
            if (cls != nil) {
                if (cls!.responds(to: sel)) {
                    cls?.performSelector(inBackground: sel, with: "http")
                    cls?.performSelector(inBackground: sel, with: "https")
                }
            }
        }
    }
    
    public func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return nil
    }
    
    public func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        delegate?.onStartLoad?(self)
    }
    
    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        // cookie
        if (acceptCookies) {
            let c = HTTPCookieStorage.shared.cookies
            if (c != nil) {
                if (cookie == nil) {
                    cookie = [String: Any]()
                    c!.forEach { body in
                        self.cookie![body.name] = body.value
                    }
                }
            }
        }
        // meta
        bridge.callHandler("getMeta", data: nil) { resp in
            if (resp != nil) {
                let arr = resp as! [[String: String]]
                self.meta = [String: String]()
                arr.forEach { d in
                    let name = d["name"]!
                    let value = d["content"]!
                    self.meta![name] = value
                }
                self.parseMeta()
            }
        }
        delegate?.onEndLoad?(self)
    }
    
    private func parseMeta() {
        delegate?.onMeta?(self, meta)
    }
    
    public func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        if (navigationAction.targetFrame == nil) {
            webView.load(navigationAction.request)
        }
        decisionHandler(WKNavigationActionPolicy.allow)
    }
    public func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        self.viewController()?.alert(title: "Alert", message: message, btn: "OK") { completionHandler() }
    }
    
    public func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
        self.viewController()?.alert(title: "Confirm", message: message, btn1: "OK", btn2: "Cancel") { which in
            completionHandler(which == 0)
        }
    }
    
    public func webView(_ webView: WKWebView, runJavaScriptTextInputPanelWithPrompt prompt: String, defaultText: String?, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (String?) -> Void) {
        var initStr = defaultText
        if (initStr == nil) { initStr = "" }
        self.viewController()?.alert(title: "Input", message: prompt, btn1: "OK", btn2: "Cancel", placeholder: "", initText: initStr!) { which, text in
            completionHandler(which == 0 ? text : nil)
        }
    }
    public func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        
    }
    
    public func getCookie() -> [String: Any]? {
        return cookie
    }
    
    public func setCookie(_ c: [String: Any]?) {
        cookie = c
    }
    
    private func httpCookie() -> HTTPCookie {
        var c = [HTTPCookiePropertyKey: Any]()
        cookie?.forEach { k, v in
            c[HTTPCookiePropertyKey(k)] = v
        }
        return HTTPCookie(properties: c)!
    }
    
    private func singleSharePool() -> WKProcessPool {
        if (pool == nil) {
            pool = WKProcessPool()
        }
        return pool!
    }
    
    /*
     + (WKProcessPool *)singleWkProcessPool{
     AFDISPATCH_ONCE_BLOCK(^{
     sharedPool = [[WKProcessPool alloc] init];
     })
     return sharedPool;
     }
    */
}
