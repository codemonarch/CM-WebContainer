package com.rarnu.web

interface WebDelegate {
    fun onStartLoad(wv: WebContainer)
    fun onEndLoad(wv: WebContainer)
    fun onMeta(wv: WebContainer, meta: Map<String, String>?)
}