package com.rarnu.web.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.rarnu.web.WebContainerDelegate
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), WebContainerDelegate, View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        wc.delegate = this
        wc.loadLocal("index.html")

        btnCall.setOnClickListener(this)
    }

    override fun onJsCall(param: Map<String, Any?>?): Map<String, Any?>? {
        var ret: Map<String, Any>? = null
        if (param != null) {
            val a = param["a"] as Int
            val b = param["b"] as Int
            ret = mapOf("a" to a * 2, "b" to b * 3)
        }
        return ret
    }

    override fun onClick(v: View?) {
        wc.callJs(mapOf("p1" to 666, "p2" to 777)) {
            Log.e("Callback", "from js => $it")
        }
    }

}
