package com.etek.controller.scan;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.etek.controller.common.AppConstants;
import com.pax.api.scanner.ScanResult;
import com.pax.api.scanner.ScannerManager;
import com.pax.api.scanner.hw.ScannerHwException;
import com.pax.api.scanner.hw.ScannerManagerHw;

import java.lang.reflect.Method;

/**
 * 百富(PAX)的扫描器类
 */
public class PAXScanner extends ScannerBase {

    private static final String RES_ACTION = "android.intent.action.SCANRESULT";

    private final String TAG="PAXScanner";
    private ScannerManagerHw mScannerManagerHw;

    public PAXScanner(Context context) {
        super(context);
        mScannerManagerHw = ScannerManagerHw.getInstance();
    }

    //是否广播模式
    public static final String KEY_OUTPUT_ACTION = "android.intent.action.BARCODEOUTPUT";

    @Override
    public void ShowUI() {

    }

    @Override
    public void open() {
        Log.d(TAG,"PAXScanner open");
        try {
            mScannerManagerHw.scanOpen();
        } catch (ScannerHwException e) {
            // TODO Auto-generated catch block
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

        Log.d(TAG,"PAXScanner close");

        if(null==mScannerManagerHw){
            return;
        }

        try {
            lockScanKey();
            mScannerManagerHw.scanClose();
        } catch (ScannerHwException e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        }
        return;
    }

    @Override
    public void scan_start() {

    }

    @Override
    public void scan_stop() {

    }

    @Override
    public void lockScanKey() {
        Log.d(TAG,"PAXScanner lockScanKey");
        //恢复左右扫描键功能
        setProperity("pax.ctrl.scankey.event", "true");
        return;
    }

    @Override
    public void unlockScanKey() {
        Log.d(TAG,"PAXScanner unlockScanKey");

        //禁用左右扫描键功能
        setProperity("pax.ctrl.scankey.event", "false");
        return;
    }

    @Override
    public void setOutputMode(int mode) {
        Log.d(TAG,"PAXScanner setOutputMode");

        if(mContext != null){
            Intent intent = new Intent(KEY_OUTPUT_ACTION);
            intent.putExtra(KEY_OUTPUT_ACTION, mode);
            mContext.sendBroadcast(intent);
        }else{
            Log.d(TAG,"mContext is NULL");
        }
    }

    @Override
    public void enablePlayBeep(boolean enable) {

    }

    @Override
    public void enableFailurePlayBeep(boolean enable) {

    }

    @Override
    public void enablePlayVibrate(boolean enable) {

    }

    @Override
    public void enableAddKeyValue(int value) {

    }

    @Override
    public void addPrefix(String text) {

    }

    @Override
    public void addSuffix(String text) {

    }

    @Override
    public void interceptTrimleft(int num) {

    }

    @Override
    public void interceptTrimright(int num) {

    }

    @Override
    public void lightSet(boolean enable) {

    }

    @Override
    public void timeOutSet(int value) {

    }

    @Override
    public void filterCharacter(String text) {

    }

    @Override
    public void continceScan(boolean enable) {

    }

    @Override
    public void intervalSet(int value) {

    }

    @Override
    public void SetErrorBroadCast(boolean enable) {
        return;
    }

    @Override
    public void resultScan() {

    }

    private boolean setProperity(String key, String value) {
        Log.d(TAG,"PAXScanner setProperity " + key +" " + value);

        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", new Class[]{String.class,
                    String.class});
            set.invoke(c, new Object[]{key, value});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        }
        return false;
    }

    @Override
    public void doScan(){
        Log.d(TAG,"PAXScanner doScan");
        String strqrcode ="";
        try {
            ScanResult rs =  mScannerManagerHw.scanRead(1000);
            strqrcode = rs.getContent();
        } catch (ScannerHwException e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
            return;
        }

        if(mContext != null){
            Intent intent = new Intent();
            intent.setAction(RES_ACTION);
            intent.putExtra("value", strqrcode);
            Log.d(TAG,"LocalBroadcastManager sendBroadcast");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
        Log.d(TAG,"PAXScanner doScan "+strqrcode);

        return;
    }
}
