package com.etek.controller.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.ConnectTestAdapter;
import com.etek.controller.adapter.FiltrateAdapter;
import com.etek.controller.adapter.ProjectDetailAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.task.DetsBusChargeTask;
import com.etek.controller.hardware.task.ITaskCallback;
import com.etek.controller.hardware.util.SoundPoolHelp;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.utils.VibrateUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 连接检测
 */
public class ConnectTestActivity extends BaseActivity implements View.OnClickListener, ProjectDetailAdapter.OnItemClickListener, ITaskCallback {

    private static final String TAG = "ConnectTestActivity";
    private LinearLayout backImag;
    private TextView textTitle;
    private TextView textBtn;
    private RecyclerView recycleView;
    private ConnectTestAdapter connectTestAdapter;
    private List<ProjectDetonator> connectData = new ArrayList<>();
    private List<ProjectInfoEntity> projectInfoEntities;
    private PopupWindow popWindow;
    private RecyclerView rvFiltrate;
    private FiltrateAdapter filtrateAdapter;
    private List<DetonatorEntity> mDetonatorEntities;
    private ProjectInfoEntity mProjectInfoEntity;
    private int projectPosition = -1;
    private TestAsyncTask testAsyncTask;
    private long proId;
    private List<ProjectDetonator> detonatorEntityList;
    private ProgressDialog progressValueDialog;
    private SoundPoolHelp soundPoolHelp;

    // 是否取消连接检测
    private boolean isCancelTest = false;
    private TextView missEvent;
    private TextView falseConnect;
    private TextView allDet;
    private ProgressDialog busChargeProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_test);
        getProjectId();
        initView();
        initDate();
        initSound();
    }

    /**
     * 初始化音效
     */
    private void initSound() {
        soundPoolHelp = new SoundPoolHelp(this);
        soundPoolHelp.initSound();
    }

    /**
     * 获取项目id
     */
    private void getProjectId() {
        proId = getIntent().getLongExtra(AppIntentString.PROJECT_ID, -1);
        XLog.d("proId: " + proId);
    }

    /**
     * 页面展示的数据
     */
    private void initDate() {
        // 获取到项目列表（暂时隐藏）
//        projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
        //根据项目id获取雷管并展示
        if (proId >= 0) {
            detonatorEntityList = DBManager.getInstance().getProjectDetonatorDao().queryBuilder().where(ProjectDetonatorDao.Properties.ProjectInfoId.eq(proId)).list();
            connectData.addAll(detonatorEntityList);
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        backImag = findViewById(R.id.back_img);
        backImag.setOnClickListener(this);
        textTitle = findViewById(R.id.text_title);
        textTitle.setText(R.string.title_activity_connecttest);
        textBtn = findViewById(R.id.text_btn);
        textBtn.setText("项目列表");
        textBtn.setOnClickListener(this);
        textBtn.setVisibility(View.GONE);

//        missEvent = findViewById(R.id.miss_event);
//        falseConnect = findViewById(R.id.false_connect);
//        allDet = findViewById(R.id.all_det);
//
//        missEvent.setOnClickListener(this);
//        falseConnect.setOnClickListener(this);
//        allDet.setOnClickListener(this);

        recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        connectTestAdapter = new ConnectTestAdapter(this, connectData);
        recycleView.setAdapter(connectTestAdapter);

        connectTestAdapter.setOnItemClickListener(this);
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
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgAlpha();
            }
        });
        initFiltrate(contentView);
        popWindow.showAsDropDown(textBtn, 0, 25);
    }


    /**
     * showPopWindow消失后取消背景色
     */
    private void bgAlpha() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = (float) 1.0; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    /**
     * 初始化列表,根据项目名称来进行筛选
     */
    private void initFiltrate(View contentView) {
        rvFiltrate = contentView.findViewById(R.id.rv_filtrate);
        rvFiltrate.setLayoutManager(new LinearLayoutManager(this));
        //动态设置rvFiltrate的高度
        if (projectInfoEntities.size() > 5) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 600);
            rvFiltrate.setLayoutParams(lp);
        }

        filtrateAdapter = new FiltrateAdapter(R.layout.filtrate_item, projectInfoEntities);
        rvFiltrate.setAdapter(filtrateAdapter);

        filtrateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showFiltrateData(position);
                popWindow.dismiss();
            }
        });
    }


    /**
     * 获取筛选的数据并展示
     */
    private void showFiltrateData(int position) {
//        if (projectPosition == position) {
//            return;
//        }
//        this.projectPosition = position;
//        mProjectInfoEntity = projectInfoEntities.get(position);
//        mDetonatorEntities = DBManager.getInstance().getDetonatorEntityDao()._queryProjectInfoEntity_DetonatorList(mProjectInfoEntity.getId());
//        connectData.clear();
//        if (mDetonatorEntities != null && mDetonatorEntities.size() > 0) {
//            connectData.addAll(mDetonatorEntities);
//        } else {
//            ToastUtils.show(ConnectTestActivity.this, "项目未录入数据");
//        }
//        connectTestAdapter.notifyDataSetChanged();
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img://返回
                finish();
                break;
            case R.id.text_btn://筛选
                if (projectInfoEntities == null || projectInfoEntities.size() == 0) {
                    ToastUtils.show(this, this.getString(R.string.no_filtrate_project));
                } else {
                    showPopWindow();
                }
                break;
//            case R.id.miss_event:
                // 筛选失联
//                changeMissEvent();
//                checkShow(1);
//                break;
//            case R.id.false_connect:
                // 筛选误接
//                changeFalseConnect();
//                checkShow(2);
//                break;
            case R.id.all_det:
                // 展示全部
//                showAllDet();
//                checkShow(3);
                break;

        }
    }

