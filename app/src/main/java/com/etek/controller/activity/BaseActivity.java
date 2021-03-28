package com.etek.controller.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;

import com.etek.controller.activity.project.ToastNewUtils;
import com.etek.sommerlibrary.R;
import com.etek.sommerlibrary.common.ActivityCollector;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    protected Toolbar mToolbar;

    protected Context mContext;

    protected static String LOG_TAG = "";
    private ProgressDialog progressDialog;


    protected void showProDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
	
	protected void setProDialogText(String strText){
        if(null!=progressDialog){
            progressDialog.setMessage(strText);
        }
    }
		

    protected void missProDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
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
        super.onDestroy();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mToolbar != null) {
            mToolbar.setTitle(title);
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

    protected String getStringInfo(String index) {
        preferences = getSharedPreferences("detInfo", MODE_PRIVATE);
        return preferences.getString(index, "");

    }




    protected int getMyColor(int colorID) {
        return mContext.getResources().getColor(colorID);
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
                ToastNewUtils.getInstance(mContext).showShortToast(message);
            }
        });

    }

    protected void showLongToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastNewUtils.getInstance(mContext).showLongToast(message);
            }
        });

    }

}
