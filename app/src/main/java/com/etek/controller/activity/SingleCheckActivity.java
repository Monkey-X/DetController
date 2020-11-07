package com.etek.controller.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.etek.controller.R;
import com.etek.controller.hardware.command.DetApp;
import com.etek.sommerlibrary.activity.BaseActivity;

/**
 * 单线检测
 */
public class SingleCheckActivity extends BaseActivity {

    private TextView mPipeCode;
    private TextView mUid;
    private TextView mDelayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_check);
        initSupportActionBar(R.string.title_act_single_check);
        initView();
        initDate();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mPipeCode = findViewById(R.id.pipe_code);
        mUid = findViewById(R.id.uid);
        mDelayed = findViewById(R.id.delayed);
    }

    /**
     * 页面展示的数据
     */
    private void initDate() {
        //TODO 通过接口获取数据并展示
        DetApp detApp = DetApp.getInstance();

        int i = detApp.MainBoardEcho();

        Toast.makeText(getApplicationContext(), "MainBoardEcho:" + i, Toast.LENGTH_SHORT).show();

        System.out.println("MainBoardEcho:" + i);
    }
}