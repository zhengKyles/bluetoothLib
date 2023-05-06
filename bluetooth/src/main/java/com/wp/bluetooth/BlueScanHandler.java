package com.wp.bluetooth;

import com.inuker.bluetooth.library.search.SearchResult;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;


/**
 * author : kyle
 * e-mail : 1239878682@qq.com
 * date   : 9/18/21
 * 看了我的代码，感动了吗?
 */
public interface BlueScanHandler {
    /***
     * 开始扫描
     */
    void onSearchStart();

    /***
     * 扫描到设备
     * @param device 蓝牙设备信息 {@link SearchResult}
     */
    void onSearchResult(@NotNull SearchResult device);

    /**
     * 停止扫描
     */
    void onSearchStop();

    /**
     * 取消扫描
     */
    void onSearchCanceled();

}
