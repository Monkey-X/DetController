package com.etek.controller.activity.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.adapter.ContractAdapter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 授权下载
 */
public class AuthorizedDownloadActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, BaseQuickAdapter.OnItemLongClickListener {

    private RecyclerView recycleView;
    private LinearLayout noDataView;
    private List<ProjectInfoEntity> projectInfos = new ArrayList<>();
    private ContractAdapter contractAdapter;
    private String respStr = "";
    private static final int UPDATE = 10;
    private String TAG = "AuthorizedDownloadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_download);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_btn:
                goToOfflineEditActivity();
                break;
            case R.id.back_img:
                finish();
                break;
        }
    }

    private void goToOfflineEditActivity() {
        String userStr = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        if (TextUtils.isEmpty(userStr)) {
            Intent intent = new Intent(this, UserInfoActivity2.class);
            startActivity(intent);
            return;
        }
        Intent intent = new Intent(this, OfflineEditActivity.class);
        startActivityForResult(intent,200);
    }

    /**
     * 初始化View
     */
    private void initView() {
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        View backImg = findViewById(R.id.back_img);
        textTitle.setText("授权下载");
        textBtn.setText("添加项目");
        backImg.setOnClickListener(this);
        textBtn.setOnClickListener(this);
        recycleView = findViewById(R.id.authorized_download_recycleView);
        noDataView = findViewById(R.id.nodata_view);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        contractAdapter = new ContractAdapter(R.layout.contract_item_view, projectInfos);
        recycleView.setAdapter(contractAdapter);
        contractAdapter.setOnItemClickListener(this);
        contractAdapter.setOnItemLongClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        List<ProjectInfoEntity> projectDownLoadEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();

        projectInfos.clear();
        if (projectDownLoadEntities != null && projectDownLoadEntities.size() > 0) {
            noDataView.setVisibility(View.GONE);
            Collections.reverse(projectDownLoadEntities);
            projectInfos.addAll(projectDownLoadEntities);
        } else {
            noDataView.setVisibility(View.VISIBLE);
        }
        contractAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ProjectInfoEntity projectInfoEntity = projectInfos.get(position);
        Long projectId = projectInfoEntity.getId();

        Intent intent = new Intent(this, AuthDownLoadDetailActivity.class);
        intent.putExtra(AuthDownLoadDetailActivity.PROJECT_ID,projectId);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

        ProjectInfoEntity projectInfoEntity = projectInfos.get(position);

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
                DBManager.getInstance().getProjectInfoEntityDao().delete(projectInfoEntity);
                projectInfos.remove(position);
                contractAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.create().show();

        return true;
    }
}
