package com.etek.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.etek.controller.activity.project.UserInfoActivity2;
import com.etek.controller.activity.service.DownloadUtil;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.common.Globals;
import com.etek.controller.entity.AppUpdateBean;
import com.etek.controller.entity.MainBoardInfoBean;
import com.etek.controller.fragment.MainBoardDialog;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.InitialCheckCallBack;
import com.etek.controller.model.User;
import com.etek.controller.scan.ScannerInterface;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.UpdateAppUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.jsoup.helper.StringUtil;

import java.io.File;

/**
 * 首页
 */
public class HomeActivity2 extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {


    private String TAG = "HomeActivity";
    private long lastBackKeyDownTick = 0;
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;
    private RelativeLayout update;
    private ProgressBar updateProgress;
    private TextView speed;
    private RelativeLayout projectManage;
    private RelativeLayout projectImplement;
    private RelativeLayout assistFunction;
    private RelativeLayout localSetting;
    private long startTime;
    private int appUpdate = 0;
    private int mainBoardupdate = 1;
    private AlertDialog updateDialog;
    private MainBoardInfoBean mainBoardInfoBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home2);

        int initialize = DetApp.getInstance().Initialize();
        Log.d(TAG, "onCreate: initialize= " + initialize);

        initView();

        initMainBoard();

        unlockScanKey();

        getUserInfo();

        getMainBoardInfo();
        // 进行app升级的检查
        checkAppUpdate();
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
                Log.e(TAG, "check app update error");
            }
        });
    }

    private void checkAppData(AppUpdateBean.ResultBean.AppBean app, AppUpdateBean.ResultBean.MainBoardBean mainBoard) {
        //app更新
        if (AppUtils.getAppVersion(HomeActivity2.this) < app.getVersionCode()) {
            showUpdateDialog(appUpdate, app, mainBoard);
            return;
        }

        //主控制板更新
        if (mainBoardInfoBean != null) {
            String strSoftwareVer = mainBoardInfoBean.getStrSoftwareVer();
            if (mainBoard.getVersionName().compareToIgnoreCase(strSoftwareVer) > 0) {
                showUpdateDialog(mainBoardupdate, app, mainBoard);
            }
        }
    }

    /**
     * 根据flag弹更新提示框
     *
     * @param flag      0代表app更新    1代表控制板更新
     * @param app
     * @param mainBoard
     */
    private void showUpdateDialog(int flag, AppUpdateBean.ResultBean.AppBean app, AppUpdateBean.ResultBean.MainBoardBean mainBoard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity2.this);
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
                        ToastUtils.show(HomeActivity2.this, "下载链接错误，请检查");
                    }
                } else if (flag == mainBoardupdate) {
                    String downloadUrl = mainBoard.getDownloadUrl();
                    if (!TextUtils.isEmpty(downloadUrl) && downloadUrl.startsWith("http")) {
                        downLoadFile(1, downloadUrl, FileUtils.ExternalStorageDirectory + File.separator + "test", "MainBoard.bin");
                    } else {
                        ToastUtils.show(HomeActivity2.this, "下载链接错误，请检查");
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
                        HomeActivity2.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "file下载完成: " + file.getName());
                                update.setVisibility(View.GONE);
                                if (flag == appUpdate) {
                                    UpdateAppUtils.installApk(HomeActivity2.this, file);
                                } else if (flag == mainBoardupdate) {
                                    HomeActivity2.this.startActivity(new Intent(HomeActivity2.this, MainBoardUpdateActivity.class));
                                }
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {
                        HomeActivity2.this.runOnUiThread(new Runnable() {
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
                        HomeActivity2.this.runOnUiThread(new Runnable() {
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

    private void getUserInfo() {
        String userStr = getPreInfo("userInfo");
        if (TextUtils.isEmpty(userStr)) {
            startActivity(UserInfoActivity2.class);
        } else {
            Globals.user = JSON.parseObject(userStr, User.class);
        }
    }

    private void initMainBoard() {
        MainboardTask mainboardTask = new MainboardTask();
        mainboardTask.execute();
    }

    class MainboardTask extends AsyncTask<String, Integer, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("主板自检中...");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            DetApp.getInstance().MainBoardPowerOn();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            int result = mainBoardInit();
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            missProDialog();
            if (result !=0) {
                showStatusDialog("主板初始化失败！");
            }
        }
    }

    /**
     * 解除扫描对按间的占用
     */
    private void unlockScanKey() {
        ScannerInterface scannerInterface = new ScannerInterface(this);
        scannerInterface.unlockScanKey();
    }

    private int mainBoardInit() {
        int result = DetApp.getInstance().MainBoardInitialize(new InitialCheckCallBack() {
            @Override
            public void SetInitialCheckData(String strHardwareVer, String strUpdateHardwareVer, String strSoftwareVer, String strSNO, String strConfig, byte bCheckResult) {
                Log.d(TAG, "SetInitialCheckData: strHardwareVer = " + strHardwareVer);
                Log.d(TAG, "SetInitialCheckData: strUpdateHardwareVer = " + strUpdateHardwareVer);
                Log.d(TAG, "SetInitialCheckData: strSoftwareVer = " + strSoftwareVer);
                Log.d(TAG, "SetInitialCheckData: strSNO = " + strSNO);
                Log.d(TAG, "SetInitialCheckData: strConfig = " + strConfig);
                Log.d(TAG, "SetInitialCheckData: strConfig = " + strConfig);

                MainBoardInfoBean mainBoardInfoBean = new MainBoardInfoBean();
                mainBoardInfoBean.setStrHardwareVer(strHardwareVer);
                mainBoardInfoBean.setStrUpdateHardwareVer(strUpdateHardwareVer);
                mainBoardInfoBean.setStrSoftwareVer(strSoftwareVer);
                mainBoardInfoBean.setStrSNO(strSNO);
                mainBoardInfoBean.setStrConfig(strConfig);
                setStringInfo(getString(R.string.controller_sno), strSNO);
                setStringInfo(getString(R.string.mainBoardInfo_sp), JSON.toJSONString(mainBoardInfoBean));
//                showMainBoardDialog(mainBoardInfoBean);
            }
        });
        Log.d(TAG, "SetInitialCheckData: result = " + result);
        return result;
    }

    private void showMainBoardDialog(MainBoardInfoBean mainBoardInfoBean) {
        MainBoardDialog mainBoardDialog = new MainBoardDialog();
        mainBoardDialog.setMainBoardInfo(mainBoardInfoBean);
        mainBoardDialog.show(getSupportFragmentManager(), "mainBoardDialog");
    }

    private void initView() {
        startTime = System.currentTimeMillis();
        update = findViewById(R.id.rl_update);
        speed = findViewById(R.id.speed);
        updateProgress = findViewById(R.id.update_progress);
        projectManage = findViewById(R.id.home_project_manage);
        projectImplement = findViewById(R.id.home_project_implement);
        assistFunction = findViewById(R.id.home_assist_function);
        localSetting = findViewById(R.id.home_local_setting);
        projectManage.setOnClickListener(this);
        projectImplement.setOnClickListener(this);
        assistFunction.setOnClickListener(this);
        localSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_project_manage://授权下载
                startActivity(AuthorizedDownloadActivity.class);
                break;

            case R.id.home_project_implement://工程管理
                startActivity(ProjectManagerActivity.class);
                break;

            case R.id.home_assist_function://辅助功能
                startActivity(AssistActivity.class);
                break;

            case R.id.home_local_setting://本机设置
//                startActivity(UserInfoActivity.class);
                startActivity(PersonActivity.class);
                break;
        }
    }

    private void startActivity(Class clz) {
        startActivity(new Intent(this, clz));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onSart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DetApp.getInstance().ShutdownProc();
        DetApp.getInstance().Finalize();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onBackPressed() {
        long currentTick = System.currentTimeMillis();
        if (currentTick - startTime < 5000) {
            return;
        }
        if (currentTick - lastBackKeyDownTick > MAX_DOUBLE_BACK_DURATION) {
            showToast("再按一次退出");
            lastBackKeyDownTick = currentTick;
        } else {
            finish();
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(this.getString(R.string.home), color);
    }

}
