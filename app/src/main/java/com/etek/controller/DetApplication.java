<<<<<<< HEAD
package com.etek.controller;

import android.content.Context;
import android.content.res.Configuration;

import android.os.Debug;
import android.os.Environment;


import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.elvishew.xlog.XLog;
import com.etek.controller.common.Globals;
import com.etek.controller.persistence.DBManager;
import com.etek.sommerlibrary.app.BaseApplication;
import com.polidea.rxandroidble2.LogConstants;
import com.polidea.rxandroidble2.LogOptions;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.exceptions.BleException;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;


public class DetApplication extends BaseApplication {

    //    public static Context appContext;
    public static final String APP_ID = "c1fbf3bd7a"; // TODO 替换成bugly上注册的appid
    private RxBleClient rxBleClient;

    /**
     * In practice you will use some kind of dependency injection pattern.
     */
    public static RxBleClient getRxBleClient(Context context) {
        DetApplication application = (DetApplication) context.getApplicationContext();
        return application.rxBleClient;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        // 程序创建的时候执行


        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);


        rxBleClient = RxBleClient.create(this);
//        if (Globals.isBuild) {
//            RxBleClient.updateLogOptions(new LogOptions.Builder()
//                    .setLogLevel(LogConstants.INFO)
//                    .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
//                    .setUuidsLogSetting(LogConstants.UUIDS_FULL)
//                    .setShouldLogAttributeValues(true)
//                    .build()
//            );
//        }


        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException && throwable.getCause() instanceof BleException) {
                XLog.v("Suppressed UndeliverableException: ", throwable.toString());
                return; // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw new RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable);
        });

        DBManager.init(getApplicationContext());
    }


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        XLog.d("onTerminate");

        super.onTerminate();

    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        XLog.d("onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        XLog.d("onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        XLog.d("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }


}
=======
package com.etek.controller;

import android.content.Context;
import android.content.res.Configuration;

import android.os.Debug;
import android.os.Environment;


import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.elvishew.xlog.XLog;
import com.etek.controller.common.Globals;
import com.etek.controller.persistence.DBManager;
import com.etek.sommerlibrary.app.BaseApplication;
import com.polidea.rxandroidble2.LogConstants;
import com.polidea.rxandroidble2.LogOptions;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.exceptions.BleException;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;


public class DetApplication extends BaseApplication {

    //    public static Context appContext;
    public static final String APP_ID = "c1fbf3bd7a"; // TODO 替换成bugly上注册的appid
    private RxBleClient rxBleClient;

    /**
     * In practice you will use some kind of dependency injection pattern.
     */
    public static RxBleClient getRxBleClient(Context context) {
        DetApplication application = (DetApplication) context.getApplicationContext();
        return application.rxBleClient;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        // 程序创建的时候执行


        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);


        rxBleClient = RxBleClient.create(this);
//        if (Globals.isBuild) {
//            RxBleClient.updateLogOptions(new LogOptions.Builder()
//                    .setLogLevel(LogConstants.INFO)
//                    .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
//                    .setUuidsLogSetting(LogConstants.UUIDS_FULL)
//                    .setShouldLogAttributeValues(true)
//                    .build()
//            );
//        }


        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException && throwable.getCause() instanceof BleException) {
                XLog.v("Suppressed UndeliverableException: ", throwable.toString());
                return; // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw new RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable);
        });

        DBManager.init(getApplicationContext());
    }


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        XLog.d("onTerminate");

        super.onTerminate();

    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        XLog.d("onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        XLog.d("onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        XLog.d("onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }


}
>>>>>>> 806c842... 雷管组网
