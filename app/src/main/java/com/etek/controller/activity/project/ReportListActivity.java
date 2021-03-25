package com.etek.controller.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.adapter.ProjectReportAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private RecyclerView recycleView;
    private List<PendingProject> projects = new ArrayList<>();
    private ProjectReportAdapter projectReportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        initSupportActionBar(R.string.title_activity_report);
        initView();
        initData();
    }

    private void getProjectData() {
        List<PendingProject> list = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.ProjectStatus.eq(AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT)).list();
        if (list != null && list.size() != 0) {
            projects.clear();
            Collections.reverse(list);
            projects.addAll(list);
            projectReportAdapter.notifyDataSetChanged();
        } else {
            showStatusDialog("没有上报的数据！");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProjectData();
    }

    private void initData() {

    }

    private void initView() {
        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        projectReportAdapter = new ProjectReportAdapter(R.layout.item_det_report, projects);
        projectReportAdapter.setOnItemClickListener(this);
        recycleView.setAdapter(projectReportAdapter);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        PendingProject pendingProject = projects.get(position);
        Long id = pendingProject.getId();
        Intent intent = new Intent(this, ReportDetailActivity2.class);
        intent.putExtra(AppIntentString.PROJECT_ID,id);
        startActivity(intent);
    }
}