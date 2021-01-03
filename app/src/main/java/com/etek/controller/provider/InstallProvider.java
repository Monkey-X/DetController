package com.etek.controller.provider;

import android.content.res.Configuration;
import android.support.v4.content.FileProvider;

public class InstallProvider extends FileProvider {

    @Override
    public boolean onCreate() {
        return super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
