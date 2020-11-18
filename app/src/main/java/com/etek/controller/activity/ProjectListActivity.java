package com.etek.controller.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.etek.controller.R;
import com.etek.controller.adapter.ProjectAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.activity.BaseActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目列表页
 */
public class ProjectListActivity extends BaseActivity implements ProjectAdapter.OnItemClickListener {

    private RecyclerView recycleView;
    private View noDataView;
    private ProjectAdapter projectAdapter;
    private List<ProjectInfoEntity> projectInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        initSupportActionBar(R.string.project_list);
        initView();
        initData();
    }

    /**
     * 初始化View
     */
    private void initView() {
        recycleView = findViewById(R.id.project_recycleView);
        noDataView = findViewById(R.id.nodata_view);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(this, projectInfos);
        recycleView.setAdapter(projectAdapter);
        projectAdapter.setOnItemClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        List<ProjectInfoEntity> projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
        projectInfos.clear();
        if (projectInfoEntities != null && projectInfoEntities.size() > 0) {
            noDataView.setVisibility(View.GONE);
            projectInfos.addAll(projectInfoEntities);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
        projectAdapter.notifyDataSetChanged();
    }

    /**
     * 列表条目点击事件
     */
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(mContext, ProjectImplementActivity.class);
        intent.putExtra(AppIntentString.PROJECT_ID, projectInfos.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongCLick(int position) {
        //TODO 长按事件
    }
}