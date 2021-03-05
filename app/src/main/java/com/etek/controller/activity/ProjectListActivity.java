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
import com.etek.controller.activity.project.ProjectDetailActivity;
import com.etek.controller.adapter.ProjectListAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.common.Globals;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.utils.DateStringUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.util.Log;

/**
 * 项目列表页
 */
public class ProjectListActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener {

    private RecyclerView recycleView;
    private View noDataView;
    private List<PendingProject> projectInfos = new ArrayList<>();
    private ProjectListAdapter projectListAdapter;
    private AlertDialog alertDialog;
    private final String TAG="ProjectListActivity";

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
        projectListAdapter.setOnItemLongClickListener(this);

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
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        if (Globals.user == null) {
            Intent intent = new Intent(this, UserInfoActivity.class);
            startActivity(intent);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_view, null, false);
        EditText projectCode = view.findViewById(R.id.changeDelayTime);
        TextView textTitle = view.findViewById(R.id.text_title);
        textTitle.setText("工程编号：");
        String projectNum = getProjectNum();
        projectCode.setText(projectNum);
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
                Log.d(TAG,String.format("工程编号：%s",projectCodeStr));
                if (TextUtils.isEmpty(projectCodeStr)) {
                    ToastUtils.showShort(ProjectListActivity.this, "请输入工程编号！");
                    return;
                }
                if (!projectInfos.isEmpty()) {
                    for (PendingProject projectInfo : projectInfos) {
                        if (projectInfo.getProjectCode().equals(projectCodeStr)) {
                            ToastUtils.showShort(ProjectListActivity.this, "工程编号已存在！");
                            return;
                        }
                    }
                }
                createNewProject(projectCodeStr);
                PendingProject pendingProject = new PendingProject();
                pendingProject.setProjectCode(projectCodeStr);
                pendingProject.setDate(DateStringUtils.getCurrentTime());
                pendingProject.setCompanyCode(Globals.user.getCompanyCode());
                pendingProject.setControllerId(getStringInfo(getString(R.string.controller_sno)));
                DBManager.getInstance().getPendingProjectDao().insert(pendingProject);
                initData();
                recycleView.scrollToPosition(0);
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void createNewProject(String stringInfo) {
        String projectNum = "";
        String dateString = DateStringUtils.getDateString();
        if (TextUtils.isEmpty(stringInfo)) {
            projectNum = dateString + "-1";
            setStringInfo(AppIntentString.PROJECT_ID, projectNum);
            return;
        }

        String[] split = stringInfo.split("-");
        if(split.length<2) return;

        try{
            int ret = Integer.parseInt(split[1]);
        }catch (NumberFormatException e)
        {
            Log.d(TAG,e.getMessage());
            return;
        }

        if (split[0].equalsIgnoreCase(dateString)) {
            projectNum = split[0] + "-" + (Integer.parseInt(split[1]) + 1);
        } else {
            projectNum = dateString + "-1";
        }

        setStringInfo(AppIntentString.PROJECT_ID, projectNum);
    }

    /**
     * 获取项目编号
     */
    private String getProjectNum() {
        String projectNum = getStringInfo(AppIntentString.PROJECT_ID);
        String dateString = DateStringUtils.getDateString();
        if (TextUtils.isEmpty(projectNum) || !projectNum.contains(dateString)) {
            projectNum = dateString + "-1";
        }
        return projectNum;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        PendingProject pendingProject = projectInfos.get(position);
        Intent intent = new Intent(mContext, ProjectDetailActivity.class);
        intent.putExtra(AppIntentString.PROJECT_ID, pendingProject.getId());
        startActivity(intent);
    }

    /**
     * 工程列表中的工程长按删除
     * @param adapter
     * @param view
     * @param position
     * @return
     */
    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        PendingProject pendingProject = projectInfos.get(position);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("确定删除吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //删除数据
                DBManager.getInstance().getPendingProjectDao().delete(pendingProject);
                List<ProjectDetonator> projectDetonators = DBManager.getInstance().getProjectDetonatorDao()._queryPendingProject_DetonatorList(pendingProject.getId());
                DBManager.getInstance().getProjectDetonatorDao().deleteInTx(projectDetonators);
                projectInfos.remove(position);
                projectListAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }
}