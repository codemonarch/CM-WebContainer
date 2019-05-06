//
//  WebContainerDelegate.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/6.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit

@objc public protocol WebContainerDelegate: NSObjectProtocol {
    @objc optional func onJsCall(_ param: [String: Any]?) -> [String: Any]?
}
