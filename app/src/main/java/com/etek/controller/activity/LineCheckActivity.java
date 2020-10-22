package com.etek.controller.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.etek.controller.R;
import com.etek.sommerlibrary.activity.BaseActivity;

/**
 * 线路检测
 */
public class LineCheckActivity extends BaseActivity {

    private TextView mSumLineElectricity;

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
        mSumLineElectricity = findViewById(R.id.sum_line_electricity);
    }

    /**
     * 页面展示的数据
     */
    private void initDate() {
        //TODO 通过接口获取数据并展示
    }
}