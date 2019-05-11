//
//  WebContainerViewControllerDelegate.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/11.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit


@objc public protocol WebContainerViewControllerDelegate: NSObjectProtocol {
    @objc optional func onSecondaryButtonClicked(_ wv: WebContainerViewController)
}
