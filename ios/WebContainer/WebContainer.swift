//
//  WebContainer.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/5.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit
import WebKit

public class WebContainer: UIView, UIScrollViewDelegate {

    private var wv: WKWebView!
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        
        let cfg = WKWebViewConfiguration()
        let pref = WKPreferences()
        pref.javaScriptEnabled = true
        cfg.preferences = pref
        cfg.selectionGranularity = WKSelectionGranularity.character
        cfg.userContentController = WKUserContentController()
        
        wv = WKWebView(frame: frame, configuration: cfg)
        wv.scrollView.bounces = false
        wv.scrollView.delegate = self
        wv.scrollView.showsVerticalScrollIndicator = false
        wv.scrollView.showsHorizontalScrollIndicator = false
        
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
    
    public func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return nil
    }
    
}
