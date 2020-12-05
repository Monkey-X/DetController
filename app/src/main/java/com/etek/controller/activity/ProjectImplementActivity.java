package com.etek.controller.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.etek.controller.R;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.fragment.ProjectDialog;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.List;


/**
 * 工程实施页
 */
public class ProjectImplementActivity extends BaseActivity implements View.OnClickListener, ProjectDialog.OnMakeProjectListener {

    private ProjectInfoEntity projectInfoEntity;
    private long proId = -1;
    private RelativeLayout connectTest;
    private RelativeLayout delayDownload;
    private RelativeLayout checkAuthorization;
    private RelativeLayout powerBomb;
    private RelativeLayout dataReport;
    private RelativeLayout createNet;

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
            case R.id.project_net:
                startActivity(new Intent(this, ProjectDetailActivity.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;
            case R.id.project_connect_test://连接检测
                startActivity(new Intent(this, ConnectTestActivity.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;

            case R.id.project_delay_download://延时下载
                startActivity(new Intent(this, DelayDownloadActivity.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;

            case R.id.project_check_authorization://检查授权
                startActivity(new Intent(this, OnlineAuthorizeActivity2.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;

            case R.id.project_power_bomb://充电起爆
                startActivity(new Intent(this, PowerBombActivity.class));
                break;

            case R.id.project_data_report://数据上传
                startActivity(new Intent(this, ReportDetailActivity2.class).putExtra(AppIntentString.PROJECT_ID, proId));
                break;
        }
    }

    /**
     * 获取项目id
     */
    private void getProjectId() {
        List<ProjectInfoEntity> projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
        if (projectInfoEntities != null && projectInfoEntities.size() != 0) {
            // 没有项目，创建项目
            createProject();
        } else {
            ProjectInfoEntity projectInfoEntity = projectInfoEntities.get(0);
            proId = projectInfoEntity.getId();
        }
    }

    @Override
    public void makeProjectCancel() {
        this.finish();
    }

    private void createProject() {
        ProjectDialog projectDialog = new ProjectDialog();
        projectDialog.setOnMakeProjectListener(this);
        projectDialog.show(getSupportFragmentManager(),"projectDialog");
    }

    @Override
    public void makeProject(ProjectInfoEntity bean) {
        if (bean != null) {
            proId = DBManager.getInstance().getProjectInfoEntityDao().insert(bean);
        }else{
            ToastUtils.showShort(this,"创建项目失败！");
            this.finish();
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        connectTest = findViewById(R.id.project_connect_test);
        createNet = findViewById(R.id.project_net);
        delayDownload = findViewById(R.id.project_delay_download);
        checkAuthorization = findViewById(R.id.project_check_authorization);
        powerBomb = findViewById(R.id.project_power_bomb);
        dataReport = findViewById(R.id.project_data_report);
        connectTest.setOnClickListener(this);
        delayDownload.setOnClickListener(this);
        checkAuthorization.setOnClickListener(this);
        powerBomb.setOnClickListener(this);
        dataReport.setOnClickListener(this);
        createNet.setOnClickListener(this);
    }

    /**
     * 刷新页面
     */
    private void refreshData() {
        if (proId >= 0) {
            projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
        }
        if (projectInfoEntity == null) {
            return;
        }

        String status = projectInfoEntity.getProjectImplementStates();
        if (status == null) {//如果为空，给个默认值（默认第一个是可点击的）
            status = AppIntentString.PROJECT_IMPLEMENT_CONNECT_TEST;
        }

        switch (status) {
            case AppIntentString.PROJECT_IMPLEMENT_CONNECT_TEST://连接检测有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.un_project_data_report);
//                delayDownload.setClickable(false);
//                checkAuthorization.setClickable(false);
//                powerBomb.setClickable(false);
//                dataReport.setClickable(false);
//                connectTest.setClickable(true);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_DELAY_DOWNLOAD://延时下载有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.un_project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.un_project_data_report);
//                connectTest.setClickable(false);
//                delayDownload.setClickable(true);
//                checkAuthorization.setClickable(false);
//                powerBomb.setClickable(false);
//                dataReport.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_ONLINE_AUTHORIZE://检查授权有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.un_project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.un_project_data_report);
//                connectTest.setClickable(false);
//                delayDownload.setClickable(false);
//                checkAuthorization.setClickable(true);
//                powerBomb.setClickable(false);
//                dataReport.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_POWER_BOMB://充电起爆有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.un_project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.un_project_data_report);
//                connectTest.setClickable(false);
//                delayDownload.setClickable(false);
//                powerBomb.setClickable(true);
//                checkAuthorization.setClickable(false);
//                dataReport.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT://数据上传有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.un_project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.project_data_report);
//                connectTest.setClickable(false);
//                delayDownload.setClickable(false);
//                checkAuthorization.setClickable(false);
//                dataReport.setClickable(true);
//                powerBomb.setClickable(false);
                break;
        }
    }

}