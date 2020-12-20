package com.etek.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.etek.controller.R;
import com.etek.controller.adapter.ProjectDetailAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.entity.DetDelayBean;
import com.etek.controller.fragment.DelaySettingDialog;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.PowerCheckCallBack;
import com.etek.controller.hardware.util.DataConverter;
import com.etek.controller.hardware.util.DetIDConverter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.scan.ScannerInterface;
import com.etek.sommerlibrary.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷管组网界面
 */
public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener, ProjectDetailAdapter.OnItemClickListener, DelaySettingDialog.OnDelaySettingListener {

    private static final String TAG = "ProjectDetailActivity";
    private RecyclerView recycleView;
    private List<ProjectDetonator> detonators;
    private long projectId;
    private ProjectDetailAdapter projectDetailAdapter;
    private LinearLayout rootView;
    private List<ProjectDetonator> mDetonatorEntities;
    private ScannerInterface scanner;

    //*******重要
    private static final String RES_ACTION = "android.intent.action.SCANRESULT";
    private ScannerResultReceiver scanReceiver;
    private boolean isInsertItem = false;
    private int insertPosition;
    private DetDelayBean detDelayBean;
    private int scanType;
    private boolean isScan = false;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        initProjectID();
        initDelaySetting();
        initView();
        initRecycleView();
        initIntentData();
    }

    private void initDelaySetting() {
        String delaySetting = getDelaySetting(AppIntentString.DELAY_SETTING);
        if (!TextUtils.isEmpty(delaySetting)) {
            DetDelayBean detDelayBean = JSON.parseObject(delaySetting, DetDelayBean.class);
            if (detDelayBean != null) {
                this.detDelayBean = detDelayBean;
            }
        }
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
        mDetonatorEntities = DBManager.getInstance().getProjectDetonatorDao()._queryPendingProject_DetonatorList(projectId);
        if (mDetonatorEntities != null && mDetonatorEntities.size() != 0) {
            detonators.addAll(mDetonatorEntities);
            projectDetailAdapter.notifyDataSetChanged();
        } else {
            if (detDelayBean == null) {
                showDelaySettingDialog();
            }
        }
    }

    private void initView() {
        View backImag = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        backImag.setOnClickListener(this);
        textTitle.setText("雷管组网");
        textBtn.setText("保存工程");
        textBtn.setVisibility(View.INVISIBLE);

        textBtn.setOnClickListener(this);

        // TODO: 2020/12/17
        View delayText = findViewById(R.id.delay_edit);
        View projectHandle = findViewById(R.id.project_handle);
        delayText.setOnClickListener(this);
        projectHandle.setOnClickListener(this);


        rootView = findViewById(R.id.rootview);

        // 正式布局
        // 起始延时

        recycleView = findViewById(R.id.recycleView);

        // 孔内
        View layoutHoleIn = findViewById(R.id.hole_in);
        // 孔间
        View layoutHoleOut = findViewById(R.id.hole_out);
        View layoutHoleNoChange = findViewById(R.id.hole_nochange);

        layoutHoleNoChange.setOnClickListener(this);
        layoutHoleIn.setOnClickListener(this);
        layoutHoleOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.text_btn:
                break;
            case R.id.hole_in:
                // 设置孔内延时
                ReadDetNumTask readDetNumTask = new ReadDetNumTask(AppIntentString.TYPE_HOLE_IN);
                readDetNumTask.execute();
                break;
            case R.id.hole_out:
                // 设置孔间延时
                ReadDetNumTask readDetNumTask1 = new ReadDetNumTask(AppIntentString.TYPE_HOLE_OUT);
                readDetNumTask1.execute();
                break;
            case R.id.hole_nochange:
                // 延时不变
                ReadDetNumTask readDetNumTask2 = new ReadDetNumTask(AppIntentString.TYPE_HOLE_NO_CHANGE);
                readDetNumTask2.execute();
                break;
            case R.id.delay_edit:
                // 弹出修改延时的对话框
                showDelaySettingDialog();
                break;
            case R.id.project_handle:
                // 跳转操作界面，连接检测，延时下载,检查授权
                if (detonators.size() == 0) {
                    showStatusDialog("请先进行雷管组网！");
                    return;
                }
                Intent intent = new Intent(this, ProjectImplementActivity.class);
                intent.putExtra(AppIntentString.PROJECT_ID, projectId);
                startActivity(intent);
                break;
        }
    }

    private void showDelaySettingDialog() {
        DelaySettingDialog delaySettingDialog = new DelaySettingDialog();
        delaySettingDialog.setOnDelaySettingListener(this);
        delaySettingDialog.show(getSupportFragmentManager(), "delaySettingDialog");
    }

    /**
     * 设置孔间延时
     */
    private void setHoleOutTime() {
//        if (detonators != null && detonators.size() == 0) {
//            return;
//        }
//
//        int lastPosition = detonators.size() - 1;
//        ProjectDetonator detonatorEntity = detonators.get(lastPosition);
//        if (!TextUtils.isEmpty(detonatorEntity.getRelay())) {
//            return;
//        }
//        if (detonators.size() >= 2) {
//            ProjectDetonator detonatorEntity1 = detonators.get(lastPosition - 1);
//            String lastDelayTime = detonatorEntity1.getRelay();
//            String holePosition = detonatorEntity1.getHolePosition();
//            String[] split = holePosition.split("-");
//            int intFormString = getIntFormString(split[0]);
//            int delayholeoutTime = getIntFormString(delayholeout.getText().toString().trim());
//            int newDelayTime = getIntFormString(lastDelayTime) + delayholeoutTime;
//            detonatorEntity.setRelay(String.valueOf(newDelayTime));
//            detonatorEntity.setHolePosition(String.valueOf(intFormString + 1) + "-" + 1);
//            DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
//            projectDetailAdapter.notifyDataSetChanged();
//        }
    }

    /**
     * 设置孔内延时时间
     */
    private void setHoleInTime() {
//        if (detonators != null && detonators.size() == 0) {
//            return;
//        }
//
//        int lastPosition = detonators.size() - 1;
//        ProjectDetonator detonatorEntity = detonators.get(lastPosition);
//        if (!TextUtils.isEmpty(detonatorEntity.getRelay())) {
//            return;
//        }
//
//        if (detonators.size() == 1) {
//            int startDelayTime = getIntFormString(delayTimeNew.getText().toString().trim());
//            detonatorEntity.setRelay(String.valueOf(startDelayTime));
//            detonatorEntity.setHolePosition(1 + "-" + 1);
//            projectDetailAdapter.notifyDataSetChanged();
//            return;
//        }
//
//        ProjectDetonator detonatorEntity1 = detonators.get(lastPosition - 1);
//        String lastDelayTime = detonatorEntity1.getRelay();
//
//        String holePosition = detonatorEntity1.getHolePosition();
//        String[] split = holePosition.split("-");
//        int intFormString = getIntFormString(split[1]);
//        int first = getIntFormString(split[0]);
//
//        int delayholeinTime = getIntFormString(delayholein.getText().toString().trim());
//        int newDelayTime = getIntFormString(lastDelayTime) + delayholeinTime;
//        detonatorEntity.setRelay(String.valueOf(newDelayTime));
//        detonatorEntity.setHolePosition(first + "-" + String.valueOf(intFormString + 1));
//        DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
//        projectDetailAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(View view, int position) {
        // 点击条目
        shouPopuWindow(view, position);
    }

    @Override
    public void onDelayTimeClick(int position) {
        // 点击修改 延时
        ProjectDetonator detonatorEntity = detonators.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_view, null, false);
        EditText changeDelayTime = view.findViewById(R.id.changeDelayTime);
        changeDelayTime.setText(detonatorEntity.getRelay() + "");
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
                detonatorEntity.setRelay(Integer.parseInt(nowDelayTime));
                DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
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

        ProjectDetonator detonatorEntity = detonators.get(position);
        try {
            DBManager.getInstance().getProjectDetonatorDao().delete(detonatorEntity);
        } catch (Exception e) {
        }
        detonators.remove(position);
        projectDetailAdapter.notifyDataSetChanged();
    }


    @Override
    public void setDelayTime(DetDelayBean bean) {
        this.detDelayBean = bean;
        setDelaySetting(AppIntentString.DELAY_SETTING, JSON.toJSONString(bean));
        if (!detonators.isEmpty()) {
            changeDetDelay();
        }

    }

    // 重新批量设置雷管的延时
    private void changeDetDelay() {
        for (int i = 0; i < detonators.size(); i++) {
            if (i == 0) {
                ProjectDetonator projectDetonator = detonators.get(i);
                projectDetonator.setRelay(detDelayBean.getStartTime());
            } else {
                ProjectDetonator lastProjectDet = detonators.get(i - 1);
                ProjectDetonator projectDetonator = detonators.get(i);
                String lastHolePosition = lastProjectDet.getHolePosition();
                String holePosition = projectDetonator.getHolePosition();
                int newDelayTime = 0;
                if (isHoleIn(lastHolePosition, holePosition)) {
                    newDelayTime = lastProjectDet.getRelay() + detDelayBean.getHoleInTime();
                } else {
                    newDelayTime = lastProjectDet.getRelay() + detDelayBean.getHoleOutTime();
                }
                projectDetonator.setRelay(newDelayTime);
            }
        }
        DBManager.getInstance().getProjectDetonatorDao().saveInTx(detonators);
        projectDetailAdapter.notifyDataSetChanged();
    }

    // 区分孔内还是孔间
    private boolean isHoleIn(String lastHolePosition, String holePosition) {
        String[] lastSplit = lastHolePosition.split("-");
        String[] split = holePosition.split("-");
        if (lastSplit[0].equals(split[0])) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void makeCancel() {
        if (this.detDelayBean == null) {
            // 没有设置延时，提示设置延时
            showDelaySettingDialog();
            showStatusDialog("请配置延时！");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: keyCode = " + keyCode);
        // 左边189 右边190  中间188
            if (keyCode == 189 && event.getAction() == KeyEvent.ACTION_DOWN) {
                scanType = AppIntentString.TYPE_HOLE_OUT;
                return true;
            }
            // 中间按钮
            if (keyCode == 188 && event.getAction() == KeyEvent.ACTION_DOWN) {
                scanType = AppIntentString.TYPE_HOLE_NO_CHANGE;
                return true;
            }
            // 右边按钮
            if (keyCode == 190 && event.getAction() == KeyEvent.ACTION_DOWN) {
                scanType = AppIntentString.TYPE_HOLE_IN;
                return true;
            }
        Log.d(TAG, "onKeyDown: scanType = " + scanType);
        return super.onKeyUp(keyCode, event);
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
                    showAutoMissDialog("扫描失败！");
                    isInsertItem = false;
                }
            }
        }
    }

    private void createDetData(String strgm) {
        // 检查重复的雷管
        if (checkTheSameDet(strgm)) {
            isInsertItem = false;
            return;
        }


        // 扫描插入
        if (isInsertItem) {
            isInsertItem = false;
            ProjectDetonator detonatorEntity = detonators.get(insertPosition);
            ProjectDetonator detonatorEntity1 = new ProjectDetonator();
            detonatorEntity1.setRelay(detonatorEntity.getRelay());
            detonatorEntity1.setHolePosition(detonatorEntity.getHolePosition());
            detonatorEntity1.setCode(strgm);
            detonatorEntity1.setProjectInfoId(projectId);
            String detId = getDetIdByGm(strgm);
            detonatorEntity1.setDetId(detId);
            detonatorEntity1.setUid(getDetUid(detId));
            DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity1);
            detonators.add(insertPosition, detonatorEntity1);
            projectDetailAdapter.notifyDataSetChanged();
            return;
        }
        createProjectDetData(strgm, scanType);
