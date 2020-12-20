package com.etek.controller.common;

import android.Manifest;

public class AppIntentString {

    public static final String PROJECT_ID = "projectId";
    public static final String DELAY_SETTING = "delay_setting";

    public static final String PROJECT_NUM = "project_num";

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

    public static final int TYPE_HOLE_IN = 1;
    public static final int TYPE_HOLE_OUT = 2;
    public static final int TYPE_HOLE_NO_CHANGE = 3;


   // 本地数据校验测试
    public static final String textString = "[{\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A0\", \"fbh\": \"6001119500160\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A1\", \"fbh\": \"6001119500161\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A2\", \"fbh\": \"6001119500162\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A3\", \"fbh\": \"6001119500163\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A4\", \"fbh\": \"6001119500164\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A5\", \"fbh\": \"6001119500165\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A6\", \"fbh\": \"6001119500166\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A7\", \"fbh\": \"6001119500167\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A8\", \"fbh\": \"6001119500168\", \"yxq\": \"\", \"gzm\": \"\"}, {\"gzmcwxx\": \"2\", \"uid\": \"000000576C00A9\", \"fbh\": \"6001119500169\", \"yxq\": \"\", \"gzm\": \"\"}]";

}