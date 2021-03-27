package com.etek.controller.yunnan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.activity.BaseActivity;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.gen.YunnanAuthBobmEntityDao;
import com.etek.controller.utils.DateStringUtils;
import com.etek.controller.yunnan.enetity.YunnanAuthBobmEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YunDownloadDetailActivity extends BaseActivity {

    public static final String PROJECT_ID = "project_id";
    private YunnanAuthBobmEntity yunnanAuthBobmEntity;
    private List<String> detStrings = new ArrayList<>();
    private View headView;
    private RecyclerView detailList;
    private YunDownloadDetailAdapter yunDownloadDetailAdapter;
    private TextView fileID;
    private TextView proName;
    private TextView startTime;
    private TextView endTime;
    private TextView applyTime;
    private TextView devicesCode;
    private TextView allowErea;
    private TextView detStatus;

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
                yunnanAuthBobmEntity = DBManager.getInstance().getYunnanAuthBombEntityDao().queryBuilder().where(YunnanAuthBobmEntityDao.Properties.Id.eq(projectId)).unique();
            }
        }
    }

    private void initView() {
        headView = LayoutInflater.from(this).inflate(R.layout.item_yun_head_view, null);
        fileID = headView.findViewById(R.id.fileId);
        proName = headView.findViewById(R.id.pro_name);
        startTime = headView.findViewById(R.id.start_time);
        endTime = headView.findViewById(R.id.end_time);
        applyTime = headView.findViewById(R.id.applyDate);
        devicesCode = headView.findViewById(R.id.devicesCode);
        allowErea = headView.findViewById(R.id.allow_area);
        detStatus = headView.findViewById(R.id.det_status);
        detStatus.setVisibility(View.GONE);

        detailList = findViewById(R.id.detail_list);
        yunDownloadDetailAdapter = new YunDownloadDetailAdapter(R.layout.item_auth_download, detStrings);
        yunDownloadDetailAdapter.addHeaderView(headView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        detailList.setLayoutManager(linearLayoutManager);
        detailList.setAdapter(yunDownloadDetailAdapter);
    }

    private void initData() {
        if (yunnanAuthBobmEntity != null) {
            fileID.setText(yunnanAuthBobmEntity.getFileId());
            proName.setText(yunnanAuthBobmEntity.getMc());
            startTime.setText(yunnanAuthBobmEntity.getKssj());
            endTime.setText(yunnanAuthBobmEntity.getJssj());
            applyTime.setText(DateStringUtils.getDateString(yunnanAuthBobmEntity.getDate()));

            String qbqStr = yunnanAuthBobmEntity.getQbqStr();
            if (!TextUtils.isEmpty(qbqStr)) {
                String[] split = qbqStr.split("/");
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    stringBuilder.append(split[i]);
                    if (i != split.length - 1) {
                        stringBuilder.append("\n");
                    }
                }
                devicesCode.setText(stringBuilder.toString());
            }

            String zbqyStr = yunnanAuthBobmEntity.getZbqyStr();
            if (!TextUtils.isEmpty(zbqyStr)) {
                StringBuilder stringBuilder = new StringBuilder();
                String[] qyList = zbqyStr.split("/");
                for (int i = 0; i < qyList.length; i++) {
                    String locationStr = qyList[i];
                    String location = "";
                    if (!TextUtils.isEmpty(locationStr)) {
                        String[] split = locationStr.split("&");
                        if (split.length >= 2) {
                            String s1 = split[0];
                            String s2 = split[1];
                            location = String.format("%.4f,%.4f", Double.parseDouble(s1), Double.parseDouble(s2));
                        }
                    }
                    stringBuilder.append(location);
                    if (i != qyList.length - 1) {
                        stringBuilder.append("\n");
                    }
                }
                allowErea.setText(stringBuilder.toString());
            }

            String lgmStr = yunnanAuthBobmEntity.getLgmStr();
            if (!TextUtils.isEmpty(lgmStr)) {
                String[] split = lgmStr.split("/");
                List<String> strings = Arrays.asList(split);
                detStrings.addAll(strings);
                yunDownloadDetailAdapter.notifyDataSetChanged();
            }
        }
    }
}