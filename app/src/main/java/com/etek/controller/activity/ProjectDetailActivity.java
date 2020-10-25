package com.etek.controller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.adapter.ProjectDetailAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ProjectDetailActivity";
    private TextView areaNum;
    private EditText delayTime;
    private RecyclerView recycleView;
    private List<DetonatorEntity> detonators;
    private long projectId;
    private ProjectDetailAdapter projectDetailAdapter;
    private EditText delayholein;
    private EditText delayholeout;
    private LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        initView();
        initRecycleView();
        initIntentData();
        initData();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        projectId = intent.getLongExtra(AppIntentString.PROJECT_ID,-1);
        Log.d(TAG, "initIntentData: projectId = " + projectId);
        if (projectId == -1) {
            return;
        }
        List<DetonatorEntity> detonatorEntities = DBManager.getInstance().getDetonatorEntityDao()._queryProjectInfoEntity_DetonatorList(projectId);
        if (detonatorEntities != null) {
            detonators.addAll(detonatorEntities);
            projectDetailAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        View backImag = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        backImag.setOnClickListener(this);
        textTitle.setText(R.string.project_detail);
        textBtn.setVisibility(View.GONE);

        rootView = findViewById(R.id.rootview);

        // 正式布局
        View reduce = findViewById(R.id.reduce);
        areaNum = findViewById(R.id.area_num);
        View add = findViewById(R.id.add);
        // 起始延时
        delayTime = findViewById(R.id.delay_time);

        recycleView = findViewById(R.id.recycleView);

        delayholein = findViewById(R.id.delayholein);
        delayholeout = findViewById(R.id.delayholeout);

        reduce.setOnClickListener(this);
        add.setOnClickListener(this);

    }


    private void initRecycleView() {
        detonators = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DetonatorEntity detonatorEntity = new DetonatorEntity();
            detonatorEntity.setHolePosition(i+"-");
            detonatorEntity.setRelay(i * 10 +"");
            detonatorEntity.setUid(i+"-"+i*10);
            detonatorEntity.setProjectInfoId(projectId);
            detonators.add(detonatorEntity);
        }
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        projectDetailAdapter = new ProjectDetailAdapter(this, detonators);
        recycleView.setAdapter(projectDetailAdapter);
    }

    private void initData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.reduce:
                areaReduce();
                break;
            case R.id.add:
                areaAdd();
                break;
        }
    }

    // 操作区域加法
    private void areaAdd() {
        String num = areaNum.getText().toString().trim();
        int parseInt = Integer.parseInt(num);
        if (parseInt >= 99) {
            return;
        }
        areaNum.setText(parseInt + 1 + "");
    }

    // 操作区域减法
    private void areaReduce() {
        String num = areaNum.getText().toString().trim();
        int parseInt = Integer.parseInt(num);
        if (parseInt <= 1) {
            return;
        }
        areaNum.setText(parseInt - 1 + "");
    }
}