//
//  JsInjection.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/6.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit
import WebKit

class JsInjection: NSObject {
    class func injectJs(cfg: WKWebViewConfiguration) {
        let js = [
            "function setupWebViewJavascriptBridge(callback) {if (window.WebViewJavascriptBridge) { return callback(WebViewJavascriptBridge); }if (window.WVJBCallbacks) { return window.WVJBCallbacks.push(callback); }window.WVJBCallbacks = [callback];var WVJBIframe = document.createElement('iframe');WVJBIframe.style.display = 'none';WVJBIframe.src = 'https://__bridge_loaded__';document.documentElement.appendChild(WVJBIframe);setTimeout(function() { document.documentElement.removeChild(WVJBIframe) }, 0);}": WKUserScriptInjectionTime.atDocumentStart,
            "function device(data, callback) {setupWebViewJavascriptBridge(function(bridge) {bridge.callHandler('js2device', data, function(response) {callback(response);});});}": WKUserScriptInjectionTime.atDocumentStart,
            "setupWebViewJavascriptBridge(function(bridge) {bridge.registerHandler('device2js', function(data, responseCallback) {var ret = fromDevice(data);responseCallback(ret)});});": WKUserScriptInjectionTime.atDocumentEnd]
        js.forEach { k, v in
            let script = WKUserScript(source: k, injectionTime: v, forMainFrameOnly: false)
            cfg.userContentController.addUserScript(script)
        }
    }
}
