package com.rarnu.web.picker

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.webkit.ValueCallback
import java.io.File
import java.util.*

object FilePicker {

    fun chooseCamera(context: Context, callback: (uri: Uri?) -> Unit) {
        if (context !is Activity) return
        val inTake = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        inTake.addCategory(Intent.CATEGORY_DEFAULT)
        val fDir = File(context.getExternalFilesDir(""), "tmp")
        if (!fDir.exists()) {
            fDir.mkdirs()
        }
        val fDest = File(fDir, "${UUID.randomUUID()}.jpg")
        val fUri = FileProvider.getUriForFile(context, "${context.packageName}.cmw.provider", fDest)
        inTake.putExtra(MediaStore.EXTRA_OUTPUT, fUri)
        context.startActivityForResult(inTake) { code, _ ->
            var retData: Uri? = null
            if (code == Activity.RESULT_OK) {
                if (fDest.exists()) {
                    retData = fUri
                }
            }
            callback(retData)
        }
    }

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