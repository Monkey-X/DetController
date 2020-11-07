package com.etek.controller.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.common.Globals;
import com.etek.controller.entity.HomeItem;
import com.etek.controller.entity.MainBoardInfoBean;
import com.etek.controller.fragment.MainBoardDialog;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.DetCallback;
import com.etek.controller.hardware.test.InitialCheckCallBack;
import com.etek.controller.widget.ClearableEditText;
import com.etek.controller.widget.HeaderView;
import com.etek.controller.adapter.HomeAdapter;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {



    private HeaderView mHeaderView;

    private String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        int initialize = DetApp.getInstance().Initialize();
        Log.d(TAG, "onCreate: initialize= " + initialize);

        DetApp.getInstance().MainBoardPowerOn();

        initView();

        mainBoardInit();
    }

    private void mainBoardInit() {
        DetApp.getInstance().MainBoardInitialize(new InitialCheckCallBack() {
            @Override
            public void SetInitialCheckData(String strHardwareVer, String strUpdateHardwareVer, String strSoftwareVer, String strSNO, String strConfig, byte bCheckResult) {
                Log.d(TAG, "SetInitialCheckData: strHardwareVer = " + strHardwareVer);
                Log.d(TAG, "SetInitialCheckData: strUpdateHardwareVer = " + strUpdateHardwareVer);
                Log.d(TAG, "SetInitialCheckData: strSoftwareVer = " + strSoftwareVer);
                Log.d(TAG, "SetInitialCheckData: strSNO = " + strSNO);
                Log.d(TAG, "SetInitialCheckData: strConfig = " + strConfig);

                MainBoardInfoBean mainBoardInfoBean = new MainBoardInfoBean();
                mainBoardInfoBean.setStrHardwareVer(strHardwareVer);
                mainBoardInfoBean.setStrUpdateHardwareVer(strUpdateHardwareVer);
                mainBoardInfoBean.setStrSoftwareVer(strSoftwareVer);
                mainBoardInfoBean.setStrSNO(strSNO);
                mainBoardInfoBean.setStrConfig(strConfig);
                setStringInfo(getString(R.string.mainBoardInfo_sp), JSON.toJSONString(mainBoardInfoBean));
                showMainBoardDialog(mainBoardInfoBean);
            }
        });
    }

    private void showMainBoardDialog(MainBoardInfoBean mainBoardInfoBean) {
        MainBoardDialog mainBoardDialog = new MainBoardDialog();
        mainBoardDialog.setMainBoardInfo(mainBoardInfoBean);
        mainBoardDialog.show(getSupportFragmentManager(), "mainBoardDialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onSart: ");
    }

    private void initView() {

        View networking = findViewById(R.id.networking);
        View connet_test = findViewById(R.id.connet_test);
        View delay = findViewById(R.id.delay);
        View auth = findViewById(R.id.auth);
        View report = findViewById(R.id.report);
        View assist = findViewById(R.id.assist);
        View user = findViewById(R.id.user);

        networking.setOnClickListener(this);
        connet_test.setOnClickListener(this);
        delay.setOnClickListener(this);
        auth.setOnClickListener(this);
        report.setOnClickListener(this);
        assist.setOnClickListener(this);
        user.setOnClickListener(this);
    }
     @Override
    public void onClick(View v) {
         switch (v.getId()) {
             case R.id.networking:
                 startActivity(NetWorkActivity.class);
                 break;
             case R.id.connet_test:
                 startActivity(ConnectTestActivity.class);
                 break;
             case R.id.delay:
                 startActivity(DelayDownloadActivity.class);
                 break;
             case R.id.auth:
                 startActivity(AuthBombActivity.class);
                 break;
             case R.id.report:
                 startActivity(ReportActivity2.class);
                 break;
             case R.id.assist:
                 startActivity(AssistActivity.class);
                 break;
             case R.id.user:
                 startActivity(UserInfoActivity.class);
                 break;
         }
     }

     private void startActivity(Class clz){
        startActivity(new Intent(this,clz));
     }


    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_loading_img, null, false);

        ImageView imgLoading = view.findViewById(R.id.img_loading);

        Animation operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        imgLoading.startAnimation(operatingAnim);
        //3.将输入框赋值给Dialog,并增加确定取消按键
        builder.setView(view);

        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();


        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        DetApp.getInstance().ShutdownProc();
//        DetApp.getInstance().Finalize();
        Log.d(TAG, "onDestroy: ");
    }

    //返回
    private long lastBackKeyDownTick = 0;
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;

    @Override
    public void onBackPressed() {

        long currentTick = System.currentTimeMillis();
        if (currentTick - lastBackKeyDownTick > MAX_DOUBLE_BACK_DURATION) {
            showToast("再按一次退出");
//            SnackBarUtils.makeShort(mDrawerLayout, "再按一次退出").success();
            lastBackKeyDownTick = currentTick;
        } else {
            finish();
//            System.exit(0);
        }
    }




}
