package com.etek.controller.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.etek.controller.R;

import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;

import com.etek.controller.dto.AppResp;
import com.etek.controller.dto.Cwxx;
import com.etek.controller.enums.ReportServerEnum;
import com.etek.controller.model.UpdateAppResp;
import com.etek.controller.service.DownLoadAppService;
import com.etek.controller.utils.UpdateAppUtils;
import com.etek.sommerlibrary.common.ActivityCollector;

import com.etek.controller.utils.SommerUtils;

import com.etek.sommerlibrary.activity.BaseActivity;

import com.etek.sommerlibrary.utils.ToastUtils;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.elvishew.xlog.XLog;


public class SettingsActivity extends BaseActivity implements OnToggledListener {


    @BindView(R.id.settings_exitLayout)
    RelativeLayout exitLayout;

    @BindView(R.id.danling_switch)
    LabeledSwitch danningSwitch;

    @BindView(R.id.sim_switch)
    LabeledSwitch simSwitch;

    @BindView(R.id.zhongbao_switch)
    LabeledSwitch zhongbaoSwitch;


    @BindView(R.id.settings_sim)
    RelativeLayout settings_sim;
    //    private RelativeLayout settingsMore;
//    private RelativeLayout logLayout;
//

    @BindView(R.id.update_app)
     RelativeLayout rlUpdateApp;
    @BindView(R.id.zhongbao_select)
    RelativeLayout zbAddressSel;
    @BindView(R.id.zhongbao_spinner)
    Spinner mySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initView();
        initSupportActionBar(R.string.title_activity_settings);
    }

    @OnClick(R.id.update_app)
    public void updateApp(){
        AppUpdate(mContext);
    }

    int count = 0;

    @OnClick(R.id.rl_site)
    public void rl_site(){
        count++;
        if(count==2){
            ToastUtils.showCustom(mContext,"连续点击六次进入更多的设定");
        }

        if(count>3){
            count = 0;
            Intent i = new Intent(mContext,SettingsMoreActivity.class);
            startActivity(i);
        }
    }





    private void initView() {


        danningSwitch.setOn(Globals.isServerDanningOn);
        danningSwitch.setOnToggledListener(this);
//        LabeledSwitch zhongbaoSwitch = findViewById(R.id.zhongbao_switch);
//        zhongbaoSwitch.setOn(Globals.isServerZhongbaoOn);
//        zhongbaoSwitch.setOnToggledListener(this);
//

//        simSwitch.setOn(Globals.isSimUPload);
//        simSwitch.setOnToggledListener(this);

        zhongbaoSwitch.setOn(Globals.isServerZhongbaoOn);
        zhongbaoSwitch.setOnToggledListener(this);

        TextView appVersion = findViewById(R.id.set_app_version);
        appVersion.setText(getString(R.string.about_version, SommerUtils.getVersionName(this),
                "" + SommerUtils.getVersionCode(this)));


        exitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.finishAll();
            }
        });


//        rlUpdateApp =  findViewById(R.id.update_app);


//        zbAddressSel = findViewById(R.id.zhongbao_select);;

        List<String> list = new ArrayList<String>();

        ArrayAdapter<String> adapter;
        list.add("中爆黔南");
        list.add("中爆黔东南");
        list.add("中爆广西");
        list.add("中爆贵阳");


//         第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        // 第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(adapter);
        ReportServerEnum uploadServerEnum = ReportServerEnum.getByName(Globals.zhongbaoAddress);
        if (uploadServerEnum != null) {
            mySpinner.setSelection(uploadServerEnum.getCode());
        }

        // 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                /* 将所选mySpinner 的值带入myTextView 中 */
//                showToast("您选择的是：" + adapter.getItem(arg2));
                Globals.zhongbaoAddress = adapter.getItem(arg2);
                setStringInfo("zhongbaoAddress", Globals.zhongbaoAddress);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
