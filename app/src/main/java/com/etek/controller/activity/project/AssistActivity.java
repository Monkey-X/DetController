package com.etek.controller.activity.project;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.etek.controller.R;
import com.etek.controller.hardware.command.DetApp;
import com.etek.sommerlibrary.activity.BaseActivity;

/**
 * 辅助功能
 */
public class AssistActivity extends BaseActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assist);
        initSupportActionBar(R.string.title_act_assist_function);
        initView();
    }


    /**
     * 初始化View
     */
    private void initView() {
        View view = findViewById(R.id.single_det_check);
        View lineCheck = findViewById(R.id.project_power_bomb);
        view.setOnClickListener(this);
        lineCheck.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_det_check:
                Intent singleIntent = new Intent(AssistActivity.this, SingleCheckActivity.class);
                startActivity(singleIntent);
                break;
            case R.id.project_power_bomb:
                Intent lineIntent = new Intent(AssistActivity.this, LineCheckActivity.class);
                startActivity(lineIntent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 必须总线下电
        DetApp.getInstance().MainBoardBusPowerOff();
    }
}