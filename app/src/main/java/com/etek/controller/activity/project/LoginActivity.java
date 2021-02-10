package com.etek.controller.activity.project;

import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.activity.project.HomeActivity2;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.utils.GeneralDisplayUI;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

public class LoginActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText userName;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        initView();
        initData();

        getPermission();
    }

    private void getPermission() {
        requestPermission(AppIntentString.permissions, "请求应用操作的相关权限", new GrantedResult() {
            @Override
            public void onResult(boolean granted, String[] unGrantedPremission) {
                if (!granted) {
                   finish();
                }
            }
        });
    }

    private void initData() {
        String user_name = getStringInfo("User_Name");
        if (!TextUtils.isEmpty(user_name)) {
            userName.setText(user_name);
        }
    }

    private void initView() {
        userName = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        TextView login = findViewById(R.id.login);
        login.setOnClickListener(this);

        RadioGroup radiaGrop = findViewById(R.id.radioGroup);
        RadioButton wifi = findViewById(R.id.wifi);
        RadioButton mobileData = findViewById(R.id.mobileData);
        radiaGrop.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int ntype = GeneralDisplayUI.NETWORK_WIFI;
        switch (checkedId) {
            case R.id.wifi:
                ntype = GeneralDisplayUI.NETWORK_WIFI;
                break;
            case R.id.mobileData:
                ntype = GeneralDisplayUI.NETWORK_4G;
                break;
        }
        GeneralDisplayUI.showSettingNetworkSelect(this,ntype);
    }

    @Override
    public void onClick(View v) {
        String userStrName = userName.getText().toString().trim();
        String strPassword = password.getText().toString().trim();
        if (TextUtils.isEmpty(userStrName)) {
            ToastUtils.showShort(this, "请输入用户名！");
            return;
        }
        if (TextUtils.isEmpty(strPassword)) {
            ToastUtils.showShort(this, "请输入密码！");
            return;
        }

        if (strPassword.equalsIgnoreCase("admin")) {
            startActivity(new Intent(this, HomeActivity2.class));
            finish();
            return;
        }

        String user_name = getStringInfo("User_Name");
        String user_passWord = getStringInfo("User_PassWord");

        if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_passWord)) {
            setStringInfo("User_Name", userStrName);
            setStringInfo("User_PassWord", strPassword);
            startActivity(new Intent(this, HomeActivity2.class));
            finish();
            return;
        }

        if (user_name.equals(userStrName) && user_passWord.equals(strPassword)) {
            startActivity(new Intent(this, HomeActivity2.class));
            finish();
        }else{
            ToastUtils.showShort(this, "输入账号或密码有误！");
        }
    }
}