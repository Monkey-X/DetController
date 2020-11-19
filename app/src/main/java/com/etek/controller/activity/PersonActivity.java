package com.etek.controller.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.sommerlibrary.activity.BaseActivity;


/**
 * 本机设置界面
 */
public class PersonActivity extends BaseActivity implements View.OnClickListener {

    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        initView();
    }

    private void initView() {
        userName = findViewById(R.id.user_name);
        TextView userManager = findViewById(R.id.user_manager);
        TextView dataClean = findViewById(R.id.clean_data);
        TextView mainBoardUpdate = findViewById(R.id.mainboard_update);
        TextView dataSetting = findViewById(R.id.data_setting);
        TextView about = findViewById(R.id.about);
        userManager.setOnClickListener(this);
        dataClean.setOnClickListener(this);
        mainBoardUpdate.setOnClickListener(this);
        dataSetting.setOnClickListener(this);
        about.setOnClickListener(this);
        getUserName();

    }

    private void getUserName() {
        String user_name = getStringInfo("User_Name");
        userName.setText(user_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_manager:
                Intent intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.clean_data:
                break;
            case R.id.mainboard_update:
                Intent mainIntent = new Intent(this, MainBoardUpdateActivity.class);
                startActivity(mainIntent);
                break;
            case R.id.data_setting:
                startActivity(SettingsActivity.class);
                break;
            case R.id.about:
                break;
        }
    }

    private void startActivity(Class clz) {
        startActivity(new Intent(this, clz));
    }
}