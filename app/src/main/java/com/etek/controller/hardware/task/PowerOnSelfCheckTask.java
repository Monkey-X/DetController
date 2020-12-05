package com.etek.controller.hardware.task;

import android.os.AsyncTask;
import android.util.Log;

import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.PowerCheckCallBack;

import java.lang.ref.WeakReference;

public class PowerOnSelfCheckTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "PowerOnSelfCheckTask";
    private final WeakReference<ITaskCallback> iTaskCallbackWeakReference;

    public PowerOnSelfCheckTask(ITaskCallback callback) {
        iTaskCallbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        int result = DetApp.getInstance().PowerOnSelfCheck(new PowerCheckCallBack() {
            @Override
            public void DisplayText(String strText) {
                Log.d(TAG, "DisplayText: " + strText);
                ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
                iTaskCallback.setDisplayText(strText);
            }

            @Override
            public void SetProgressbarValue(int nVal) {
                publishProgress(nVal);
            }
        });
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.showProgressDialog("总线充电中...",ITaskCallback.POWER_ON);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "onProgressUpdate: " + values[0]);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.setProgressValue(values[0]);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Log.d(TAG, "onPostExecute: "+integer);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.postResult(integer,ITaskCallback.POWER_ON);
    }
}
