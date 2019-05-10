package com.rarnu.web.picker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.rarnu.web.R
import kotlinx.android.synthetic.main.dialog_pick.*

class PickDialog(context: Context, val callback: (which: Int) -> Unit) : Dialog(context, R.style.pickDialog) {

    companion object {
        const val RESULT_PHOTO = 0
        const val RESULT_FILE = 1
        const val RESULT_CANCEL = 2
        const val RESULT_DISMISS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pick)
        val lp = window?.attributes
        lp?.gravity = Gravity.BOTTOM
        lp?.width = context.resources.displayMetrics.widthPixels
        window?.attributes = lp
        setOnDismissListener { callback(RESULT_DISMISS) }
        layPhoto.setOnClickListener { dismiss(); callback(RESULT_PHOTO) }
        layFile.setOnClickListener { dismiss(); callback(RESULT_FILE) }
        layCancel.setOnClickListener { dismiss(); callback(RESULT_CANCEL) }
    }

}