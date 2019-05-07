package com.rarnu.web

import android.content.Context
import android.webkit.WebResourceResponse
import kotlin.concurrent.thread

object JsLocalResources {
    var basePath = ""
    private val listResource = mutableListOf<String>()
    fun load(ctx: Context, resourcePath: String) = thread {
        basePath = resourcePath
        val am = ctx.assets
        val files = am.list(resourcePath)
        if (files != null) {
            listResource.addAll(files)
        }
    }

    fun find(ctx: Context, resource: String): WebResourceResponse? {
        var ret: WebResourceResponse? = null
        if (listResource.contains(resource)) {
            val am = ctx.assets
            val ins = ctx.assets.open("$basePath/$resource")
            ret = WebResourceResponse(getMimeType(resource), "UTF-8", ins)
        }
        return ret
    }

    private fun getMimeType(resource: String) = when (resource.substringAfterLast(".")) {
        "js" -> "application/x-javascript"
        "css" -> "text/css"
        "zip" -> "application/zip"
        "mp3" -> "audio/mpeg"
        "wav" -> "audio/x-wav"
        "gif" -> "image/gif"
        "jpg" -> "image/jpeg"
        "html" -> "text/html"
        "mp4" -> "video/mp4"
        "3gp" -> "video/3gpp"
        "pdf" -> "application/pdf"
        "png" -> "image/png"
        "svg" -> "image/svg-xml"
        "ttf" -> "application/octet-stream"
        else -> "text/plain"
    }
}