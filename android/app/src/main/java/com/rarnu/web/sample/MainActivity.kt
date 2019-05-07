package com.rarnu.web.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.rarnu.web.JsRouting
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        wc.loadLocal("index.html")
        btnCall.setOnClickListener(this)

        JsRouting.registerRouting("sample") {
            var ret: Map<String, Any>? = null
            if (it != null) {
                val a = it["a"] as Int
                val b = it["b"] as Int
                ret = mapOf("a" to a * 2, "b" to b * 3)
            }
            ret
        }
    }

    override fun onClick(v: View?) {
        wc.callJs("sample", mapOf("p1" to 666, "p2" to 777)) {
            Log.e("Callback", "from js => $it")
        }
    }

}
