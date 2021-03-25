package com.etek.sommerlibrary.utils;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast工具类
 *
 * Created by sommer on 2016/11/3.

 */
public class ToastUtils {

    static Toast toast = null;


    public static void show(Context context, String text) {

        try {

            if(toast!=null){
                toast.setText(text);
            }else{
                toast= Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.show();
        } catch (Exception e) {//子线程中Toast异常情况处理
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
    public static boolean isShow = true;


    public static void showShort(Context context, String message)
    {
        if (isShow) {
            if (toast==null){
                toast=Toast.makeText(context,message,Toast.LENGTH_SHORT);
            }
            else {
                toast.setText(message);
            }
            toast.show();
        }
    }


    public static void showLong(Context context, String message)
    {
        if (isShow) {
            if (toast==null){
                toast=Toast.makeText(context,message,Toast.LENGTH_LONG);
            }
            else {
                toast.setText(message);
            }
            toast.show();
        }
    }


    public static void show(Context context, CharSequence message, int duration)
    {
        if (isShow) {
            if (toast==null){
                toast=Toast.makeText(context,message,duration);
            }
            else {
                toast.setText(message);
            }
            toast.show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration)
    {
        if (isShow) {
            if (toast==null){
                toast=Toast.makeText(context,message,duration);
            }
            else {
                toast.setText(message);
            }
            toast.show();
        }
    }
}
