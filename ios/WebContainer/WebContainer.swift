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

public class WebContainer: UIView, UIScrollViewDelegate, WKNavigationDelegate, WKUIDelegate {

    private var wv: WKWebView!
    private var bridge: WebViewJavascriptBridge!
    private var cookie: [String: Any]? = nil
    
    private var btnBack: UIButton!
    private var btnShare: UIButton!
    
    var acceptCookies = true

    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        let cfg = WKWebViewConfiguration()
        let pref = WKPreferences()
        pref.javaScriptEnabled = true
        cfg.preferences = pref
        cfg.selectionGranularity = WKSelectionGranularity.character
        cfg.userContentController = WKUserContentController()
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

    public func loadLocal(_ file: String) {
        let path = Bundle.main.path(forResource: file, ofType: "")!
        let pathURL = URL(fileURLWithPath: path)
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
    
    public func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return nil
    }
    
    public func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        print("webViewDidStartLoad")
    }
    
    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
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
}
