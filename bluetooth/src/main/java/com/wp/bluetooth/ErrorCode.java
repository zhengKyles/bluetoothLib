package com.wp.bluetooth;

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 9/18/21
 * 看了我的代码，感动了吗?
 */
public enum ErrorCode {
    OK("ok"),
    NOT_INIT("蓝牙未初始化"),
    UN_SUPPORT_DEVICE("不支持的设备"),
    BLUETOOTH_UN_OPEN("手机未开启蓝牙"),
    LOCATION_PERMISSION_DENIED("定位权限未授予"),
    LOCATION_BUTTON_NOT_OPEN("定位开关未开启");

    public String msg;

    ErrorCode(String msg) {
        this.msg = msg;
    }
}
