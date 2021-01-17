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
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.etek.controller.hardware.util.SoundPoolHelp;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.scan.ScannerInterface;
import com.etek.controller.utils.VibrateUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private int START_TIME_TYPE = 1;
    private int HOLE_IN_TYPE = 2;
    private int HOLE_OUT_TYPE = 3;
    private TextView delayStartTime;
    private AlertDialog delayAlertDialog;
    private TextView holeTimeOut;
    private TextView holeTimeIn;
    private TextView numTypeIn;
    private TextView numTypeOut;

    // 孔位好的正则关系
    private static  final String HOLE_REGEX = "^[0-9]{1,}-[0-9]{1,}";

    // 初始时间的状态
    private boolean isStartTimeChange = false;
    private SoundPoolHelp soundPoolHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        initProjectID();
        initDelaySetting();
        initView();
        initRecycleView();
        initIntentData();
        initSoundPool();
    }

    private void initDelaySetting() {
        String delaySetting = getDelaySetting(AppIntentString.DELAY_SETTING);
        if (!TextUtils.isEmpty(delaySetting)) {
            DetDelayBean detDelayBean = JSON.parseObject(delaySetting, DetDelayBean.class);
            if (detDelayBean != null) {
                this.detDelayBean = detDelayBean;
            }
        }
        if (detDelayBean == null) {
            detDelayBean = new DetDelayBean();
        }
    }

    private void initSoundPool() {
        soundPoolHelp = new SoundPoolHelp(this);
        soundPoolHelp.initSound();
    }

    private void playSound(boolean b) {
        if (soundPoolHelp != null ) {
            soundPoolHelp.playSound(b);
        }
    }


    private void releaseSound() {
        if (soundPoolHelp != null) {
            soundPoolHelp.releaseSound();
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
    protected void onDestroy() {
        releaseSound();
        super.onDestroy();
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
            Collections.sort(mDetonatorEntities);
            detonators.addAll(mDetonatorEntities);
            projectDetailAdapter.notifyDataSetChanged();
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

        View layoutStartTime = findViewById(R.id.delay_edit);
        delayStartTime = findViewById(R.id.delayStartTime);

        delayStartTime.setText(detDelayBean.getStartTime()+"");

        View projectHandle = findViewById(R.id.project_handle);
        layoutStartTime.setOnClickListener(this);
        projectHandle.setOnClickListener(this);


        rootView = findViewById(R.id.rootview);

        // 正式布局
        // 起始延时

        recycleView = findViewById(R.id.recycleView);

        // 孔内
        View layoutHoleIn = findViewById(R.id.hole_in);
        // 孔间
        View layoutHoleOut = findViewById(R.id.hole_out);

//        View layoutHoleNoChange = findViewById(R.id.hole_nochange);
//        layoutHoleNoChange.setOnClickListener(this);
        layoutHoleIn.setOnClickListener(this);
        layoutHoleOut.setOnClickListener(this);

        numTypeIn = findViewById(R.id.numTypeIn);
        numTypeOut = findViewById(R.id.numTypeOut);
        holeTimeIn = findViewById(R.id.holeInTime);
        holeTimeOut = findViewById(R.id.holeOutTime);
        if (detDelayBean.getHoleInTime() >= 0) {
            numTypeIn.setVisibility(View.VISIBLE);
        } else {
            numTypeIn.setVisibility(View.GONE);
        }

        if (detDelayBean.getHoleOutTime() >= 0) {
            numTypeOut.setVisibility(View.VISIBLE);
        } else {
            numTypeOut.setVisibility(View.GONE);
        }
        holeTimeIn.setText(detDelayBean.getHoleInTime()+"");
        holeTimeOut.setText(detDelayBean.getHoleOutTime()+"");

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
//                ReadDetNumTask readDetNumTask = new ReadDetNumTask(AppIntentString.TYPE_HOLE_IN);
//                readDetNumTask.execute();
                setDelayDialog("孔内延时", HOLE_IN_TYPE, holeTimeIn);
                break;
            case R.id.hole_out:
                // 设置孔间延时
//                ReadDetNumTask readDetNumTask1 = new ReadDetNumTask(AppIntentString.TYPE_HOLE_OUT);
//                readDetNumTask1.execute();
                setDelayDialog("孔间延时", HOLE_OUT_TYPE, holeTimeOut);
                break;
//            case R.id.hole_nochange:
//                // 延时不变
//                ReadDetNumTask readDetNumTask2 = new ReadDetNumTask(AppIntentString.TYPE_HOLE_NO_CHANGE);
//                readDetNumTask2.execute();
//                break;
            case R.id.delay_edit:
                // 弹出修改延时的对话框
//                showDelaySettingDialog();
                setDelayDialog("起始延时", START_TIME_TYPE, delayStartTime);
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
//        DelaySettingDialog delaySettingDialog = new DelaySettingDialog();
//        delaySettingDialog.setOnDelaySettingListener(this);
//        delaySettingDialog.show(getSupportFragmentManager(), "delaySettingDialog");
        // TODO: 2020/12/27  设置起始延时

    }

    /**
     * 设置起始时间，孔间延时，孔内延时
     *
     * @param title
     * @param type
     * @param delayText
     */
    private void setDelayDialog(String title, int type, TextView delayText) {
        if (delayAlertDialog != null && delayAlertDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_view, null, false);
        TextView textTitle = view.findViewById(R.id.text_title);
        textTitle.setText(title);
        EditText changeDelayTime = view.findViewById(R.id.changeDelayTime);
        if (type == START_TIME_TYPE) {
            changeDelayTime.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        } else {
            changeDelayTime.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_SIGNED);
        }
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String time = changeDelayTime.getText().toString().trim();
                if (TextUtils.isEmpty(time)) {
                    ToastUtils.showShort(ProjectDetailActivity.this, "请设置延时！");
                    return;
                }
                int intTime = Integer.parseInt(time);
                if (Math.abs(intTime) > 15000) {
                    ToastUtils.showShort(ProjectDetailActivity.this, "延时请设置在0ms---15000ms范围内");
                    return;
                }
                if (type == HOLE_IN_TYPE) {
                    if (intTime >= 0) {
                        numTypeIn.setVisibility(View.VISIBLE);
                    } else {
                        numTypeIn.setVisibility(View.GONE);
                    }
                    detDelayBean.setHoleInTime(intTime);
                } else if (type == HOLE_OUT_TYPE) {
                    if (intTime >= 0) {
                        numTypeOut.setVisibility(View.VISIBLE);
                    } else {
                        numTypeOut.setVisibility(View.GONE);
                    }
                    detDelayBean.setHoleOutTime(intTime);
                }else{
                    // 设置的是起始的时间
                    isStartTimeChange = true;
                    detDelayBean.setStartTime(intTime);
                }
                setDelaySetting(AppIntentString.DELAY_SETTING, JSON.toJSONString(detDelayBean));
                delayText.setText(String.valueOf(intTime));
                dialog.dismiss();
            }
        });
        delayAlertDialog = builder.create();
        delayAlertDialog.setCanceledOnTouchOutside(false);
        delayAlertDialog.show();
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
        changeDelayTime.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        changeDelayTime.setText(detonatorEntity.getRelay() + "");
        builder.setView(view);
        builder.setCancelable(false);
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
                int intDelayTime = Integer.parseInt(nowDelayTime);
                if (Math.abs(intDelayTime) > 15000) {
                    ToastUtils.showShort(ProjectDetailActivity.this, "延时请设置在0ms---15000ms范围内");
                    return;
                }
                detonatorEntity.setRelay(Integer.parseInt(nowDelayTime));
                DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
                projectDetailAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onHolePostionClick(int position) {
        //  设置孔位
        ProjectDetonator detonatorEntity = detonators.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_view, null, false);
        EditText changeDelayTime = view.findViewById(R.id.changeDelayTime);
        changeDelayTime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        changeDelayTime.setKeyListener(DigitsKeyListener.getInstance("1234567890-"));
        TextView textTitle = view.findViewById(R.id.text_title);
        textTitle.setText("修改孔位号：");
        changeDelayTime.setText(detonatorEntity.getHolePosition() + "");
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String holePosition = changeDelayTime.getText().toString().trim();
                if (TextUtils.isEmpty(holePosition)) {
                    return;
                }

                // 校验孔位号
                Pattern compile = Pattern.compile(HOLE_REGEX);
                Matcher matcher = compile.matcher(holePosition);
                if (!matcher.matches()) {
                    ToastUtils.showShort(ProjectDetailActivity.this,"请输入正确的孔位号！");
                    return;
                }

                detonatorEntity.setHolePosition(holePosition);
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
//        if (this.detDelayBean == null) {
//            // 没有设置延时，提示设置延时
//            showDelaySettingDialog();
//            showStatusDialog("请配置延时！");
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: keyCode = " + keyCode);
        // 左边189 右边190  中间188
        if (keyCode == 189 && event.getAction() == KeyEvent.ACTION_DOWN) {
            scanType = AppIntentString.TYPE_HOLE_IN;
            return true;
        }
        // 中间按钮
        if (keyCode == 188 && event.getAction() == KeyEvent.ACTION_DOWN) {
            scanType = AppIntentString.TYPE_HOLE_OUT;
            return true;
        }
        // 右边按钮
        if (keyCode == 190 && event.getAction() == KeyEvent.ACTION_DOWN) {
            scanType = AppIntentString.TYPE_HOLE_IN;
            return true;
        }

        // 按钮7
        if (keyCode == 14 && event.getAction() == KeyEvent.ACTION_DOWN) {
            ReadDetNumTask readDetNumTask1 = new ReadDetNumTask(AppIntentString.TYPE_HOLE_OUT);
            readDetNumTask1.execute();
            return true;
        }
        //按钮8
