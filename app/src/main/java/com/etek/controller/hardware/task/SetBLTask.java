package com.etek.controller.hardware.task;

import android.os.AsyncTask;

import com.etek.controller.hardware.command.DetApp;

import java.lang.ref.WeakReference;

/**
 * BL 角拉高或者拉低
 */
public class SetBLTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "SetBLTask";

    private final WeakReference<ITaskCallback> iTaskCallbackWeakReference;
    private boolean bHigh;

    public SetBLTask(ITaskCallback callback, boolean bHigh) {
        iTaskCallbackWeakReference = new WeakReference<>(callback);
        this.bHigh = bHigh;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!bHigh) {
            ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
            iTaskCallback.showProgressDialog("请稍等...",ITaskCallback.BL_FALSE);
        }
    }

    @Override
    protected Integer doInBackground(String... strings) {
        int result = DetApp.getInstance().MainBoardSetBL(bHigh);
        if (!bHigh) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.postResult(integer, bHigh ? ITaskCallback.BL_TRUE : ITaskCallback.BL_FALSE);
    }
}
