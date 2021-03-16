package com.etek.controller.activity.project;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.util.SoundPoolHelp;
import com.etek.controller.utils.VibrateUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

/**
 * 线路检测
 */
public class LineCheckActivity extends BaseActivity implements View.OnClickListener {


    private TextView dianya;
    private TextView dianliu;
    private String TAG = "LineCheckActivity";

    private Handler handler = new Handler();
    private SoundPoolHelp soundPoolHelp;

    private boolean m_bCancel = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder strData = new StringBuilder();
            int i = DetApp.getInstance().MainBoardLineCheckGetValue(strData);
            showResultNew(i, strData.toString());
            if(i==2){
                m_bCancel = true;
            }
            if(!m_bCancel){
                Log.d(TAG,"1S后继续执行");
                handler.postDelayed(runnable, 1000);
            }
        }
    };

    private void showResultNew(int ret, String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ret == 1) {
                    Log.d(TAG, "获取电压电流 失败 " + ret);
                    playSound(false);
                    ToastUtils.showCustom(LineCheckActivity.this, data);
                    return;
                } else if (ret == 0) {
                    float fv = (float) (Integer.parseInt(data.substring(0, 8), 16) * 0.001);
                    float fc = (float) (Integer.parseInt(data.substring(8, 16), 16) * 0.001);
                    dianya.setText(fv + "V");
                    dianliu.setText(fc + "mA");
                    Log.d(TAG, String.format("电压：%.2fV\t电流：%,2fmA", fv, fc));
                    return;
                } else if (ret == 2) {
                    //  Runnable必须先停止，否则会导致接收串口延时
                    if (handler != null) {
                        if (runnable != null) {
                            handler.removeCallbacks(runnable);
                        }
                        handler.removeCallbacksAndMessages(null);
                    }
                    // 等于2 才可以退出
                    DetApp.getInstance().MainBoardSetBL(true);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_check);
        initView();
        initData();
        initSound();

        checkLineData();

    }

    private void checkLineData() {
        DetApp.getInstance().MainBoardLineCheckStart();
        handler.postDelayed(runnable, 1000);
    }

    /**
     * 初始化View
     */
    private void initView() {
        View backImg = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        textTitle.setText("线路检测");
        textBtn.setVisibility(View.GONE);
        backImg.setOnClickListener(this);
        TextView cancelCheck = findViewById(R.id.cancel_check);
        dianya = findViewById(R.id.dianya);
        dianliu = findViewById(R.id.dianliu);
        cancelCheck.setOnClickListener(this);
    }
    /**
     * 初始化音效
     */
    private void initSound() {
        soundPoolHelp = new SoundPoolHelp(this);
        soundPoolHelp.initSound();
    }
    private void releaseSound() {
        if (soundPoolHelp!=null) {
            soundPoolHelp.releaseSound();
        }
    }
    private void playSound(boolean b) {
        if (soundPoolHelp != null && !b) {
            soundPoolHelp.playSound(b);
            VibrateUtil.vibrate(LineCheckActivity.this, 150);
        }
    }

    /**
     * 页面展示的数据
     */
    private void initData() {
        m_bCancel = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_check:
                // 取消检测
                cancelLineCheck();
                break;
            case R.id.back_img:
                cancelLineCheck();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        cancelLineCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseSound();
        // 必须总线下电
        DetApp.getInstance().MainBoardBusPowerOff();

    }

    /**
     * 取消循环检测
     */
    private void cancelLineCheck() {
        DetApp.getInstance().MainBoardSetBL(false);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //  右下角返回键
        if(4==keyCode){
            cancelLineCheck();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}