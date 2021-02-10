package com.etek.controller.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/***
 * 通用的功能展示类
 */
public class GeneralDisplayUI {

    //  网络选择
    //  选择WIFI
    public static final int NETWORK_WIFI = 0;
    //  选择移动数据
    public static final int NETWORK_4G = 1;

    //  展示 通用设置中的WIFI或4G功能
    public static void showSettingNetworkSelect(Context context, int nType){
        Intent intent = null;
        switch (nType) {
            case NETWORK_WIFI:
                intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                break;
            default:
                intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                break;
        }
        if(null!=intent){
            intent.putExtra("extra_prefs_show_button_bar", true);
            intent.putExtra("extra_prefs_set_next_text", "完成");
            intent.putExtra("extra_prefs_set_back_text", "返回");
            intent.putExtra("wifi_enable_next_on_connect",true);
            context.startActivity(intent);
        }
    }
}
