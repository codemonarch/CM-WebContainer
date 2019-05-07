package com.rarnu.web

import android.webkit.WebView
import android.util.Base64

object JsInjection {

    fun inject(wv: WebView) {
        val js = "function device(routing, data, callback) {var d = null;if (data) {d = JSON.stringify(data);}setTimeout(function(){var ret = android.js2device(routing, d);if (callback) {callback(JSON.parse(ret));}},0);}\n" +
                "function device2js(routing, data) {var j = null;if (data) {j = JSON.parse(data);}var ret = fromDevice(routing, j);var retstr = null;if (ret) {retstr = JSON.stringify(ret);}return retstr;}"
        val encoded = Base64.encodeToString(js.toByteArray(), Base64.NO_WRAP)
        val injectCode = "javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var script = document.createElement('script');script.type = 'text/javascript';script.innerHTML = window.atob('$encoded');parent.appendChild(script);})()"
        wv.loadUrl(injectCode)
    }
}