//
//  WebContainerViewController.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/11.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit
import sfunctional

public class WebContainerViewController: UIViewController, WebDelegate {

    public var acceptPageMeta = false
    
    public var metaBackgroundColor = "#FFFFFF"
    public var metaShowTitle = true
    public var metaTitle = ""
    public var metaShowSecondary = false
    public var metaSecondaryTitle = "..."
    public var metaWhiteStatus = false
    public var loadUrl = ""
    public var localPath = ""
    public var isLocal = false
    public var delegate: WebContainerViewControllerDelegate? = nil
    
    private var wc: WebContainer!
    
    private var originManaged = false
    private var originColor: UIColor?
    private var originTextColor: UIColor?
    private var originTitleColor: UIColor?
    private var originBarStyle: UIBarStyle?
    
    private var isNavHidden: Bool? = false
    
    private var btnBack: UIButton!
    private var btnSecondary: UIButton!
    private var barItemSecondary: UIBarButtonItem!
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.isUserInteractionEnabled = true
        self.title = metaTitle
        
        let size = UIScreen.main.bounds.size
        wc = WebContainer(frame: CGRect(x: 0, y: 0, width: size.width, height: size.height))
        wc.delegate = self
        self.view.addSubview(wc)
        if (isLocal) {
            wc.loadLocal(loadUrl, localPath)
        } else {
            wc.load(loadUrl)
        }
        if (!acceptPageMeta) {
            // 由 ViewController 控制
            if (metaShowTitle) {
                if (metaShowSecondary) {
                    generateSecondaryBarItem()
                }
            } else {
                generateBackBtn()
                if (metaShowSecondary) {
                    generateSecondaryBtn()
                }
            }
        }
    }
    
    private func generateBackBtn() {
        btnBack = UIButton(type: UIButton.ButtonType.custom)
        btnBack.setTitle("<", for: UIControl.State.normal)
        let statusHeight = statusbarSize().height
        btnBack.frame = CGRect(x: 8, y: statusHeight + 8, width: 40, height: 40)
        btnBack.backgroundColor = UIColor(displayP3Red: 0.5, green: 0.5, blue: 0.5, alpha: 0.5)
        btnBack.layer.cornerRadius = 20
        btnBack.clipsToBounds = true
        btnBack.isUserInteractionEnabled = true
        btnBack.addTarget(self, action: #selector(btnBackClicked(_:)), for: UIControl.Event.touchUpInside)
        self.view.addSubview(btnBack)
    }
    
    private func generateSecondaryBarItem() {
        barItemSecondary = UIBarButtonItem(title: metaSecondaryTitle, style: UIBarButtonItem.Style.plain, target: self, action: #selector(btnSecondaryClicked(_:)))
        self.navigationItem.rightBarButtonItem = barItemSecondary
    }
    
    private func generateSecondaryBtn() {
        btnSecondary = UIButton(type: UIButton.ButtonType.custom)
        btnSecondary.setTitle(metaSecondaryTitle, for: UIControl.State.normal)
        let statusHeight = statusbarSize().height
        let size = UIScreen.main.bounds.size
        btnSecondary.frame = CGRect(x: size.width - 48 , y: statusHeight + 8, width: 40, height: 40)
        btnSecondary.backgroundColor = UIColor(displayP3Red: 0.5, green: 0.5, blue: 0.5, alpha: 0.5)
        btnSecondary.layer.cornerRadius = 20
        btnSecondary.clipsToBounds = true
        btnSecondary.isUserInteractionEnabled = true
        btnSecondary.addTarget(self, action: #selector(btnSecondaryClicked(_:)), for: UIControl.Event.touchUpInside)
        self.view.addSubview(btnSecondary)
    }
    
    
    public func getContainer() -> WebContainer {
        return wc
    }

    @objc func btnBackClicked(_ sender: Any?) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @objc func btnSecondaryClicked(_ sender: Any?) {
        if (self.delegate != nil) {
            if (self.delegate!.responds(to: #selector(self.delegate?.onSecondaryButtonClicked(_:)))) {
                self.delegate!.onSecondaryButtonClicked!(self)
            }
        }
    }
    
    public override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if (!originManaged) {
            originManaged = true
            originColor = self.navigationController?.navigationBar.barTintColor
            originTextColor = self.navigationController?.navigationBar.tintColor
            originTitleColor = self.navigationController?.navigationBar.titleTextAttributes?[NSAttributedString.Key.foregroundColor] as? UIColor
            originBarStyle = self.navigationController?.navigationBar.barStyle
            isNavHidden = self.navigationController?.isNavigationBarHidden
            
        }
        // 导航全局控制
        self.navigationController?.navigationBar.barTintColor = UIColor.parseString(metaBackgroundColor)
        self.navigationController?.navigationBar.tintColor = metaWhiteStatus ? UIColor.white : UIColor.black
        self.navigationController?.navigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor: metaWhiteStatus ? UIColor.white : UIColor.black]
        self.navigationController?.navigationBar.barStyle = metaWhiteStatus ? UIBarStyle.black : UIBarStyle.default
        self.navigationController?.setNavigationBarHidden(!metaShowTitle, animated: true)
        
    }
    
    public override func viewWillDisappear(_ animated: Bool) {
        // 全局还原控制，CMW 的导航是单独的
        self.navigationController?.navigationBar.barTintColor = originColor
        self.navigationController?.navigationBar.tintColor = originTextColor
        if (originTitleColor != nil) {
            self.navigationController?.navigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor: originTitleColor!]
        }
        self.navigationController?.navigationBar.barStyle = originBarStyle == nil ? UIBarStyle.default : originBarStyle!
        self.navigationController?.setNavigationBarHidden(isNavHidden == nil ? false : isNavHidden!, animated: true)
        super.viewWillDisappear(animated)
    }
    
    public func onMeta(_ wv: WebContainer, _ meta: [String : String]?) {
        if (acceptPageMeta) {
            // 由页面控制
            if (meta != nil) {
                let mt = meta!["title"]
                if (mt != nil) {
                    metaTitle = mt!
                    self.title = metaTitle
                }
                let mbc = meta!["background-color"]
                if (mbc != nil) {
                    metaBackgroundColor = mbc!
                    self.navigationController?.navigationBar.barTintColor = UIColor.parseString(metaBackgroundColor)
                }
                let mws = meta!["white-status"]
                if (mws != nil) {
                    metaWhiteStatus = mws! == "true"
                    self.navigationController?.navigationBar.barStyle = metaWhiteStatus ? UIBarStyle.black : UIBarStyle.default
                    self.navigationController?.navigationBar.tintColor = metaWhiteStatus ? UIColor.white : UIColor.black
                    self.navigationController?.navigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor: metaWhiteStatus ? UIColor.white : UIColor.black]
                }
                let mst = meta!["show-title"]
                let mss = meta!["show-secondary"]
                if (mss != nil) {
                    metaShowSecondary = mss! == "true"
                }
                let mstit = meta!["secondary-title"]
                if (mstit != nil) {
                    metaSecondaryTitle = mstit!
                }
                if (mst != nil) {
                    metaShowTitle = mst! == "true"
                    self.navigationController?.setNavigationBarHidden(!metaShowTitle, animated: true)
                    if (metaShowTitle) {
                        if (metaShowSecondary) {
                            generateSecondaryBarItem()
                        }
                    } else {
                        generateBackBtn()
                        if (metaShowSecondary) {
                            generateSecondaryBtn()
                        }
                    }
                }
            }
        }
    }
}
