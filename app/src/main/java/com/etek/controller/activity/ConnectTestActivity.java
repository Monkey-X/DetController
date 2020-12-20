package com.etek.controller.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
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
import com.etek.controller.hardware.task.ITaskCallback;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap<Integer, Integer> soundmap;
    private SoundPool soundPool;
    private ProgressDialog progressValueDialog;

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
        SoundPool.Builder builder = new SoundPool.Builder();
        //传入最多播放音频数量,
        builder.setMaxStreams(10);
        //AudioAttributes是一个封装音频各种属性的方法
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        //设置音频流的合适的属性
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        //加载一个AudioAttributes
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();
        //可以通过四种途径来记载一个音频资源：
        //1.通过一个AssetFileDescriptor对象
        //int load(AssetFileDescriptor afd, int priority)
        //2.通过一个资源ID
        //int load(Context context, int resId, int priority)
        //3.通过指定的路径加载
        //int load(String path, int priority)
        //4.通过FileDescriptor加载
        //int load(FileDescriptor fd, long offset, long length, int priority)
        //声音ID 加载音频资源,这里用的是第二种，第三个参数为priority，声音的优先级*API中指出，priority参数目前没有效果，建议设置为1。
        //先将提示音加载，把声音ID保存在Map中
        soundmap = new HashMap<>();
        soundmap.put(0, soundPool.load(this, R.raw.di, 1));
        soundmap.put(1, soundPool.load(this, R.raw.dididi, 1));
    }

    private void playSound(boolean isOK) {
        if (soundPool == null || soundmap == null)
            return;
        //第一个参数soundID
        //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
        //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
        //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
        //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
        //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
        if (isOK) {
            soundPool.play(soundmap.get(0), 1, 1, 0, 0, 1);
        } else {
            soundPool.play(soundmap.get(1), 1, 1, 0, 0, 1);
        }
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
            ProjectDetonator detonatorEntity = connectData.get(position);
            DBManager.getInstance().getProjectDetonatorDao().delete(detonatorEntity);
            connectData.remove(position);
            connectTestAdapter.notifyDataSetChanged();
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
        ProjectDetonator detonatorEntity = connectData.get(position);
        String detId = detonatorEntity.getDetId();
        Log.d(TAG, "detSingleCheck: detId = " + detId);
        int wakeupStatus = DetApp.getInstance().MainBoardHVEnable();
        Log.d(TAG, "detSingleCheck: MainBoardHVEnable = " + wakeupStatus);
        // 进行雷管的链接检测
        int testResult = DetApp.getInstance().ModuleSingleCheck(Integer.parseInt(detId));
        Log.d(TAG, "detSingleCheck: testResult = " + testResult);
        if (testResult == 0) {
            playSound(true);
        }else{
            playSound(false);
        }
        detonatorEntity.setTestStatus(testResult);
        DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
    }

    private void updateProjectStatus() {
        ProjectInfoEntity projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
        if (projectInfoEntity != null) {
            projectInfoEntity.setProjectImplementStates(AppIntentString.PROJECT_IMPLEMENT_DELAY_DOWNLOAD);
            DBManager.getInstance().getProjectInfoEntityDao().save(projectInfoEntity);
        }
    }


    // 异步进行在线检测
    public class TestAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            for (int i = 0; i < connectData.size(); i++) {
                detSingleCheck(i);
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showTextProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (progressValueDialog!=null) {
                progressValueDialog.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            connectTestAdapter.notifyDataSetChanged();
            updateProjectStatus();
            dissProgressDialog();
        }
    }

    public void dissProgressDialog() {
        if (progressValueDialog != null) {
            progressValueDialog.dismiss();
        }
    }

    private void showTextProgressDialog() {
        progressValueDialog = new ProgressDialog(this);
        progressValueDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressValueDialog.setTitle("检测中...");
        progressValueDialog.setCancelable(false);
        progressValueDialog.setCanceledOnTouchOutside(false);
        progressValueDialog.setMax(connectData.size());
        progressValueDialog.show();
    }
}