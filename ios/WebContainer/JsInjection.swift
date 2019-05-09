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
        do {
            let jsPath = Bundle.init(for: self.classForCoder()).path(forResource: "__webc__", ofType: "js")
            if (jsPath != nil) {
                let jsCode = try String(contentsOfFile: jsPath!)
                let script = WKUserScript(source: jsCode, injectionTime: WKUserScriptInjectionTime.atDocumentEnd, forMainFrameOnly: false)
                cfg.userContentController.addUserScript(script)
            }
        } catch {
            
        }
    }
}
