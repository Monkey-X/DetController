package com.etek.sommerlibrary.app;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;


import com.etek.sommerlibrary.BuildConfig;
import com.etek.sommerlibrary.exception.MyCrashHandler;





public class BaseApplication extends Application {


    @Override
    public void onCreate() {

        super.onCreate();

        if (!BuildConfig.DEBUG) {
            MyCrashHandler handler = new MyCrashHandler();
            Thread.setDefaultUncaughtExceptionHandler(handler);
        }
    }







}
