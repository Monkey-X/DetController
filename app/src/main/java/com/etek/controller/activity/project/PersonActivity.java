package com.etek.controller.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etek.controller.R;
import com.etek.controller.activity.project.AboutActivity;
import com.etek.controller.activity.project.MainBoardUpdateActivity;
import com.etek.controller.activity.project.SettingsActivity2;
import com.etek.controller.activity.project.UserInfoActivity2;

import com.etek.controller.activity.project.eventbus.MessageEvent;
import com.etek.controller.common.AppConstants;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 本机设置界面
 */
public class PersonActivity extends BaseActivity implements View.OnClickListener {

    private TextView userName;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        EventBus.getDefault().register(this);

        initSupportActionBar(R.string.home_local_setting);
        initView();
    }

    private void initView() {
        userName = findViewById(R.id.user_name);
        TextView userManager = findViewById(R.id.user_manager);
        TextView uploadLog = findViewById(R.id.upload_log);
        TextView mainBoardUpdate = findViewById(R.id.mainboard_update);
        TextView dataSetting = findViewById(R.id.data_setting);
        TextView about = findViewById(R.id.about);
        userManager.setOnClickListener(this);
        mainBoardUpdate.setOnClickListener(this);
        uploadLog.setOnClickListener(this);
        dataSetting.setOnClickListener(this);
        about.setOnClickListener(this);
        getUserName();

        TextView locate_position = findViewById(R.id.locate_position);
        locate_position.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        finish();
    }

    private void getUserName() {
        String user_name = getStringInfo("User_Name");
        userName.setText(user_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_manager:
                Intent intent = new Intent(this, UserInfoActivity2.class);
                startActivity(intent);
                break;
            case R.id.mainboard_update:
                Intent mainIntent = new Intent(this, MainBoardUpdateActivity.class);
                startActivity(mainIntent);
                break;
            case R.id.data_setting:
                startActivity(SettingsActivity2.class);
                break;
            case R.id.about:
                startActivity(AboutActivity.class);
                break;
            case R.id.upload_log:
                uploadLog();
                break;
            case R.id.locate_position:
                Intent intentpos = new Intent(this, MapActivity.class);
                intentpos.putExtra("cachePositon","CACHE");
                startActivity(intentpos);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void startActivity(Class clz) {
        startActivity(new Intent(this, clz));
    }

    private void uploadLog(){
        //  日志文件路径
        String path = Environment.getExternalStorageDirectory() + "/Log/"; //文件路径
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String fileName =path + String.format("ETEK%s.txt",simpleDateFormat.format(date));

        File logfile = new File(fileName);
        if(!logfile.exists()){
            Toast.makeText(this, "无 日志 需要上传！", Toast.LENGTH_SHORT).show();
            return;
        }

        //  起爆器编号
        String strsno = getPreInfo(getString(R.string.controller_sno));
        if(TextUtils.isEmpty(strsno))
            strsno = "F00A8000000";
        Log.d("LOG",String.format("起爆器编号：%s",strsno));

        //  上传地址
        String url =String.format(AppConstants.UPLOAD_LOG + "/%s",strsno);

        //  HTTP请求
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try{
            params.put("file",logfile);
        }catch (Exception e){
            Log.d("LOG",e.getMessage());
            return;
        }

        progressDialog = ProgressDialog.show(mContext,"提示","请稍等...",true,false);
        progressDialog.show();

        // 上传文件
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                // 上传成功后要做的工作
                Toast.makeText(mContext, "日志 上传成功", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                // 上传失败后要做到工作
                Toast.makeText(mContext, "日志 上传失败", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        return;
    }
}