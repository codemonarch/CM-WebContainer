//
//  ViewController.swift
//  Sample
//
//  Created by rarnu on 2019/5/6.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit
import WebContainer

class ViewController: UIViewController, WebContainerDelegate {

    private var wc: WebContainer!
    private var btn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let size = UIScreen.main.bounds.size
        wc = WebContainer(frame: CGRect(x: 0, y: 0, width: size.width, height: size.height))
        self.view.addSubview(wc)
        btn = UIButton(type: UIButton.ButtonType.system)
        btn.setTitle("CallJS", for: UIControl.State.normal)
        btn.addTarget(self, action: #selector(btnClicked(_:)), for: UIControl.Event.touchDown)
        btn.frame = CGRect(x: 0, y: 300, width: 100, height: 30)
        self.view.addSubview(btn)
        wc.delegate = self
        wc.loadLocal("index.html")
    }
    
    @objc func btnClicked(_ sender: Any) {
        wc.callJs(data: ["p1":"666", "p2":"777"]) { resp in
            print("callvack from js: ", resp)
        }
    }

    func onJsCall(_ param: [String : Any]?) -> [String : Any]? {
        var ret: [String: Any]?
        if (param != nil) {
            let a = param!["a"] as! Int
            let b = param!["b"] as! Int
            ret = ["a": a * 2, "b": b * 3]
        }
        return ret
    }

}
