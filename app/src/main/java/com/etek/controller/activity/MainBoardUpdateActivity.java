package com.etek.controller.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.etek.controller.model.User;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.jsoup.helper.StringUtil;

public class MainBoardUpdateActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainBoardUpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board_update);
        initSupportActionBar(R.string.title_main_board_update);
        View update = findViewById(R.id.update);
        initData();

        update.setOnClickListener(this);
    }

    private void initData() {
        String preInfo = getPreInfo(getString(R.string.mainBoardInfo_sp));
        if (!StringUtil.isBlank(preInfo)) {
            try {
                MainBoardInfoBean mainBoardInfoBean = JSON.parseObject(preInfo, MainBoardInfoBean.class);
                if (mainBoardInfoBean != null) {
                    TextView hardver = findViewById(R.id.hardVer);
                    TextView updateHardwareVer = findViewById(R.id.updateHardwareVer);
                    TextView softwareVer = findViewById(R.id.softwareVer);
                    TextView sno = findViewById(R.id.sno);
                    TextView config = findViewById(R.id.config);

                    hardver.setText("v" + mainBoardInfoBean.getStrHardwareVer());
                    updateHardwareVer.setText("v" + mainBoardInfoBean.getStrUpdateHardwareVer());
                    softwareVer.setText("v" + mainBoardInfoBean.getStrSoftwareVer());
                    sno.setText(mainBoardInfoBean.getStrSNO());
                    config.setText(mainBoardInfoBean.getStrConfig());
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

                int result = DetApp.getInstance().DownloadProc("/test/CoreBrd1768_Std_v1.0.13.bin", new DetCallback() {
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
}