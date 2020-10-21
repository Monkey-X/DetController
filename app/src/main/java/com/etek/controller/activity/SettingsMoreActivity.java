package com.etek.controller.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import android.widget.EditText;
import android.widget.RelativeLayout;

import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.coreprogress.ProgressUIListener;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.UploadHelper;
import com.etek.sommerlibrary.activity.BaseActivity;


import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SettingsMoreActivity extends BaseActivity implements OnToggledListener {



    @BindView(R.id.rl_back_server)
    RelativeLayout backService;

    @BindView(R.id.back_server_address)
    TextView tvBackServer;


//    @BindView(R.id.sim_switch)
//    LabeledSwitch simSwitch;
//
//    @BindView(R.id.settings_sim)
//    RelativeLayout settings_sim;



    @BindView(R.id.test_switch)
    LabeledSwitch testSwitch;

    @OnClick(R.id.settings_copy_db)
    public void settingsCopyDb() {

//        File file = mContext.getDatabasePath(DBManager.DB_NAME);

//                    String currentApkPath = mContext.getPackageResourcePath();
//                    File apkFile = new File(currentApkPath);
//        Map<String, String> appInfo = AppUtils.getAppInfo(mContext);
//        XLog.v(JSON.toJSONString(appInfo));
//        File file2 = new File(appInfo.get("immi")+"_"+ DateUtil.getDateDoc(new Date())+"_"+"det.db");
//        file.renameTo(file2);
        File dbTempFile = getDBTempFile();
        UploadHelper.upload(AppConstants.LONGMAO_UPLOAD, dbTempFile, new ProgressUIListener() {
            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                XLog.v("percent:"+percent);
            }
        }, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("call error:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                XLog.i("call :" + resp);
                showToast("上传服务器成功:" +resp);
            }
        });
    }


    @OnClick(R.id.settings_upload_log)
    public void settings_upload_log() {

        File tempLogFile = getTempLogFile();
        UploadHelper.upload(AppConstants.LONGMAO_UPLOAD, tempLogFile, new ProgressUIListener() {
            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                XLog.v("percent:"+percent);
            }
        }, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("call error:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                XLog.i("call :" + resp);
                showToast("上传服务器成功:" +resp);
            }
        });
    }

    File getTempLogFile(){

        File path =  new File(Environment.getExternalStorageDirectory(), "detonation");
        File newFile = new File(path,DateUtil.getDateLogStr(new Date()));
//                    String currentApkPath = mContext.getPackageResourcePath();
//                    File apkFile = new File(currentApkPath);
        Map<String, String> appInfo = AppUtils.getAppInfo(mContext);
        String name = Globals.user.getName();
        if(StringUtils.isEmpty(name)){
            name = "";
        }
//        XLog.v(JSON.toJSONString(appInfo));
        File file2 = new File(FileUtils.ExternalStorageDirectory,appInfo.get("immi")+"_"+name+"_"+ DateUtil.getDateLogStr(new Date())+"_"+"det.log");
//        XLog.v(file2);
        FileUtils.copyFile(newFile,file2,false);
//        XLog.v(file2);
        return file2;

    }
    File getDBTempFile(){

            File file = mContext.getDatabasePath(DBManager.DB_NAME);

//                    String currentApkPath = mContext.getPackageResourcePath();
//                    File apkFile = new File(currentApkPath);
            Map<String, String> appInfo = AppUtils.getAppInfo(mContext);
//            XLog.v(JSON.toJSONString(appInfo));
        String name = Globals.user.getName();
        if(StringUtils.isEmpty(name)){
            name = "";
        }
            File file2 = new File(FileUtils.ExternalStorageDirectory,appInfo.get("immi")+"_"+name+"_"+ DateUtil.getDateDoc(new Date())+"_"+"det.db");
//            XLog.v(file2);
            FileUtils.copyFile(file,file2,false);
//            XLog.v(file2);
     return file2;

    }


    @OnClick(R.id.settings_update_firmware)
    public void settings_update_firmware() {
    Intent i = new Intent(mContext,UpdateActivity.class);
    startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_more);
        ButterKnife.bind(this);
        initView();
        initToolBar(R.string.title_activity_settings_more);
    }


    private void initView() {

//        simSwitch.setOn(Globals.isSimUPload);
//        simSwitch.setOnToggledListener(this);
        testSwitch.setOn(Globals.isTest);
        testSwitch.setOnToggledListener(this);
    }

    @OnClick(R.id.rl_back_server)
    public void editBackServer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请输入");
        //设置对话框标题
        builder.setIcon(android.R.drawable.btn_radio);


        final EditText edit = new EditText(mContext);
        edit.setText(tvBackServer.getText().toString());

        //3.将输入框赋值给Dialog,并增加确定取消按键
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String fileSn = edit.getText().toString();
                tvBackServer.setText(fileSn);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtils.showCustom(mContext, "你点了取消");
            }
        });
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();


    }


    @Override
    public void onSwitched(ToggleableView toggleableView, boolean isOn) {
        switch (toggleableView.getId()) {

            case R.id.sim_switch:

//                Globals.isSimUPload = isOn;
//                setBooleanInfo("isSimUPload", isOn);
                break;

            case R.id.test_switch:

                Globals.isTest = isOn;
                setBooleanInfo("isTest", isOn);
                break;
            default:
                break;
        }
    }
}
