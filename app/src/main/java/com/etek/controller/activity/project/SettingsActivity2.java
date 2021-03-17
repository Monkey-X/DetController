package com.etek.controller.activity.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.eventbus.MessageEvent;
import com.etek.controller.activity.project.manager.DataCleanManager;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.common.AppConstants;
import com.etek.controller.utils.GeneralDisplayUI;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class SettingsActivity2 extends BaseActivity implements OnToggledListener, View.OnClickListener {

    private LabeledSwitch danningSwitch;
    private LabeledSwitch zhongbaoSwitch;
    private LabeledSwitch etekSwitch;

    private final String TAG="SettingsActivity2";
    private TextView serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        initSupportActionBar(R.string.title_activity_settings);
        initView();
        initData();
    }

    private void initData() {
        Boolean isServerDanningOn = SpManager.getIntance().getSpBoolean(AppSpSaveConstant.SEVER_DANNING_ON);
        danningSwitch.setOn(isServerDanningOn);

        Boolean isServerZhongbaoOn = SpManager.getIntance().getSpBoolean(AppSpSaveConstant.SEVER_ZHONGBAO_ON);
        zhongbaoSwitch.setOn(isServerZhongbaoOn);

        Boolean isServerEtekOn = SpManager.getIntance().getSpBoolean(AppSpSaveConstant.SEVER_ETEK_ON);
        etekSwitch.setOn(isServerEtekOn);

        String straddr = SpManager.getIntance().getSpString(AppSpSaveConstant.ZHONGBAO_ADDRESS);
        if(TextUtils.isEmpty(straddr)){
            straddr = "中爆黔南";
        }
        serverAddress.setText(straddr);
    }

    private void initView() {
        danningSwitch = findViewById(R.id.danling_switch);
        zhongbaoSwitch = findViewById(R.id.zhongbao_switch);
        etekSwitch = findViewById(R.id.etek_switch);
        serverAddress = findViewById(R.id.server_address);
        danningSwitch.setOnToggledListener(this);
        zhongbaoSwitch.setOnToggledListener(this);
        etekSwitch.setOnToggledListener(this);

        View wifiSetting = findViewById(R.id.wifi_setting);
        View modleNet = findViewById(R.id.modle_net);
        View setPassWord = findViewById(R.id.set_bomb_password);
        View loginReset = findViewById(R.id.set_login_in_info);
        View recoverData = findViewById(R.id.recover_data);
        wifiSetting.setOnClickListener(this);
        modleNet.setOnClickListener(this);
        setPassWord.setOnClickListener(this);
        loginReset.setOnClickListener(this);
        recoverData.setOnClickListener(this);
    }

    @Override
    public void onSwitched(ToggleableView toggleableView, boolean isOn) {
        switch (toggleableView.getId()) {
            case R.id.danling_switch:
                danningSwitch.setOn(isOn);
                SpManager.getIntance().saveSpBoolean(AppSpSaveConstant.SEVER_DANNING_ON,isOn);
                break;
            case R.id.zhongbao_switch:
                zhongbaoSwitch.setOn(isOn);
                SpManager.getIntance().saveSpBoolean(AppSpSaveConstant.SEVER_ZHONGBAO_ON,isOn);
                if (isOn) {
                    shouPopuWindow(toggleableView);
                }
                break;
            case R.id.etek_switch:
                etekSwitch.setOn(isOn);
                SpManager.getIntance().saveSpBoolean(AppSpSaveConstant.SEVER_ETEK_ON,isOn);
                break;
            default:
                break;
        }
    }

    private void cleanAppData() {
        DataCleanManager.deleteFile(new File("data/data/" + getPackageName()));

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setMessage("已恢复出厂设置，需要重启应用");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventBus.getDefault().post(new MessageEvent(""));
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        int ntype = GeneralDisplayUI.NETWORK_WIFI;

        switch (v.getId()) {
            case R.id.wifi_setting:
                ntype = GeneralDisplayUI.NETWORK_WIFI;
                showNetSetting(ntype);
                break;
            case R.id.modle_net:
                ntype = GeneralDisplayUI.NETWORK_4G;
                showNetSetting(ntype);
                break;
            case R.id.set_bomb_password:
                startActivity(new Intent(this, BombPassWordSettingActivity.class));
                break;
            case R.id.set_login_in_info:
                startActivity(new Intent(this, LoginInInfoResetActivity.class));
                break;
            case R.id.recover_data:
                showCleanDialog();
                break;
        }
    }

    private void showCleanDialog() {
        // 展示提示框，进行数据清除
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_intput, null);
        EditText editPossword = view.findViewById(R.id.edit_msg);
        builder.setTitle("请输入恢复密码:");
        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String possword = editPossword.getText().toString().trim();
                if (TextUtils.isEmpty(possword)) {
                    ToastUtils.show(SettingsActivity2.this, "请输入密码！");
                } else {
                    if (possword.equals(AppConstants.CLEAN_DATA_PASSWORD)) {
                        cleanAppData();
                    } else {
                        ToastUtils.show(SettingsActivity2.this, "输入密码有误！");
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showNetSetting(int ntype) {
        GeneralDisplayUI.showSettingNetworkSelect(this, ntype);
    }

    private void shouPopuWindow(View view) {
        String straddr = SpManager.getIntance().getSpString(AppSpSaveConstant.ZHONGBAO_ADDRESS);
        Log.d(TAG, "原设置为：" + straddr);

        View popuView = getLayoutInflater().inflate(R.layout.popup_zhongbao_center, null, false);
        PopupWindow popupWindow = new PopupWindow(popuView, 200, 300);
        TextView tv = popuView.findViewById(R.id.zhongbao_qiannan);
        if(straddr.equals("中爆黔南"))
            tv.setTextColor(Color.BLACK);
        popuView.findViewById(R.id.zhongbao_qiannan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"中爆黔南");
                serverAddress.setText("中爆黔南");
                SpManager.getIntance().saveSpString(AppSpSaveConstant.ZHONGBAO_ADDRESS,"中爆黔南");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        tv = popuView.findViewById(R.id.zhongbao_qiandongnan);
        if(straddr.equals("中爆黔东南"))
            tv.setTextColor(Color.BLACK);
        popuView.findViewById(R.id.zhongbao_qiandongnan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"中爆黔东南");
                serverAddress.setText("中爆黔东南");
                SpManager.getIntance().saveSpString(AppSpSaveConstant.ZHONGBAO_ADDRESS,"中爆黔东南");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        tv = popuView.findViewById(R.id.zhongbao_guangxi);
        if(straddr.equals("中爆广西"))
            tv.setTextColor(Color.BLACK);
        popuView.findViewById(R.id.zhongbao_guangxi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"中爆广西");
                serverAddress.setText("中爆广西");
                SpManager.getIntance().saveSpString(AppSpSaveConstant.ZHONGBAO_ADDRESS,"中爆广西");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        tv = popuView.findViewById(R.id.zhongbao_guiyang);
        if(straddr.equals("中爆贵阳"))
            tv.setTextColor(Color.BLACK);
        popuView.findViewById(R.id.zhongbao_guiyang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"中爆贵阳");
                serverAddress.setText("中爆贵阳");
                SpManager.getIntance().saveSpString(AppSpSaveConstant.ZHONGBAO_ADDRESS,"中爆贵阳");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.RIGHT|Gravity.TOP, 100,300);
    }

}