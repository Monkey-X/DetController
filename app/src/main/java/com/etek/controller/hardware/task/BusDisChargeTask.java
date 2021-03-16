package com.etek.controller.hardware.task;

import android.os.AsyncTask;
import android.util.Log;

import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.BusChargeCallback;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.lang.ref.WeakReference;

/**
 * 总线放电
 *
 */
public class BusDisChargeTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "BusDisChargeTask";
    private final WeakReference<ITaskCallback> iTaskCallbackWeakReference;

    private String strerrmsg ="";
    public BusDisChargeTask(ITaskCallback callback) {
        iTaskCallbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.showProgressDialog("总线放电中...",ITaskCallback.MIS_CHARGE);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.d(TAG, "onPostExecute: "+integer);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.postResult(integer,ITaskCallback.MIS_CHARGE);
    }

    @Override
    protected Integer doInBackground(String... strings) {

        int result = DetApp.getInstance().DetsBusDischarge(new BusChargeCallback() {
            @Override
            public void SetProgressbarValue(int nVal) {
                publishProgress(nVal);
            }

            @Override
            public void DisplayText(String strText) {
                strerrmsg = strText;

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

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "onProgressUpdate: " + values[0]);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.setProgressValue(values[0]);
    }
}
