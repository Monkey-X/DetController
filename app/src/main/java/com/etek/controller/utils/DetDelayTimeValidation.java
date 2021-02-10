package com.etek.controller.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.etek.controller.R;
import com.etek.controller.activity.project.ProjectDetailActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

/***
 * 雷管延时输入通用性判断
 */
public class DetDelayTimeValidation {

    //  延时的最大长度
    private static final int MAX_DELAY_TIME_LENGTH = 5;
    //  延时最大（毫秒）
    public static final int MAX_DELAY_TIME_MSECOND = 15000;

    /***
     * 输入的字符串转为整型后的规则判断
     * @param context
     * @param strDelayTime
     * @return
     */
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

        //  这里允许输入负数？
        if (Math.abs(intTime) > MAX_DELAY_TIME_MSECOND) {
            ToastUtils.showShort(context, String.format("延时请设置在0-%dms范围内",MAX_DELAY_TIME_MSECOND));
            return -1;
        }
        return intTime;
    }

    /**
     * 整数判断
     * @param context
     * @param nTime
     * @return
     */
    public static boolean validateDelayTime(Context context,int nTime){
        if ((nTime > MAX_DELAY_TIME_MSECOND)||(nTime<0)) {
            ToastUtils.showShort(context, String.format("延时请设置在0-%dms范围内",MAX_DELAY_TIME_MSECOND));
            return false;
        }

        return  true;
    }
}
