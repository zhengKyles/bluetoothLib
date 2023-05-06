package com.wp.bluetooth

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.inuker.bluetooth.library.search.SearchResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 7/26/21
 * 看了我的代码，感动了吗?
 */
@SuppressLint("StaticFieldLeak")
object BleJsInterface {

    var activity: AppCompatActivity? = null

    var gson = Gson()

    var webView: WebView? = null

    @SuppressLint("JavascriptInterface", "StaticFieldLeak")
    fun register(activity: AppCompatActivity, webView: WebView) {
        webView.addJavascriptInterface(BleInterface(), "ble")
        this.activity = activity
        this.webView = webView
    }

    fun toJs(methodName: String, param: Any?) {
        Observable.create<Any> {
            if (param != null) {
                val json = gson.toJson(param)
                webView?.loadUrl("javascript:$methodName($json)")
                Log.e("json", json)
            } else {
                webView?.loadUrl("javascript:$methodName()")
            }
        }.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    class BleInterface {
        @JavascriptInterface
        fun init() {
            Observable.create<Any> {
                BleManager.getInstance(activity).init(object : BlueBaseHandler {
                    override fun onInitSuccess() {
                        toJs("onBleInitSuccess", null)
                    }

                    override fun onInitFail(code: ErrorCode) {
                        toJs("onBleInitFail", code.ordinal)
                    }

                    override fun onBlueToothOpened() {
                        toJs("onBleOpened", null)
                    }

                    override fun onBlueToothClosed() {
                        toJs("onBleClosed", null)
                    }

                    override fun onBluetoothConnectStatusChanged(mac: String?, isConnected: Boolean) {
                        val map = HashMap<String, Any?>()
                        map.put("mac", mac)
                        map.put("isConnected", isConnected)
                        toJs("onBleConnectStatusChanged", map)
                    }

                    override fun onReceive(mac: String?, serviceUUID: UUID?, characteristicUUID: UUID?, data: ByteArray) {
                        val map = HashMap<String, Any?>()
                        map.put("mac", mac)
                        map.put("data", HexUtil.encodeHexStr(data))
                        toJs("onBleReceived", map)
                    }

                    override fun onReceiveData(mac: String?, result: BleResult) {
                        toJs("onBleReceiveData", result)
                    }

                })
            }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe()

        }

        @JavascriptInterface
        fun startScan(allowDuplicatesKey: Boolean, interval: Int) {
            startScan(null, allowDuplicatesKey, interval)
        }

        @JavascriptInterface
        fun startScan(filter: String?, allowDuplicatesKey: Boolean, interval: Int) {
            BleManager.getInstance(activity).startScan(filter, allowDuplicatesKey, interval, object : BlueScanHandler {
                override fun onSearchStart() {
                    toJs("onBleSearchStart", null)
                }

                override fun onSearchResult(device: SearchResult) {
                    if (device.name != null&& "NULL" != device.name) {
                        toJs("onBleSearchResult", JsBlueToothDevice(device.name, device.address))
                    }
                }

                override fun onSearchStop() {
                    toJs("onBleSearchStop", null)
                }

                override fun onSearchCanceled() {
                    toJs("onBleSearchCanceled", null)
                }

            })
        }

        @JavascriptInterface
        fun stopScan(mac: String) {
            BleManager.getInstance(activity).stopScan()
        }

        @JavascriptInterface
        fun connect(mac: String) {
            BleManager.getInstance(activity).connect(mac)
        }

        @JavascriptInterface
        fun openBle() {
            BleManager.getInstance(activity).open()
        }

        @JavascriptInterface
        fun checkBleInitStatus() {
            toJs("onCheckBleInitResult", BleManager.getInstance(activity).isInit)
        }

        @JavascriptInterface
        fun closeBle() {
            BleManager.getInstance(activity).close()
        }

        @JavascriptInterface
        fun disconnect(mac: String) {
            BleManager.getInstance(activity).disconnect(mac)
        }

        @JavascriptInterface
        fun checkBluetoothIsOpen() {
            toJs("onCheckBleOpenResult", BleManager.getInstance(activity).isEnabled)
        }

        @JavascriptInterface
        fun goGpsSetting() {
            BleManager.getInstance(activity).goGpsSetting()
        }

        @JavascriptInterface
        fun sendInstruct(mac: String, byteString: String) {
            val bytes = HexUtil.decodeHex(byteString)
            BleManager.getInstance(activity).sendInstruct(mac, bytes, object : BlueSendHandler {
                override fun onSendSuccess(mac: String?, code: Int) {
                    toJs("onBleSendSuccess", mac)
                }

                override fun onSendFail(mac: String?, code: Int) {
                    toJs("onSendFail", mac)
                }

            })
        }

        @JavascriptInterface
        fun getConnectedDevices() {
            val list = BleManager.getInstance(activity).connectedDevices
            toJs("onGetConnectedDevicesResult", list)
        }

        @JavascriptInterface
        fun isConnected(mac: String) {
            val map = HashMap<String, Any>()
            map.put("mac", mac)
            map.put("isConnected", BleManager.getInstance(activity).isConnected(mac))
            toJs("onCheckBleConnectStatus", map)
        }

        @JavascriptInterface
        fun isDiscovering() {
            toJs("onCheckBleDiscoveringStatus", BleManager.getInstance(activity).isDiscovering)
        }
    }

}