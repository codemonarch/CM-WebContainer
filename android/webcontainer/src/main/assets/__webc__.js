function device(routing, data, callback) {
    var d = null;
    if (data) {
        d = JSON.stringify(data);
    }
    setTimeout(function(){
        var ret = android.js2device(routing, d);
        if (callback) {
            callback(JSON.parse(ret));
        }
        },0);
}

function device2js(routing, data) {
    var j = null;
    if (data) {
        j = JSON.parse(data);
    }
    var ret = fromDevice(routing, j);
    var retstr = null;
    if (ret) {
        retstr = JSON.stringify(ret);
    }
    return retstr;
}

function getMeta() {
    var arr = [];
    var p = document.getElementsByTagName("meta");
    for (var i = 0; i < p.length; i++) {
        var name = p[i].getAttribute("name");
        var content = p[i].getAttribute("content");
        arr.push({name:name, content:content});
    }
    return JSON.stringify(arr);
}