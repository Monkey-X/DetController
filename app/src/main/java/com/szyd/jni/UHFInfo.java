package com.szyd.jni;

import android.util.Log;

import java.io.FileDescriptor;

/***
 * iData手持机 串口驱动
 */
public class UHFInfo {

    public FileDescriptor mFd;
    //  public FileDescriptor mFd1;

    /**
     * @param flag true 打开uart0 , false 打开uart1
     * @return 当前串口设备的IO句柄
     */
    public FileDescriptor getmFd(boolean flag) {
        //  Log.e("cyd", "mfd = " + mFd);
        //mFd1 = open(flag ? "/dev/ttyS0" : "/dev/ttyS1", 115200);
        return mFd = open(flag ? "/dev/ttyS0" : "/dev/ttyS1", 115200);
    }

    /**
     * 开关总电源
     *
     * @param operationValue 1,2 开关总电源  3,4 开关串口S0电源    5,6开关串口S1电源
     * @return true 成功，false失败
     */
    public native boolean ctlPowerSupply(int operationValue);


    // true 拉高，false拉低
    public native boolean controlGpio73(boolean ifpullHigh);

    //获取GPIO74的值
    public native String getGpio74();

    /**
     * 打开指定串口uart1，获取文件描述符
     *
     * @param baudRate 波特率，支持设置9600，19200，115200三种，设置其他值默认为115200
     * @return 文件句柄/文件描述符
     */
    public native FileDescriptor open(String serialPortName, int baudRate);

    /**
     * 关闭串口，回收资源
     */
    public native void close(String fdName);


    static {
        try {
            System.out.println("loadLibrary sptctl");
//            System.
            System.loadLibrary("sptctl");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
