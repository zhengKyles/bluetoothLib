package com.wp.bluetooth;

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 9/18/21
 * 看了我的代码，感动了吗?
 */
public interface BlueSendHandler {
    /**
     * 发送成功
     * @param mac 蓝牙设备mac地址
     * @param code 返回码 {@link com.inuker.bluetooth.library.Constants}
     */
    void onSendSuccess(String mac,int code);

    /**
     * 发送失败
     * @param mac 蓝牙设备mac地址
     * @param code 返回码 {@link com.inuker.bluetooth.library.Constants}
     */
    void onSendFail(String mac ,int code);
}
