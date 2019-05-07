//
//  JsRouting.swift
//  WebContainer
//
//  Created by rarnu on 2019/5/7.
//  Copyright © 2019 rarnu. All rights reserved.
//

import UIKit

public protocol Route {
    var routing: String { get set }
    var exec: ([String: Any]?) -> [String: Any]? { get set }
    func execute(_ param: [String: Any]?) -> [String: Any]?
}

private class RouteImpl: Route {
    var routing: String
    var exec: ([String: Any]?) -> [String: Any]?
    func execute(_ param: [String : Any]?) -> [String : Any]? {
        return exec(param)
    }
    init(_ r: String, _ e: @escaping ([String: Any]?) -> [String: Any]?) {
        routing = r
        exec = e
    }
}

public class JsRouting: NSObject {
    private static var listRouting = [Route]()
    private class func routingExists(_ route: Route) -> Bool {
        return listRouting.filter { r in
            return r.routing == route.routing
        }.count >  0
    }
    private class func routingExists(_ route: String) -> Bool {
        return listRouting.filter { r in
            return r.routing == route
        }.count > 0
    }
    public class func registerRouting(_ route: Route) {
        if (!JsRouting.routingExists(route)) {
            listRouting.append(route)
        }
    }
    public class func registerRouting(_ routing: String, exec: @escaping ([String: Any]?) -> [String: Any]?) {
        if (!JsRouting.routingExists(routing)) {
            let r = RouteImpl(routing, exec)
            listRouting.append(r)
        }
    }
    
    public class func removeRouting(_ route: Route) {
        listRouting.removeAll { r in
            return r.routing == route.routing
        }
    }
    
    public class func removeRouting(_ route: String) {
        listRouting.removeAll { r in
            return r.routing == route
        }
    }
    
    class func find(route: String) -> Route? {
        return listRouting.first { r in
            return r.routing == route
        }
    }
}


