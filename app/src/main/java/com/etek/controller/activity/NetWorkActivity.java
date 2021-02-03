package com.etek.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.activity.project.ProjectDetailActivity;
import com.etek.controller.adapter.ProjectAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.fragment.ProjectDialog;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷管组网
 */
public class NetWorkActivity extends BaseActivity implements View.OnClickListener, ProjectAdapter.OnItemClickListener, ProjectDialog.OnMakeProjectListener {

    private RecyclerView recycleView;
    private View noDataView;
    private static String TAG = "NetWorkActivity";
    private List<ProjectInfoEntity> projectInfos = new ArrayList<>();
    private ProjectAdapter projectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work);
        initView();
        initData();
    }

    private void initData() {
        List<ProjectInfoEntity> projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
        projectInfos.clear();
        if (projectInfoEntities != null && projectInfoEntities.size() > 0) {
            noDataView.setVisibility(View.GONE);
            Log.d(TAG, "initData: projectInfoEntities.size() = " + projectInfoEntities.size());
            projectInfos.addAll(projectInfoEntities);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
        projectAdapter.notifyDataSetChanged();
    }


    private void initView() {
        View backImag = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        backImag.setOnClickListener(this);
        textTitle.setText(R.string.activity_network);
        textBtn.setText(R.string.create_project);
        textBtn.setOnClickListener(this);
        recycleView = findViewById(R.id.recycleView);
        noDataView = findViewById(R.id.nodata_view);

        recycleView.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(this, projectInfos);
        recycleView.setAdapter(projectAdapter);
        projectAdapter.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.text_btn:
                //创建项目
                showMakeProjectDialog();
                break;
        }
    }

    private void showMakeProjectDialog() {
        String userStr = getPreInfo("userInfo");
        if (TextUtils.isEmpty(userStr)) {
            startActivity(new Intent(NetWorkActivity.this,UserInfoActivity.class));
            return;
        }
        ProjectDialog projectDialog = new ProjectDialog();
        projectDialog.setOnMakeProjectListener(this);
        projectDialog.show(getSupportFragmentManager(),"makeProDialog");
    }

    @Override
    public void onItemClick(int position) {
        ProjectInfoEntity projectInfoEntity = projectInfos.get(position);
        Long id = projectInfoEntity.getId();

        Intent intent = new Intent(this, ProjectDetailActivity.class);
        intent.putExtra(AppIntentString.PROJECT_ID, id);
        startActivity(intent);
    }

    @Override
    public void onItemLongCLick(int position) {
        ProjectInfoEntity projectInfoEntity = projectInfos.get(position);
        //长按弹出对话框，提示
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                DBManager.getInstance().getProjectInfoEntityDao().delete(projectInfoEntity);

                initData();
            }
        });
        builder.create().show();
    }

    @Override
    public void makeProject(ProjectInfoEntity bean) {
        // 创建项目的回调
        long insert = DBManager.getInstance().getProjectInfoEntityDao().insert(bean);
        if (insert >= 0) {
            noDataView.setVisibility(View.GONE);
            projectInfos.add(bean);
            projectAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void makeProjectCancel() {

    }
}