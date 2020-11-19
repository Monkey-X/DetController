package com.etek.controller.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import com.etek.controller.R;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;


/**
 * 工程实施页
 */
public class ProjectImplementActivity extends BaseActivity implements View.OnClickListener {

    private ProjectInfoEntity projectInfoEntity;
    private long proId;
    private RelativeLayout connectTest;
    private RelativeLayout delayDownload;
    private RelativeLayout checkAuthorization;
    private RelativeLayout powerBomb;
    private RelativeLayout dataReport;

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
                startActivity(ConnectTestActivity.class);
                break;

            case R.id.project_delay_download://延时下载
                startActivity(DelayDownloadActivity.class);
                break;

            case R.id.project_check_authorization://检查授权(跳转原来的在线授权页面)
                startActivity(OnlineAuthorizeActivity2.class);
                break;

            case R.id.project_power_bomb://充电起爆
                startActivity(PowerBombActivity.class);
                break;

            case R.id.project_data_report://数据上传
                startActivity(ReportActivity2.class);
                break;
        }
    }

    /**
     * 获取项目id
     */
    private void getProjectId() {
        proId = getIntent().getLongExtra(AppIntentString.PROJECT_ID, 0);
    }

    /**
     * 初始化View
     */
    private void initView() {
        connectTest = findViewById(R.id.project_connect_test);
        delayDownload = findViewById(R.id.project_delay_download);
        checkAuthorization = findViewById(R.id.project_check_authorization);
        powerBomb = findViewById(R.id.project_power_bomb);
        dataReport = findViewById(R.id.project_data_report);
        connectTest.setOnClickListener(this);
        delayDownload.setOnClickListener(this);
        checkAuthorization.setOnClickListener(this);
        powerBomb.setOnClickListener(this);
        dataReport.setOnClickListener(this);
    }

    /**
     * 跳转页面
     */
    private void startActivity(Class clz){
        startActivity(new Intent(this,clz));
    }

    /**
     * 刷新页面
     */
    private void refreshData() {
        if (proId > 0) {
            projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
        }

        String status = projectInfoEntity.getProjectImplementStates();
        if (status == null){//如果为空，给个默认值（默认第一个是可点击的）
            status = AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT;
        }

        switch (status) {
            case AppIntentString.PROJECT_IMPLEMENT_CONNECT_TEST://前一个有颜色，其余四个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.un_project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.un_project_data_report);
                delayDownload.setClickable(false);
                checkAuthorization.setClickable(false);
                powerBomb.setClickable(false);
                dataReport.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_DELAY_DOWNLOAD://前二个有颜色，其余三个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.un_project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.un_project_data_report);
                checkAuthorization.setClickable(false);
                powerBomb.setClickable(false);
                dataReport.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_ONLINE_AUTHORIZE://前三个有颜色，其余二个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.un_project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.un_project_data_report);
                powerBomb.setClickable(false);
                dataReport.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_POWER_BOMB://前四个有颜色，其余一个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.un_project_data_report);
                dataReport.setClickable(false);
                break;

            case AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT://前五个有颜色，其余一个置灰(不可点击)
                connectTest.setBackgroundResource(R.drawable.project_connect_test);
                delayDownload.setBackgroundResource(R.drawable.project_delay_download);
                checkAuthorization.setBackgroundResource(R.drawable.project_check_authorization);
                powerBomb.setBackgroundResource(R.drawable.project_power_bomb);
                dataReport.setBackgroundResource(R.drawable.project_data_report);
                break;
        }
    }
}