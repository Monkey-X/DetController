package com.etek.controller.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.adapter.ProjectAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷管组网
 */
public class NetWorkActivity extends BaseActivity implements View.OnClickListener, ProjectAdapter.OnItemClickListener {

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
        // 读取数据库里面的数据，有数据展示数据，没有数据展示没有数据界面
//        for (int i = 0; i < 10; i++) {
//            ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
//            projectInfoEntity.setId((long) i);
//            projectInfoEntity.setProName("項目"+i);
//            long insert = DBManager.getInstance().getProjectInfoEntityDao().insert(projectInfoEntity);
//        }
        List<ProjectInfoEntity> projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
        if (projectInfoEntities == null && projectInfoEntities.size() == 0) {
            noDataView.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "initData: projectInfoEntities.size() = " + projectInfoEntities.size());
            projectInfos.clear();
            projectInfos.addAll(projectInfoEntities);
            projectAdapter.notifyDataSetChanged();
        }
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
        projectAdapter = new ProjectAdapter(this,projectInfos);
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
                ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
                long insert = DBManager.getInstance().getProjectInfoEntityDao().insert(projectInfoEntity);
                if (insert >=0) {
                    noDataView.setVisibility(View.GONE);
                    projectInfos.add(projectInfoEntity);
                    projectAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        ProjectInfoEntity projectInfoEntity = projectInfos.get(position);
        Long id = projectInfoEntity.getId();

        Intent intent = new Intent(this,ProjectDetailActivity.class);
        intent.putExtra(AppIntentString.PROJECT_ID,id);
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
}