package com.rarnu.web

interface WebContainerDelegate {
    fun onJsCall(param: Map<String, Any?>?): Map<String, Any?>?
}