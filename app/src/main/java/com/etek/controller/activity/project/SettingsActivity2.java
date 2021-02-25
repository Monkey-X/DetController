package com.etek.controller.activity.project;

import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.etek.controller.R;
import com.etek.controller.common.Globals;
import com.etek.controller.utils.GeneralDisplayUI;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import android.widget.PopupWindow;
import android.view.Gravity;
import android.widget.TextView;

public class SettingsActivity2 extends BaseActivity implements OnToggledListener, View.OnClickListener {

    private LabeledSwitch danningSwitch;
    private LabeledSwitch zhongbaoSwitch;
    private LabeledSwitch etekSwitch;

    private final String TAG="SettingsActivity2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        initSupportActionBar(R.string.title_activity_settings);
        initView();
        initData();
    }

    private void initData() {
        Boolean isServerDanningOn = getBooleanInfo("isServerDanningOn");
        danningSwitch.setOn(isServerDanningOn);

        Boolean isServerZhongbaoOn = getBooleanInfo("isServerZhongbaoOn");
        zhongbaoSwitch.setOn(isServerZhongbaoOn);

        Boolean isServerEtekOn = getBooleanInfo("isServerEtekOn");
        etekSwitch.setOn(isServerEtekOn);
    }

    private void initView() {
        danningSwitch = findViewById(R.id.danling_switch);
        zhongbaoSwitch = findViewById(R.id.zhongbao_switch);
        etekSwitch = findViewById(R.id.etek_switch);
        danningSwitch.setOnToggledListener(this);
        zhongbaoSwitch.setOnToggledListener(this);
        etekSwitch.setOnToggledListener(this);

        View wifiSetting = findViewById(R.id.wifi_setting);
        View modleNet = findViewById(R.id.modle_net);
        View setPassWord = findViewById(R.id.set_bomb_password);
        wifiSetting.setOnClickListener(this);
        modleNet.setOnClickListener(this);
        setPassWord.setOnClickListener(this);
    }

    @Override
    public void onSwitched(ToggleableView toggleableView, boolean isOn) {
        switch (toggleableView.getId()) {
            case R.id.danling_switch:
                danningSwitch.setOn(isOn);
                setBooleanInfo("isServerDanningOn", isOn);
                break;
            case R.id.zhongbao_switch:
                zhongbaoSwitch.setOn(isOn);
                setBooleanInfo("isServerZhongbaoOn", isOn);
                if(isOn){
                    shouPopuWindow(toggleableView);
                }
                break;
            case R.id.etek_switch:
                etekSwitch.setOn(isOn);
                setBooleanInfo("isServerEtekOn", isOn);
                break;
            default:
                break;
        }
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
                startActivity(new Intent(this,BombPassWordSettingActivity.class));
                break;
        }
    }

    private void showNetSetting(int ntype) {
        GeneralDisplayUI.showSettingNetworkSelect(this,ntype);
    }


    private void shouPopuWindow(View view) {
        String straddr = getStringInfo("zhongbaoAddress");
        Log.d(TAG,"原设置为："+straddr);

        View popuView = getLayoutInflater().inflate(R.layout.popup_zhongbao_center, null, false);
        PopupWindow popupWindow = new PopupWindow(popuView, 200, 300);
        TextView tv = popuView.findViewById(R.id.zhongbao_qiannan);
        if(straddr.equals("中爆黔南"))
            tv.setTextColor(Color.BLACK);
        popuView.findViewById(R.id.zhongbao_qiannan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"中爆黔南");
                setStringInfo("zhongbaoAddress","中爆黔南");
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
                setStringInfo("zhongbaoAddress","中爆黔东南");
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
                setStringInfo("zhongbaoAddress","中爆广西");
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
                setStringInfo("zhongbaoAddress","中爆贵阳");
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(view, Gravity.RIGHT|Gravity.TOP, 100,300);
    }

}