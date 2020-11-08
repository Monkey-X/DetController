package com.etek.controller.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.etek.controller.R;
import com.etek.controller.adapter.FunctionTestAdapter;
import com.etek.controller.hardware.command.DetApp;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class FunctionTestActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private String TAG = "FunctionTestActivity";
    private View layout;
    private TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_test);
        initSupportActionBar(R.string.title_function_test);

        initView();

        RecyclerView listTest = findViewById(R.id.list_test);

        List<String> stringList = getStringList();

        FunctionTestAdapter functionTestAdapter = new FunctionTestAdapter(R.layout.item_function_test, stringList);
        listTest.setLayoutManager(new LinearLayoutManager(this));
        listTest.setAdapter(functionTestAdapter);
        functionTestAdapter.setOnItemClickListener(this);
    }

    private void initView() {
        layout = findViewById(R.id.layout);
        text1 = findViewById(R.id.text1);
    }

    private List<String> getStringList() {
        List<String> datas = new ArrayList<>();
        datas.add("关闭总线电源");
        datas.add("打开总线电源并高压使能");
        datas.add("模组休眠");
        datas.add("模组唤醒");
        datas.add("模组充电");
        datas.add("模组放电");
        datas.add("获取模组总线电压和电流");
        return datas;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position) {
            case 0:
                // 关闭总线电源
                int i = DetApp.getInstance().MainBoardBusPowerOff();
                Log.d(TAG, "MainBoardBusPowerOff result: " + i);
                ToastUtils.show(this, "关闭总线电源 " + i);
                break;
            case 1:
                // 打开总线电源并高压使能
                int i1 = DetApp.getInstance().MainBoardHVEnable();
                Log.d(TAG, "MainBoardHVEnable result: " + i1);
                ToastUtils.show(this, "打开总线电源并高压使能 " + i1);
                break;
            case 2:
                // 模组休眠
                int i2 = DetApp.getInstance().ModuleSetDormantStatus(0);
                Log.d(TAG, "ModuleSetDormantStatus result: " + i2);
                ToastUtils.show(this, "模组休眠 " + i2);
                break;
            case 3:
                // 模组唤醒
                int i3 = DetApp.getInstance().ModuleSetWakeupStatus(0);
                Log.d(TAG, "ModuleSetWakeupStatus result: " + i3);
                ToastUtils.show(this, "模组唤醒 " + i3);
                break;
            case 4:
                // 模组充电
                int i4 = DetApp.getInstance().ModuleCapacitorCharge(0, true);
                Log.d(TAG, "ModuleCapacitorCharge true result: " + i4);
                ToastUtils.show(this, "模组充电 " + i4);
                break;
            case 5:
                // 模组放电
                int i5 = DetApp.getInstance().ModuleCapacitorCharge(0, false);
                Log.d(TAG, "ModuleCapacitorCharge false result: " + i5);
                ToastUtils.show(this, "模组放电 " + i5);
                break;
            case 6:
                // 获取模组总线电压和电流
                StringBuilder strData = new StringBuilder();
                int ret = DetApp.getInstance().MainBoardGetCurrentVoltage(strData);
                if (ret != 0) {
                    Log.d(TAG, "获取电压电流 失败 " + ret);
                    ToastUtils.show(this, "获取电压电流 失败 " + ret);
                } else {
                    String str0 = strData.toString();

                    float fv = (float) (Integer.parseInt(str0.substring(0, 8), 16) * 1.00);
                    float fc = (float) (Integer.parseInt(str0.substring(8, 16), 16) * 1.00);
                    Log.d(TAG, String.format("电压：%.2fmV\t电流：%,2fmA", fv, fc));
                    layout.setVisibility(View.VISIBLE);
                    text1.setText(String.format("电压：%.2fmV\t电流：%,2fmA", fv, fc));
                }
                break;

        }

    }
}