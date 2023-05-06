package com.wp.bluetooth;

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 1/4/22
 * 看了我的代码，感动了吗?
 */
public class JsBlueToothDevice {
    private String name;
    private String mac;

    public JsBlueToothDevice(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
