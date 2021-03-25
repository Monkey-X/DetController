package com.etek.controller.activity.project;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.etek.controller.R;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.activity.BaseActivity;


/**
 * 工程实施页
 */
public class ProjectImplementActivity extends BaseActivity implements View.OnClickListener {

    private PendingProject projectInfoEntity;
    private long proId = -1;
    private RelativeLayout connectTest;
    private RelativeLayout delayDownload;
    private RelativeLayout checkAuthorization;
    private RelativeLayout powerBomb;
    private String controllerSno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_implement);
        initSupportActionBar(R.string.home_project_implement);
        getProjectId();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.project_connect_test://连接检测
                startActivity(new Intent(this, ConnectTestActivity.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;

            case R.id.project_delay_download://延时下载
                startActivity(new Intent(this, DelayDownloadActivity.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;

            case R.id.project_check_authorization://检查授权
                startActivity(new Intent(this, AuthBombActivity2.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;

            case R.id.project_power_bomb://充电起爆
                startActivity(new Intent(this, PowerBombActivity.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取项目id
     */
    private void getProjectId() {
        Intent intent = getIntent();
        proId = intent.getLongExtra(AppIntentString.PROJECT_ID, -1);
    }



    /**
     * 初始化View
     */
    private void initView() {
        connectTest = findViewById(R.id.project_connect_test);
        delayDownload = findViewById(R.id.project_delay_download);
        checkAuthorization = findViewById(R.id.project_check_authorization);
        powerBomb = findViewById(R.id.project_power_bomb);
        connectTest.setOnClickListener(this);
        delayDownload.setOnClickListener(this);
        checkAuthorization.setOnClickListener(this);
        powerBomb.setOnClickListener(this);
        // 设备序列号
        controllerSno = getStringInfo(getString(R.string.controller_sno));
    }

    /**
     * 刷新页面
     */
    private void refreshData() {
        if (proId >= 0) {
            projectInfoEntity = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.Id.eq(proId)).unique();
        }
        if (projectInfoEntity == null) {
            return;
        }

        int projectStatus = projectInfoEntity.getProjectStatus();
        if (projectStatus == 0) {//如果为空，给个默认值（默认第一个是可点击的）
            projectStatus = AppIntentString.PROJECT_IMPLEMENT_CONNECT_TEST1;
        }

        if (!TextUtils.isEmpty(controllerSno)) {
            if (controllerSno.contains("F99")) {
                return;
            }
        }

        switch (projectStatus) {
            case AppIntentString.PROJECT_IMPLEMENT_CONNECT_TEST1://连接检测有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                connectTest.setClickable(true);
                delayDownload.setClickable(true);
                checkAuthorization.setClickable(false);
                powerBomb.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_DELAY_DOWNLOAD1://延时下载有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.un_project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                connectTest.setClickable(true);
                delayDownload.setClickable(true);
                checkAuthorization.setClickable(false);
                powerBomb.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_ONLINE_AUTHORIZE1://检查授权有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.un_project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                connectTest.setClickable(true);
                delayDownload.setClickable(true);
                checkAuthorization.setClickable(true);
                powerBomb.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_POWER_BOMB1://充电起爆有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.un_project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.project_power_bomb);
                connectTest.setClickable(true);
                delayDownload.setClickable(true);
                powerBomb.setClickable(true);
                checkAuthorization.setClickable(true);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT1://数据上传有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.un_project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                connectTest.setClickable(false);
                delayDownload.setClickable(false);
                checkAuthorization.setClickable(false);
                powerBomb.setClickable(false);
                break;
        }
    }

}