package com.etek.controller.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.etek.controller.R;
import com.etek.controller.activity.project.LineCheckActivity;
import com.etek.controller.activity.project.SingleCheckActivity;
import com.etek.controller.hardware.command.DetApp;
import com.etek.sommerlibrary.activity.BaseActivity;

/**
 * 辅助功能
 */
public class AssistActivity extends BaseActivity implements View.OnClickListener {

//    private RecyclerView mRecyclerView;
//    private ArrayList<AssistItem> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assist);
        initSupportActionBar(R.string.title_act_assist_function);
        initView();
        initDate();
        initAdapter();
    }


    /**
     * 初始化View
     */
    private void initView() {
        View view = findViewById(R.id.single_det_check);
        View lineCheck = findViewById(R.id.project_power_bomb);
        view.setOnClickListener(this);
        lineCheck.setOnClickListener(this);
//        mRecyclerView = findViewById(R.id.rv_list);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    /**
     * 页面要展示的数据
     */
    private void initDate() {
//        mDataList = new ArrayList<>();
//        mDataList.add(new AssistItem(this.getString(R.string.title_act_single_check), R.mipmap.jiance));
//        mDataList.add(new AssistItem(this.getString(R.string.title_act_line_check), R.mipmap.zongxian));
////        mDataList.add(new AssistItem(this.getString(R.string.title_main_board_update), R.mipmap.zhuban));
//        mDataList.add(new AssistItem(getString(R.string.title_function_test), R.mipmap.ceshi));

    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
//        AssistAdapter assistAdapter = new AssistAdapter(R.layout.home_item_view, mDataList);
//        assistAdapter.openLoadAnimation();
//        assistAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                switch (position) {
//                    case 0: //跳转单线检测页面
//                        Intent singleIntent = new Intent(AssistActivity.this, SingleCheckActivity.class);
//                        startActivity(singleIntent);
//                        break;
//
//                    case 1: //跳转线路检测页面
//                        Intent lineIntent = new Intent(AssistActivity.this, LineCheckActivity.class);
//                        startActivity(lineIntent);
//                        break;
//                    case 2: //功能测试
//                        Intent testIntent = new Intent(AssistActivity.this, FunctionTestActivity.class);
//                        startActivity(testIntent);
//                        break;
//                }
//            }
//        });
//        mRecyclerView.setAdapter(assistAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_det_check:
                Intent singleIntent = new Intent(AssistActivity.this, SingleCheckActivity.class);
                startActivity(singleIntent);
                break;
            case R.id.project_power_bomb:
                Intent lineIntent = new Intent(AssistActivity.this, LineCheckActivity.class);
                startActivity(lineIntent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 必须总线下电
        DetApp.getInstance().MainBoardBusPowerOff();
    }
}