//        ProjectDetonator detonatorEntity = new ProjectDetonator();
//        detonatorEntity.setProjectInfoId(projectId);
//        detonatorEntity.setCode(strgm);
//        detonatorEntity.setDetId(getDetIdByGm(strgm));
//        DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
//        detonators.add(detonatorEntity);
//        projectDetailAdapter.notifyDataSetChanged();
    }


    public void showAutoMissDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog !=null) {
                    alertDialog.dismiss();
                }
            }
        },1000);
    }

    /**
     * 判断是否有相同的雷管
     * @param strgm
     * @return
     */
    private boolean checkTheSameDet(String strgm) {
        if (detonators != null && detonators.size() != 0) {
            for (int i = 0; i < detonators.size(); i++) {
                ProjectDetonator detonatorEntity = detonators.get(i);
                if (detonatorEntity.getCode().equals(strgm)) {
                    showAutoMissDialog("此雷管已扫描！");
                    recycleView.scrollToPosition(i);
                    return true;
                }
            }
        }
        return false;
    }

    // 将雷管管吗转化为雷管Id
    public String getDetIdByGm(String gm) {
        byte[] bytes = DetIDConverter.GetDCByString(gm);
        byte[] bytes1 = DetIDConverter.Conv_DC2ID(bytes);
        int detId = DataConverter.lsbBytes2Int(bytes1);
        return String.valueOf(detId);
    }

    /**
     * 连接获取雷管信息
     */
    public class ReadDetNumTask extends AsyncTask<String, Integer, String> {

        private final int type;

        public ReadDetNumTask(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("读取中...");
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder detNum = new StringBuilder();
            StringBuilder detId = new StringBuilder();
            int result = DetApp.getInstance().DetsGetIDAndDC(detId, detNum, new PowerCheckCallBack() {
                @Override
                public void DisplayText(String strText) {
                    Log.d(TAG, "DisplayText: "+strText);
                }

                @Override
                public void SetProgressbarValue(int nVal) {
                    Log.d(TAG, "SetProgressbarValue: "+nVal);
                }
            });
            Log.d(TAG, "doInBackground: detNum = "+detNum.toString());
            Log.d(TAG, "doInBackground: detId = "+detId.toString());
            if (result == 0) {
                return detNum.toString();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            missProDialog();
            if (TextUtils.isEmpty(result)) {
                showAutoMissDialog("获取雷管码失败！");
            } else {
                createProjectDetData(result, type);
            }
        }
    }

    // 根据孔内或者孔间设置延时
    private void createProjectDetData(String detCode, int type) {
        if (checkTheSameDet(detCode)) {
            return;
        }
        Log.d(TAG, "createProjectDetData: type = " + type);
        ProjectDetonator projectDetonator = new ProjectDetonator();
        projectDetonator.setProjectInfoId(projectId);
        projectDetonator.setCode(detCode);
        String detId = getDetIdByGm(detCode);
        projectDetonator.setDetId(detId);
        projectDetonator.setUid(getDetUid(detId));
        if (detonators.size() == 0) {
            projectDetonator.setHolePosition("1-1");
            projectDetonator.setRelay(detDelayBean.getStartTime());
        } else {
            ProjectDetonator projectDetonatorLast = detonators.get(detonators.size() - 1);
            int lastDelayTime = projectDetonatorLast.getRelay();
            String lastHolePosition = projectDetonatorLast.getHolePosition();
            int nextDelayTime = getNextDelayTime(lastDelayTime, type);
            String nextHolePosition = getNextHolePosition(lastHolePosition, type);
            projectDetonator.setHolePosition(nextHolePosition);
            projectDetonator.setRelay(nextDelayTime);
        }
        DBManager.getInstance().getProjectDetonatorDao().save(projectDetonator);
        detonators.add(projectDetonator);
        projectDetailAdapter.notifyDataSetChanged();
        recycleView.scrollToPosition(detonators.size() - 1);
    }

    // 根据上个的空位号获取下一个的空位号
    private String getNextHolePosition(String lastHolePostion, int type) {
        String nextHolePostion = "";
        String[] split = lastHolePostion.split("-");

        switch (type) {
            case AppIntentString.TYPE_HOLE_IN:
            case AppIntentString.TYPE_HOLE_NO_CHANGE:
                nextHolePostion = split[0] + "-" + (Integer.parseInt(split[1]) + 1);
                break;
            case AppIntentString.TYPE_HOLE_OUT:
                nextHolePostion = (Integer.parseInt(split[0]) + 1) + "-1";
                break;
        }
        return nextHolePostion;

    }

    // 根据类型获取新的延时
    private int getNextDelayTime(int delayTime, int type) {

        int nextDelayTime = 0;
        switch (type) {
            case AppIntentString.TYPE_HOLE_IN:
                nextDelayTime = delayTime + detDelayBean.getHoleInTime();
                break;
            case AppIntentString.TYPE_HOLE_OUT:
                nextDelayTime = delayTime + detDelayBean.getHoleOutTime();
                break;
            case AppIntentString.TYPE_HOLE_NO_CHANGE:
                nextDelayTime = delayTime;
                break;
        }
        return nextDelayTime;

    }


    // 雷管ID 获取uid
    private String getDetUid(String detId) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = DetApp.getInstance().ModuleGetUID(Integer.parseInt(detId), stringBuilder);
        Log.d(TAG, "getDetUid: "+stringBuilder.toString());
        return stringBuilder.toString();
    }


}