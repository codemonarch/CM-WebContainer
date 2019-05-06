//
//  DeviceExport.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/6.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit
import JavaScriptCore

@objc protocol DeviceExport: JSExport {
    func execute(param: [String: Any]) -> [String: Any]
}

@objc class DeviceImpl: NSObject, DeviceExport {
    func execute(param: [String : Any]) -> [String : Any] {
        print("param", param)
        return ["ret1": "a", "ret2": "b"]
    }
}
