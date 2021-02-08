package com.etek.controller.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.etek.controller.activity.project.ProjectDetailActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

public class DetDelayTimeValidation {

    //  延时的最大长度
    private static final int MAX_DELAY_TIME_LENGTH = 5;
    //  延时最大
    public static final int MAX_DELAY_TIME_MSECOND = 15000;

    public static int validateDelayTime(Context context, String strDelayTime){
        if (TextUtils.isEmpty(strDelayTime)) {
            ToastUtils.showShort(context, "请设置延时！");
            return -1;
        }
        if(strDelayTime.length()>MAX_DELAY_TIME_LENGTH){
            ToastUtils.showShort(context, String.format("延时设置在%d位数内！",MAX_DELAY_TIME_MSECOND));
            return -1;
        }

        int intTime = 0;
        try{
            intTime = Integer.parseInt(strDelayTime);
        }catch (NumberFormatException e){
            ToastUtils.showShort(context, "无效的延时设置！");
            return -1;
        }

        if (Math.abs(intTime) > MAX_DELAY_TIME_MSECOND) {
            ToastUtils.showShort(context, String.format("延时请设置在0-%dms范围内",MAX_DELAY_TIME_MSECOND));
            return -1;
        }
        return intTime;
    }
}
