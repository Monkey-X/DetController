package com.etek.controller.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.FiltrateAdapter;
import com.etek.controller.adapter.ProjectDelayAdapter;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.entity.FastEditBean;
import com.etek.controller.fragment.FastEditDialog;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.util.SoundPoolHelp;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 延时下载
 */
public class DelayDownloadActivity extends BaseActivity implements View.OnClickListener, ProjectDelayAdapter.OnItemClickListener, FastEditDialog.OnMakeSureListener {

    private static final String TAG = "DelayDownloadActivity";
    private RecyclerView mDelayList;
    private List<ProjectDetonator> detonators;
    private ProjectDelayAdapter mProjectDelayAdapter;
    private List<ProjectInfoEntity> projectInfoEntities;
    private PopupWindow popWindow;
    private TextView textBtn;
    private RecyclerView rvFiltrate;
    private FiltrateAdapter filtrateAdapter;
    private ProjectInfoEntity mProjectInfoEntity;
    private int projectPosition = -1;
    private long proId;
    private List<ProjectDetonator> detonatorEntityList;
    private DelayDownloadTask delayDownloadTask;
    private ProgressDialog progressValueDialog;
    private SoundPoolHelp soundPoolHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay_download);
        getProjectId();
        initView();
        initRecycleView();
        initProject();
        initSoundPool();
    }

    private void initSoundPool() {
        soundPoolHelp = new SoundPoolHelp(this);
        soundPoolHelp.initSound();
    }

    private void playSound(boolean b) {
        if (soundPoolHelp != null) {
            soundPoolHelp.playSound(b);
        }
    }


    private void releaseSound() {
        if (soundPoolHelp != null) {
            soundPoolHelp.releaseSound();
        }
    }

    /**
     * 获取项目id
     */
    private void getProjectId() {
        proId = getIntent().getLongExtra(AppIntentString.PROJECT_ID, -1);
        XLog.d("proId: " + proId);
    }

    private void initView() {
        View backImag = findViewById(R.id.back_img);
        TextView textTitle = findViewById(R.id.text_title);
        textBtn = findViewById(R.id.text_btn);
        backImag.setOnClickListener(this);
        textTitle.setText(R.string.activity_delay_download);
        textBtn.setText("项目列表");
        textBtn.setVisibility(View.GONE);

        TextView allEdit = findViewById(R.id.all_edit);

        allEdit.setOnClickListener(this);

        textBtn.setOnClickListener(this);

        mDelayList = findViewById(R.id.delayList);
    }

    private void initRecycleView() {
        detonators = new ArrayList<>();

        mDelayList.setLayoutManager(new LinearLayoutManager(this));
        mProjectDelayAdapter = new ProjectDelayAdapter(this, detonators);
        mDelayList.setAdapter(mProjectDelayAdapter);
        mProjectDelayAdapter.setOnItemClickListener(this);
    }


    private void initProject() {
        // 获取工程项目列表（暂时隐藏）
//        projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
        //根据项目id获取雷管并展示页面
        if (proId >= 0){
            detonatorEntityList = DBManager.getInstance().getProjectDetonatorDao().queryBuilder().where(ProjectDetonatorDao.Properties.ProjectInfoId.eq(proId)).list();
            detonators.addAll(detonatorEntityList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.text_btn:
                // 展示项目列表 todo
                if (projectInfoEntities == null || projectInfoEntities.size() == 0) {
                    ToastUtils.show(this, this.getString(R.string.no_filtrate_project));
                } else {
                    showProjectPopuWindow();
                }
                break;
            case R.id.all_edit:
                // 批量编辑
                changeAllEdit();
                break;
        }
    }


    private void changeAllEdit() {
        // 进行批量修改，弹出快捷编辑对话框
        if (detonators == null || detonators.size() == 0) {
            ToastUtils.show(this, "未录入数据！");
            return;
        }
        FastEditDialog fastEditDialog = new FastEditDialog();
        fastEditDialog.setSerialNumber(detonators.size());
        fastEditDialog.setOnMakeSureListener(this);
        fastEditDialog.show(getSupportFragmentManager(), "fastEditDialog");
    }

    // 展示项目列表
    private void showProjectPopuWindow() {
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

    private void showFiltrateData(int position) {
//        if (this.projectPosition == position) {
//            return;
//        }
//        projectPosition = position;
//        mProjectInfoEntity = projectInfoEntities.get(position);
//        List<DetonatorEntity> detonatorEntities = DBManager.getInstance().getDetonatorEntityDao()._queryProjectInfoEntity_DetonatorList(mProjectInfoEntity.getId());
//        detonators.clear();
//        if (detonatorEntities != null && detonatorEntities.size() > 0) {
//            detonators.addAll(detonatorEntities);
//        } else {
//            ToastUtils.show(DelayDownloadActivity.this, "项目未录入数据");
//        }
//        mProjectDelayAdapter.notifyDataSetChanged();
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


    @Override
    public void onItemClick(View view, int position) {
        // 点击条目
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
        downloadAgain.setText("下载");
        downloadAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 插入
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                downloadItem(position);
            }
        });
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(view, 200, -10, Gravity.RIGHT);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BUTTON_1) {
            allDetDownload();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    //再次下载
    private void downloadItem(int position) {
        showProDialog("下载中...");
        detSingleDownload(position);
        mProjectDelayAdapter.notifyDataSetChanged();
        missProDialog();
    }

    // 删除条目
    private void deleteItemView(int position) {
        if (position <= detonators.size() - 1) {
            ProjectDetonator detonatorEntity = detonators.get(position);
            DBManager.getInstance().getProjectDetonatorDao().delete(detonatorEntity);
            detonators.remove(position);
            mProjectDelayAdapter.notifyDataSetChanged();
//            List<DetonatorEntity> detonatorEntities = DBManager.getInstance().getDetonatorEntityDao()._queryProjectInfoEntity_DetonatorList(mProjectInfoEntity.getId());
//            if (detonatorEntities != null) {
//                detonators.clear();
//                detonators.addAll(detonatorEntities);
//                mProjectDelayAdapter.notifyDataSetChanged();
//            }
        }
    }

    @Override
    public void onDelayTimeClick(int position) {
        // 点击修改 延时
        ProjectDetonator detonatorEntity = detonators.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_view, null, false);
        EditText changeDelayTime = view.findViewById(R.id.changeDelayTime);
        changeDelayTime.setText(detonatorEntity.getRelay()+"");
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
                mProjectDelayAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void makeSure(FastEditBean bean) {
        // Todo
        // 对第一个先设置延时
        int holePosition = bean.getStartNum() + bean.getHoleNum();
        int delayTime = bean.getStartTime();
        ProjectDetonator detonatorEntity = detonators.get(bean.getStartNum() - 1);
        detonatorEntity.setRelay(delayTime);
        for (int i = bean.getStartNum() + 1; i <= bean.getEndNum(); i++) {
            ProjectDetonator detonatorEntity1 = detonators.get(i - 1);
            Log.d(TAG, "makeSure: holePosition = " + holePosition);
            Log.d(TAG, "makeSure: delayTime = " + delayTime);
            if (i < holePosition) {
                delayTime = delayTime + bean.getHoleInTime();
                detonatorEntity1.setRelay(delayTime);
            } else if (holePosition == i) {
                delayTime = delayTime + bean.getHoleOutTime();
                detonatorEntity1.setRelay(delayTime);
                holePosition = holePosition + bean.getHoleNum();
            }
        }
        // 修改保存到数据库
        DBManager.getInstance().getProjectDetonatorDao().saveInTx(detonators);
        mProjectDelayAdapter.notifyDataSetChanged();
    }

    /**
     * 单个雷管的延时下载
     *
     * @param position
     */
    public void detSingleDownload(int position) {
        ProjectDetonator detonatorEntity = detonators.get(position);
        String detId = detonatorEntity.getDetId();
        int relayTime = detonatorEntity.getRelay();
        int wakeupStatus = DetApp.getInstance().MainBoardHVEnable();
        Log.d(TAG, "detSingleDownload: detId = " + detId);
        Log.d(TAG, "detSingleDownload: MainBoardHVEnable = " + wakeupStatus);
        // 进行雷管的链接检测
        int downloadResult = DetApp.getInstance().ModuleSetDelayTime(Integer.parseInt(detId),relayTime);
        playSound(downloadResult == 0);
        Log.d(TAG, "detSingleDownload: downloadResult = " + downloadResult);
        detonatorEntity.setDownLoadStatus(downloadResult);
        DBManager.getInstance().getProjectDetonatorDao().save(detonatorEntity);
    }


    /**
     * 进行项目中所有的雷管的延时下载
     */
    private void allDetDownload() {
        if (detonators == null || detonators.size() == 0) {
            return;
        }
        delayDownloadTask = new DelayDownloadTask();
        delayDownloadTask.execute();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (delayDownloadTask != null) {
            delayDownloadTask.cancel(true);
        }
        releaseSound();
    }


    private void updateProjectStatus(){
        ProjectInfoEntity projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
        if (projectInfoEntity!=null) {
            projectInfoEntity.setProjectImplementStates(AppIntentString.PROJECT_IMPLEMENT_ONLINE_AUTHORIZE);
            DBManager.getInstance().getProjectInfoEntityDao().save(projectInfoEntity);
        }
    }


    /**
     * 异步进行 雷管的延时下载
     */
    public class DelayDownloadTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            for (int i = 0; i < detonators.size(); i++) {
                detSingleDownload(i);
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            updateProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showTextProgressDialog();
        }

        @Override
        protected void onPostExecute(Integer o) {
            super.onPostExecute(o);
            missProDialog();
            mProjectDelayAdapter.notifyDataSetChanged();
            updateProjectStatus();
            dissProgressDialog();
        }
    }


    public void dissProgressDialog() {
        if (progressValueDialog != null) {
            progressValueDialog.dismiss();
        }
    }

    public void updateProgress(int values) {
        if (progressValueDialog != null) {
            progressValueDialog.setProgress(values);
        }
    }

    private void showTextProgressDialog() {
        progressValueDialog = new ProgressDialog(this);
        progressValueDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressValueDialog.setTitle("下载中...");
        progressValueDialog.setCancelable(false);
        progressValueDialog.setCanceledOnTouchOutside(false);
        progressValueDialog.setMax(detonators.size());
        progressValueDialog.show();
    }
}