//
//  WebDelegate.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/9.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit

@objc public protocol WebDelegate: NSObjectProtocol {
    @objc optional func onStartLoad(_ wv: WebContainer)
    @objc optional func onEndLoad(_ wv: WebContainer)
    @objc optional func onMeta(_ wv: WebContainer, _ meta: [String: String]?)
}
