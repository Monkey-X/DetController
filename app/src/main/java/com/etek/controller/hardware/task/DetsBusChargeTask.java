package com.etek.controller.hardware.task;

import android.os.AsyncTask;
import android.util.Log;

import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.BusChargeCallback;

import java.lang.ref.WeakReference;

/**
 * 雷管总线充电
 */
public class DetsBusChargeTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "DetsBusChargeTask";
    private final WeakReference<ITaskCallback> iTaskCallbackWeakReference;

    public DetsBusChargeTask(ITaskCallback callback) {
        iTaskCallbackWeakReference = new WeakReference<>(callback);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.showProgressDialog("雷管充电中...",ITaskCallback.CHARGE_TYPE);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.d(TAG, "onPostExecute: "+integer);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.postResult(integer,ITaskCallback.CHARGE_TYPE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "onProgressUpdate: " + values[0]);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.setProgressValue(values[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override

    protected Integer doInBackground(String... strings) {
        int result = DetApp.getInstance().DetsBusCharge(new BusChargeCallback() {
            @Override
            public void SetProgressbarValue(int nVal) {
                publishProgress(nVal);
            }

            @Override
            public void DisplayText(String strText) {
                Log.d(TAG, "DisplayText: " + strText);
                ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
                iTaskCallback.setDisplayText(strText);
            }

            @Override
            public void setChargeData(int nVoltage, int nCurrent) {
                ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
                iTaskCallback.setChargeData(nVoltage,nCurrent);

            }
        });
        return result;
    }
}
