package com.etek.controller.activity;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import android.os.Bundle;
import com.elvishew.xlog.XLog;

import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.etek.controller.BuildConfig;
import com.etek.controller.R;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.AppResp;
import com.etek.controller.dto.Cwxx;
import com.etek.controller.model.User;

import com.etek.controller.service.DownLoadAppService;
import com.etek.controller.utils.SommerUtils;
import com.etek.controller.utils.UpdateAppUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.NetUtil;
import com.etek.sommerlibrary.utils.ToastUtils;


import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.Arrays;


public class SplashActivity extends BaseActivity {


    private final long SPLASH_LENGTH = 1000;
    boolean isPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView version = (TextView) findViewById(R.id.splash_version);
        version.setText(getString( R.string.show_version, SommerUtils.getVersionName(this)));
        globalInit();
        requestPermission();
    }

    private void globalInit() {
        String userStr = getPreInfo("userInfo");
        if (!StringUtil.isBlank(userStr)) {
            try {
                Globals.user = JSON.parseObject(userStr, User.class);
            } catch (JSONException e) {
                e.printStackTrace();
                XLog.e(e.getMessage());
            }
        }else{
            Globals.user = new User();
        }
        Globals.isServerDanningOn = getBooleanInfo("isServerDanningOn");
        Globals.isServerZhongbaoOn = getBooleanInfo("isServerZhongbaoOn");
        Globals.isBuild = BuildConfig.DEBUG;

        Globals.isLogDocument = getBooleanInfoDefaultTrue("isLogDocument");

        Globals.isLogDatabase = getBooleanInfoDefaultTrue("isLogDatabase");
        Globals.isTest = getBooleanInfo("isTest");
//        Globals.isSimUPload = getBooleanInfoDefaultTrue("isSimUPload");

        Globals.zhongbaoAddress = getStringInfo("zhongbaoAddress");
        Globals.contractId = getStringInfo("contractId");
        Globals.proId = getStringInfo("proId");
        if(StringUtils.isBlank(Globals.zhongbaoAddress)){
            Globals.zhongbaoAddress = "中爆黔南";
        }
//        if(!Globals.isTest){
//            Globals.isSimUPload = false;
//        }

    }

    private int checkNet() {
        int net = NetUtil.getNetType(mContext);
        if (net < 0) {
            showToast("没有网络功能，请去设置！");
//            delayAction(new Intent(Settings.ACTION_SETTINGS), 1000);
        }
        return net;
    }

    //请求权限
    private void requestPermission() {
        requestPermission(new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.CHANGE_NETWORK_STATE

                },
                "请求雷管控制器相关权限", new GrantedResult() {
                    @Override
                    public void onResult(boolean granted, String[] granteds) {
                        if (granted) {
//                            startMain();
                           if(checkNet()>=0){
                               AppUpdate(mContext);
                           }else {
                               startMain();
                           }

//                            XLog.d( "granted good");
//                            FileUtils.saveFileToSDcard("detonator", "class.json", JSON.toJSONString(mDataList));

                            isPermission = true;
//                ToastUtils.showCustom(this,"Opened log at " + mLog.getPath());
//                XLog.i(LOG_TAG, "Opened log at " + mLog.getPath());

                        } else {
                            XLog.e( "granted error ", Arrays.toString(granteds));
                            finish();
                        }
                    }
                });
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
//                                Toast.makeText(mConxtext, "正在下载中", Toast.LENGTH_SHORT).show();
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
                                startMain();
                            }
                        });
                        Looper.prepare();
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        Looper.loop();




                    } else {
//                        showToast(getString(R.string.without_update));
                        Looper.prepare();
                        startMain();
                        Looper.loop();


                    }
                } catch (PackageManager.NameNotFoundException e) {
                    XLog.e(e);
                }

            }

            @Override
            public void onError() {
                showToast("版本更新出错！");
                Looper.prepare();
                startMain();
                Looper.loop();
            }
        });

    }

    void startMain(){

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_LENGTH);

    }

}
