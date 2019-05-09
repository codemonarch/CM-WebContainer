function setupWebViewJavascriptBridge(callback) {
    if (window.WebViewJavascriptBridge) { 
        return callback(WebViewJavascriptBridge); 
    }
    if (window.WVJBCallbacks) { 
        return window.WVJBCallbacks.push(callback); 
    }
    window.WVJBCallbacks = [callback];
    var WVJBIframe = document.createElement('iframe');
    WVJBIframe.style.display = 'none';
    WVJBIframe.src = 'https://__bridge_loaded__';
    document.documentElement.appendChild(WVJBIframe);
    setTimeout(function() { 
        document.documentElement.removeChild(WVJBIframe); 
    }, 0);
}

function device(routing, data, callback) {
    if(!data){
        data = {};
    }
    data['__routing__'] = routing;
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('js2device', data, function(response) {
            callback(response);
        });
    });
}

setupWebViewJavascriptBridge(function(bridge) {
    bridge.registerHandler('device2js', function(data, responseCallback) {
        var routing = data['__routing__']; 
        delete data['__routing__'];
        var ret = fromDevice(routing, data);
        responseCallback(ret);
    });
    bridge.registerHandler('getMeta', function(data, responseCallback) {
        var arr = [];
        var p = document.getElementsByTagName("meta");
        for (var i = 0; i < p.length; i++) {
            var name = p[i].getAttribute("name");
            var content = p[i].getAttribute("content");
            arr.push({name:name, content:content});
        }
        responseCallback(arr);
    });
});
