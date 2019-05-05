//
//  ViewController.swift
//  Sample
//
//  Created by rarnu on 2019/5/5.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit
import WebContainer

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        let size = UIScreen.main.bounds.size
        let wc = WebContainer(frame: CGRect(x: 0, y: 0, width: size.width, height: size.height))
        self.view.addSubview(wc)
        // wc.loadLocal("index.html")
        wc.load("http://172.16.9.64/code_compact.html")
    }


}

