package com.etek.controller.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.etek.controller.R;
import com.etek.controller.adapter.ProjectDelayAdapter;
import com.etek.controller.adapter.ProjectDetailAdapter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 延时下载
 */
public class DelayDownloadActivity extends BaseActivity implements View.OnClickListener, ProjectDelayAdapter.OnItemClickListener {

    private RecyclerView mDelayList;
    private List<DetonatorEntity> detonators;
    private ProjectDelayAdapter mProjectDelayAdapter;
    private List<ProjectInfoEntity> projectInfoEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay_download);
        initView();
        initRecycleView();
        initProject();
    }

    private void initView() {
        View backImag = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        backImag.setOnClickListener(this);
        textTitle.setText(R.string.activity_delay_download);
        textBtn.setText("项目列表");

        TextView projectSave = findViewById(R.id.project_save);

        textBtn.setOnClickListener(this);
        projectSave.setOnClickListener(this);

        mDelayList = findViewById(R.id.delayList);
    }

    private void initRecycleView() {
        detonators = new ArrayList<>();

        mDelayList.setLayoutManager(new LinearLayoutManager(this));
        mProjectDelayAdapter = new ProjectDelayAdapter(this, detonators);
        mDelayList.setAdapter(mProjectDelayAdapter);
        mProjectDelayAdapter.setOnItemClickListener(this);
    }


    private void initProject() {
       // 获取工程项目列表
        projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.text_btn:
                // 展示项目列表 todo
                if (projectInfoEntities == null || projectInfoEntities.size() == 0) {
                    ToastUtils.show(this, this.getString(R.string.no_filtrate_project));
                } else {
                    showProjectPopuWindow();
                }
                break;
            case R.id.project_save:
                // 保存列表数据 todo

                break;
        }
    }

    // 展示项目列表
    private void showProjectPopuWindow() {

    }

    @Override
    public void onItemClick(View view, int position) {
          // 点击条目
        shouPopuWindow(view, position);
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
        TextView downloadAgain = popuView.findViewById(R.id.insert_item);
        downloadAgain.setText("下载");
        downloadAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 插入
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                downloadItem(position);
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view, 200, -10, Gravity.RIGHT);
    }

    //再次下载 todo
    private void downloadItem(int position) {

    }

    // 删除条目
    private void deleteItemView(int position) {
        detonators.remove(position);
        mProjectDelayAdapter.notifyDataSetChanged();
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
                mProjectDelayAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}