//                showToast("NONE");
            }
        });

        if (Globals.isServerZhongbaoOn) {
            zbAddressSel.setVisibility(View.VISIBLE);
        } else {
            zbAddressSel.setVisibility(View.GONE);
        }

        if(!Globals.isTest){
            settings_sim.setVisibility(View.GONE);
        }else {
            settings_sim.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onSwitched(ToggleableView toggleableView, boolean isOn) {
        switch (toggleableView.getId()) {
            case R.id.danling_switch:
//                ToastUtils.showCustom(this,"toggleis:"+isOn);
                Globals.isServerDanningOn = isOn;
                setBooleanInfo("isServerDanningOn", isOn);
                break;
//
            case R.id.zhongbao_switch:
//                ToastUtils.showCustom(this,"toggleis:"+isOn);
                Globals.isServerZhongbaoOn = isOn;
                setBooleanInfo("isServerZhongbaoOn", isOn);
                if(Globals.isServerZhongbaoOn){
                    zbAddressSel.setVisibility(View.VISIBLE);
                }else {
                    zbAddressSel.setVisibility(View.GONE);
                }
                break;
            case R.id.sim_switch:
//                ToastUtils.showCustom(this,"toggleis:"+isOn);
//                Globals.isSimUPload = isOn;
//                setBooleanInfo("isSimUPload", isOn);
                break;
            default:
                break;
        }
    }
    private   void AppUpdate(Context mConxtext) {
        String url = AppConstants.ETEKTestServer+AppConstants.CheckoutReport;
        UpdateAppUtils.checkUpdate(url, mConxtext, new UpdateAppUtils.UpdateCallback() {
            @Override
            public void onSuccess(AppResp updateInfo) {
                PackageManager packageManager = mConxtext.getPackageManager();
                PackageInfo packageInfo;


                try {
                    packageInfo = packageManager.getPackageInfo(mConxtext.getPackageName(), 0);
                    int versionCode = packageInfo.versionCode;
                    if( updateInfo==null||updateInfo.getCwxx().isEmpty()){
                        return;
                    }
                    Cwxx cwxx =updateInfo.getCwxx().get(0);
                    int upVersionCode = Integer.parseInt(cwxx.getVersion());

                    if (upVersionCode> versionCode) {
//                         ToastUtils.show(mConxtext,getString(R.string.has_update));
                        AlertDialog.Builder builder = new AlertDialog.Builder(mConxtext);
                        builder.setTitle(R.string.has_update);
                        builder.setMessage("服务器版本:"+cwxx.getVersion()+"版本号："+cwxx.getFileName());
                        //设置正面按钮
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent downService = new Intent(mConxtext, DownLoadAppService.class);
                                downService.putExtra("downloadurl", AppConstants.DET_APP + cwxx.getId());
                                Toast.makeText(mConxtext, "正在下载中", Toast.LENGTH_SHORT).show();
                                //兼容8.0
                                boolean installAllowed;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    installAllowed = mConxtext.getPackageManager().canRequestPackageInstalls();
                                    if (installAllowed) {
                                        mConxtext.startService(downService);
                                    } else {
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + mConxtext.getPackageName()));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        mConxtext.startActivity(intent);
                                        mConxtext.startService(downService);

                                    }
                                } else {
                                    mConxtext.startService(downService);
                                }




                            }
                        });
                        //设置反面按钮
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        Looper.prepare();
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        Looper.loop();




                    } else {

                        Looper.prepare();
                        ToastUtils.showCustom(mConxtext, mConxtext.getString(R.string.without_update));
                        Looper.loop();


                    }
                } catch (PackageManager.NameNotFoundException e) {
                    XLog.e(e.getMessage());
                }

            }

            @Override
            public void onError() {

                Looper.prepare();
                ToastUtils.showCustom(mConxtext, "版本更新出错！");
                Looper.loop();

            }
        });

    }

}
