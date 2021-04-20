package com.etek.controller.scan;

import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * 扫描器工厂类
 */
public class ScannerFactory {

    private static final String TAG="ScannerFactory";
    public static ScannerBase getScannerObject(Context context){
        String strModel = Build.MODEL.toUpperCase();
        Log.d(TAG,"MODEL:"+strModel);

        ScannerBase scanobj;
        //  百富设备型号为： X3s
        if(strModel.equals("X3S")){
            Log.d(TAG,"百富 扫描仪");
            scanobj =  new PAXScanner(context);
            scanobj.lockScanKey();
            return scanobj;
        }

        Log.d(TAG,"iData扫描仪");
        scanobj =  new ScannerInterface(context);
        scanobj.unlockScanKey();
        return scanobj;
    }
}
