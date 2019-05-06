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

    public var delegate: WebContainerDelegate?
    
    private var wv: WKWebView!
    private var bridge: WebViewJavascriptBridge!
    private var cfg: WKWebViewConfiguration!

    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        cfg = WKWebViewConfiguration()
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
        
        // WebViewJavascriptBridge.enableLogging()
        
        bridge = WebViewJavascriptBridge(wv)
        bridge.setWebViewDelegate(self)
        bridge.disableJavscriptAlertBoxSafetyTimeout()

        bridge.registerHandler("js2device", handler: { (data, responseCallback) in
            if (self.delegate != nil) {
                if (self.delegate!.responds(to: #selector(self.delegate?.onJsCall(_:)))) {
                    let d = self.delegate!.onJsCall!(data as? [String: Any])
                    if (responseCallback != nil) {
                        responseCallback!(d)
                    }
                }
            }
        })
        
        self.addSubview(wv)
        
    }
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    public func load(_ url: String) {
        wv.load(URLRequest(url: URL(string: url)!))
    }

    public func loadLocal(_ file: String) {
        let path = Bundle.main.path(forResource: file, ofType: "")!
        let pathURL = URL(fileURLWithPath: path)
        let bundleURL = URL(fileURLWithPath: Bundle.main.bundlePath)
        wv.loadFileURL(pathURL, allowingReadAccessTo: bundleURL)
    }
    
    public func callJs(data: [String: Any], callback:@escaping ([String: Any]) -> Void) {
        bridge.callHandler("device2js", data: data) { resp in
            let retData = resp as! [String: Any]
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
        print("webViewDidFinishLoad")
        
    }
    
    public func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        if (navigationAction.targetFrame == nil) {
            webView.load(navigationAction.request)
        }
        decisionHandler(WKNavigationActionPolicy.allow)
    }
    public func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        let alertAcion = UIAlertAction(title: "OK", style: UIAlertAction.Style.cancel) { action in
            completionHandler()
        }
        let alertController = UIAlertController(title: "Alert", message: message, preferredStyle: UIAlertController.Style.alert)
        alertController.addAction(alertAcion)
        self.viewController()?.present(alertController, animated: true, completion: nil)
    }
}
