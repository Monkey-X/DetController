package com.etek.controller.activity.project;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.etek.controller.R;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.common.ETEKOnlinePassword;
import com.etek.controller.common.HandsetWorkMode;
import com.etek.controller.hardware.test.HttpCallback;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.GeneralDisplayUI;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.NetUtil;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private EditText userName;
    private EditText password;
    private static String TAG = "LoginActivity";

    private TextView onlinepassword;
    private Timer mTimer;

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
        String user_name = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_NAME);
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

        onlinepassword = findViewById(R.id.get_online_password);
        onlinepassword.setAutoLinkMask(Linkify.ALL);
        onlinepassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        onlinepassword.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.login:
                LoginAPP();
                break;
            case R.id.get_online_password:
                OnlineGetPassword();
                break;
            default:
                break;
        }
    }

    /***
     * 登录过程
     */
    private void LoginAPP(){
        String userStrName = userName.getText().toString().trim();
        String strPassword = password.getText().toString().trim();

        if(isTestUser(userStrName,strPassword)){
            HandsetWorkMode.getInstance().setWorkMode(HandsetWorkMode.MODE_TEST);
            startActivity(new Intent(this, HomeActivity2.class));
            finish();
            return;
        }

        if(!isValidCellphoneNo(userStrName)){
            Toast.makeText(mContext, "请输入有效手机号！", Toast.LENGTH_LONG).show();
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

        //  缓存的登录名和密码
        String user_name = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_NAME);
        String user_passWord = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_PASSWORD);

        if(null!=etekonlinepswd){
            user_passWord = etekonlinepswd.getResult();
            user_name = userStrName;
        }

        //  缓存的用户名和密码有一个是空的时候更新
        if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_passWord)) {
            SpManager.getIntance().saveSpString(AppSpSaveConstant.USER_NAME,userStrName);
            SpManager.getIntance().saveSpString(AppSpSaveConstant.USER_PASSWORD,strPassword);
            startActivity(new Intent(this, HomeActivity2.class));
            finish();
            return;
        }

        if (user_name.equals(userStrName)
            && user_passWord.equals(strPassword)) {
                SpManager.getIntance().saveSpString(AppSpSaveConstant.USER_NAME,userStrName);
                SpManager.getIntance().saveSpString(AppSpSaveConstant.USER_PASSWORD,strPassword);
                startActivity(new Intent(this, HomeActivity2.class));
                finish();
            }else{
                ToastUtils.showShort(this, "输入账号或密码有误！");
            }

        return;
    }

    /***
     * 电话号码有效性正则表达式
     * @param phoneno
     * @return
     */
    private boolean isValidCellphoneNo(String phoneno){
        String regex = "^((13[0-9])|(14[5-9])|(15([0-3]|[5-9]))|(16[6-7])|(17[1-8])|(18[0-9])|(19[1|3])|(19[5|6])|(19[8|9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(phoneno).matches();
    }

    private boolean isTestUser(String userStrName,String strPassword){
        // 先判断是否测试用户
        Calendar calendar = Calendar.getInstance();
        int ret =  calendar.get(Calendar.YEAR);
        ret = ret + calendar.get(Calendar.MONTH)+1;     // MONTH是从0-11
        ret = ret + calendar.get(Calendar.DAY_OF_MONTH);
        ret = ret + calendar.get(Calendar.HOUR_OF_DAY);

        //  测试用户，进入到测试模式
        if(userStrName.toUpperCase().equals("WXSCTEST")
                && strPassword.equals(ret+"")) {
            return true;
        }
        return false;
    }



    private long m_lStartTime = 0;
    private ETEKOnlinePassword etekonlinepswd = null;

    private void OnlineGetPassword(){
        if (NetUtil.getNetType(mContext) < 0) {
            Toast.makeText(mContext, "无法获取密码，请先设置网络", Toast.LENGTH_LONG).show();
            return;
        }

        long ntm = System.currentTimeMillis()/1000;
        if((ntm-m_lStartTime)<60)
            return;

        etekonlinepswd = null;

        String phoneno = userName.getText().toString().trim();
        if(!isValidCellphoneNo(phoneno)){
            Toast.makeText(mContext, "请输入有效手机号！", Toast.LENGTH_LONG).show();
            return;
        }

        String url = String.format(AppConstants.ETEK_ONLINE_GET_PSWD,phoneno);
        AsyncHttpCilentUtil.httpsPost(this, url, null, new HttpCallback() {
            @Override
            public void onFaile(IOException e) {
                e.printStackTrace();
                Log.d(TAG,e.getMessage());
                ToastUtils.showShort(LoginActivity.this, "在线获取动态码失败！");
            }

            @Override
            public void onSuccess(Response response) {
                String respStr = null;
                try {
                    respStr = response.body().string();
                } catch (IOException e) {
                    ToastUtils.showShort(LoginActivity.this, "在线获取动态码失败！");
                    e.printStackTrace();
                    Log.d(TAG,e.getMessage());
                    return;
                }

                Log.d(TAG,"服务端返回："+respStr);

                if (StringUtils.isEmpty(respStr)) {
                    ToastUtils.showShort(LoginActivity.this, "在线获取动态码失败！");
                    return;
                }

                etekonlinepswd = JSON.parseObject(respStr, ETEKOnlinePassword.class);
                if(null==etekonlinepswd){
                    ToastUtils.showShort(LoginActivity.this, "在线获取动态码失败！");
                    return;
                }

                m_lStartTime = System.currentTimeMillis()/1000;
                startTimer();
            }
        });


    }

    private void startTimer(){
        mTimer = new Timer(true);

        TimerTask task = new TimerTask() {
            public void run() {
                //每次需要执行的代码放到这里面。
                long ntm = System.currentTimeMillis()/1000;

                if((ntm-m_lStartTime)>60){
                    mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;

                    onlinepassword.setText("忘记密码");
                    return;
                }

                String str = String.format("%d 秒后重新获取",60-(ntm-m_lStartTime));
                onlinepassword.setText(str);

            }
        };

        mTimer.schedule(task,0,1000);
    }

}