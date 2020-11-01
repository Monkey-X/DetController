package com.etek.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.etek.controller.scan.ScannerInterface;
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
    private List<DetonatorEntity> mDetonatorEntities;
    private ScannerInterface scanner;

    //*******重要
    private static final String RES_ACTION = "android.intent.action.SCANRESULT";
    private ScannerResultReceiver scanReceiver;

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

    @Override
    protected void onResume() {
        super.onResume();
        initScanner();
    }

    private void initScanner() {
        scanner = new ScannerInterface(this);
        scanner.setOutputMode(1);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RES_ACTION);

        //注册广播接受者
        scanReceiver = new ScannerResultReceiver();
        registerReceiver(scanReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消接收扫描广播，并恢复输出模式为默认

        if (scanReceiver != null){
            unregisterReceiver(scanReceiver);
        }

        if (scanner != null){
            scanner.setOutputMode(0);
        }
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
        mDetonatorEntities = DBManager.getInstance().getDetonatorEntityDao()._queryProjectInfoEntity_DetonatorList(projectId);

        if (mDetonatorEntities != null && mDetonatorEntities.size() != 0) {
            detonators.addAll(mDetonatorEntities);
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
            if (detonators.size() != 0) {
                lastDelay = lastDelay + delayholeinTime;
            }
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
                DBManager.getInstance().getDetonatorEntityDao().save(detonatorEntity);
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

        DetonatorEntity detonatorEntity = detonators.get(position);
        if (mDetonatorEntities !=null) {
            boolean contains = mDetonatorEntities.contains(detonatorEntity);
            if (contains) {
                DBManager.getInstance().getDetonatorEntityDao().delete(detonatorEntity);
            }
        }
        detonators.remove(position);
        projectDetailAdapter.notifyDataSetChanged();
    }


    /**
     * 扫描结果广播接收
     */
    //*********重要
    private class ScannerResultReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.d("111","intent.getAction()-->"+intent.getAction());//

            //*******重要，注意Extral为"value"
            final String scanResult = intent.getStringExtra("value");

            //*******重要
            if (intent.getAction().equals(RES_ACTION)){
                //获取扫描结果
                if(scanResult.length()>0){ //如果条码长度>0，解码成功。如果条码长度等于0解码失败。
//                    tvScanResult.append("Barcode："+scanResult+"\n");

//                    int offset=tvScanResult.getLineCount()*tvScanResult.getLineHeight();
//                    if(offset>tvScanResult.getHeight()){
//                        tvScanResult.scrollTo(0,offset-tvScanResult.getHeight());
//                    }
                }else{
                    /**扫描失败提示使用有两个条件：
                     1，需要先将扫描失败提示接口打开只能在广播模式下使用，其他模式无法调用。
                     2，通过判断条码长度来判定是否解码成功，当长度等于0时表示解码失败。
                     * */
                    Toast.makeText(getApplicationContext(), "解码失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}