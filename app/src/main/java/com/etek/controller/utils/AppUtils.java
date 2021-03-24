package com.etek.controller.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.serializer.ValueFilter;

public class AppUtils {

   public static ValueFilter filter = (Object object, String name, Object v) -> {
        if (v == null) return "";
        return v;
    };

    public static int getAppVersion(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
