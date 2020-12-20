package com.etek.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.adapter.AuthDownloadDetailAdapter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class AuthDownLoadDetailActivity extends BaseActivity {

    private RecyclerView detailList;
    public static final String PROJECT_ID = "project_id";
    private ProjectInfoEntity projectInfoEntity;
    private TextView proName;
    private TextView proCode;
    private TextView companyName;
    private TextView companyCode;
    private TextView contractName;
    private TextView contractCode;
    private TextView applyDate;
    private TextView devicesCode;
    private View headView;
    private List<DetonatorEntity> detonatorList = new ArrayList<>();
    private AuthDownloadDetailAdapter authDownloadDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_down_load_detail);
        initSupportActionBar(R.string.title_activity_detail_download);
        getIntentData();
        initView();
        initData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            long projectId = intent.getLongExtra(PROJECT_ID, -1);
            if (projectId != -1) {
                projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(projectId)).unique();
            }
        }
    }

    private void initView() {
//        headView = LayoutInflater.from(this).inflate(R.layout.list_head_view, null);
//        proName = headView.findViewById(R.id.proName);
//        proCode = headView.findViewById(R.id.proCode);
//        companyName = headView.findViewById(R.id.companyName);
//        companyCode = headView.findViewById(R.id.companyCode);
//        contractName = headView.findViewById(R.id.contractName);
//        contractCode = headView.findViewById(R.id.contractCode);
//        applyDate = headView.findViewById(R.id.applyDate);
//        devicesCode = headView.findViewById(R.id.devicesCode);
        detailList = findViewById(R.id.detail_list);
        authDownloadDetailAdapter = new AuthDownloadDetailAdapter(R.layout.item_auth_download, detonatorList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        detailList.setLayoutManager(linearLayoutManager);
        detailList.setAdapter(authDownloadDetailAdapter);
//        detailList.addView(headView,0);
    }


    private void initData() {

        if (projectInfoEntity != null) {
//            proName.setText(projectInfoEntity.getProName());
//            proCode.setText(projectInfoEntity.getProCode());
//            companyName.setText(projectInfoEntity.getCompanyName());
//            companyCode.setText(projectInfoEntity.getCompanyCode());
//            contractName.setText(projectInfoEntity.getCompanyName());
//            contractCode.setText(projectInfoEntity.getCompanyCode());
//            applyDate.setText(projectInfoEntity.getApplyDate().toString());

            List<ControllerEntity> controllerList = projectInfoEntity.getControllerList();
            if (controllerList != null && controllerList.size() != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (ControllerEntity controllerEntity : controllerList) {
                    String name = controllerEntity.getName();
                    stringBuilder.append(name).append("\n");
                }
//                devicesCode.setText(stringBuilder.toString());
            }

            List<DetonatorEntity> detonatorList1 = projectInfoEntity.getDetonatorList();
            if (!detonatorList1.isEmpty()) {
                this.detonatorList.addAll(detonatorList1);
                authDownloadDetailAdapter.notifyDataSetChanged();
            }

        }

    }
}