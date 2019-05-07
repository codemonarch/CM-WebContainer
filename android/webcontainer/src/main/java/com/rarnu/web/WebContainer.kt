package com.rarnu.web

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.rarnu.kt.android.alert
import org.json.JSONObject
import android.webkit.ValueCallback

class WebContainer : RelativeLayout {

    private lateinit var wv: WebView

    private var uploadMessage: ValueCallback<Array<Uri>>? = null
    private var cookie: Map<String, Any>? = null

    private lateinit var btnBack: ImageButton
    private lateinit var bntShare: ImageButton

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initSettings()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initSettings() {
        wv = WebView(context)
        wv.layoutParams = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        wv.addJavascriptInterface(JsObject(), "android")
        addView(wv)

        with(wv.settings) {
            builtInZoomControls = false
            displayZoomControls = false
            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            useWideViewPort = true
            loadsImagesAutomatically = true
            javaScriptEnabled = true
            setAppCacheEnabled(true)
            databaseEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = false
            defaultTextEncodingName = "UTF-8"
            mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
            offscreenPreRaster = true
        }
        with(wv) {
            isHorizontalFadingEdgeEnabled = false
            isHorizontalScrollBarEnabled = false
            isVerticalFadingEdgeEnabled = false
            isVerticalScrollBarEnabled = false
            overScrollMode = OVER_SCROLL_NEVER
            webViewClient = CMWebViewClient()
            webChromeClient = CMChromeClient()
        }
        // cookie
        CookieManager.setAcceptFileSchemeCookies(true)
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true)
    }

    var acceptCookies: Boolean
        get() = CookieManager.getInstance().acceptCookie()
        set(value) {
            CookieManager.setAcceptFileSchemeCookies(value)
            CookieManager.getInstance().setAcceptCookie(value)
            CookieManager.getInstance().setAcceptThirdPartyCookies(wv, value)
        }

    fun load(url: String) {
        if (acceptCookies) {
            val c = CookieManager.getInstance()
            if (cookie != null) {
                c.setCookie(url, cookie!!.toCookieString())
            }
            c.flush()
        }
        wv.loadUrl(url)
    }

    fun loadLocal(filename: String, path: String) {
        val url = "file:///android_asset/$path/$filename"
        if (acceptCookies) {
            val c = CookieManager.getInstance()
            if (cookie != null) {
                c.setCookie(url, cookie!!.toCookieString())
            }
            c.flush()
        }
        wv.loadUrl(url)
    }

    fun callJs(routing: String, data: Map<String, Any>?, callback: (Map<String, Any?>?) -> Unit) {
        val str = data?.toJSONString()
        wv.evaluateJavascript("javascript:device2js('$routing', '$str');") {
            val sBack = it?.replace("\\\"", "\"")?.trim('\"')
            callback(sBack?.toMap())
        }
    }

    fun loadLocalResource(resourcePath: String) = JsLocalResources.load(context, resourcePath)

    inner class CMWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (request != null) {
                view?.loadUrl(request.url.toString())
            }
            return true
        }

        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
            // TODO: load local resource
            Log.e("loadResource", "loadResource => ${request?.url}")




            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            JsInjection.inject(wv)
            if (acceptCookies) {
                val cookieString = CookieManager.getInstance().getCookie(url)
                cookie = cookieString?.toCookie()
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
        }
    }

    inner class CMChromeClient : WebChromeClient() {

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            // TODO: select upload file
            uploadMessage = filePathCallback

            return true
        }

        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            this@WebContainer.context.alert("Alert", "$message", "OK") { }
            result?.cancel()
            return true
        }

        override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            this@WebContainer.context.alert("Confirm", "$message", "OK", "Cancel") {
                when (it) {
                    0 -> result?.confirm()
                    1 -> result?.cancel()
                }
            }
            return true
        }

        override fun onJsPrompt(
            view: WebView?,
            url: String?,
            message: String?,
            defaultValue: String?,
            result: JsPromptResult?
        ): Boolean {
            this@WebContainer.context.alert("Input", "$message", "OK", "Cancel", "", defaultValue) { which, text ->
                when (which) {
                    0 -> result?.confirm(text)
                    1 -> result?.cancel()
                }
            }
            return true
        }
    }

    inner class JsObject {

        @JavascriptInterface
        fun js2device(routing: String, data: String?): String? {
            var ret: Map<String, Any>? = null
            var retstr: String? = null
            val executer = JsRouting.find(routing)
            if (executer != null) {
                ret = executer.execute(data?.toMap())
            }
            if (ret != null) {
                retstr = ret.toJSONString()
            }
            return retstr
        }
    }
}

private fun Map<String, Any>.toJSONString(): String {
    var ret = "{"
    this.keys.forEach {
        ret += "\"$it\":"
        val o = this[it]
        ret += if (o is String) {
            "\"${o.toJsonEncoded()}\","
        } else {
            "$o,"
        }
    }
    ret = ret.trimEnd(',')
    ret += "}"
    return ret
}

private fun String.toMap(): Map<String, Any>? {
    val m = mutableMapOf<String, Any>()
    try {
        val j = JSONObject(this)
        j.keys().forEach {
            val o = j[it]
            if (o != null) {
                m[it] = o
            }
        }
    } catch (e: Exception) {
        Log.e("String.toMap()", "Error: $e")
    }
    return m
}

private fun String.toJsonEncoded() = this.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")

private fun Map<String, Any>.toCookieString() = map { "${it.key}=${it.value}" }.joinToString(";")
private fun String.toCookie() = split(";").map { it.trim() }.map {
    val kv = it.split("=")
    Pair<String, Any>(kv[0].trim(), kv[1].trim()) }.toMap()

