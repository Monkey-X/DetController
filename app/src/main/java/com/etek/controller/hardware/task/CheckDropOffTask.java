package com.etek.controller.hardware.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.etek.controller.hardware.command.DetApp;

import java.lang.ref.WeakReference;

/**
 * 电压和是否脱落检测
 */
public class CheckDropOffTask extends AsyncTask<String, Integer, Integer> {


    private static final String TAG = "CheckDropOffTask";

    private final WeakReference<ITaskCallback> iTaskCallbackWeakReference;

    public CheckDropOffTask(ITaskCallback callback) {
        iTaskCallbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        int i = 0;
        while (true) {
            if (isCancelled()) {
                return 0;
            }
            StringBuilder stringBuilder = new StringBuilder();
            i = DetApp.getInstance().DetsCheckDropOff(stringBuilder);
            String checkResult = stringBuilder.toString();
            Log.d(TAG, "doInBackground: checkResult = " + checkResult);
            if (checkResult.length() < 8) {
                continue;
            }
            String checkString = checkResult.substring(8);
            int checkInt = Integer.parseInt(checkString, 16);
            if (checkInt != 1) {
                i = -1;
                return i;
            }
            String dianya = checkResult.substring(0, 8);
            int nVoltage = Integer.parseInt(dianya, 16);
            iTaskCallback.setChargeData(nVoltage, 0);
            Log.d(TAG, "doInBackground: substring = " + checkString);
            Log.d(TAG, "doInBackground: checkInt = " + checkInt);
            Log.d(TAG, "doInBackground: substring1 = " + dianya);
            Log.d(TAG, "doInBackground: nVoltage = " + nVoltage);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(checkResult)) {
                break;
            }
        }
        return i;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.postResult(integer, ITaskCallback.DROP_OFF);
    }
}
