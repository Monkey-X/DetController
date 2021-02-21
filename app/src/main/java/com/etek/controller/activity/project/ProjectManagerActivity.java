package com.etek.controller.activity.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.etek.controller.R;
import com.etek.controller.activity.ProjectListActivity;
import com.etek.controller.activity.ReportListActivity;
import com.etek.sommerlibrary.activity.BaseActivity;

public class ProjectManagerActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_manager);
        initSupportActionBar(R.string.home_project_implement);
        initView();
    }

    private void initView() {
        View projectCreate = findViewById(R.id.project_create);
        View projectDataReport = findViewById(R.id.project_data_report);
        projectCreate.setOnClickListener(this);
        projectDataReport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.project_create:
                startActivity(new Intent(this, ProjectListActivity.class));
                break;
            case R.id.project_data_report:
                startActivity(new Intent(this, ReportListActivity.class));
                break;
        }
    }
}