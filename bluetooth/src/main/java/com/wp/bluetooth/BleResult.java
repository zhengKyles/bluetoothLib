package com.wp.bluetooth;

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 10/15/21
 * 看了我的代码，感动了吗?
 */
public class BleResult {
    private String mac;
    private String requestCode;
    private String data;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }
}