//    private void checkShow(int type) {
//        if (type == 1) {
//            missEvent.setSelected(true);
//            falseConnect.setSelected(false);
//            allDet.setSelected(false);
//        } else if (type == 2) {
//            missEvent.setSelected(false);
//            falseConnect.setSelected(true);
//            allDet.setSelected(false);
//        } else if (type == 3) {
//            missEvent.setSelected(false);
//            falseConnect.setSelected(false);
//            allDet.setSelected(true);
//        }
//    }

    private void showAllDet() {
        // 筛选后点击展示全部
        if (proId >= 0) {
            List<ProjectDetonator> list = DBManager.getInstance().getProjectDetonatorDao().queryBuilder().where(ProjectDetonatorDao.Properties.ProjectInfoId.eq(proId)).list();
            connectData.clear();
            connectData.addAll(list);
            connectTestAdapter.notifyDataSetChanged();
        }
    }

    // 筛选 误接状态
    private void changeFalseConnect() {
        if (connectData == null || connectData.size() == 0) {
            ToastUtils.show(this, "未录入数据");
            return;
        }
        // TODO: 2020/10/31

    }

    // 筛选失联 状态
    private void changeMissEvent() {
        if (connectData == null || connectData.size() == 0) {
            ToastUtils.show(this, "未录入数据");
            return;
        }

        List<ProjectDetonator> missConnect = new ArrayList<>();
        for (ProjectDetonator connectDatum : connectData) {
            if (connectDatum.getTestStatus() != 0) {
                missConnect.add(connectDatum);
            }
        }
        connectData.clear();
        connectData.addAll(missConnect);
        connectTestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        // 点击条目弹出 popuWindow 提示删除或者测试
        shouPopuWindow(view, position);
    }

    private void shouPopuWindow(View view, int position) {
        View popuView = getLayoutInflater().inflate(R.layout.popuwindow_view, null, false);
        PopupWindow mPopupWindow = new PopupWindow(popuView, 150, 120);
        popuView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除条目
                deleteItemView(position);
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }

            }
        });
        TextView downloadAgain = popuView.findViewById(R.id.insert_item);
        downloadAgain.setText("检测");
        downloadAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 再次测试
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                testItem(position);
            }
        });
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(view, 200, -10, Gravity.RIGHT);
    }

    /**
     * 对雷管的再次检测
     *
     * @param position
     */
    private void testItem(int position) {
        // 进行单个雷管的测试 todo
        showProDialog("检测中...");
        detSingleCheck(position);
        connectTestAdapter.notifyDataSetChanged();
        missProDialog();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BUTTON_1 && event.getAction() == KeyEvent.ACTION_DOWN) {
            allDetConnectTest();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    /**
     * 进行项目中所有的雷管的连接检测
     */
    private void allDetConnectTest() {
        if (connectData == null || connectData.size() == 0) {
            return;
        }
        // 连接检测前需要进行总线上电操作
        DetsBusChargeTask detsBusChargeTask = new DetsBusChargeTask(this);
        detsBusChargeTask.execute();
    }

    @Override
    protected void onDestroy() {
        if (testAsyncTask != null) {
            testAsyncTask.cancel(true);
        }

        // 必须总线下电
        DetApp.getInstance().MainBoardBusPowerOff();

        releaseSound();
        super.onDestroy();
    }

    private void releaseSound() {
        if (soundPoolHelp!=null) {
            soundPoolHelp.releaseSound();
        }
    }

    private void checkAllDetStatus() {
        // TODO: 2020/11/21  检查所有雷管的信息
    }

    // 删除条目
    private void deleteItemView(int position) {
        if (position <= connectData.size() - 1) {
            ProjectDetonator detonatorEntity = connectData.get(position);
            DBManager.getInstance().getProjectDetonatorDao().delete(detonatorEntity);
            connectData.remove(position);
            connectTestAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDelayTimeClick(int position) {

    }

    @Override
    public void onHolePostionClick(int position) {

    }

    /**
     * 单个雷管的链接测试
     *
     * @param position
     */
    public boolean detSingleCheck(int position) {
        ProjectDetonator detonatorEntity = connectData.get(position);
        String detId = detonatorEntity.getDetId();
        Log.d(TAG, "detSingleCheck: detId = " + detId);
        int wakeupStatus = DetApp.getInstance().MainBoardHVEnable();
        Log.d(TAG, "detSingleCheck: MainBoardHVEnable = " + wakeupStatus);
        // 进行雷管的链接检测
        int testResult = DetApp.getInstance().ModuleSingleCheck(Integer.parseInt(detId));
        Log.d(TAG, "detSingleCheck: testResult = " + testResult);
        StringBuilder stringBuilder = new StringBuilder();
        testResult = DetApp.getInstance().ModuleGetUID(Integer.parseInt(detId), stringBuilder);
        detonatorEntity.setTestStatus(testResult);
        detonatorEntity.setUid(stringBuilder.toString());
        DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
        playSound(testResult == 0);
        return testResult == 0;
    }

    private void playSound(boolean b) {
        if (soundPoolHelp != null && !b) {
            soundPoolHelp.playSound(b);
            VibrateUtil.vibrate(ConnectTestActivity.this, 150);
        }
    }


    /**
     * 更新展示检测的结果
     */
    private void updateProjectStatus() {
        // TODO: 2020/12/27  连接检测结速了，要展示结果或者提示进入延时下载操作
        List<ProjectDetonator> projectDetonators = DBManager.getInstance().getProjectDetonatorDao()._queryPendingProject_DetonatorList(proId);
        if (projectDetonators == null || projectDetonators.size() == 0) {
            return;
        }
        int successNum = 0;
        int faileNum = 0;
        for (ProjectDetonator projectDetonator : projectDetonators) {
            if (projectDetonator.getTestStatus() == 0) {
                successNum++;
            } else {
                faileNum++;
            }
        }
        if (successNum == projectDetonators.size()) {
            //全部检测测功了，更新项目状态和，提示进去延时下载
            updateAndHint();
        } else {
            // 未全部检测成功，展示检测结果
            showTestResult(projectDetonators.size(), successNum, faileNum);
        }
    }

    // 更新项目状态
    private void updateAndHint() {
        PendingProject projectInfoEntity = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.Id.eq(proId)).unique();
        if (projectInfoEntity != null) {
            projectInfoEntity.setProjectStatus(AppIntentString.PROJECT_IMPLEMENT_DELAY_DOWNLOAD1);
            DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("检测成功，请进行延时下载！");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ConnectTestActivity.this, DelayDownloadActivity.class);
                intent.putExtra(AppIntentString.PROJECT_ID, proId);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    // 展示检测的结果
    private void showTestResult(int size, int successNum, int faileNum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(String.format("共检测雷管：%d发", size));
        builder.setMessage(String.format("成功：%d\t失败：%d", successNum, faileNum));
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    // 异步进行在线检测
    public class TestAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            for (int i = 0; i < connectData.size(); i++) {

                if (isCancelTest) {
                    return null;
                }
                boolean b = detSingleCheck(i);
                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showTextProgressDialog();
            isCancelTest = false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            updateTestProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            connectTestAdapter.notifyDataSetChanged();
            dissTestProgressDialog();
            updateProjectStatus();
        }
    }

    @Override
    public void showProgressDialog(String msg, int type) {
        busChargeProgressDialog = new ProgressDialog(this);
        busChargeProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        busChargeProgressDialog.setMessage("系统准备中...");
        busChargeProgressDialog.setCancelable(false);
        busChargeProgressDialog.setCanceledOnTouchOutside(false);
        busChargeProgressDialog.setMax(100);
        busChargeProgressDialog.setProgressPercentFormat(null);
        busChargeProgressDialog.show();
    }

    @Override
    public void setProgressValue(int value) {
        if (busChargeProgressDialog != null) {
            busChargeProgressDialog.setProgress(value);
        }
    }

    @Override
    public void dissProgressDialog() {
        if (busChargeProgressDialog != null) {
            busChargeProgressDialog.dismiss();
        }
    }

    @Override
    public void setDisplayText(String msg) {
        Log.d(TAG, "setDisplayText: " + msg);
    }

    @Override
    public void postResult(int result, int type) {
        dissProgressDialog();
        if (result == 0) {
            //  充电成功了，才进行检测
            for (ProjectDetonator connectDatum : connectData) {
                connectDatum.setTestStatus(-1);
            }
            DBManager.getInstance().getProjectDetonatorDao().saveInTx(connectData);
            connectTestAdapter.notifyDataSetChanged();
            testAsyncTask = new TestAsyncTask();
            testAsyncTask.execute();
        } else {
            showStatusDialog("系统准备失败！");
        }
    }

    @Override
    public void setChargeData(int nVoltage, int nCurrent) {

    }


    public void dissTestProgressDialog() {
        if (progressValueDialog != null) {
            progressValueDialog.dismiss();
        }
    }


    public void updateTestProgress(int values) {
        if (progressValueDialog != null) {
            progressValueDialog.setProgress(values);
        }
    }

    /**
     * 显示进度条
     */
    private void showTextProgressDialog() {
        progressValueDialog = new ProgressDialog(this);
        progressValueDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressValueDialog.setTitle("检测中...");
        progressValueDialog.setCancelable(false);
        progressValueDialog.setCanceledOnTouchOutside(false);
        progressValueDialog.setMax(connectData.size());
        progressValueDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: 2020/12/20
                isCancelTest = true;
                if (testAsyncTask !=null) {
                    testAsyncTask.cancel(true);
                }
            }
        });
        progressValueDialog.show();
    }
}