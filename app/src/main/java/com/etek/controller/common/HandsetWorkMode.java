package com.etek.controller.common;


import android.util.Log;

public class HandsetWorkMode{

    private final static String TAG="HandsetWorkMode";

    public String getControllerNo() {
        return ControllerNo;
    }

    public void setControllerNo(String controllerNo) {
        Log.d(TAG,"起爆器编号："+controllerNo);
        ControllerNo = controllerNo;
    }

    private static String ControllerNo="";

    public int getWorkMode() {
        if(ControllerNo.contains("F99")){
            Log.d(TAG,"模式："+MODE_TEST);
            return MODE_TEST;
        }

        Log.d(TAG,"模式："+WorkMode);
        return WorkMode;
    }

    public void setWorkMode(int workMode) {
        Log.d(TAG,"模式："+WorkMode);
        WorkMode = workMode;
    }

    public static int MODE_TEST = 0;
    public static int MODE_NORMAL = 1;

    private static int WorkMode = MODE_NORMAL;

    private static class SingletonHoler{
        private static final HandsetWorkMode sIntance = new HandsetWorkMode();
    }

    public static HandsetWorkMode getInstance(){
        return HandsetWorkMode.SingletonHoler.sIntance;
    }



}
