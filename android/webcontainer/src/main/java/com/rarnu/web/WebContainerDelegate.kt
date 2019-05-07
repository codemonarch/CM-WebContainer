package com.rarnu.web

interface WebContainerDelegate {
    fun onJsCall(routing: String, param: Map<String, Any?>?): Map<String, Any?>?
}