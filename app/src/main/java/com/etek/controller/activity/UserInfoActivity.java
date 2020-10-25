package com.etek.controller.activity;


import android.os.Bundle;


import butterknife.BindView;
import butterknife.ButterKnife;

import com.elvishew.xlog.XLog;


import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;


import com.alibaba.fastjson.JSON;
import com.etek.controller.R;
import com.etek.controller.common.Globals;
import com.etek.controller.model.User;

import com.etek.controller.utils.IdCardUtil;
import com.etek.controller.widget.ClearableAutoCompleteTextview;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.etek.sommerlibrary.activity.BaseActivity;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserInfoActivity extends BaseActivity {


    @BindView(R.id.personal_info_name)
    EditText userName;
    @BindView(R.id.personal_info_sex)
    EditText sex;
    @BindView(R.id.personal_info_age)
    EditText age;
    @BindView(R.id.personal_id_code)
    EditText idCode;
    @BindView(R.id.company_name)
    EditText companyName;
    @BindView(R.id.company_code)
    ClearableAutoCompleteTextview companyCode;

    @BindView(R.id.personal_info_modify)
    Button modify;


    List<String> result_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_personal_info);

        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        saveUserInfo();
    }

    void saveUserInfo() {
        User user = new User();
        try {
            int ageStr = Integer.parseInt(age.getText().toString());
            user.setAge(ageStr);
        } catch (Exception e) {
           showToast( "年龄不能为空，必须为数字！");
            return;
        }

        if (StringUtils.isEmpty(userName.getText().toString())) {
           showToast( "用户名不能为空");
            return;
        }
        user.setName(userName.getText().toString());

        if (StringUtils.isEmpty(sex.getText().toString())) {
           showToast( "性别不能为空，必须为男女");
            return;
        }
        user.setSex(sex.getText().toString());

        if (StringUtils.isEmpty(idCode.getText().toString())) {
           showToast( "身份证号码必须不为空");
            return;
        }
//        var cardId=/^[1-9][0-9]{5}(19|20)[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|31)|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}([0-9]|x|X)$/;
        if(!IdCardUtil.isValidatedAllIdcard(idCode.getText().toString())){
           showToast( "请输入有效的身份证号码");
            return;
        }


        user.setIdCode(idCode.getText().toString());

        if (StringUtils.isEmpty(companyCode.getText().toString())) {
           showToast( "单位代码不能为空");
            return;
        }
        user.setCompanyCode(companyCode.getText().toString());
        if (StringUtils.isEmpty(companyName.getText().toString())) {
            showToast( "单位名称不能为空");
            return;
        }
        user.setCompanyName(companyName.getText().toString());

        Globals.user = user;
        String cCode = companyCode.getText().toString();
        setStringInfo("userInfo", JSON.toJSONString(user));
        boolean isExist = false;
        for (String s : result_code) {
            if (cCode.equalsIgnoreCase(s)) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            List arrList = new ArrayList(result_code);
            arrList.add(cCode);
            setStringInfo("company_codes", StringUtils.join(arrList, ","));

        }
        showLongToast("信息更新完成" + user.getCompanyCode());
//       showToast( "用户信息更新完成:" + user.getCompanyCode());
    }
    private InputMethodManager m;
    private void initView() {

        modify.setOnClickListener(v -> saveUserInfo());

        String codes = getStringInfo("company_codes");
        if (!StringUtils.isEmpty(codes)) {
            result_code = Arrays.asList(StringUtils.split(codes, ","));
        } else {
            result_code = new ArrayList<>();
        }
        String userinfo = getStringInfo("userInfo");
        XLog.d(LOG_TAG, userinfo);
        if (userinfo != null && userinfo.length() > 0) {
            User user = JSON.parseObject(userinfo, User.class);

            if (user != null && !StringUtil.isBlank(user.getName())) {
                XLog.d(LOG_TAG, user.toString());
                userName.setText(user.getName());
                sex.setText(user.getSex());
                age.setText("" + user.getAge());
                idCode.setText(user.getIdCode());
                companyName.setText(user.getCompanyName());
                companyCode.setText(user.getCompanyCode());
            }
        }
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,
                result_code);
        companyCode.setAdapter(adapt);

//        Arrays.asList(StringUtils.split(str, ",");
    }


//    @OnClick(R.id.clear_company_code)
//    public void clearCompanyCode(){
//        companyCode.setText("");
//    }


}
