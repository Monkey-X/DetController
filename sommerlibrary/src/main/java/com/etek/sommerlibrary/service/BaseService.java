package com.etek.sommerlibrary.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class BaseService extends Service {
    protected Context mContext;

    protected static  String LOG_TAG="";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        LOG_TAG = mContext.getClass().getSimpleName();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
