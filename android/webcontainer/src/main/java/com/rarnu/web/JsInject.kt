package com.rarnu.web

import android.webkit.WebView
import android.util.Base64

object JsInject {

    fun inject(wv: WebView) {
        val ins = wv.context.assets.open("__webc__.js")
        val buf = ByteArray(ins.available())
        ins.read(buf)
        ins.close()
        val encoded = Base64.encodeToString(buf, Base64.NO_WRAP)
        wv.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var script = document.createElement('script');script.type = 'text/javascript';script.innerHTML = window.atob('$encoded');parent.appendChild(script);})()")

    }

}