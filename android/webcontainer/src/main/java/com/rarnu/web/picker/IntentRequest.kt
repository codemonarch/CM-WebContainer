@file:Suppress("DEPRECATION")

package com.rarnu.web.picker

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle

fun Activity.startActivityForResult(intent: Intent, callback: (resultCode: Int, data: Intent?) -> Unit) {
    ResultRequest(this).startForResult(intent, object : ResultRequest.Callback {
        override fun onActivityResult(resultCode: Int, data: Intent?) {
            callback(resultCode, data)
        }
    })
}

@SuppressLint("ValidFragment")
class DispatchFragment: Fragment() {
    companion object { const val TAG = "on_act_result_event_dispatcher" }
    private var callback: ResultRequest.Callback? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    fun startForResult(intent: Intent, callback: ResultRequest.Callback) {
        this.callback = callback
        startActivityForResult(intent, callback.hashCode())
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callback?.onActivityResult(resultCode, data)
    }
}

class ResultRequest(activity: Activity) {
    private var fragment: DispatchFragment
    init { fragment = getDispatchFragment(activity) }
    private fun getDispatchFragment(activity: Activity): DispatchFragment {
        val fragmentManager = activity.fragmentManager
        var fragment = findEventDispatchFragment(fragmentManager)
        if (fragment == null) {
            fragment = DispatchFragment()
            fragmentManager.beginTransaction().add(fragment, DispatchFragment.TAG).commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
        return fragment
    }
    private fun findEventDispatchFragment(manager: FragmentManager) = manager.findFragmentByTag(DispatchFragment.TAG) as? DispatchFragment
    fun startForResult(intent: Intent, callback: Callback) { fragment.startForResult(intent, callback) }
    interface Callback { fun onActivityResult(resultCode: Int, data: Intent?) }
}