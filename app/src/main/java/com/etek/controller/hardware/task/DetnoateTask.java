package com.etek.controller.hardware.task;

import android.os.AsyncTask;

import com.etek.controller.hardware.command.DetApp;

import java.lang.ref.WeakReference;

/**
 * 进行起爆的操作
 */
public class DetnoateTask extends AsyncTask<String, Integer, Integer> {

    private static final String TAG = "DetnoateTask";
    private final WeakReference<ITaskCallback> iTaskCallbackWeakReference;

    public DetnoateTask(ITaskCallback callback) {
        iTaskCallbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        int result = DetApp.getInstance().ModuleDetonate(0);
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        ITaskCallback iTaskCallback = iTaskCallbackWeakReference.get();
        iTaskCallback.postResult(integer,ITaskCallback.DETONATE);
    }
}
