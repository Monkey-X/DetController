package com.etek.controller.activity.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

public class LoginInInfoResetActivity extends BaseActivity implements View.OnClickListener {
    private TextView txtLoginID;
    private TextView txtLoginPswd;
    private TextView txtNewPassword;
    private TextView txtNewPasswordConfirm;

    private final String TAG="LoginInInfoResetActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in_info_reset);
        initSupportActionBar(R.string.title_activity_login_in_info_reset);
        initView();
        initData();
    }

    private void initView() {
        txtLoginID = findViewById(R.id.login_id);
        txtLoginID.setKeyListener(null);

        txtLoginPswd = findViewById(R.id.login_password);
        txtNewPassword = findViewById(R.id.new_passowrd);
        txtNewPasswordConfirm = findViewById(R.id.new_passowrd_confirm);

        Button personalInfoModify = findViewById(R.id.btn_ok);
        personalInfoModify.setOnClickListener(this);
    }

    private void initData() {
        String str0 = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_NAME);
        Log.d(TAG,String.format("登录用户名：%s",str0));
        txtLoginID.setText(str0);
        txtLoginPswd.setText("");
        txtNewPassword.setText("");
        txtNewPasswordConfirm.setText("");
;    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                saveLoginInfo();
                break;
        }
    }

    private void saveLoginInfo() {
        String str0 = txtLoginID.getText().toString().trim();
        if (TextUtils.isEmpty(str0)) {
            showToast( "登录名不能为空！");
            return;
        }

        str0 = txtLoginPswd.getText().toString().trim();
        if (TextUtils.isEmpty(str0)) {
            showToast( "旧密码不能为空！");
            return;
        }

        str0 = txtNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(str0)) {
            showToast( "新密码不能为空！");
            return;
        }

        str0 = txtNewPasswordConfirm.getText().toString().trim();
        if (TextUtils.isEmpty(str0)) {
            showToast( "确认密码不能为空！");
            return;
        }

        if(!str0.equals(txtNewPassword.getText().toString().trim())){
            showToast( "两次输入的密码不一致！");
            return;
        }

        String old_password = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_PASSWORD);
        if(!old_password.equals(txtLoginPswd.getText().toString())){
            Log.d(TAG,String.format("%s:%s",old_password,txtLoginPswd.getText().toString().trim()));
            showToast( "旧密码不正确，不能修改!");
            return;
        }

        SpManager.getIntance().saveSpString(AppSpSaveConstant.USER_NAME,txtLoginID.getText().toString());
        SpManager.getIntance().saveSpString(AppSpSaveConstant.USER_PASSWORD,txtNewPassword.getText().toString());
        ToastUtils.show(this,"重置成功！");
        return;
    }
}



