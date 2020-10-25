package com.etek.controller.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.serializer.ValueFilter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppUtils {

    @SuppressLint("MissingPermission")
    public static Map<String,String> getAppInfo(Context context){

        Map map = new HashMap();
        String manufacturer = android.os.Build.MANUFACTURER;
        String brand = android.os.Build.BRAND;
        map.put("manufacturer",manufacturer);
        map.put("brand",brand);
        String device = android.os.Build.DEVICE;
        map.put("device",device);
        String model = android.os.Build.MODEL;
        map.put("model",model);
        String product = android.os.Build.PRODUCT;
        map.put("product",product);
        String fingerprint = android.os.Build.FINGERPRINT;
        map.put("fingerprint",fingerprint);
          TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            map.put("immi", tm.getDeviceId());
        }

        map.put("release",Build.VERSION.RELEASE);

        map.put("language",Locale.getDefault().getLanguage());

        return  map;
    }

   public static ValueFilter filter = (Object object, String name, Object v) -> {
        if (v == null) return "";
        return v;
    };
}
