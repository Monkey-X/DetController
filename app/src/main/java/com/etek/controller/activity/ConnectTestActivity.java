package com.etek.controller.activity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.etek.controller.fragment.FastEditDialog;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 连接检测
 */
public class ConnectTestActivity extends BaseActivity implements View.OnClickListener, ProjectDetailAdapter.OnItemClickListener {

    private static final String TAG = "ConnectTestActivity";
    private LinearLayout backImag;
    private TextView textTitle;
    private TextView textBtn;
    private RecyclerView recycleView;
    private ConnectTestAdapter connectTestAdapter;
    private List<DetonatorEntity> connectData = new ArrayList<>();
    private List<ProjectInfoEntity> projectInfoEntities;
    private PopupWindow popWindow;
    private RecyclerView rvFiltrate;
    private FiltrateAdapter filtrateAdapter;
    private List<DetonatorEntity> mDetonatorEntities;
    private ProjectInfoEntity mProjectInfoEntity;
    private int projectPosition = -1;
    private TestAsyncTask testAsyncTask;
    private long proId;
    private List<DetonatorEntity> detonatorEntityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_test);
        getProjectId();
        initView();
        initDate();
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
            detonatorEntityList = DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(proId)).list();
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

        TextView missEvent = findViewById(R.id.miss_event);
        TextView falseConnect = findViewById(R.id.false_connect);
        TextView allDet = findViewById(R.id.all_det);

        missEvent.setOnClickListener(this);
        falseConnect.setOnClickListener(this);
        allDet.setOnClickListener(this);

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
        if (projectPosition == position) {
            return;
        }
        this.projectPosition = position;
        mProjectInfoEntity = projectInfoEntities.get(position);
        mDetonatorEntities = DBManager.getInstance().getDetonatorEntityDao()._queryProjectInfoEntity_DetonatorList(mProjectInfoEntity.getId());
        connectData.clear();
        if (mDetonatorEntities != null && mDetonatorEntities.size() > 0) {
            connectData.addAll(mDetonatorEntities);
        } else {
            ToastUtils.show(ConnectTestActivity.this, "项目未录入数据");
        }
        connectTestAdapter.notifyDataSetChanged();
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
            case R.id.miss_event:
                // 筛选失联
                changeMissEvent();
                break;
            case R.id.false_connect:
                // 筛选误接
                changeFalseConnect();
                break;
            case R.id.all_det:
                // 展示全部
                showAllDet();
                break;

        }
    }

    private void showAllDet() {
        // 筛选后点击展示全部
        if (proId >= 0) {
            List<DetonatorEntity> list = DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(proId)).list();
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

        List<DetonatorEntity> missConnect = new ArrayList<>();
        for (DetonatorEntity connectDatum : connectData) {
            if (connectDatum.getTestStatus() == 170) {
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
        PopupWindow mPopupWindow = new PopupWindow(popuView, 150, 220);
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
        downloadAgain.setText("测试");
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

        if (keyCode == KeyEvent.KEYCODE_BUTTON_1) {
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
        testAsyncTask = new TestAsyncTask();
        testAsyncTask.execute();
    }

    @Override
    protected void onDestroy() {
        if (testAsyncTask != null) {
            testAsyncTask.cancel(true);
        }
        checkAllDetStatus();
        super.onDestroy();
    }

    private void checkAllDetStatus() {
        // TODO: 2020/11/21  检查所有雷管的信息
    }

    // 删除条目
    private void deleteItemView(int position) {
        if (position <= connectData.size() - 1) {
            DetonatorEntity detonatorEntity = connectData.get(position);
            DBManager.getInstance().getDetonatorEntityDao().delete(detonatorEntity);
            connectData.remove(position);
            connectTestAdapter.notifyDataSetChanged();
//            List<DetonatorEntity> detonatorEntities = DBManager.getInstance().getDetonatorEntityDao()._queryProjectInfoEntity_DetonatorList(mProjectInfoEntity.getId());
//            if (detonatorEntities != null) {
//                connectData.clear();
//                connectData.addAll(detonatorEntities);
//                connectTestAdapter.notifyDataSetChanged();
//            }

        }
    }

    @Override
    public void onDelayTimeClick(int position) {

    }

    /**
     * 单个雷管的链接测试
     *
     * @param position
     */
    public void detSingleCheck(int position) {
        DetonatorEntity detonatorEntity = connectData.get(position);
        String detId = detonatorEntity.getDetId();
        Log.d(TAG, "detSingleCheck: detId = " + detId);
        int wakeupStatus = DetApp.getInstance().ModuleSetWakeupStatus(Integer.parseInt(detId));
        Log.d(TAG, "detSingleCheck: wakeupStatus = " + wakeupStatus);
        // 进行雷管的链接检测
        int testResult = DetApp.getInstance().ModuleSingleCheck(Integer.parseInt(detId));
        Log.d(TAG, "detSingleCheck: testResult = " + testResult);
        detonatorEntity.setTestStatus(testResult);
        DBManager.getInstance().getDetonatorEntityDao().save(detonatorEntity);
    }

    private void updateProjectStatus(){
        ProjectInfoEntity projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
        if (projectInfoEntity!=null) {
            projectInfoEntity.setProjectImplementStates(AppIntentString.PROJECT_IMPLEMENT_DELAY_DOWNLOAD);
            DBManager.getInstance().getProjectInfoEntityDao().save(projectInfoEntity);
        }
    }


    // 异步进行在线检测
    public class TestAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProDialog("检测中...");
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            for (int i = 0; i < connectData.size(); i++) {
                detSingleCheck(i);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            connectTestAdapter.notifyDataSetChanged();
            updateProjectStatus();
            missProDialog();
        }
    }
}