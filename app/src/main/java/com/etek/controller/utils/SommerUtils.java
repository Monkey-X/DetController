package com.etek.controller.utils;

/**
 * Created by longmao on 16/7/2.
 */

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.text.Html;
import android.text.Spanned;

import android.util.DisplayMetrics;

import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
//import com.j256.ormlite.dao.ForeignCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SommerUtils {



    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String byteToHexString(byte src) {
        StringBuilder stringBuilder = new StringBuilder("");
            int v = src & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);

        return stringBuilder.toString();
    }
    public static String bytesToHexBlock(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
//             int j=0;
        for (int i = 0; i < src.length; i++) {
//            if(i%16==0){
//                stringBuilder.append(j++);
//                stringBuilder.append(" \n");
//            }
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");

        }
        return stringBuilder.toString();
    }


    public static String bytesToHexArrString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            if(i%16==0){
                stringBuilder.append('\n');
            }
            int v = src[i] & 0xFF;
            stringBuilder.append("0x");
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(',');

        }
        stringBuilder.delete(stringBuilder.length()-1,stringBuilder.length());
        return stringBuilder.toString();
    }


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            //	XLog.v("remoteSend", "data ----> " + d[i]);
        }
        return d;
    }


    public static int[] intStringToInts(String intString) {
        if (intString == null || intString.equals("")) {
            return null;
        }


        int length = intString.length() / 2;
        char[] hexChars = intString.toCharArray();
        int[] d = new int[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;

            String s = intString.substring(pos, pos + 2);
            d[i] = Integer.valueOf(s);
            //	XLog.v("remoteSend", "data ----> " + d[i]);
        }
        return d;
    }

    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static int getProData(String proporty, String name) {
        String[] strs = proporty.split(",");
        for (String s : strs) {

            if (s.contains(name)) {
//                System.out.println(s);
                int index = s.indexOf("=");
                s = s.substring(index + 1, s.length());
//                System.out.println(s);
                return Integer.valueOf(s);
            }
        }

        return 0;
    }

    public static byte getXor(byte[] data, int pos, int len) {
        byte temp = 0;
        for (int i = pos; i < len; i++) {
            temp ^= data[i];
        }
        return temp;
    }

    public static byte[] getByteFromInteger(int data) {
//        byte[] byteArr = new byte[len];
//        for(int i=0;i<len;i++){
//            if(i!=(len-1)){
//                int d = Integer.valueOf(data/((i-1)*10));
//                XLog.v("null","d = "+d);
//                String s =String.valueOf(data/((i-1)*10) );
//                byteArr[i] =  s.getBytes()[0];
//            }else {
//                String s =String.valueOf(data%10 );
//                byteArr[i] =  s.getBytes()[0];
//            }
//
//        }
        String s = Integer.toString(data);
        char[] cs = s.toCharArray();
        byte[] byteArr = new byte[cs.length];
        for (int i = 0; i < cs.length; i++) {
            byteArr[i] = (byte) cs[i];
        }
        return byteArr;
    }

    boolean detectLocationProvider() {
        return true;
    }


    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
