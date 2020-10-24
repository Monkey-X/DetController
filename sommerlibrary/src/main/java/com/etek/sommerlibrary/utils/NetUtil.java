<<<<<<< HEAD
package com.etek.sommerlibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {

    //判断网络
    public static int getNetType(Context context) {
        //设置一个无网络状态值
        int noState = -1;
        //获取系统网络管理类
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //得到NetWorkInfo
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //如果activeNetworkInfo为空,说明没网络
        if (activeNetworkInfo == null) {
            return noState;
        }
        //否则就获取网路类型
        int type = activeNetworkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {//网络类型是WiFi
            return 1;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {//网络类型是手机网络
            return 0;
        }

        return noState;
    }
}

=======
package com.etek.sommerlibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {

    //判断网络
    public static int getNetType(Context context) {
        //设置一个无网络状态值
        int noState = -1;
        //获取系统网络管理类
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //得到NetWorkInfo
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //如果activeNetworkInfo为空,说明没网络
        if (activeNetworkInfo == null) {
            return noState;
        }
        //否则就获取网路类型
        int type = activeNetworkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {//网络类型是WiFi
            return 1;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {//网络类型是手机网络
            return 0;
        }

        return noState;
    }
}

>>>>>>> 806c842... 雷管组网
