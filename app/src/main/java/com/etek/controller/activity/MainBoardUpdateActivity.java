package com.etek.controller.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.common.Globals;
import com.etek.controller.entity.MainBoardInfoBean;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.DetCallback;
import com.etek.controller.hardware.test.InitialCheckCallBack;
import com.etek.controller.model.User;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.jsoup.helper.StringUtil;

public class MainBoardUpdateActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainBoardUpdateActivity";
    private TextView hardver;
    private TextView updateHardwareVer;
    private TextView softwareVer;
    private TextView sno;
    private MainboardTask mainboardTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board_update);
        initSupportActionBar(R.string.title_main_board_update);
        View update = findViewById(R.id.AppUpdate);
        View MainBoardUpdate = findViewById(R.id.MainBoardUpdate);
        initData();

//        update.setOnClickListener(this);
        MainBoardUpdate.setOnClickListener(this);

        checkMainBoardInfo();

    }

    private void checkMainBoardInfo() {
        mainboardTask = new MainboardTask();
        mainboardTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainboardTask !=null) {
            mainboardTask.cancel(true);
        }
    }

    private void initData() {

        hardver = findViewById(R.id.hardVer);
        updateHardwareVer = findViewById(R.id.updateHardwareVer);
        softwareVer = findViewById(R.id.softwareVer);
        sno = findViewById(R.id.sno);

        String preInfo = getPreInfo(getString(R.string.mainBoardInfo_sp));
        if (!StringUtil.isBlank(preInfo)) {
            try {
                MainBoardInfoBean mainBoardInfoBean = JSON.parseObject(preInfo, MainBoardInfoBean.class);
                if (mainBoardInfoBean != null) {
                    hardver.setText("v" + mainBoardInfoBean.getStrHardwareVer());
                    updateHardwareVer.setText("v" + mainBoardInfoBean.getStrUpdateHardwareVer());
                    softwareVer.setText("v" + mainBoardInfoBean.getStrSoftwareVer());
                    sno.setText(mainBoardInfoBean.getStrSNO());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                XLog.e(e.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        showProDialog("升级中...");
        new Thread() {
            @Override
            public void run() {
                DetApp.getInstance().MainBoardPowerOff();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int result = DetApp.getInstance().DownloadProc("/test/MainBoard.bin", new DetCallback() {
                    @Override
                    public void DisplayText(String strText) {
                        Log.d(TAG, "DisplayText: " + strText);
                    }

                    @Override
                    public void StartProgressbar() {

                    }

                    @Override
                    public void SetProgressbarValue(int nVal) {

                    }

                    @Override
                    public void SetSingleModuleCheckData(int nID, byte[] szDC, int nDT, byte bCheckResult) {

                    }

                    /***
                     * 批量操作雷管时结果回调函数
                     * @param nID
                     * @param nResult
                     */
                    @Override
                    public void SetDetsSettingResult(int nID,int nResult){

                    }


                    /***
                     * 充电和放电过程中返回的电压和电流值
                     * @param nVoltage
                     * @param nCurrent
                     */
                    @Override
                    public void setChargeData(int nVoltage,int nCurrent){

                    }
                });
                showResult(result);
            }
        }.start();

    }

    private void showResult(int result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                missProDialog();
                if (result == 0) {
                    ToastUtils.show(MainBoardUpdateActivity.this, "升级完成！");
                } else {
                    ToastUtils.show(MainBoardUpdateActivity.this, "升级失败！");
                }
            }
        });
    }


    class MainboardTask extends AsyncTask<String, Integer, MainBoardInfoBean> {

        @Override
        protected MainBoardInfoBean doInBackground(String... strings) {

            MainBoardInfoBean mainBoardInfoBean = new MainBoardInfoBean();
            int result = DetApp.getInstance().MainBoardInitialize(new InitialCheckCallBack() {
                @Override
                public void SetInitialCheckData(String strHardwareVer, String strUpdateHardwareVer, String strSoftwareVer, String strSNO, String strConfig, byte bCheckResult) {
                    Log.d(TAG, "SetInitialCheckData: strHardwareVer = " + strHardwareVer);
                    Log.d(TAG, "SetInitialCheckData: strUpdateHardwareVer = " + strUpdateHardwareVer);
                    Log.d(TAG, "SetInitialCheckData: strSoftwareVer = " + strSoftwareVer);
                    Log.d(TAG, "SetInitialCheckData: strSNO = " + strSNO);
                    Log.d(TAG, "SetInitialCheckData: strConfig = " + strConfig);
                    Log.d(TAG, "SetInitialCheckData: strConfig = " + strConfig);
                    mainBoardInfoBean.setStrHardwareVer(strHardwareVer);
                    mainBoardInfoBean.setStrUpdateHardwareVer(strUpdateHardwareVer);
                    mainBoardInfoBean.setStrSoftwareVer(strSoftwareVer);
                    mainBoardInfoBean.setStrSNO(strSNO);
                    mainBoardInfoBean.setStrConfig(strConfig);
                }
            });
            return mainBoardInfoBean;
        }

        @Override
        protected void onPostExecute(MainBoardInfoBean mainBoardInfoBean) {
            super.onPostExecute(mainBoardInfoBean);
            if (mainBoardInfoBean!=null) {
                hardver.setText("v" + mainBoardInfoBean.getStrHardwareVer());
                updateHardwareVer.setText("v" + mainBoardInfoBean.getStrUpdateHardwareVer());
                softwareVer.setText("v" + mainBoardInfoBean.getStrSoftwareVer());
                sno.setText(mainBoardInfoBean.getStrSNO());
            }
        }
    }
}