//        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
//                    System.out.println(i + "===状态===" + networkInfo[i].getState());
//                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static short ChkSum16Generate(byte[] srcData, int len)

    {


        short chkSum16;


        chkSum16 = 0;
        int temp;
        for (int i = 0; i < len; i++) {

            temp = srcData[i] & 0xff;
            chkSum16 += temp;
//            System.out.println("chk["+i+"]="+chkSum16);
        }


        return chkSum16;


    }

    public static byte[] ChkSum16ArrayGenerator(byte[] srcData) {
        byte[] newArr = new byte[srcData.length + 2];
        short chkSum16 = ChkSum16Generate(srcData, srcData.length);
        System.arraycopy(srcData, 0, newArr, 0, srcData.length);
        newArr[newArr.length - 1] = (byte) chkSum16;
        newArr[newArr.length - 2] = (byte) (chkSum16 >> 8);
        return newArr;
    }


    public static boolean ChkSum16CheckErr(byte[] srcData)

    {
        if(srcData.length<3){
            return false;
        }

        short chkSum16 = ChkSum16Generate(srcData, srcData.length - 2);


        if ((byte) (chkSum16 >> 8) != srcData[srcData.length - 2] || (byte) (chkSum16) != srcData[srcData.length - 1])

            return true;


        return false;

    }

    public static boolean copyAppDbToDownloadFolder(Context context,String dbName,String toName)throws IOException {
        try {
            File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),toName);
            //例如"my_data_backup.db"文件
            File currentDB = context.getApplicationContext().getDatabasePath(dbName); // databaseName =您当前的应用程序数据库名称,例如"my_data.db"
            if(currentDB.exists()){
                FileInputStream fis = new FileInputStream(currentDB);
                FileOutputStream fos = new FileOutputStream(backupDB);
                fos.getChannel().transferFrom(fis.getChannel(),0,fis.getChannel().size());
                //或fis.getChannel().transferTo(0,fis.getChannel().size(),fos.getChannel()); 
                fis.close();
                fos.close();
                XLog.i("数据库成功 复制到下载文件夹");
                return true;
            } else XLog.i("复制数据库  失败,数据库未找到");
        } catch(IOException e){
            XLog.e("复制数据库 失败,原因：",e);
        }
        return  false;
    }
    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(String url, LinkedHashMap<String,String> params) {

        Iterator<String> keys = params.keySet().iterator();
        Iterator<String> values = params.values().iterator();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("?");

        for (int i = 0; i < params.size(); i++) {
            String value = null;
            try {
                value = URLEncoder.encode(values.next(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            stringBuffer.append(keys.next() + "=" + value);
            if (i != params.size() - 1) {
                stringBuffer.append("&");
            }

        }

        return url + stringBuffer.toString();
    }


    public static String attachHttpGetParams(String url, LinkedHashMap<String,String> params,String encode) {

        Iterator<String> keys = params.keySet().iterator();
        Iterator<String> values = params.values().iterator();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("?");

        for (int i = 0; i < params.size(); i++) {
            String value = null;
            try {
                value = URLEncoder.encode(values.next(), encode);
            } catch (Exception e) {
                e.printStackTrace();
            }

            stringBuffer.append(keys.next() + "=" + value);
            if (i != params.size() - 1) {
                stringBuffer.append("&");
            }

        }

        return url + stringBuffer.toString();
    }

    public static Point getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        Point point = new Point();
        point.set(dm.widthPixels, dm.heightPixels);
        return point;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int dpToPx(Context context, float dp) {
        return Math.round(dp * getDisplayMetrics(context).density);
    }


    /**
     * API 14
     *
     * @see Build.VERSION_CODES#ICE_CREAM_SANDWICH
     */
    public static boolean hasIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * API 16
     *
     * @see Build.VERSION_CODES#JELLY_BEAN
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * API 19
     *
     * @see Build.VERSION_CODES#KITKAT
     */
    public static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * API 21
     *
     * @see Build.VERSION_CODES#LOLLIPOP
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * API 23
     *
     * @see Build.VERSION_CODES#M
     */
    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String replaceBlank(String str){
        String dest = null;
        if(str == null){
            return dest;
        }else{
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
            return dest;
        }
    }

    /**
     * API 24
     *
     * @see Build.VERSION_CODES#N
     */
    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return "v" + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return context.getString(android.R.string.unknownName);
        }
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * Adjusts the alpha of a color.
     *
     * @param color the color
     * @param alpha the alpha value we want to set 0-255
     * @return the adjusted color
     */
    public static int adjustAlpha(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }



    @SuppressWarnings("deprecation")
    public static Spanned fromHtmlCompat(String text) {
        if (hasNougat()) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    @SuppressWarnings("deprecation")
    public static void textAppearanceCompat(TextView textView, int resId) {
        if (hasMarshmallow()) {
            textView.setTextAppearance(resId);
        } else {
            textView.setTextAppearance(textView.getContext(), resId);
        }
    }

    /**
     * Show Soft Keyboard with new Thread
     *
     * @param activity
     */
    public static void hideSoftInput(final Activity activity) {
        if (activity.getCurrentFocus() != null) {
            new Runnable() {
                public void run() {
                    InputMethodManager imm =
                            (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                }
            }.run();
        }
    }

    /**
     * Hide Soft Keyboard from Dialogs with new Thread
     *
     * @param context
     * @param view
     */
    public static void hideSoftInputFrom(final Context context, final View view) {
        new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm =
                        (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }.run();
    }

    /**
     * Show Soft Keyboard with new Thread
     *
     * @param context
     * @param view
     */
    public static void showSoftInput(final Context context, final View view) {
        new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm =
                        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }.run();
    }

    /**
     * Create the reveal effect animation
     *
     * @param view the View to reveal
     * @param cx   coordinate X
     * @param cy   coordinate Y
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void reveal(final View view, int cx, int cy) {
        if (!hasLollipop()) {
            view.setVisibility(View.VISIBLE);
            return;
        }

        //Get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        //Create the animator for this view (the start radius is zero)
        Animator animator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        //Make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        animator.start();
    }

    /**
     * Create the un-reveal effect animation
     *
     * @param view the View to hide
     * @param cx   coordinate X
     * @param cy   coordinate Y
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void unReveal(final View view, int cx, int cy) {
        if (!hasLollipop()) {
            view.setVisibility(View.GONE);
            return;
        }

        //Get the initial radius for the clipping circle
        int initialRadius = view.getWidth();

        //Create the animation (the final radius is zero)
        Animator animator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        //Make the view invisible when the animation is done
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });

        //Start the animation
        animator.start();
    }




//    public static String getCollection(ForeignCollection collections){
//       Object[] c = collections.toArray();
//        return Arrays.toString(c);
//    }

    public static double bytes2Double(byte[] arr,int offset) {
        long value = 0;
        for (int i = offset; i < offset+4; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    public static float getFloat(byte[] b,int offset) {
        int accum = 0;
        accum = accum|(b[offset] & 0xff) << 0;
        accum = accum|(b[offset+1] & 0xff) << 8;
        accum = accum|(b[offset+2] & 0xff) << 16;
        accum = accum|(b[offset+3] & 0xff) << 24;
        System.out.println(accum);
        return Float.intBitsToFloat(accum);
    }


    public static long bytesToLong(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    public static boolean isServiceRunning(String serviceName,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppRunning(String serviceName,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : manager.getRunningAppProcesses()) {
//            XLog.d("SOMMERUTILS", JSON.toJSONString(appProcessInfo));
            if (serviceName.equals(appProcessInfo.processName)) {
                return true;
            }
        }
        return false;
    }


}
