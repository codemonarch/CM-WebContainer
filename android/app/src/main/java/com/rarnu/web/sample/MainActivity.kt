package com.rarnu.web.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.rarnu.web.JsRouting
import com.rarnu.web.WebContainer
import com.rarnu.web.WebDelegate
import com.rarnu.web.picker.FilePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.channels.FileChannel


class MainActivity : Activity(), View.OnClickListener, WebDelegate {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnCall.setOnClickListener(this)
        wc.loadLocalResource("Pages")
        JsRouting.registerRouting("sample") {
            var ret: Map<String, Any>? = null
            if (it != null) {
                val a = it["a"] as Int
                val b = it["b"] as Int
                ret = mapOf("a" to a * 2, "b" to b * 3)
            }
            ret
        }
        wc.delegate = this
        wc.loadLocal("index.html", "Pages")
    }

    override fun onClick(v: View?) {
        wc.callJs("sample", mapOf("p1" to 666, "p2" to 777)) {
            Log.e("Callback", "from js => $it")
        }
    }

    override fun onStartLoad(wv: WebContainer) {
    }

    override fun onEndLoad(wv: WebContainer) {
    }

    override fun onMeta(wv: WebContainer, meta: Map<String, String>?) {
        Log.e("onMeta", "meta => $meta")
    }

}
