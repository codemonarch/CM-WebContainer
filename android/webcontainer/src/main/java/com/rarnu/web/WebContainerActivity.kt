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
import android.widget.RelativeLayout
import com.rarnu.kt.android.dip2px
import com.rarnu.kt.android.initUI
import com.rarnu.kt.android.resDrawable
import com.rarnu.kt.android.showActionBack
import kotlinx.android.synthetic.main.activity_webcontainer.*

class WebContainerActivity : Activity(), WebDelegate {

    companion object {
        private const val MENUID_SECONDARY = Menu.FIRST + 9999
        var acceptPageMeta = false
        var metaBackgroundColor = "#FFFFFF"
        var metaShowTitle = true
        var metaTitle = ""
        var metaShowSecondary = false
        var metaSecondaryTitle = "..."
        var metaWhiteStatus = true
        var loadUrl = ""
        var localPath = ""
        var isLocal = false
        var delegate: WebContainerActivityDelegate? = null
    }

    private var btnBack: Button? = null
    private var btnSecondary: Button? = null
    private var menuSecondary: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initUI()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webcontainer)
        showActionBack()
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(metaBackgroundColor)))
        val icoBack = resDrawable(if (metaWhiteStatus) R.drawable.ic_menu_back_white else R.drawable.ic_menu_back_black)
        actionBar?.setHomeAsUpIndicator(icoBack)
        actionBar?.title = Html.fromHtml("<span style='color:${if (metaWhiteStatus) "#FFFFFF" else "#000000"}'>$metaTitle</span>", 0)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor(metaBackgroundColor)

        var ui = window.decorView.systemUiVisibility
        ui = if (metaWhiteStatus) {
            ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = ui

        if (metaShowTitle) {
            actionBar?.show()
        } else {
            actionBar?.hide()
        }

        wc.delegate = this
        if (isLocal) {
            wc.loadLocal(loadUrl, localPath)
        } else {
            wc.load(loadUrl)
        }
        if (!acceptPageMeta) {
            if (metaShowTitle) {
                menuSecondary?.isVisible = metaShowSecondary
            } else {
                generateBackBtn()
                if (metaShowSecondary) {
                    generateSecondaryBtn()
                }
            }
        }
    }

    private fun generateBackBtn() {
        if (btnBack == null) {
            btnBack = Button(this)
            btnBack?.text = "<"
            btnBack?.setTextColor(Color.WHITE)
            val lp = RelativeLayout.LayoutParams(40.dip2px(), 40.dip2px())
            lp.topMargin = 8.dip2px()
            lp.leftMargin = 8.dip2px()
            btnBack?.layoutParams = lp
            btnBack?.setBackgroundResource(R.drawable.selector_btn_oper)
            btnBack?.setOnClickListener { finish() }
            layWebContainerRoot.addView(btnBack)
        }
    }

    private fun generateSecondaryBtn() {
        if (btnSecondary == null) {
            btnSecondary = Button(this)
            btnSecondary?.text = metaSecondaryTitle
            btnSecondary?.setTextColor(Color.WHITE)
            val lp = RelativeLayout.LayoutParams(40.dip2px(), 40.dip2px())
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
            lp.topMargin = 8.dip2px()
            lp.rightMargin = 8.dip2px()
            btnSecondary?.layoutParams = lp
            btnSecondary?.setBackgroundResource(R.drawable.selector_btn_oper)
            btnSecondary?.setOnClickListener { delegate?.onSecondaryButtonClicked(this@WebContainerActivity) }
            layWebContainerRoot.addView(btnSecondary)
        }
    }

    override fun onStartLoad(wv: WebContainer) { }
    override fun onEndLoad(wv: WebContainer) { }

    override fun onMeta(wv: WebContainer, meta: Map<String, String>?) {
        if (acceptPageMeta) {
            if (meta != null) {
                val mst = meta["show-title"]
                if (mst != null) {
                    metaShowTitle = mst == "true"
                }
                val mws = meta["white-status"]
                if (mws != null) {
                    metaWhiteStatus = mws == "true"
                }
                val mt = meta["title"]
                if (mt != null) {
                    metaTitle = mt
                }
                val mbc = meta["background-color"]
                if (mbc != null) {
                    metaBackgroundColor = mbc
                }
                val mss = meta["show-secondary"]
                if (mss != null) {
                    metaShowSecondary = mss == "true"
                }
                val mstit = meta["secondary-title"]
                if (mstit != null) {
                    metaSecondaryTitle = mstit
                }

                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                if (metaShowTitle) {
                    actionBar?.show()
                    actionBar?.title = Html.fromHtml("<span style='color:${if (metaWhiteStatus) "#FFFFFF" else "#000000"}'>$metaTitle</span>", 0)
                    val icoBack = resDrawable(if (metaWhiteStatus) R.drawable.ic_menu_back_white else R.drawable.ic_menu_back_black)
                    actionBar?.setHomeAsUpIndicator(icoBack)
                    window.statusBarColor = Color.parseColor(metaBackgroundColor)
                    actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(metaBackgroundColor)))
                    menuSecondary?.isVisible = metaShowSecondary
                    menuSecondary?.title = Html.fromHtml("<span style='color: ${if (metaWhiteStatus) "#FFFFFF" else "#000000"}'>$metaSecondaryTitle</span>", 0)
                    if (metaShowSecondary) {
                        menuSecondary?.isVisible = metaShowSecondary
                    }
                } else {
                    metaWhiteStatus = false
                    metaBackgroundColor = "#FFFFFF"
                    metaTitle = ""
                    actionBar?.hide()
                    window.statusBarColor = Color.WHITE
                    actionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                    generateBackBtn()
                    if (metaShowSecondary) {
                        generateSecondaryBtn()
                    }
                }
                var ui = window.decorView.systemUiVisibility
                ui = if (metaWhiteStatus) {
                    ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                } else {
                    ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                window.decorView.systemUiVisibility = ui

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            MENUID_SECONDARY -> delegate?.onSecondaryButtonClicked(this)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuSecondary = menu?.add(0, MENUID_SECONDARY, 0, Html.fromHtml("<span style='color: ${if (metaWhiteStatus) "#FFFFFF" else "#000000"}'>$metaSecondaryTitle</span>", 0))
        menuSecondary?.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT or MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuSecondary?.isVisible = metaShowSecondary
        return true
    }

}