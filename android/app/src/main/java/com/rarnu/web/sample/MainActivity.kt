package com.rarnu.web.sample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import com.rarnu.web.*
import com.rarnu.web.picker.FilePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.channels.FileChannel

class MainActivity : Activity(), View.OnClickListener, WebDelegate, WebContainerActivityDelegate {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
                .permission.CAMERA), 0)
        }


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
        // wc.loadLocal("index.html", "Pages")

        wc.load("https://www.baidu.com") { it }

    }

    override fun onClick(v: View?) {
        /*
        wc.callJs("sample", mapOf("p1" to 666, "p2" to 777)) {
            Log.e("Callback", "from js => $it")
        }
        wc.runJs("document.getElementById('btn').style.backgroundColor = 'yellow';") { }

         */
        WebContainerActivity.loadUrl = "index.html"
        WebContainerActivity.localPath = "Pages"
        WebContainerActivity.isLocal = true
        WebContainerActivity.metaTitle = "CMW"
        WebContainerActivity.metaShowTitle = true
        WebContainerActivity.acceptPageMeta = true
        WebContainerActivity.delegate = this
        startActivity(Intent(this, WebContainerActivity::class.java))
    }

    override fun onStartLoad(wv: WebContainer) {

    }

    override fun onEndLoad(wv: WebContainer) {
    }

    override fun onMeta(wv: WebContainer, meta: Map<String, String>?) {
        Log.e("onMeta", "meta => $meta")
    }

    override fun onSecondaryButtonClicked(wv: WebContainerActivity) {
        Log.e("onSecondaryButtonClicked", "onSecondaryButtonClicked => clicked")
    }

}
