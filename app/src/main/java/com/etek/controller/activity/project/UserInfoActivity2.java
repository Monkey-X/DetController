package com.etek.controller.activity.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.etek.controller.R;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.common.Globals;
import com.etek.controller.model.User;
import com.etek.controller.utils.IdCardUtil;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInfoActivity2 extends BaseActivity implements View.OnClickListener {

    private static final String COMPANYCODE_REGEX = "^[0-9]{13}";
    private EditText personalInfoName;
    private EditText personalIdCode;
    private EditText companyName;
    private EditText companyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info2);
        initView();

        initData();
    }

    private void initView() {
        View backImg = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setText("用户信息");
        TextView textBtn = findViewById(R.id.text_btn);
        textBtn.setVisibility(View.GONE);
        backImg.setOnClickListener(this);

        personalInfoName = findViewById(R.id.personal_info_name);
        personalIdCode = findViewById(R.id.personal_id_code);
        companyName = findViewById(R.id.company_name);
        companyCode = findViewById(R.id.company_code);
        companyCode.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        Button personalInfoModify = findViewById(R.id.personal_info_modify);
        personalInfoModify.setOnClickListener(this);
    }


    private void initData() {
        String userinfo = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        if (!TextUtils.isEmpty(userinfo)) {
            User user = JSON.parseObject(userinfo, User.class);
            if (user != null && !TextUtils.isEmpty(user.getName())) {
                personalInfoName.setText(user.getName());
                personalIdCode.setText(user.getIdCode());
                companyName.setText(user.getCompanyName());
                companyCode.setText(user.getCompanyCode());
            }
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_info_modify:
                saveUserInfo();
                break;
            case R.id.back_img:
                boolean b = checkUserInfo();
                if (!b) {
                    return;
                }
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        boolean b = checkUserInfo();
        if (!b) {
            return;
        }
        finish();
    }

    private boolean checkUserInfo() {
        String userinfo = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        if (TextUtils.isEmpty(userinfo)) {
            return false;
        }
        return true;
    }

    /**
     * 保存用户信息
     */
    private void saveUserInfo() {
        String userName = personalInfoName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            showToast( "用户名不能为空");
            return;
        }

        String userId = personalIdCode.getText().toString().trim();
        if (TextUtils.isEmpty(userId)) {
            showToast( "身份证号码必须不为空");
            return;
        }

        personalIdCode.setText(userId.toUpperCase());

        if(!IdCardUtil.isValidatedAllIdcard(userId)){
            showToast( "请输入有效的身份证号码");
            return;
        }

        String comName = companyName.getText().toString().trim();
        if (TextUtils.isEmpty(comName)) {
            showToast( "单位名称不能为空");
            return;
        }

        String comCode = companyCode.getText().toString().trim();
        if (TextUtils.isEmpty(comCode)) {
            showToast( "单位代码不能为空");
            return;
        }

        Pattern compile = Pattern.compile(COMPANYCODE_REGEX);
        Matcher matcher = compile.matcher(comCode);
        if (!matcher.matches()) {
            showToast("请输入有效的单位代码");
            return;
        }
        User user = new User();
        user.setCompanyCode(comCode);
        user.setIdCode(userId);
        user.setCompanyName(comName);
        user.setName(userName);
        Globals.user = user;
//        setStringInfo("userInfo", JSON.toJSONString(user));
        SpManager.getIntance().saveSpString(AppSpSaveConstant.USER_INFO,JSON.toJSONString(user));
        showToast("信息已保存！");
    }
}