//        if (keyCode == 15 && event.getAction() == KeyEvent.ACTION_DOWN) {
//            ReadDetNumTask readDetNumTask1 = new ReadDetNumTask(AppIntentString.TYPE_HOLE_NO_CHANGE);
//            readDetNumTask1.execute();
//            return true;
//        }
        // 按钮9
        if (keyCode == 16 && event.getAction() == KeyEvent.ACTION_DOWN) {
            ReadDetNumTask readDetNumTask1 = new ReadDetNumTask(AppIntentString.TYPE_HOLE_IN);
            readDetNumTask1.execute();
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

            VibrateUtil.vibrate(ProjectDetailActivity.this,150);

            //*******重要
            if (intent.getAction().equals(RES_ACTION)) {
                //获取扫描结果
                if (scanResult.length() > 0 && DetIDConverter.VerifyQRCheckValue(scanResult)) { //如果条码长度>0，解码成功。如果条码长度等于0解码失败。
                    // 扫描成功
                    String strgm="";
                    //  12位条码
                    if(scanResult.length()==12){
                        byte[] dc = DetIDConverter.GetDCByOldQRString(scanResult);
                        strgm = DetIDConverter.GetDisplayDC(dc);
                    } else{
                        strgm = scanResult.substring(0, 13);
                    }

                    playSound(true);

                    createDetData(strgm);
                } else {
                    playSound(false);
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
            detonatorEntity1.setUid(getDetUid(detId, strgm));
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


    public void showAutoMissDialog(String msg) {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
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
                    projectDetailAdapter.setSelectedPosition(i);
                    projectDetailAdapter.notifyDataSetChanged();
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

            VibrateUtil.vibrate(ProjectDetailActivity.this,150);
            if (TextUtils.isEmpty(result)) {
                showAutoMissDialog("获取雷管码失败！");
                playSound(false);
            } else {
                createProjectDetData(result, type);
                playSound(true);
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
        projectDetonator.setUid(getDetUid(detId, detCode));
        if (detonators.size() == 0) {
            projectDetonator.setHolePosition("1-1");
            String startTime = delayStartTime.getText().toString().trim();
            projectDetonator.setRelay(Integer.valueOf(startTime));
        } else {


            ProjectDetonator projectDetonatorLast = detonators.get(detonators.size() - 1);
            int nextDelayTime = getNextDelayTime(projectDetonatorLast, type);
            if (nextDelayTime < 0 || nextDelayTime > 15000) {
                ToastUtils.showShort(ProjectDetailActivity.this, "延时请设置在0ms---15000ms范围内");
                return;
            }
            String nextHolePosition = getNextHolePosition(projectDetonatorLast, type);
            projectDetonator.setHolePosition(nextHolePosition);
            projectDetonator.setRelay(nextDelayTime);
        }
        DBManager.getInstance().getProjectDetonatorDao().save(projectDetonator);
        detonators.add(projectDetonator);
        projectDetailAdapter.setSelectedPosition(detonators.size() - 1);
        projectDetailAdapter.notifyDataSetChanged();
        recycleView.scrollToPosition(detonators.size() - 1);
    }

    // 根据上个的空位号获取下一个的空位号
    private String getNextHolePosition(ProjectDetonator projectDetonatorLast, int type) {

        String lastHolePostion = projectDetonatorLast.getHolePosition();
        String nextHolePostion = "";
        String[] split = lastHolePostion.split("-");

        switch (type) {
            case AppIntentString.TYPE_HOLE_IN:
                nextHolePostion = split[0] + "-" + (Integer.parseInt(split[1]) + 1);
                break;
            case AppIntentString.TYPE_HOLE_NO_CHANGE:
                nextHolePostion = lastHolePostion;
                break;
            case AppIntentString.TYPE_HOLE_OUT:
                nextHolePostion = (Integer.parseInt(split[0]) + 1) + "-1";
                break;
        }
        return nextHolePostion;

    }

    // 根据类型获取新的延时
    private int getNextDelayTime(ProjectDetonator projectDetonatorLast, int type) {

        int nextDelayTime = 0;
        if (isStartTimeChange) {
            isStartTimeChange = false;
            nextDelayTime = detDelayBean.getStartTime();
            return nextDelayTime;
        }

        int delayTime = projectDetonatorLast.getRelay();

        switch (type) {
            case AppIntentString.TYPE_HOLE_IN:
                String holeInTime = holeTimeIn.getText().toString().trim();
                nextDelayTime = delayTime + getIntFormString(holeInTime);
                break;
            case AppIntentString.TYPE_HOLE_OUT:
                int lastHoleOutTime = getHoleOutTime();
                String holeOutTime = holeTimeOut.getText().toString().trim();
                nextDelayTime = lastHoleOutTime + getIntFormString(holeOutTime);
                break;
            case AppIntentString.TYPE_HOLE_NO_CHANGE:
                nextDelayTime = delayTime;
                break;
        }
        return nextDelayTime;
    }

    // 获取下一个孔间的时间
    private int getHoleOutTime() {
        for (int i = detonators.size()-1; i >= 0 ; i--) {
            ProjectDetonator projectDetonator = detonators.get(i);
            String holePosition = projectDetonator.getHolePosition();
            if (holePosition.split("-")[1].equals("1")) {
                return  projectDetonator.getRelay();
            }
        }
        return detDelayBean.getStartTime();
    }

    //  数字型的字符串转为数字
    public int getIntFormString(String stringNum) {
        int i = Integer.parseInt(stringNum);
        return i;
    }


    // 雷管ID 获取uid
    private String getDetUid(String detId) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = DetApp.getInstance().ModuleGetUID(Integer.parseInt(detId), stringBuilder);
        Log.d(TAG, "getDetUid: " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    // 雷管ID 获取uid
    private String getDetUid(String detId, String detdc) {
        int nid = Integer.parseInt(detId);

        String struid = detdc.substring(0, 2);

        String stry = detdc.substring(2, 3);
        int nyear = Integer.parseInt(stry);
        if (nyear == 0x09) {
            stry = "19";
        } else {
            stry = String.valueOf(20 + nyear);
        }
        struid = struid + stry + "A8" + String.format("%08X", nid);
        return struid;
    }


}