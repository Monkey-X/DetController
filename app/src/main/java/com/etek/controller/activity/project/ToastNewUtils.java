package com.etek.controller.activity.project;

import android.content.Context;
import android.widget.Toast;

public class ToastNewUtils {

    private static ToastNewUtils mToastUtils;
    private static Toast mToast;

    private ToastNewUtils(Context context){
        if (null == mToast){
            mToast = Toast.makeText(context.getApplicationContext(),"",Toast.LENGTH_LONG);
        }
    }

    public static ToastNewUtils getInstance(Context context) {
        if (mToastUtils == null){
            mToastUtils = new ToastNewUtils(context.getApplicationContext());
        }
        return mToastUtils;
    }

    public void showShortToast(String mString){
        if (mToast == null){
            return;
        }
        mToast.setText(mString);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showLongToast(String mString){
        if (mToast == null){
            return;
        }
        mToast.setText(mString);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

}
