package com.etek.controller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.etek.controller.R;
import com.etek.controller.adapter.ProjectDetailAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener, ProjectDetailAdapter.OnItemClickListener {

    private static final String TAG = "ProjectDetailActivity";
    private TextView areaNum;
    private EditText delayTimeNew;
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
        initProjectID();
        initView();
        initRecycleView();
        initIntentData();
        initData();
    }

    private void initProjectID() {
        Intent intent = getIntent();
        projectId = intent.getLongExtra(AppIntentString.PROJECT_ID, -1);
        Log.d(TAG, "initIntentData: projectId = " + projectId);
    }

    private void initIntentData() {
        if (projectId == -1) {
            return;
        }
        List<DetonatorEntity> detonatorEntities = DBManager.getInstance().getDetonatorEntityDao()._queryProjectInfoEntity_DetonatorList(projectId);

        if (detonatorEntities != null && detonatorEntities.size() != 0) {
            detonators.addAll(detonatorEntities);
            projectDetailAdapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < 10; i++) {
                if (i == 3) {
                    areaNum.setText("2");
                }
                if (i == 6) {
                    areaNum.setText("3");
                }

                if (i == 9) {
                    areaNum.setText("4");
                }
                getDatas();
            }
        }
    }

    private void initView() {
        View backImag = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        backImag.setOnClickListener(this);
        textTitle.setText(R.string.project_detail);
        textBtn.setText("保存工程");

        textBtn.setOnClickListener(this);

        rootView = findViewById(R.id.rootview);

        // 正式布局
        View reduce = findViewById(R.id.reduce);
        areaNum = findViewById(R.id.area_num);
        View add = findViewById(R.id.add);
        // 起始延时
        delayTimeNew = findViewById(R.id.delay_time);

        recycleView = findViewById(R.id.recycleView);

        delayholein = findViewById(R.id.delayholein);
        delayholeout = findViewById(R.id.delayholeout);

        reduce.setOnClickListener(this);
        add.setOnClickListener(this);

    }


    private void initRecycleView() {
        detonators = new ArrayList<>();

        recycleView.setLayoutManager(new LinearLayoutManager(this));
        projectDetailAdapter = new ProjectDetailAdapter(this, detonators);
        recycleView.setAdapter(projectDetailAdapter);
        projectDetailAdapter.setOnItemClickListener(this);
    }

    private void initData() {

    }

    private void getDatas() {
        // 录入数据 todo
        int lastDelay;
        int lastAreaNum;//操作区域
        int lastHoleNum;// 孔内编号
        if (detonators.size() == 0) {
            lastDelay = getIntFormString(delayTimeNew.getText().toString().trim());
            lastAreaNum = getIntFormString(areaNum.getText().toString().trim());
            lastHoleNum = 0;
        } else {
            DetonatorEntity detonatorEntity = detonators.get(detonators.size() - 1);
            lastDelay = getIntFormString(detonatorEntity.getRelay());
            String[] split = detonatorEntity.getHolePosition().split("-");
            lastAreaNum = getIntFormString(split[0]);
            lastHoleNum = getIntFormString(split[1]);
        }

        int nowAreaNum = getIntFormString(areaNum.getText().toString().trim());
        int delayholeinTime = getIntFormString(delayholein.getText().toString().trim());
        int delayholeoutTime = getIntFormString(delayholeout.getText().toString().trim());
        if (nowAreaNum == lastAreaNum) {
            // 孔内
            lastHoleNum = lastHoleNum + 1;
            lastDelay = lastDelay + delayholeinTime;
        } else {
            // 空间
            lastHoleNum = 1;
            lastDelay = lastDelay + delayholeoutTime;
        }

        DetonatorEntity detonatorEntity = new DetonatorEntity();
        detonatorEntity.setProjectInfoId(projectId);
        detonatorEntity.setHolePosition(nowAreaNum + "-" + lastHoleNum);
        detonatorEntity.setRelay(String.valueOf(lastDelay));
        detonatorEntity.setUid(projectId +"-"+ detonatorEntity.getHolePosition()+detonatorEntity.getRelay());
        detonators.add(detonatorEntity);
    }


    //  数字型的字符串转为数字
    public int getIntFormString(String stringNum) {
        int i = Integer.parseInt(stringNum);
        return i;
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
            case R.id.text_btn:
                if (detonators.size() == 0) {
                    Toast.makeText(this, "未录入数据", Toast.LENGTH_SHORT);
                    return;
                }
                DBManager.getInstance().getDetonatorEntityDao().saveInTx(detonators);
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

    @Override
    public void onItemClick(View view, int position) {
        // 点击条目
        shouPopuWindow(view, position);
    }

    @Override
    public void onDelayTimeClick(int position) {
        // 点击修改 延时
        DetonatorEntity detonatorEntity = detonators.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_view, null, false);
        EditText changeDelayTime = view.findViewById(R.id.changeDelayTime);
        changeDelayTime.setText(detonatorEntity.getRelay());
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nowDelayTime = changeDelayTime.getText().toString().trim();
                detonatorEntity.setRelay(nowDelayTime);
                projectDetailAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void shouPopuWindow(View view, int position) {
        View popuView = getLayoutInflater().inflate(R.layout.popuwindow_view, null, false);
        PopupWindow popupWindow = new PopupWindow(popuView, 200, 200);
        popuView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除条目
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                deleteItemView(position);
            }
        });
        popuView.findViewById(R.id.insert_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 插入
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                insertItemView(position);
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view, 200, -10, Gravity.RIGHT);
    }

    // 插入数据 TODO
    private void insertItemView(int position) {
        DetonatorEntity detonatorEntity = detonators.get(position);
        DetonatorEntity detonatorEntity1 = new DetonatorEntity();
        detonatorEntity1.setRelay(detonatorEntity.getRelay());
        detonatorEntity1.setHolePosition(detonatorEntity.getHolePosition());
        detonatorEntity1.setUid("1111");
        detonators.add(position, detonatorEntity1);
        projectDetailAdapter.notifyDataSetChanged();
    }

    // 删除条目
    private void deleteItemView(int position) {
        detonators.remove(position);
        projectDetailAdapter.notifyDataSetChanged();
    }
}