package com.etek.controller.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.adapter.ProjectListAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 项目列表页
 */
public class ProjectListActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    private RecyclerView recycleView;
    private View noDataView;
    private List<PendingProject> projectInfos = new ArrayList<>();
    private ProjectListAdapter projectListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        initView();
        initData();
    }

    /**
     * 初始化View
     */
    private void initView() {

        View backImag = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        ImageView imgAdd = findViewById(R.id.img_add);
        backImag.setOnClickListener(this);
        textTitle.setText("工程实施");
        imgAdd.setOnClickListener(this);


        recycleView = findViewById(R.id.project_recycleView);
        noDataView = findViewById(R.id.nodata_view);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        projectListAdapter = new ProjectListAdapter(R.layout.item_list_project, projectInfos);
        recycleView.setAdapter(projectListAdapter);
        projectListAdapter.setOnItemClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        List<PendingProject> pendingProjects = DBManager.getInstance().getPendingProjectDao().loadAll();
        projectInfos.clear();
        Collections.reverse(pendingProjects);
        if (pendingProjects != null && pendingProjects.size() > 0) {
            noDataView.setVisibility(View.GONE);
            projectInfos.addAll(pendingProjects);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
        projectListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.img_add:
                // 创建项目
                showCreateProjectDialog();
                break;
        }
    }

    private void showCreateProjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_view, null, false);
        EditText projectCode = view.findViewById(R.id.changeDelayTime);
        TextView textTitle = view.findViewById(R.id.text_title);
        textTitle.setText("项目编号：");
        projectCode.setHint("YYYYMMDD");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String projectCodeStr = projectCode.getText().toString().trim();
                if (TextUtils.isEmpty(projectCodeStr)) {
                    ToastUtils.showShort(ProjectListActivity.this, "请输入项目编号！");
                    return;
                }
                if (!projectInfos.isEmpty()) {
                    for (PendingProject projectInfo : projectInfos) {
                        if (projectInfo.getProjectCode().equals(projectCodeStr)) {
                            ToastUtils.showShort(ProjectListActivity.this, "项目编号已存在！");
                            return;
                        }
                    }
                }
                PendingProject pendingProject = new PendingProject();
                pendingProject.setProjectCode(projectCodeStr);
                pendingProject.setDate(getCurrentTime());
                pendingProject.setControllerId(getStringInfo(getString(R.string.controller_sno)));
                DBManager.getInstance().getPendingProjectDao().insert(pendingProject);
                initData();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 获取当前的时间
     * @return
     */
    private String getCurrentTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String format = simpleDateFormat.format(date);
        return format;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        PendingProject pendingProject = projectInfos.get(position);
        Intent intent = new Intent(mContext, ProjectDetailActivity.class);
        intent.putExtra(AppIntentString.PROJECT_ID, pendingProject.getId());
        startActivity(intent);
    }
}