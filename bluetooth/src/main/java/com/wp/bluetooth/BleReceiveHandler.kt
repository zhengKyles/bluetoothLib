package com.wp.bluetooth

import java.util.*

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 2023/1/30
 * 看了我的代码，感动了吗?
 */
interface BleReceiveHandler {
    /***
     * 接收到蓝牙设备返回的数据
     * @param mac 蓝牙设备mac地址
     * @param serviceUUID
     * @param characteristicUUID
     * @param data 蓝牙设备返回数据字节数组
     */
    fun onReceive(mac: String?, serviceUUID: UUID?, characteristicUUID: UUID?, data: ByteArray)

    /***
     * 接收到蓝牙设备返回的数据
     * @param mac 蓝牙设备mac地址
     * @param result 蓝牙设备返回数据实体类
     */
    fun onReceiveData(mac: String?, result: BleResult)
}