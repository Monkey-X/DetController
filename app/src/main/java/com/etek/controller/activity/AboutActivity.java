package com.etek.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        TextView appVersion = findViewById(R.id.set_app_version);
        TextView checkUpdate = findViewById(R.id.check_update);
        checkUpdate.setOnClickListener(this);


        appVersion.setText(SommerUtils.getVersionName(this));
    }

    @Override
    public void onClick(View v) {
        // TODO: 2020/11/21 点击检查应用的升级
    }
}