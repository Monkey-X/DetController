package com.etek.controller.common;

import android.Manifest;

public class AppIntentString {

    public static final String PROJECT_ID = "projectId";

    public static final String PROJECT_IMPLEMENT_CONNECT_TEST = "1";//连接检测
    public static final String PROJECT_IMPLEMENT_DELAY_DOWNLOAD = "2";//延时下载
    public static final String PROJECT_IMPLEMENT_ONLINE_AUTHORIZE = "3";//检查授权
    public static final String PROJECT_IMPLEMENT_POWER_BOMB = "4";//充电起爆
    public static final String PROJECT_IMPLEMENT_DATA_REPORT = "5";//数据上传


    public static final String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE
    };



}