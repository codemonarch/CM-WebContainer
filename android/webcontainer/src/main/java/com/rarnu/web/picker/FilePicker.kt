package com.rarnu.web.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.ValueCallback

object FilePicker {
    fun chooseImage(context: Context, callback: (uri: Uri?) -> Unit) {
        if (context !is Activity) return
        val inImg = Intent(Intent.ACTION_PICK, null)
        inImg.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        context.startActivityForResult(inImg) { code, data ->
            var retData: Uri? = null
            if (code == Activity.RESULT_OK) {
                retData = data?.data
            }
            callback(retData)
        }
    }

    fun chooseFile(context: Context, callback: (uri: Uri?) -> Unit) {
        if (context !is Activity) return
        val inFile = Intent(Intent.ACTION_GET_CONTENT)
        inFile.type = "*/*"
        inFile.addCategory(Intent.CATEGORY_OPENABLE)
        context.startActivityForResult(inFile) { code, data ->
            var retData: Uri? = null
            if (code == Activity.RESULT_OK) {
                retData = data?.data
            }
            callback(retData)
        }
    }
}