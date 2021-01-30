package com.etek.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.activity.service.DownloadUtil;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.entity.AppUpdateBean;
import com.etek.controller.entity.MainBoardInfoBean;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.SommerUtils;
import com.etek.controller.utils.UpdateAppUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.jsoup.helper.StringUtil;

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
        if (!StringUtil.isBlank(preInfo)) {
            try {
                mainBoardInfoBean = JSON.parseObject(preInfo, MainBoardInfoBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
                XLog.e(e.getMessage());
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
    }

    @Override
    public void onClick(View v) {
        checkAppUpdate();
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
        if (AppUtils.getAppVersion(AboutActivity.this) < app.getVersionCode()) {
            showUpdateDialog(appUpdate, app, mainBoard);
            return;
        }

        //主控制板更新
        if (mainBoardInfoBean != null) {
            String strSoftwareVer = mainBoardInfoBean.getStrSoftwareVer();
            if (mainBoard.getVersionName().compareToIgnoreCase(strSoftwareVer) > 0) {
                showUpdateDialog(mainBoardupdate, app, mainBoard);
                return;
            }
        }
        ToastUtils.showShort(AboutActivity.this,"已是最新版本！");
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
                    }
                } else if (flag == mainBoardupdate) {
                    String downloadUrl = mainBoard.getDownloadUrl();
                    if (!TextUtils.isEmpty(downloadUrl) && downloadUrl.startsWith("http")) {
                        downLoadFile(1, downloadUrl, FileUtils.ExternalStorageDirectory + File.separator + "test", "MainBoard.bin");
                    } else {
                        ToastUtils.show(AboutActivity.this, "下载链接错误，请检查");
                    }
                }
            }
        });

        if (flag == appUpdate && app.getVersionType() == 0) {
            builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateDialog.dismiss();
                }
            });
        }

        if (flag == mainBoardupdate && mainBoard.getVersionType() == 0) {
            builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateDialog.dismiss();
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
                            }
                        });
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }
                });
            }
        }).start();
    }
}