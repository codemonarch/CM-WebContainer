package com.rarnu.web

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.rarnu.kt.android.resDrawable
import com.rarnu.kt.android.showActionBack
import kotlinx.android.synthetic.main.activity_webcontainer.*


class WebContainerActivity: Activity(), WebDelegate {

    companion object {
        private const val MENUID_SECONDARY = Menu.FIRST + 9999
    }

    var acceptPageMeta = false
    var metaBackgroundColor = "#FFFFFF"
    var metaShowTitle = true
    var metaTitle = ""
    var metaTitleTextColor = "#000000"
    var metaShowSecondary = false
    var metaSecondaryTitle = "..."
    var metaWhiteStatus = true
    var loadUrl = ""
    var localPath = ""
    var isLocal = false
    var delegate: WebContainerActivityDelegate? = null

    private lateinit var btnBack: Button
    private lateinit var btnSecondary: Button
    private var menuSecondary: MenuItem? = null

    /*
    private var barItemSecondary: UIBarButtonItem!
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webcontainer)
        wc.delegate = this

        val color = Color.parseColor("#0000FF")
        val cd = ColorDrawable(color)
        actionBar?.setBackgroundDrawable(cd)
        showActionBack()

        val icoBack = resDrawable(R.drawable.ic_menu_back_white)
        actionBar?.setHomeAsUpIndicator(icoBack)

        actionBar?.title = Html.fromHtml("<span style='color:#FFFFFF'>Sample</span>", 0)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color

        val decor = window.decorView
        var ui = decor.systemUiVisibility
        ui = if (metaWhiteStatus) {
            ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }
        decor.systemUiVisibility = ui

    }

    override fun onStartLoad(wv: WebContainer) { }
    override fun onEndLoad(wv: WebContainer) { }

    override fun onMeta(wv: WebContainer, meta: Map<String, String>?) {
        //

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuSecondary = menu?.add(0, MENUID_SECONDARY, 0, "")
        menuSecondary?.setIcon(R.drawable.ic_menu_more_white)
        menuSecondary?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuSecondary?.isVisible = false
        return true
    }

}