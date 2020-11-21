package com.etek.controller.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.hardware.command.DetApp;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

/**
 * 线路检测
 */
public class LineCheckActivity extends BaseActivity implements View.OnClickListener {


    private TextView checkLineResult;
    private TextView dianya;
    private TextView dianliu;
    private String TAG = "LineCheckActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_check);
        initSupportActionBar(R.string.title_act_line_check);
        initView();
        initDate();
    }

    /**
     * 初始化View
     */
    private void initView() {
        checkLineResult = findViewById(R.id.check_line_result);
        TextView checkLine = findViewById(R.id.check_line);
        dianya = findViewById(R.id.dianya);
        dianliu = findViewById(R.id.dianliu);
        TextView checkM = findViewById(R.id.check_m);
        checkLine.setOnClickListener(this);
        checkM.setOnClickListener(this);
    }

    /**
     * 页面展示的数据
     */
    private void initDate() {
        //TODO 通过接口获取数据并展示
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_line:
                checkShort();
                break;
            case R.id.check_m:
                checkVA();
                break;
        }
    }

    // 检查总线电流和电压
    private void checkVA() {
        showProDialog("检测中...");
        new Thread() {
            @Override
            public void run() {
                StringBuilder strData = new StringBuilder();
                int ret = DetApp.getInstance().MainBoardGetCurrentVoltage(strData);
                showResult(ret, strData.toString());
            }
        }.start();

    }

    private void showResult(int ret, String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                missProDialog();
                if (ret != 0) {
                    Log.d(TAG, "获取电压电流 失败 " + ret);
                    ToastUtils.show(LineCheckActivity.this, "获取电压电流 失败 " + ret);
                } else {
                    float fv = (float) (Integer.parseInt(data.substring(0, 8), 16) * 1.00);
                    float fc = (float) (Integer.parseInt(data.substring(8, 16), 16) * 1.00);
                    dianya.setText(fv + "mV");
                    dianliu.setText(fc + "mA");
                    Log.d(TAG, String.format("电压：%.2fmV\t电流：%,2fmA", fv, fc));
                }
            }
        });
    }

    // 检查短路
    private void checkShort() {
        showProDialog("检测中...");
        new Thread() {
            @Override
            public void run() {
                int i = DetApp.getInstance().CheckBusShortCircuit();
                Log.d(TAG, "CheckBusShortCircuit result = " + i);
                showResult(i);
            }
        }.start();
    }


    private void showResult(int result){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                missProDialog();
                if (result == 0) {
                    checkLineResult.setText("正常");
                }else {
                    checkLineResult.setText("异常");
                }
            }
        });
    }
}