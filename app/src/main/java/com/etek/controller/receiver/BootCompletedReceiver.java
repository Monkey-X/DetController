package com.etek.controller.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.elvishew.xlog.XLog;

import com.etek.controller.activity.SplashActivity;
import com.etek.controller.utils.SommerUtils;


public class BootCompletedReceiver extends BroadcastReceiver {


//    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context mContext, Intent intent) {
        XLog.i("boot broadcast");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mContext, SplashActivity.class);
        mContext.startActivity(intent);
        if(!SommerUtils. isAppRunning("com.etek.controller",mContext)){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(mContext, SplashActivity.class);
            mContext.startActivity(intent);
        }

//        SommerUtils.isServiceRunning("com.etek.controller.service.MQTTService",mContext);
//        SommerUtils. isAppRunning("com.etek.controller",mContext);
//            Intent service = new Intent(context, MQTTService.class);
//            context.startService(service);

//            boolean isServiceRunning = SommerUtils.isServiceRunning("com.etek.controller.service.MQTTService",mContext);
//            if(!isServiceRunning){
//                Intent service = new Intent(mContext, MQTTService.class);
//                mContext.startService(service);
//                ToastUtils.showCustom(mContext,"启动MQTT消息服务器！");
//            }else{
//                ToastUtils.showCustom(mContext,"MQTT消息服务器已经启动！");
//            }



    }

}