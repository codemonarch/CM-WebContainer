package com.rarnu.web

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.widget.ImageButton
import android.widget.RelativeLayout

class WebContainer: RelativeLayout {

    private lateinit var wv: WebView

    var delegate: WebContainerDelegate? = null

    private lateinit var btnBack: ImageButton
    private lateinit var bntShare: ImageButton

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
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
        CookieManager.setAcceptFileSchemeCookies(true)
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true)
    }

    fun load(url: String) = wv.loadUrl(url)

    fun loadLocal(filename: String) = wv.loadUrl("file:///android_asset/$filename")

    fun callJs(data: Map<String, Any>?, callback: (Map<String, Any?>?) -> Unit) {
        val str = data?.toJSONString()
        wv.evaluateJavascript("javascript:device2js('$str');") {
            val sBack = it?.replace("\\\"", "\"")?.trim('\"')
            callback(sBack?.toMap())
        }
    }

    inner class CMWebViewClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
        }

        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            return super.shouldOverrideKeyEvent(view, event)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            JsInject.inject(wv)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
        }

        override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
            return super.onRenderProcessGone(view, detail)
        }
    }

    inner class CMChromeClient: WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }

        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            return super.onJsAlert(view, url, message, result)
        }

        override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            return super.onJsConfirm(view, url, message, result)
        }
    }

    inner class JsObject {

        @JavascriptInterface
        fun js2device(data: String?): String? {
            var ret: Map<String, Any?>? = null
            var retstr: String? = null
            if (delegate != null) {
                ret = delegate!!.onJsCall(data?.toMap())
            }
            if (ret != null) {
                retstr = ret.toJSONString()
            }
            return retstr
        }
    }
}