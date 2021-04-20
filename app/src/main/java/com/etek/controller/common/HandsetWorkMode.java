package com.etek.controller.common;


import android.util.Log;

/**
 * 设备工作模式：测试模式和正常模式
 * 测试模式：    1. 起爆器编号前3位是F99；
 *              2. 操作员名称为WXSCTEST
 */
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

    public static int MODE_NORMAL = 0;      //  正常模式
    public static int MODE_TEST = 1;        //  测试模式1：所有的限制都没有，雷管组网不限制厂商，操作步骤不限制，经纬度等可以修改
    public static int MODE_TESTER = 2;      //  测试模式2：除【离线授权编辑】和【检查授权】中的经纬度、起爆器编号可修改外，其他和MODE_NORMAL一致。

    private static int WorkMode = MODE_NORMAL;

    private static class SingletonHoler{
        private static final HandsetWorkMode sIntance = new HandsetWorkMode();
    }

    public static HandsetWorkMode getInstance(){
        return HandsetWorkMode.SingletonHoler.sIntance;
    }



}
