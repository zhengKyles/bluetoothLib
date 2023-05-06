package com.wp.bluetooth;


import org.jetbrains.annotations.NotNull;


/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 9/18/21
 * 看了我的代码，感动了吗?
 */
public interface BlueBaseHandler extends BleReceiveHandler{

    /***
     * 初始化成功
     */
    void onInitSuccess();

    /***
     * 初始化失败
     * @param code 错误码{@link ErrorCode}
     */
    void onInitFail(@NotNull ErrorCode code);

    /***
     * 蓝牙打开
     */
    void onBlueToothOpened();

    /***
     * 蓝牙关闭
     */
    void onBlueToothClosed();

    /**
     * 蓝牙连接状态变更
     * @param mac 蓝牙设备mac地址
     * @param isConnected  是否连接
     */
    void onBluetoothConnectStatusChanged(String mac, boolean isConnected);



}
