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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.common.Globals;
import com.etek.controller.entity.HomeItem;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.DetCallback;
import com.etek.controller.widget.ClearableEditText;
import com.etek.controller.widget.HeaderView;
import com.etek.controller.adapter.HomeAdapter;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {


    private ArrayList<HomeItem> mDataList;
    private RecyclerView mRecyclerView;


    private HeaderView mHeaderView;

    private String TAG = "HomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeToolbar();
        Log.d(TAG, "onCreate: 1");
        DetApp.getInstance().PowerOnSelfCheck(new DetCallback() {
            @Override
            public void DisplayText(String strText) {
                ToastUtils.show(HomeActivity.this,strText);
            }

            @Override
            public void StartProgressbar() {

            }

            @Override
            public void SetProgressbarValue(int nVal) {

            }

            @Override
            public void SetSingleModuleCheckData(int nID, byte[] szDC, int nDT, byte bCheckResult) {

            }
        });
        Log.d(TAG, "onCreate: 2");

        initView();
        initData();
        initAdapter();

    }

    @Override
    protected void onStart() {
        super.onStart();

        int initialize = DetApp.getInstance().Initialize();
        Log.d(TAG, "onStart: initialize= "+ initialize);
    }

    private void initializeToolbar() {
//        XLog.d(LOG_TAG, "initializeToolbar as actionBar");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mHeaderView = (HeaderView) findViewById(R.id.toolbar_header_view);
        mHeaderView.bindTo(getString(R.string.app_name), "");
        //mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        // Toolbar will now take on default Action Bar characteristics
        setSupportActionBar(mToolbar);
    }


    private void initView() {
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @SuppressWarnings("unchecked")
    private void initAdapter() {
        BaseQuickAdapter homeAdapter = new HomeAdapter(R.layout.home_item_view, mDataList);
        homeAdapter.openLoadAnimation();
//        View top = getLayoutInflater().inflate(R.layout.top_view, (ViewGroup) mRecyclerView.getParent(), false);
//        homeAdapter.addHeaderView(top);
        homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent(HomeActivity.this, Globals.mainFuncation[position].getFuncation());
                startActivity(intent);


            }
        });

        mRecyclerView.setAdapter(homeAdapter);
    }


    private void initData() {
        mDataList = new ArrayList<>();
        for (int i = 0; i < Globals.mainFuncation.length; i++) {
            HomeItem item = new HomeItem();
            item.setTitle(getString(Globals.mainFuncation[i].getTitle()));
            item.setActivity(Globals.mainFuncation[i].getFuncation());
            item.setImageResource(Globals.mainFuncation[i].getImage());
            mDataList.add(item);
        }

    }

    void showDialog(){
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//        if (!isPermission) {
//            ToastUtils.showCustom(mContext, getString(R.string.permissions_not_granted));
//            return false;
//        }
        int id = item.getItemId();
        Intent intent;
        switch (id) {


            case R.id.nav_checkout:
                intent = new Intent(HomeActivity.this, CheckoutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_authority:
                intent = new Intent(HomeActivity.this, AuthorizeActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_user:
                //删除app
//                Uri uri = Uri.fromParts("package", "com.etek.controller", null);
//                 intent = new Intent(Intent.ACTION_DELETE, uri);
//                startActivity(intent);
                intent = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        DetApp.getInstance().ShutdownProc();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DetApp.getInstance().Finalize();
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
            System.exit(0);
        }
    }


}
