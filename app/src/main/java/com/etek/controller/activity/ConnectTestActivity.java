package com.etek.controller.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.adapter.ConnectTestAdapter;
import com.etek.controller.entity.ConnectTestItem;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 连接检测
 */
public class ConnectTestActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout backImag;
    private TextView textTitle;
    private TextView textBtn;
    private RecyclerView recycleView;
    private ConnectTestAdapter connectTestAdapter;
    private List<ConnectTestItem> connectData;
    private PopupWindow popWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_test);
        initDate();
        initView();
    }

    /**
     * 页面展示的数据
     */
    private void initDate() {
        //暂时模拟数据，有的字段本地数据库没有
        connectData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            connectData.add(new ConnectTestItem(i + 1, "6170725D0206" + i, "1-" + (i + 1), "失败"));
        }
    }

    /**
     * 初始化ViewR.id.
     */
    private void initView() {
        backImag = findViewById(R.id.back_img);
        backImag.setOnClickListener(this);
        textTitle = findViewById(R.id.text_title);
        textTitle.setText(R.string.title_act_connect_state);
        textBtn = findViewById(R.id.text_btn);
        textBtn.setText(R.string.connect_filtrate);
        textBtn.setOnClickListener(this);
        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        connectTestAdapter = new ConnectTestAdapter(R.layout.connect_test_item, connectData);
        recycleView.setAdapter(connectTestAdapter);
        connectTestAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e("initView", "索引： " + position);
                Toast.makeText(ConnectTestActivity.this, "点击了第" + (position + 1) + "条目", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 筛选框
     */
    private void showPopWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.filtrate_popup_window, null);
        popWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popWindow.setContentView(contentView);
        WindowManager.LayoutParams parms = this.getWindow().getAttributes();
        parms.alpha = 0.5f;
        this.getWindow().setAttributes(parms);
        popWindow.showAsDropDown(textBtn,0,25);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgAlpha();
            }
        });
        TextView outContact = contentView.findViewById(R.id.out_contact);
        TextView misconnection = contentView.findViewById(R.id.misconnection);
        outContact.setOnClickListener(this);
        misconnection.setOnClickListener(this);
    }

    /**
     * showPopWindow消失后取消背景色
     */
    private void bgAlpha() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = (float)1.0; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img://返回
                finish();
                break;

            case R.id.text_btn://筛选
                showPopWindow();
                break;

            case R.id.out_contact://失联
                popWindow.dismiss();
                break;

            case R.id.misconnection://误接
                popWindow.dismiss();
                break;
        }
    }
}