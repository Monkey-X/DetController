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
import android.view.KeyEvent;
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
import com.etek.controller.hardware.util.DataConverter;
import com.etek.controller.hardware.util.DetIDConverter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.scan.ScannerInterface;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

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
    private boolean isInsertItem = false;
    private int insertPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        initProjectID();
        initView();
        initRecycleView();
        initIntentData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initScanner();
    }

    private void initScanner() {
        scanner = new ScannerInterface(this);
        scanner.setOutputMode(1);
        scanner.lockScanKey();

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

        if (scanReceiver != null) {
            unregisterReceiver(scanReceiver);
        }

        if (scanner != null) {
            scanner.unlockScanKey();
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

        // 孔内
        View layoutHoleIn = findViewById(R.id.layoutHoleIn);
        // 孔间
        View layoutHoleOut = findViewById(R.id.layoutHoleOut);

        reduce.setOnClickListener(this);
        add.setOnClickListener(this);
        layoutHoleIn.setOnClickListener(this);
        layoutHoleOut.setOnClickListener(this);
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
                ToastUtils.show(this, "保存成功！");
                break;
            case R.id.layoutHoleIn:
                // 设置孔内延时
                setHoleInTime();
                break;
            case R.id.layoutHoleOut:
                // 设置孔间延时
                setHoleOutTime();
                break;
        }
    }

    /**
     * 设置孔间延时
     */
    private void setHoleOutTime() {
        if (detonators != null && detonators.size() == 0) {
            return;
        }

        int lastPosition = detonators.size() - 1;
        DetonatorEntity detonatorEntity = detonators.get(lastPosition);
        if (!TextUtils.isEmpty(detonatorEntity.getRelay())) {
            return;
        }
        if (detonators.size() >= 2) {
            DetonatorEntity detonatorEntity1 = detonators.get(lastPosition - 1);
            String lastDelayTime = detonatorEntity1.getRelay();
            String holePosition = detonatorEntity1.getHolePosition();
            String[] split = holePosition.split("-");
            int intFormString = getIntFormString(split[0]);
            int delayholeoutTime = getIntFormString(delayholeout.getText().toString().trim());
            int newDelayTime = getIntFormString(lastDelayTime) + delayholeoutTime;
            detonatorEntity.setRelay(String.valueOf(newDelayTime));
            detonatorEntity.setHolePosition(String.valueOf(intFormString + 1) + "-" + 1);
            projectDetailAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置孔内延时时间
     */
    private void setHoleInTime() {
        if (detonators != null && detonators.size() == 0) {
            return;
        }

        int lastPosition = detonators.size() - 1;
        DetonatorEntity detonatorEntity = detonators.get(lastPosition);
        if (!TextUtils.isEmpty(detonatorEntity.getRelay())) {
            return;
        }

        if (detonators.size() == 1) {
            int startDelayTime = getIntFormString(delayTimeNew.getText().toString().trim());
//            int area = getIntFormString(areaNum.getText().toString().trim());
            detonatorEntity.setRelay(String.valueOf(startDelayTime));
            detonatorEntity.setHolePosition(1 + "-" + 1);
            projectDetailAdapter.notifyDataSetChanged();
            return;
        }

        DetonatorEntity detonatorEntity1 = detonators.get(lastPosition - 1);
        String lastDelayTime = detonatorEntity1.getRelay();

        String holePosition = detonatorEntity1.getHolePosition();
        String[] split = holePosition.split("-");
        int intFormString = getIntFormString(split[1]);
        int first = getIntFormString(split[0]);

        int delayholeinTime = getIntFormString(delayholein.getText().toString().trim());
        int newDelayTime = getIntFormString(lastDelayTime) + delayholeinTime;
        detonatorEntity.setRelay(String.valueOf(newDelayTime));
        detonatorEntity.setHolePosition(first + "-" + String.valueOf(intFormString + 1));
        projectDetailAdapter.notifyDataSetChanged();
    }


    private void initRecycleView() {
        detonators = new ArrayList<>();

        recycleView.setLayoutManager(new LinearLayoutManager(this));
        projectDetailAdapter = new ProjectDetailAdapter(this, detonators);
        recycleView.setAdapter(projectDetailAdapter);
        projectDetailAdapter.setOnItemClickListener(this);
    }


    //  数字型的字符串转为数字
    public int getIntFormString(String stringNum) {
        int i = Integer.parseInt(stringNum);
        return i;
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
        PopupWindow popupWindow = new PopupWindow(popuView, 150, 120);
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

    // 插入数据  怎么扫码插入 todo
    private void insertItemView(int position) {
        isInsertItem = true;
        insertPosition = position;
        showProDialog("请扫描雷管信息");
    }

    // 删除条目
    private void deleteItemView(int position) {

        DetonatorEntity detonatorEntity = detonators.get(position);
        try {
            DBManager.getInstance().getDetonatorEntityDao().delete(detonatorEntity);
        } catch (Exception e) {
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
            Log.d("111", "intent.getAction()-->" + intent.getAction());//

            //*******重要，注意Extral为"value"
            final String scanResult = intent.getStringExtra("value");
            if (isInsertItem) {
                missProDialog();
            }

            Log.d(TAG, "onReceive: scanResult = " + scanResult);

            //*******重要
            if (intent.getAction().equals(RES_ACTION)) {
                //获取扫描结果
                if (scanResult.length() > 0 && DetIDConverter.VerifyQRCheckValue(scanResult)) { //如果条码长度>0，解码成功。如果条码长度等于0解码失败。
                    // 扫描成功
                    String strgm = scanResult.substring(0, 13);
                    createDetData(strgm);
                } else {
                    // 扫描失败
                    showStatusDialog("扫描失败！");
                    isInsertItem = false;
                }
            }
        }
    }

    private void createDetData(String strgm) {
        // 检查重复的雷管
        if (detonators != null && detonators.size() != 0) {
            for (int i = 0; i < detonators.size(); i++) {
                DetonatorEntity detonatorEntity = detonators.get(i);
                if (detonatorEntity.getCode().equals(strgm)) {
                    showStatusDialog("此雷管已扫描！");
                    recycleView.scrollToPosition(i);
                    return;
                }
            }
        }

        // 扫描插入
        if (isInsertItem) {
            isInsertItem = false;
            DetonatorEntity detonatorEntity = detonators.get(insertPosition);
            DetonatorEntity detonatorEntity1 = new DetonatorEntity();
            detonatorEntity1.setRelay(detonatorEntity.getRelay());
            detonatorEntity1.setHolePosition(detonatorEntity.getHolePosition());
            detonatorEntity1.setCode(strgm);
            detonatorEntity1.setDetId(getDetIdByGm(strgm));
            detonatorEntity1.setProjectInfoId(projectId);
            detonators.add(insertPosition, detonatorEntity1);
            projectDetailAdapter.notifyDataSetChanged();
            return;
        }
        if (detonators.size() != 0) {
            DetonatorEntity detonatorEntity = detonators.get(detonators.size() - 1);
            if (TextUtils.isEmpty(detonatorEntity.getRelay())) {
                showStatusDialog("请设置录入雷管的延时后，再继续扫描！");
                return;
            }
        }
        DetonatorEntity detonatorEntity = new DetonatorEntity();
        detonatorEntity.setProjectInfoId(projectId);
        detonatorEntity.setCode(strgm);
        detonatorEntity.setDetId(getDetIdByGm(strgm));
        detonators.add(detonatorEntity);
        projectDetailAdapter.notifyDataSetChanged();
    }

    // 将雷管管吗转化为雷管Id
    public String getDetIdByGm(String gm) {
        byte[] bytes = DetIDConverter.GetDCByString(gm);
        byte[] bytes1 = DetIDConverter.Conv_DC2ID(bytes);
        int detId = DataConverter.bytes2Int(bytes1);
        return String.valueOf(detId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(TAG, "onKeyUp: keyCode = " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            // 孔内
            setHoleInTime();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            // 孔间
            setHoleOutTime();
            return true;
        }
        return super.onKeyUp(keyCode, event);
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
        detonatorEntity.setUid(projectId + "-" + detonatorEntity.getHolePosition() + detonatorEntity.getRelay());
        detonators.add(detonatorEntity);
    }
}