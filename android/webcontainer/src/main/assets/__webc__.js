function device(data, callback) {
    var d = null;
    if (data) {
        d = JSON.stringify(data);
    }
    var ret = android.js2device(d);
    if (callback) {
        callback(JSON.parse(ret));
    }
}

function device2js(data) {
    var j = null;
    if (data) {
        j = JSON.parse(data);
    }
    var ret = fromDevice(j);
    var retstr = null;
    if (ret) {
        retstr = JSON.stringify(ret);
    }
    return retstr;
}