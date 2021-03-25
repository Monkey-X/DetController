package com.etek.controller.activity.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.etek.controller.R;
import com.etek.controller.activity.service.DownloadUtil;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.entity.AppUpdateBean;
import com.etek.controller.entity.MainBoardInfoBean;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.SommerUtils;
import com.etek.controller.utils.UpdateAppUtils;
import com.etek.controller.activity.BaseActivity;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.NetUtil;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.orhanobut.logger.Logger;


import java.io.File;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "AboutActivity";
    private int appUpdate = 0;
    private int mainBoardupdate = 1;
    private MainBoardInfoBean mainBoardInfoBean;
    private AlertDialog updateDialog;
    private RelativeLayout update;
    private TextView speed;
    private ProgressBar updateProgress;

    private boolean m_bUpgrading = false;
    private TextView m_btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        initData();
        getMainBoardInfo();
    }

    /**
     * 获取主控板信息
     */
    private void getMainBoardInfo() {
        String preInfo = getPreInfo(getString(R.string.mainBoardInfo_sp));
        if (!TextUtils.isEmpty(preInfo)) {
            try {
                mainBoardInfoBean = JSON.parseObject(preInfo, MainBoardInfoBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
                Logger.e(e.getMessage());
            }
        }
    }

    private void initData() {

    }

    private void initView() {
        update = findViewById(R.id.rl_update);
        speed = findViewById(R.id.speed);
        updateProgress = findViewById(R.id.update_progress);
        TextView appVersion = findViewById(R.id.set_app_version);
        TextView checkUpdate = findViewById(R.id.check_update);
        checkUpdate.setOnClickListener(this);


        appVersion.setText(SommerUtils.getVersionName(this));

        m_btnUpdate = findViewById(R.id.check_update);
    }

    @Override
    public void onClick(View v) {
        if (NetUtil.getNetType(mContext) < 0) {
            showStatusDialog("请去设置网络！");
            return;
        }

        m_bUpgrading = true;
        m_btnUpdate.setVisibility(View.GONE);

        checkAppUpdate();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //  右下角返回键
        if(4==keyCode){
            if(!m_bUpgrading){
                finish();
            }else{
                return false;
            }

        }
        return super.onKeyUp(keyCode, event);
    }

    private void checkAppUpdate() {
        UpdateAppUtils.checkAppUpdate(AppIntentString.APP_DOWNLOAD_URL, this, new UpdateAppUtils.AppUpdateCallback() {
            @Override
            public void onSuccess(AppUpdateBean updateInfo) {
                if (updateInfo != null && updateInfo.getResult() != null) {
                    AppUpdateBean.ResultBean.AppBean app = updateInfo.getResult().getApp();
                    AppUpdateBean.ResultBean.MainBoardBean mainBoard = updateInfo.getResult().getMainBoard();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkAppData(app, mainBoard);
                        }
                    });
                }
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       ToastUtils.showShort(AboutActivity.this,"已是最新版本！");
                    }
                });
                Log.e(TAG, "check app update error");
            }
        });
    }

    private void checkAppData(AppUpdateBean.ResultBean.AppBean app, AppUpdateBean.ResultBean.MainBoardBean mainBoard) {
        //app更新
        Log.d(TAG,String.format("后台版本:%d，APP版本:%d",app.getVersionCode(),AppUtils.getAppVersion(AboutActivity.this)));
        if (AppUtils.getAppVersion(AboutActivity.this) < app.getVersionCode()) {
            showUpdateDialog(appUpdate, app, mainBoard);
            return;
        }

        //主控制板更新
        if (mainBoardInfoBean != null) {
            String strSoftwareVer = mainBoardInfoBean.getStrSoftwareVer();
            Log.d(TAG,String.format("主控板版本：%s 后台版本：%s",mainBoard.getVersionName(),strSoftwareVer));
            if (mainBoard.getVersionName().compareToIgnoreCase(strSoftwareVer) > 0) {
                showUpdateDialog(mainBoardupdate, app, mainBoard);
                return;
            }
        }

        ToastUtils.showShort(AboutActivity.this,"已是最新版本！");
        m_bUpgrading = false;
        m_btnUpdate.setVisibility(View.VISIBLE);
    }


    /**
     * 根据flag弹更新提示框
     *
     * @param flag      0代表app更新    1代表控制板更新
     * @param app
     * @param mainBoard
     */
    private void showUpdateDialog(int flag, AppUpdateBean.ResultBean.AppBean app, AppUpdateBean.ResultBean.MainBoardBean mainBoard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
        builder.setMessage(flag == appUpdate ? app.getVersionNote() : mainBoard.getVersionNote());
        builder.setCancelable(false);
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (flag == appUpdate) {

                    String downloadUrl = app.getDownloadUrl();
                    if (!TextUtils.isEmpty(downloadUrl) && downloadUrl.startsWith("http")) {
                        downLoadFile(0, downloadUrl, FileUtils.ExternalStorageDirectory + File.separator + "test", "雷管E联.apk");
                    } else {
                        ToastUtils.show(AboutActivity.this, "下载链接错误，请检查");

                        m_bUpgrading = false;
                        m_btnUpdate.setVisibility(View.VISIBLE);
                    }
                } else if (flag == mainBoardupdate) {
                    String downloadUrl = mainBoard.getDownloadUrl();
                    if (!TextUtils.isEmpty(downloadUrl) && downloadUrl.startsWith("http")) {
                        downLoadFile(1, downloadUrl, FileUtils.ExternalStorageDirectory + File.separator + "test", "MainBoard.bin");
                    } else {
                        ToastUtils.show(AboutActivity.this, "下载链接错误，请检查");

                        m_bUpgrading = false;
                        m_btnUpdate.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if (flag == appUpdate && app.getVersionType() == 0) {
            builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateDialog.dismiss();

                    m_bUpgrading = false;
                    m_btnUpdate.setVisibility(View.VISIBLE);
                }
            });
        }

        if (flag == mainBoardupdate && mainBoard.getVersionType() == 0) {
            builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateDialog.dismiss();
                    m_bUpgrading = false;
                    m_btnUpdate.setVisibility(View.VISIBLE);
                }
            });
        }
        updateDialog = builder.create();
        updateDialog.show();
    }


    /**
     * 下载文件
     *
     * @param flag        0代表下载.apk文件，  1代表下载.bin文件
     * @param downloadUrl 下载地址
     * @param path        文件保存路径
     * @param fileName    文件名称
     */
    private void downLoadFile(int flag, String downloadUrl, String path, String fileName) {
        update.setVisibility(View.VISIBLE);
        File targetFile = new File(path + File.separator + fileName);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadUtil.get().download(downloadUrl, path, fileName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        AboutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "file下载完成: " + file.getName());
                                update.setVisibility(View.GONE);
                                if (flag == appUpdate) {
                                    UpdateAppUtils.installApk(AboutActivity.this, file);
                                } else if (flag == mainBoardupdate) {
                                    AboutActivity.this.startActivity(new Intent(AboutActivity.this, MainBoardUpdateActivity.class));
                                }
                                m_bUpgrading = false;
                                m_btnUpdate.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {
                        AboutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateProgress.setProgress(progress);
                                speed.setText(progress + " %");
                            }
                        });
                        Log.e(TAG, "progress: " + progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        AboutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update.setVisibility(View.GONE);

                                m_bUpgrading = false;
                                m_btnUpdate.setVisibility(View.VISIBLE);
                            }
                        });
                        Log.e(TAG, "Exception: " + e.getMessage());
                        ToastUtils.show(AboutActivity.this, "下载失败，请检查网络");
                    }
                });
            }
        }).start();
    }
}