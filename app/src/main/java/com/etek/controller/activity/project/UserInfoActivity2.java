package com.etek.controller.activity.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.activity.ProjectDetailActivity;
import com.etek.controller.model.User;
import com.etek.controller.utils.IdCardUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInfoActivity2 extends BaseActivity implements View.OnClickListener {

    private static final String COMPANYCODE_REGEX = "^[0-9]{13}";
    private EditText personalInfoName;
    private EditText personalInfoSex;
    private EditText personalInfoAge;
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
        personalInfoSex = findViewById(R.id.personal_info_sex);
        personalInfoAge = findViewById(R.id.personal_info_age);
        personalIdCode = findViewById(R.id.personal_id_code);
        companyName = findViewById(R.id.company_name);
        companyCode = findViewById(R.id.company_code);

        Button personalInfoModify = findViewById(R.id.personal_info_modify);
        personalInfoModify.setOnClickListener(this);
    }


    private void initData() {
        String userinfo = getStringInfo("userInfo");
        if (!TextUtils.isEmpty(userinfo)) {
            User user = JSON.parseObject(userinfo, User.class);
            if (user != null && !StringUtil.isBlank(user.getName())) {
                personalInfoName.setText(user.getName());
                personalInfoSex.setText(user.getSex());
                personalInfoAge.setText("" + user.getAge());
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
        String userinfo = getStringInfo("userInfo");
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

        String userSex = personalInfoSex.getText().toString().trim();
        if (TextUtils.isEmpty(userSex)) {
            showToast( "性别不能为空，必须为男女");
            return;
        }

        String userAge = personalInfoAge.getText().toString().trim();
        if (TextUtils.isEmpty(userAge)) {
            showToast( "年龄不能为空，必须为数字！");
            return;
        }

        String userId = personalIdCode.getText().toString().trim();
        if (StringUtils.isEmpty(userId)) {
            showToast( "身份证号码必须不为空");
            return;
        }

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
            showToast( "请输入有效的单位代码");
            return;
        }
        User user = new User();
        user.setAge(Integer.parseInt(userAge));
        user.setCompanyCode(comCode);
        user.setIdCode(userId);
        user.setSex(userSex);
        user.setCompanyName(comName);
        user.setName(userName);
        setStringInfo("userInfo", JSON.toJSONString(user));
        showToast("信息已保存！");
    }
}