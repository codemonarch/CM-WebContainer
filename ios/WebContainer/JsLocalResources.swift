//
//  JsLocalResources.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/8.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit
import sfunctional

class JsLocalResources: NSObject {
    static var basePath = ""
    private static var listResource = [String]()
    class func load(_ resourcePath: String) {
        basePath = resourcePath
        JsLocalResources().thread {
            let files = Bundle.main.paths(forResourcesOfType: "", inDirectory: resourcePath)
            files.forEach { f in
                listResource.append(f.sub(start: f.lastIndexOf(sub: "/") + 1))
            }
        }
    }
    
    class func getMimeType(_ resource: String) -> String {
        var ret = "text/plain"
        let ext = resource.sub(start: resource.lastIndexOf(sub: ".") + 1)
        switch ext {
        case "js": ret = "application/x-javascript"
        case "css": ret = "text/css"
        case "zip": ret = "application/zip"
        case "mp3": ret = "audio/mpeg"
        case "wav": ret = "audio/x-wav"
        case "gif": ret = "image/gif"
        case "jpg": ret = "image/jpeg"
        case "html": ret = "text/html"
        case "mp4": ret = "video/mp4"
        case "3gp": ret = "video/3gpp"
        case "pdf": ret = "application/pdf"
        case "png": ret = "image/png"
        case "svg": ret = "image/svg-xml"
        case "ttf": ret = "application/octet-stream"
        default: ret = "text/plain"
        }
        return ret
    }
}
