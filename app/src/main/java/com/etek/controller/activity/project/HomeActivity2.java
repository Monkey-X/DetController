package com.etek.controller.activity.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.activity.AssistActivity;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.eventbus.MessageEvent;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.activity.service.DownloadUtil;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.common.Globals;
import com.etek.controller.entity.AppUpdateBean;
import com.etek.controller.entity.MainBoardInfoBean;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.InitialCheckCallBack;
import com.etek.controller.model.User;
import com.etek.controller.scan.ScannerInterface;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.UpdateAppUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.helper.StringUtil;

import java.io.File;

/**
 * 首页
 */
public class HomeActivity2 extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {


    private String TAG = "HomeActivity2";
    private RelativeLayout projectManage;
    private RelativeLayout projectImplement;
    private RelativeLayout assistFunction;
    private RelativeLayout localSetting;
    private int appUpdate = 0;
    private int mainBoardupdate = 1;
    private AlertDialog updateDialog;
    private MainBoardInfoBean mainBoardInfoBean;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_home2);

        EventBus.getDefault().register(this);

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
        showDownLoadDialog();
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
                                dissDownLoadDialog();
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
                                setDownLoadProgress(progress);
                            }
                        });
                        Log.e(TAG, "progress: " + progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        HomeActivity2.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dissDownLoadDialog();
                            }
                        });
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }
                });
            }
        }).start();
    }


    private void showDownLoadDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgressPercentFormat(null);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("正在下载...");
        }
        progressDialog.show();
    }

    private void setDownLoadProgress(int value) {
        if (progressDialog != null) {
            progressDialog.setProgress(value);
        }
    }


    private void dissDownLoadDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void getUserInfo() {
        String userStr = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        if (TextUtils.isEmpty(userStr)) {
            startActivity(UserInfoActivity2.class);
        } else {
            Globals.user = JSON.parseObject(userStr, User.class);
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,String.format("KeyCode=%d",keyCode));

        //  右下角的退出键
        if(KeyEvent.KEYCODE_BACK==keyCode){
            finish();
//            return true;
        }
        if (keyCode == event. KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
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

            DetApp.getInstance().SetCommTimeout(1000);

            Log.d(TAG,String.format("电平拉高"));
            DetApp.getInstance().MainBoardSetBL(true);

            Log.d(TAG,String.format("上电"));
            DetApp.getInstance().MainBoardPowerOn();
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//            }
//
//            //  1.0.0.12版本的OS，启动后是拉低
//            Log.d(TAG,String.format("电平拉高"));
//            DetApp.getInstance().MainBoardSetBL(true);

//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            Log.d(TAG,String.format("初始化"));

            int result = mainBoardInit();
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            missProDialog();

            String strerrmsg ="";
            switch (result){
                case 1:
                    strerrmsg="主板初始化失败：低压低于下限！";
                    break;
                case 2:
                    strerrmsg="主板初始化失败：低压高于下限！";
                    break;
                case 3:
                    strerrmsg="主板初始化失败：高压低于下限！";
                    break;
                case 4:
                    strerrmsg="主板初始化失败：高压高于上限！";
                    break;
                default:
                    strerrmsg = String.format("主板初始化失败！ %d",result);
                    break;
            }
            if (result !=0) {
                showStatusDialog(strerrmsg);
                DetApp.getInstance().SetCommTimeout(5000);
                finish();
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
                DetApp.getInstance().SetCommTimeout(5000);
            }
        });
        Log.d(TAG, "SetInitialCheckData: result = " + result);
        return result;
    }

    private void initView() {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.d(TAG,String.format("电平拉高"));
        DetApp.getInstance().MainBoardSetBL(true);

        DetApp.getInstance().ShutdownProc();
        DetApp.getInstance().Finalize();
        Log.d(TAG, "onDestroy: ");

        System.exit(0);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(this.getString(R.string.home), color);
    }




    // add: detect screen status, false for power off, ture for power up.
    ScreenStatusReceiver mScreenStatusReceiver;//全局广播接受对象
    //广播接受类
    private class ScreenStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                Log.d(TAG, "Detect screen on ");

            } else if("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                Log.d(TAG, "Detect screen off");
                finish();
            }
        }
    }

    private void initScreenReceiver(){
        //广播在哪里使用就在哪里进行注册
        //Register Receiver
        ScreenStatusReceiver mScreenStatusReceiver = new ScreenStatusReceiver();//new一个接受者
        IntentFilter filterIF = new IntentFilter();//new一个intent过滤器
        filterIF.addAction("android.intent.action.SCREEN_ON");//增加亮屏操作
        filterIF.addAction("android.intent.action.SCREEN_OFF");//增加灭屏操作
        registerReceiver(mScreenStatusReceiver, filterIF);//注册监听

    }

    //在ondestory里面进行对象的销毁
    private void closeScreenReceiver() {
        if(null!=mScreenStatusReceiver){
            unregisterReceiver(mScreenStatusReceiver);//注销监听
            mScreenStatusReceiver = null;//清空对象
        }
    }

}
