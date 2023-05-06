package com.wp.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 7/26/21
 * 看了我的代码，感动了吗?
 */
@SuppressLint("StaticFieldLeak")
object BaseJsInterface {

    var activity: Activity? = null

    var gson = Gson()

    var webView: WebView? = null

    @SuppressLint("JavascriptInterface", "StaticFieldLeak")
    fun register(activity: Activity, webView: WebView) {
        webView.addJavascriptInterface(BaseInterface(), "android")
        this.activity = activity
        this.webView = webView
    }

    fun toJs(methodName: String, param: Any?) {
        Observable.create<Any> {
            if (param != null) {
                val json = gson.toJson(param)
                webView?.loadUrl("javascript:$methodName($json)")
            } else {
                webView?.loadUrl("javascript:$methodName()")
            }
        }.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    class BaseInterface {
        @JavascriptInterface
        fun turnHorizontalScreen() {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        @JavascriptInterface
        fun turnVerticalScreen() {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

    }

}