package com.rarnu.web

import android.util.Log
import org.json.JSONObject
import java.lang.Exception

fun Map<String, Any?>.toJSONString(): String {
    var ret = "{"
    this.keys.forEach {
        ret += "\"$it\":"
        val o = this[it]
        ret += if (o == null) {
            "null,"
        } else {
            if (o is String) {
                "\"${o.toJsonEncoded()}\","
            } else {
                "$o,"
            }
        }
    }
    ret =ret.trimEnd(',')
    ret += "}"
    return ret
}

fun String.toMap(): Map<String, Any?>? {
    val m = mutableMapOf<String, Any?>()
    try {
        val j = JSONObject(this)
        j.keys().forEach {
            val o = j[it]
            if (o == null) {
                m[it] = null
            } else {
                m[it] = o
            }
        }
    } catch (e: Exception) {
        Log.e("String.toMap()", "Error: $e")
    }
    return m
}


private fun String.toJsonEncoded() = this.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")