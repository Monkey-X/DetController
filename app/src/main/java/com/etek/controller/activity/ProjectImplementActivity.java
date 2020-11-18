package com.etek.controller.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.adapter.ProjectImplementAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.entity.ProjectImplementItem;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * 工程实施页
 */
public class ProjectImplementActivity extends BaseActivity {

    private RecyclerView recycleView;
    private List<ProjectImplementItem> items = new ArrayList<>();
    private ProjectImplementAdapter projectImplementAdapter;
    private ProjectInfoEntity projectInfoEntity;
    private long proId;

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
        recycleView = findViewById(R.id.project_implement_recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        projectImplementAdapter = new ProjectImplementAdapter(R.layout.activity_project_implement_item, items);
        recycleView.setAdapter(projectImplementAdapter);
        projectImplementAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0://连接检测
                        startActivity(ConnectTestActivity.class);
                        break;

                    case 1://延时下载
                        startActivity(DelayDownloadActivity.class);
                        break;

                    case 2://检查授权(跳转原来的在线授权页面)
                        startActivity(OnlineAuthorizeActivity2.class);
                        break;

                    case 3://充电起爆
                        startActivity(PowerBombActivity.class);
                        break;

                    case 4://数据上传
                        startActivity(ReportActivity2.class);
                        break;
                }
            }
        });
    }

    /**
     * 跳转页面
     */
    private void startActivity(Class clz){
        startActivity(new Intent(this,clz));
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        items.clear();
        if (proId > 0) {
            projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
        }
        if (projectInfoEntity != null){
            items.add(new ProjectImplementItem(R.drawable.project_connect_test, R.drawable.un_project_connect_test, this.getString(R.string.project_connect_test), projectInfoEntity.getProjectImplementStates()));
            items.add(new ProjectImplementItem(R.drawable.project_delay_download, R.drawable.un_project_delay_download, this.getString(R.string.project_delay_download), projectInfoEntity.getProjectImplementStates()));
            items.add(new ProjectImplementItem(R.drawable.project_chaeck_authorization, R.drawable.un_project_chaeck_authorization, this.getString(R.string.project_chaeck_authorization), projectInfoEntity.getProjectImplementStates()));
            items.add(new ProjectImplementItem(R.drawable.project_power_bomb, R.drawable.un_project_power_bomb, this.getString(R.string.project_power_bomb), projectInfoEntity.getProjectImplementStates()));
            items.add(new ProjectImplementItem(R.drawable.project_data_report, R.drawable.un_project_data_report, this.getString(R.string.project_date_report), projectInfoEntity.getProjectImplementStates()));
        }
        projectImplementAdapter.notifyDataSetChanged();
    }
}