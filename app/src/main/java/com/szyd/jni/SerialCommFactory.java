package com.szyd.jni;

import android.os.Build;
import android.util.Log;

import com.etek.controller.hardware.comm.SerialCommBase;
import com.nativec.tools.PAXSerialComm;

/***
 * 串口工厂类
 */
public class SerialCommFactory {

    private static final String TAG = "SerialCommFactory";

    public static SerialCommBase getSerialCommObject(String portName,int nBaud){
        String strModel = Build.MODEL.toUpperCase();
        Log.d(TAG,"MODEL:"+strModel);
        //  百富的设备型号为： X3s
        if(strModel.equals("X3S")){
            Log.d(TAG,"百富 串口");
            return new PAXSerialComm(portName,nBaud);
        }

        Log.d(TAG,"iData 串口");
        return new HandSetSerialComm(portName,nBaud);
    }
}
