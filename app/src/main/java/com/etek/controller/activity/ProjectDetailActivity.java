package com.etek.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.PowerCheckCallBack;
import com.etek.controller.hardware.util.DataConverter;
import com.etek.controller.hardware.util.DetIDConverter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.scan.ScannerInterface;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener, ProjectDetailAdapter.OnItemClickListener {

    private static final String TAG = "ProjectDetailActivity";
    private EditText delayTimeNew;
    private RecyclerView recycleView;
    private List<ProjectDetonator> detonators;
    private long projectId;
    private ProjectDetailAdapter projectDetailAdapter;
    private EditText delayholein;
    private EditText delayholeout;
    private LinearLayout rootView;
    private List<ProjectDetonator> mDetonatorEntities;
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
        mDetonatorEntities = DBManager.getInstance().getProjectDetonatorDao()._queryPendingProject_DetonatorList(projectId);
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
        textTitle.setText("雷管组网");
        textBtn.setText("保存工程");
        textBtn.setVisibility(View.INVISIBLE);

        textBtn.setOnClickListener(this);

        rootView = findViewById(R.id.rootview);

        // 正式布局
        // 起始延时
        delayTimeNew = findViewById(R.id.delay_time);

        recycleView = findViewById(R.id.recycleView);

        delayholein = findViewById(R.id.delayholein);
        delayholeout = findViewById(R.id.delayholeout);

        // 孔内
        View layoutHoleIn = findViewById(R.id.layoutHoleIn);
        // 孔间
        View layoutHoleOut = findViewById(R.id.layoutHoleOut);

        View getDet = findViewById(R.id.get_det);

        getDet.setOnClickListener(this);
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
                if (detonators.size() == 0) {
                    Toast.makeText(this, "未录入数据", Toast.LENGTH_SHORT);
                    return;
                }
                DBManager.getInstance().getProjectDetonatorDao().saveInTx(detonators);
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
            case R.id.get_det:
                // 点击获取雷管信息
                // TODO: 2020/12/5
                ReadDetNumTask readDetNumTask = new ReadDetNumTask();
                readDetNumTask.execute();
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
        ProjectDetonator detonatorEntity = detonators.get(lastPosition);
        if (!TextUtils.isEmpty(detonatorEntity.getRelay())) {
            return;
        }
        if (detonators.size() >= 2) {
            ProjectDetonator detonatorEntity1 = detonators.get(lastPosition - 1);
            String lastDelayTime = detonatorEntity1.getRelay();
            String holePosition = detonatorEntity1.getHolePosition();
            String[] split = holePosition.split("-");
            int intFormString = getIntFormString(split[0]);
            int delayholeoutTime = getIntFormString(delayholeout.getText().toString().trim());
            int newDelayTime = getIntFormString(lastDelayTime) + delayholeoutTime;
            detonatorEntity.setRelay(String.valueOf(newDelayTime));
            detonatorEntity.setHolePosition(String.valueOf(intFormString + 1) + "-" + 1);
            DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
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
        ProjectDetonator detonatorEntity = detonators.get(lastPosition);
        if (!TextUtils.isEmpty(detonatorEntity.getRelay())) {
            return;
        }

        if (detonators.size() == 1) {
            int startDelayTime = getIntFormString(delayTimeNew.getText().toString().trim());
            detonatorEntity.setRelay(String.valueOf(startDelayTime));
            detonatorEntity.setHolePosition(1 + "-" + 1);
            projectDetailAdapter.notifyDataSetChanged();
            return;
        }

        ProjectDetonator detonatorEntity1 = detonators.get(lastPosition - 1);
        String lastDelayTime = detonatorEntity1.getRelay();

        String holePosition = detonatorEntity1.getHolePosition();
        String[] split = holePosition.split("-");
        int intFormString = getIntFormString(split[1]);
        int first = getIntFormString(split[0]);

        int delayholeinTime = getIntFormString(delayholein.getText().toString().trim());
        int newDelayTime = getIntFormString(lastDelayTime) + delayholeinTime;
        detonatorEntity.setRelay(String.valueOf(newDelayTime));
        detonatorEntity.setHolePosition(first + "-" + String.valueOf(intFormString + 1));
        DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
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
            detonatorEntity1.setDetId(getDetIdByGm(strgm));
            detonatorEntity1.setProjectInfoId(projectId);
            DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity1);
            detonators.add(insertPosition, detonatorEntity1);
            projectDetailAdapter.notifyDataSetChanged();
            return;
        }
        if (detonators.size() != 0) {
            ProjectDetonator detonatorEntity = detonators.get(detonators.size() - 1);
            if (TextUtils.isEmpty(detonatorEntity.getRelay())) {
                showStatusDialog("请设置录入雷管的延时后，再继续录入！");
                return;
            }
        }
        ProjectDetonator detonatorEntity = new ProjectDetonator();
        detonatorEntity.setProjectInfoId(projectId);
        detonatorEntity.setCode(strgm);
        detonatorEntity.setDetId(getDetIdByGm(strgm));
        DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
        detonators.add(detonatorEntity);
        projectDetailAdapter.notifyDataSetChanged();
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
                    showStatusDialog("此雷管已扫描！");
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

    /**
     * 连接获取雷管信息
     */
    public class ReadDetNumTask extends AsyncTask<String, Integer, String>{

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
                showStatusDialog("获取雷管码失败！");
            }else{
                createDetData(result);
            }
        }
    }
}