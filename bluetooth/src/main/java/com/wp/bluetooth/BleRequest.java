package com.wp.bluetooth;

/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 10/15/21
 * 看了我的代码，感动了吗?
 */
public enum BleRequest {

    //在线升级
    UPDATE("UPDATE", (byte) 0x01, "发送在线升级命令"),
    //在线升级数据
    UPDATE_DATA("UPDATE_DATA", (byte) 0x02, "发送在线升级数据"),

    //在线升级旧版
    UPDATE_OLD("UPDATE_OLD", (byte) 0x01, "发送在线升级命令(旧版)"),
    //在线升级数据旧版
    UPDATE_DATA_OLD("UPDATE_DATA_OLD", (byte) 0x03, "发送在线升级数据(旧版)"),

    //获取厂家标识
    FACTORY_SIGN("FS001", (byte) 0xac, "厂家标识"),
    //获取版本号
    VERSION("DV001", (byte) 0xaa, "版本号"),
    //设置读取读卡器版本类型
    SET_DEVICE_TYPE("SDT001", (byte) 0xa8, "设置读卡器版本类型"),
    //设置设备号
    SET_DEVICE_NUMBER("SDN001", (byte) 0xb2, "设置读卡器设备号"),
    //读取设备号
    READ_DEVICE_NUMBER("RDN001", (byte) 0xb3, "读取读卡器设备号"),
    //设置激活时间
    SET_ACTIVE_TIME("SAT001", (byte) 0xb5, "设置激活时间"),
    //读取激活时间
    READ_ACTIVE_TIME("RAT001", (byte) 0xb6, "读取激活时间"),

    //按rfid键读取 古老版本 要兼容
    READ_RFID_OLD("RRO", (byte) 0x39, "按rfid读取"),
    //按rfid键读取
    READ_RFID_DOUBLE_OLD("RRO", (byte) 0x40, "按rfid读取"),
    READ_EPC_NEW("REN", (byte) 0x4D, "新款读取epc"),
    //按rfid键读取
    READ_RFID("RR", (byte) 0x60, "按rfid读取"),
    //激光功能自动采集标签TID值应答
    AUTO_TID("ART", (byte) 0x61, "自动采集标签tid"),
    //写用户区
    WRITE_USER("WU", (byte) 0x90, "写用户区"),
    //写epc
    WRITE_EPC("WE", (byte) 0x91, "写epc"),
    //读用户区
    READ_USER("RU", (byte) 0x92, "读用户区"),
    //读取epc
    READ_EPC("RE", (byte) 0x93, "读epc"),
    //地址偏移写用户区
    DEVIATE_WRITE_USER("DWU", (byte) 0x94, "地址偏移写用户区"),
    //地址偏移写epc
    DEVIATE_WRITE_EPC("DWE", (byte) 0x95, "地址偏移写epc"),
    //地址偏移读用户区
    DEVIATE_READ_USER("DRU", (byte) 0x96, "地址偏移读用户区"),
    //地址偏移读取epc
    DEVIATE_READ_EPC("DRE", (byte) 0x97, "地址偏移读epc"),

    //设置当前时间20 22 02 02  年月日
    SET_DATE("SD", (byte) 0xb7, "设置当前时间"),

    //设置读卡器功率
    WRITE_POWER("WP", (byte) 0xd0, "设置读卡器功率"),
    //设置授权码
    WRITE_AUTHOR_CODE("WAC", (byte) 0xd1, "设置授权码"),
    //验证授权指令
    VERIFY_AUTHOR_INSTRUCTION("VAI", (byte) 0xd3, "验证授权指令"),
    //获取厂家标识
    READ_FACTORY_SIGN("RFS", (byte) 0xd4, "读取厂家标识"),
    //设置读卡器协议版本号
    WRITE_AGREEMENT_VERSION("WAV", (byte) 0xd5, "设置读卡器协议版本号"),
    //读取读卡器协议版本号
    READ_AGREEMENT_VERSION("RAV", (byte) 0xd6, "读取读卡器协议版本号"),
    //设置读卡器设备号
    WRITE_DEVICE_VERSION("WDV", (byte) 0xd9, "设置读卡器设备号"),
    //读取读卡器设备号
    READ_DEVICE_VERSION("RDV", (byte) 0xda, "读取读卡器设备号"),
    //读取读卡器电压
    READ_VOLTAGE("RV", (byte) 0xdb, "读取读卡器电压"),
    //读tid/epc
    READ_TID_EPC("RTE", (byte) 0xe1, "读取tid/epc"),
    //读tid/epc/user
    READ_TID_EPC_USER("RTEU", (byte) 0xe2, "读取tid/epc/user"),
    //地址偏移读tid/epc
    DEVIATE_READ_TID_EPC("DRTE", (byte) 0xe3, "地址偏移读tid/epc"),
    //读tid/epc/user
    DEVIATE_READ_TID_EPC_USER("DRTEU", (byte) 0xe4, "地址偏移读取tid/epc/user");
    
    
    
    private String code;
    private byte request;
    private String name;

    public static BleRequest get(String code) {
        BleRequest[] list = BleRequest.values();
        for (BleRequest bleRequest : list) {
            if (bleRequest.code.equals(code)) {
                return bleRequest;
            }
        }
        return null;
    }

    public static BleRequest get(byte request) {
        BleRequest[] list = values();
        for (BleRequest bleRequest : list) {
            if (bleRequest.request == request) {
                return bleRequest;
            }
        }
        return null;
    }

    BleRequest(String code, byte request, String name) {
        this.code = code;
        this.request = request;
        this.name = name;
    }

    public byte getRequest() {
        return request;
    }

    public void setRequest(byte request) {
        this.request = request;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
