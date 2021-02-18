package com.etek.controller.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.adapter.AuthDownloadDetailAdapter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AuthDownLoadDetailActivity extends BaseActivity {

    private RecyclerView detailList;
    public static final String PROJECT_ID = "project_id";
    private ProjectInfoEntity projectInfoEntity;
    private TextView proCode;
    private TextView contractCode;
    private TextView applyDate;
    private TextView devicesCode;
    private View headView;
    private List<DetonatorEntity> detonatorList = new ArrayList<>();
    private AuthDownloadDetailAdapter authDownloadDetailAdapter;
    private TextView allowArea;
    private LinearLayout layoutPro;
    private LinearLayout layoutContract;

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

    //只保留【项目编号】/【合同编号】选其一，或者都没有；【单位代码】【申请日期】【起爆器编号】，增加【准爆区域】（即报备经纬度信息），每颗雷管状态，用颜色加以区分，增加“删除”按钮，整个UI界面优化
    private void initView() {
        headView = LayoutInflater.from(this).inflate(R.layout.list_head_view, null);
        proCode = headView.findViewById(R.id.proCode);
        contractCode = headView.findViewById(R.id.contractCode);
        applyDate = headView.findViewById(R.id.applyDate);
        devicesCode = headView.findViewById(R.id.devicesCode);
        allowArea = headView.findViewById(R.id.allow_area);

        layoutPro = headView.findViewById(R.id.layout_pro);
        layoutContract = headView.findViewById(R.id.layout_contract);

        detailList = findViewById(R.id.detail_list);
        authDownloadDetailAdapter = new AuthDownloadDetailAdapter(R.layout.item_auth_download, detonatorList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        detailList.setLayoutManager(linearLayoutManager);
        detailList.setAdapter(authDownloadDetailAdapter);
        authDownloadDetailAdapter.addHeaderView(headView);
    }


    private void initData() {

        if (projectInfoEntity != null) {
            String proCode = projectInfoEntity.getProCode();
            if (!TextUtils.isEmpty(proCode)) {
                this.proCode.setText(proCode);
            }else{
                layoutPro.setVisibility(View.GONE);
            }

            String contractCode = projectInfoEntity.getContractCode();
            if (!TextUtils.isEmpty(contractCode)) {
                this.contractCode.setText(contractCode);
            }else{
                layoutContract.setVisibility(View.GONE);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(projectInfoEntity.getApplyDate());
            applyDate.setText(format);

            List<ControllerEntity> controllerList = projectInfoEntity.getControllerList();
            if (controllerList != null && controllerList.size() != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < controllerList.size(); i++) {
                    ControllerEntity controllerEntity = controllerList.get(i);
                    String name = controllerEntity.getName();
                    stringBuilder.append(name);
                    if (i != controllerList.size()-1) {
                        stringBuilder.append("\n");
                    }
                }
                devicesCode.setText(stringBuilder.toString());
            }

            List<PermissibleZoneEntity> permissibleZoneList = projectInfoEntity.getPermissibleZoneList();
            if (permissibleZoneList!=null && permissibleZoneList.size()!=0) {
                StringBuilder permissString = new StringBuilder();
                for (int i = 0; i < permissibleZoneList.size(); i++) {
                    PermissibleZoneEntity permissibleZoneEntity = permissibleZoneList.get(i);
                    double latitude = permissibleZoneEntity.getLatitude();
                    double longitude = permissibleZoneEntity.getLongitude();
                    permissString.append(longitude+","+latitude);
                    if (i != permissibleZoneList.size()-1) {
                        permissString.append("\n");
                    }
                }
                allowArea.setText(permissString.toString());
            }


            List<DetonatorEntity> detonatorList1 = projectInfoEntity.getDetonatorList();
            if (!detonatorList1.isEmpty()) {
                this.detonatorList.addAll(detonatorList1);
                authDownloadDetailAdapter.notifyDataSetChanged();
            }

        }

    }
}