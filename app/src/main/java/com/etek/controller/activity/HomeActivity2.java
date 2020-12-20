package com.etek.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.etek.controller.R;
import com.etek.controller.common.Globals;
import com.etek.controller.entity.MainBoardInfoBean;
import com.etek.controller.fragment.MainBoardDialog;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.InitialCheckCallBack;
import com.etek.controller.model.User;
import com.etek.controller.scan.ScannerInterface;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 */
public class HomeActivity2 extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {


    private String TAG = "HomeActivity";
    private long lastBackKeyDownTick = 0;
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;
    private RelativeLayout projectManage;
    private RelativeLayout projectImplement;
    private RelativeLayout assistFunction;
    private RelativeLayout localSetting;
    private long startTime;

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
    }

    private void getUserInfo() {
        String userStr = getPreInfo("userInfo");
        if (TextUtils.isEmpty(userStr)) {
            startActivity(UserInfoActivity.class);
        }else{
            Globals.user = JSON.parseObject(userStr, User.class);
        }
    }

    private void initMainBoard() {
        new Thread() {
            @Override
            public void run() {
                DetApp.getInstance().MainBoardPowerOn();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                mainBoardInit();
            }
        }.start();
    }

    /**
     * 解除扫描对按间的占用
     */
    private void unlockScanKey() {
        ScannerInterface scannerInterface = new ScannerInterface(this);
        scannerInterface.unlockScanKey();
    }

    private void mainBoardInit() {
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
    }

    private void showMainBoardDialog(MainBoardInfoBean mainBoardInfoBean) {
        MainBoardDialog mainBoardDialog = new MainBoardDialog();
        mainBoardDialog.setMainBoardInfo(mainBoardInfoBean);
        mainBoardDialog.show(getSupportFragmentManager(), "mainBoardDialog");
    }

    private void initView() {
        startTime = System.currentTimeMillis();
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
