package com.etek.sommerlibrary.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.MenuItem;
import android.view.WindowManager;

import com.elvishew.xlog.XLog;
import com.etek.sommerlibrary.R;
import com.etek.sommerlibrary.common.ActivityCollector;
import com.etek.sommerlibrary.utils.StringTool;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.maning.mndialoglibrary.MProgressDialog;

import java.util.ArrayList;
import java.util.Arrays;


public class BaseActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    protected Toolbar mToolbar;

    protected Context mContext;

    protected static String LOG_TAG = "";
    ProgressDialog pd1;
    private ProgressDialog progressDialog;


    protected void showProgressBar(String title, int max) {
        pd1 = new ProgressDialog(this);
        pd1.setTitle(title);
        pd1.setIcon(R.mipmap.ic_launcher);
        pd1.setMessage("传输中 请等待");
        pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd1.setCancelable(false);
        pd1.setCanceledOnTouchOutside(false);
//        pd1.setIndeterminate(true);
        pd1.setMax(max);
        pd1.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pd1.dismiss();
            }
        });
        pd1.show();

    }

    protected void showProDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void missProDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected void setProDialogText(String strText){
        if(null!=progressDialog){
            progressDialog.setMessage(strText);
        }
    }


    protected void showStatusDialog(final String content) {
        runOnUiThread(() -> {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
            builder.setTitle(content);
            //设置对话框标题
//            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                showToast("你输入的是: " + edit.getText().toString());
                    dialog.dismiss();
                }
            });

            // 4.设置常用api，并show弹出
            builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
            android.support.v7.app.AlertDialog dialog = builder.create(); //创建对话框
            dialog.setCanceledOnTouchOutside(false); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
            dialog.show();
        });

    }

    protected void showProgressDialog(String content) {
    }

    protected void closeProgressDialog() {
        MProgressDialog.dismissProgress();
    }


    protected void setProgressBar(int progress) {

        pd1.setProgress(progress);
//        LogD("progress:"+progress+"  | " + pd1.getProgress());

    }

    protected void dismissProgressBar() {
        if (pd1 != null) {
            pd1.cancel();
        }

    }

    protected int getWindowWidth() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        return outMetrics.widthPixels;
    }


    @Nullable
    protected Activity findActivity() {
        if (mContext instanceof Activity) {
            return (Activity) mContext;
        }
        if (mContext instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) mContext;
            return findActivity();
        } else {
            return null;
        }
    }

    @Nullable
    protected String getTag() {
        if (mContext instanceof Activity) {
            return mContext.getClass().getSimpleName();
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        // set the screen to portrait
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        LOG_TAG = mContext.getClass().getSimpleName();
        ActivityCollector.addActivity(this);
    }


    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        closeProgressDialog();
        super.onDestroy();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    protected void initToolBar(int titleResource) {
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(titleResource);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标
        }

    }


    protected void initSupportActionBar(int title) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//这句代码使启用Activity回退功能，并显示Toolbar上的左侧回退图标

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    protected String getPreInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);

        return preferences.getString(index, "");
    }


    protected void setStringInfo(String index, String value) {

        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(index, value);
        editor.apply();

    }


    protected void setIntInfo(String index, int value) {

        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(index, value);
        editor.apply();

    }


    protected void setLongInfo(String index, long value) {

        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(index, value);
        editor.apply();

    }

    protected long getLongInfo(String index) {

        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getLong(index, 0L);

    }


    protected String getStringInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getString(index, "");

    }

    protected int getIntInfo(String index) {

        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getInt(index, 0);

    }

    protected Uri getUriInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        String str = preferences.getString(index, "");
        if (StringTool.isNullOrBlankStr(str)) {
            return null;
        }
        Uri uri = Uri.parse(str);
        return uri;
    }

    protected void setUriInfo(String index, Uri value) {

        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(index, value.toString());
        editor.apply();
    }

    // 保存延时设置
    protected void setDelaySetting(String key,String delaySetting){
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,delaySetting);
        editor.apply();
    }

    // 获取延时设置
    protected String getDelaySetting(String key){
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getString(key,"");
    }


    protected Boolean getBooleanInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);

        return preferences.getBoolean(index, false);
    }

    protected Boolean getBooleanInfoDefaultTrue(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);

        return preferences.getBoolean(index, true);
    }

    protected void setBooleanInfo(String index, boolean value) {

        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(index, value);
        editor.apply();

    }

    protected void getPixel() {
        // 通过Activity类中的getWindowManager()方法获取窗口管理，再调用getDefaultDisplay()方法获取获取Display对象
        Display display = getWindowManager().getDefaultDisplay();

        // 方法一(推荐使用)使用Point来保存屏幕宽、高两个数据
        Point outSize = new Point();
        // 通过Display对象获取屏幕宽、高数据并保存到Point对象中
        display.getSize(outSize);
        // 从Point对象中获取宽、高
        int x = outSize.x;
        int y = outSize.y;
        // 通过吐司显示屏幕宽、高数据
        ToastUtils.showCustom(this, "手机像素为：X:" + x + "||Y:" + y);


    }


    protected int getMyColor(int colorID) {
        return mContext.getResources().getColor(colorID);
    }

    protected String getMyString(int stringId) {
        return mContext.getResources().getString(stringId);
    }

    /*----以下是android6.0动态授权的封装十分好用---------------------------------------------------------------------------*/
    private int mPermissionIdx = 0x10;//请求权限索引
    private SparseArray<GrantedResult> mPermissions = new SparseArray<>();//请求权限运行列表

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        GrantedResult runnable = mPermissions.get(requestCode);
        if (runnable == null) {
            return;
        }
        ArrayList<String> unGrantedPremList = new ArrayList<String>();
        XLog.d("permissions:" + Arrays.toString(permissions) + " -  grantResults: " + Arrays.toString(grantResults));
        runnable.mGranted = true;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    unGrantedPremList.add(permissions[i]);
                    runnable.mGranted = false;
                }
            }
        }
        runnable.unGrantedPremission = unGrantedPremList.toArray(new String[0]);
        runOnUiThread(runnable);
    }

    public void requestPermission(String[] permissions, String reason, GrantedResult runnable) {
        if (runnable == null) {
            return;
        }
        runnable.mGranted = false;
        if (Build.VERSION.SDK_INT < 23 || permissions == null || permissions.length == 0) {
            runnable.mGranted = true;//新添加
            runOnUiThread(runnable);
            return;
        }
        final int requestCode = mPermissionIdx++;
        mPermissions.put(requestCode, runnable);

		/*
			是否需要请求权限
		 */
        boolean granted = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                granted = granted && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (granted) {
            runnable.mGranted = true;
            runOnUiThread(runnable);
            return;
        }

		/*
			是否需要请求弹出窗
		 */
        boolean request = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request = request && !shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!request) {
            final String[] permissionTemp = permissions;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(reason)
                    .setPositiveButton(R.string.btn_sure, (dialog1, which) -> requestPermissions(permissionTemp, requestCode))
                    .setNegativeButton(R.string.btn_cancel, (dialog12, which) -> {
                        dialog12.dismiss();
                        GrantedResult runnable1 = mPermissions.get(requestCode);
                        if (runnable1 == null) {
                            return;
                        }
                        runnable1.mGranted = false;
                        runOnUiThread(runnable1);
                    }).create();
            dialog.show();
        } else {
            requestPermissions(permissions, requestCode);
        }
    }

    public static abstract class GrantedResult implements Runnable {
        private boolean mGranted;
        private String[] unGrantedPremission;

        public abstract void onResult(boolean granted, String[] unGrantedPremission);

        @Override
        public void run() {
            onResult(mGranted, unGrantedPremission);
        }
    }

    /*----以下是log记录---------------------------------------------------------------------------*/

    /*----TOAST---------------------------------------------------------------------------*/
    protected void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showCustom(mContext, message);
            }
        });

    }

    protected void showLongToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showLongCustom(mContext, message);
            }
        });

    }


    protected void delayAction(final Intent intent, long time) {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (intent != null) {
                    startActivity(intent);
                }

                finish();
            }
        }, time);
    }


}
