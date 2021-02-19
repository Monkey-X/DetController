package com.etek.controller.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.hardware.task.BusDisChargeTask;
import com.etek.controller.hardware.task.CheckDropOffTask;
import com.etek.controller.hardware.task.DetnoateTask;
import com.etek.controller.hardware.task.DetsBusChargeTask;
import com.etek.controller.hardware.task.ITaskCallback;
import com.etek.controller.hardware.task.PowerOnSelfCheckTask;
import com.etek.controller.hardware.task.SetBLTask;
import com.etek.controller.hardware.util.SoundPoolHelp;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.utils.VibrateUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.List;

/**
 * 充电起爆
 */
public class PowerBombActivity extends BaseActivity implements View.OnClickListener, ITaskCallback {

    private Context mContext;
    private int GO_TO_GPS = 150;
    private TextView toastText;
    private TextView showstring;

    private String TAG = "PowerBombActivity";
    private PowerOnSelfCheckTask powerAsyncTask;
    private ProgressDialog progressValueDialog;

    private String resultString;
    private CheckDropOffTask checkDropOffTask;
    private DetsBusChargeTask detsBusChargeTask;
    private AlertDialog startBombDialog;

    private boolean isBombing = false;
    private View powerBank;
    private long proId;
    private List<ProjectDetonator> detonatorEntityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_bomb);
        initSupportActionBar(R.string.title_power_bomb);
        getProjectId();
        init();
        initView();

        initSound();
    }

    private void getProjectId() {
        Intent intent = getIntent();
        proId = intent.getLongExtra(AppIntentString.PROJECT_ID, -1);
        if (proId != -1) {
            detonatorEntityList = DBManager.getInstance().getProjectDetonatorDao().queryBuilder().where(ProjectDetonatorDao.Properties.ProjectInfoId.eq(proId)).list();
        }
    }

    private void initView() {
        toastText = findViewById(R.id.toast_text);
        powerBank = findViewById(R.id.power_bank);
        showstring = findViewById(R.id.showString);
        powerBank.setOnClickListener(this);

        View detDisCharge = findViewById(R.id.det_disCharge);
        detDisCharge.setOnClickListener(this);
    }

    /**
     * 初始化
     */
    private void init() {
        this.mContext = this;
    }

    private SoundPoolHelp soundPoolHelp;
    /**
     * 初始化音效
     */
    private void initSound() {
        soundPoolHelp = new SoundPoolHelp(this);
        soundPoolHelp.initSound();
    }

    private void releaseSound() {
        if (soundPoolHelp != null) {
            soundPoolHelp.releaseSound();
        }
    }


    int mBackKeyAction = -1;
    long mActionTime = 0;
    int mOkKeyAction = -1;

    boolean isCanBomb = false;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (isCanBomb) {

            if (keyCode == 19 && action == KeyEvent.ACTION_DOWN) {
                mBackKeyAction = KeyEvent.ACTION_DOWN;  //记录按下状态
                Log.d(TAG, "dispatchKeyEvent: mBackKeyAction = KeyEvent.ACTION_DOWN");
                if (mActionTime == 0) {
                    mActionTime = System.currentTimeMillis();
                }
            }

            if (keyCode == 19 && action == KeyEvent.ACTION_UP) {
                mBackKeyAction = KeyEvent.ACTION_UP;  //记录松下状态
                Log.d(TAG, "dispatchKeyEvent: mBackKeyAction = KeyEvent.ACTION_UP");
                mActionTime = 0;
            }

            if (keyCode == 20 && event.getAction() == KeyEvent.ACTION_DOWN) {
                mOkKeyAction = KeyEvent.ACTION_DOWN;   //记录按下状态
                Log.d(TAG, "dispatchKeyEvent: mOkKeyAction = KeyEvent.ACTION_DOWN");
                if (mActionTime == 0) {
                    mActionTime = System.currentTimeMillis();
                }
            }

            if (keyCode == 20 && event.getAction() == KeyEvent.ACTION_UP) {
                Log.d(TAG, "dispatchKeyEvent: mOkKeyAction = KeyEvent.ACTION_UP");
                mOkKeyAction = KeyEvent.ACTION_UP;    //记录松下状态
                mActionTime = 0;
            }

            //长按，左右侧键
            if (isLongPress() && mBackKeyAction == KeyEvent.ACTION_DOWN && mOkKeyAction == KeyEvent.ACTION_DOWN) {
                //  长按左右键之后进行起爆操作
                Log.d(TAG, "dispatchKeyEvent: DetonateAllDet");
                mBackKeyAction = -1;
                mOkKeyAction = -1;
                mActionTime = 0;
                DetonateAllDet();
            }
        }
        return true;

    }

    private void DetonateAllDet() {
        // 进行网络起爆
        isCanBomb = false;
        if (!isBombing) {
            // 蜂鸣+震动1秒钟
            soundPoolHelp.playSound(true);
            VibrateUtil.vibrate(this, 1000);

            isBombing = true;
            showProDialog("起爆中...");
            Log.d(TAG, "DetonateAllDet: ");
            DetnoateTask detnoateTask = new DetnoateTask(this);
            detnoateTask.execute();
        }
    }

    private boolean isLongPress() {
        if (System.currentTimeMillis() - mActionTime > 1000) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 注销
     */
    @Override
    protected void onDestroy() {
        releaseSound();

        super.onDestroy();
        if (powerAsyncTask != null) {
            powerAsyncTask.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.det_disCharge:
                startDisChangeTask();
                break;
            case R.id.power_bank:
                if (detonatorEntityList == null || detonatorEntityList.size() ==0) {
                    showToast("获取雷管数据失败！");
                    return;
                }
                toastText.setText("");
                powerAsyncTask = new PowerOnSelfCheckTask(this);
                powerAsyncTask.execute();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //  右下角的退出键
        if(4==keyCode){
            if(!isBombing){
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void StartChargeTask() {
        toastText.setText("雷管充电中...");

        detsBusChargeTask = new DetsBusChargeTask(this);
        detsBusChargeTask.SetDetsCount(detonatorEntityList.size());
        detsBusChargeTask.execute();
    }

    public void showProgressDialog(String msg, int type) {
        if (type == ITaskCallback.BL_FALSE) {
            showProDialog(msg);
        }else{
            progressValueDialog = new ProgressDialog(this);
            progressValueDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressValueDialog.setTitle(msg);
            if (type == ITaskCallback.CHARGE_TYPE) {
                progressValueDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 进行充电取消操作
                        chargeCancel();
                    }
                });
            }
            progressValueDialog.setCancelable(false);
            progressValueDialog.setCanceledOnTouchOutside(false);
            progressValueDialog.setMax(100);
            progressValueDialog.setProgressPercentFormat(null);
            progressValueDialog.show();
        }
    }

    /**
     * 进行充电取消步骤
     */
    private void chargeCancel() {
        //toastText.setText("总线放电中...");
        // 充电取消,先进行拉低操作
        if (detsBusChargeTask != null) {
            detsBusChargeTask.cancel(true);
            Log.d(TAG, "chargeCancel: ");
        }
        StartSetBLTask(false);
    }

    public void setProgressValue(int value) {
        if (progressValueDialog != null) {
            progressValueDialog.setProgress(value);
        }
    }

    public void dissProgressDialog() {
        if (progressValueDialog != null) {
            progressValueDialog.dismiss();
        }
    }

    @Override
    public void setDisplayText(String msg) {
        resultString = msg;
    }

    @Override
    public void postResult(int result, int type) {
        Log.d(TAG, "postResult: result = " + result + "   type = " + type);
        toastText.setText(resultString);
        dissProgressDialog();
        if (type == ITaskCallback.CHARGE_TYPE) {
            showstring.setText("");
            if (result == 0) {
                //  等待用户选择是否起爆
                showBombDialog();
            } else {
                // 充电失败，进行拉高操作
                StartSetBLTask(true);
            }
        } else if (type == ITaskCallback.DETONATE) {
            detonateResult(result);
        } else if (type == ITaskCallback.MIS_CHARGE) {
            powerBank.setVisibility(View.VISIBLE);
        } else if (type == ITaskCallback.BL_TRUE) {
            startDisChangeTask();
        } else if (type == ITaskCallback.BL_FALSE) {
            missProDialog();
            StartSetBLTask(true);
        } else if (type == ITaskCallback.DROP_OFF) {
            dropOffResult(result);
        } else if (type == ITaskCallback.POWER_ON) {
            if (result == 0) {
                // 成功
                StartChargeTask();
            }
        }
    }

    /**
     * 处理电压或者脱落wenti
     *
     * @param result
     */
    private void dropOffResult(int result) {
        if (result != 0) {
            if (startBombDialog != null) {
                startBombDialog.dismiss();
            }
            StartSetBLTask(true);
        }
    }

    /**
     * 获取起爆后的结果
     *
     * @param result
     */
    private void detonateResult(int result) {
        isBombing = false;
        missProDialog();
        if (result == 0) {
            toastText.setText("起爆成功！");
            // 起爆成功，改变项目状态
            changeProjectStatus();
        } else {
            showFialDialog();
        }
    }

    private void changeProjectStatus() {
        PendingProject projectInfoEntity = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.Id.eq(proId)).unique();
        if (projectInfoEntity != null) {
            projectInfoEntity.setProjectStatus(AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT1);
            DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
        }
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("起爆成功，请进行数据上报！");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(PowerBombActivity.this, ReportListActivity.class);
                intent.putExtra(AppIntentString.PROJECT_ID, proId);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     *
     */
    private void showFialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("起爆失败！");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 网络起爆失败,进行总线放电
                startDisChangeTask();
            }
        });
        builder.create().show();
    }

    @Override
    public void setChargeData(int nVoltage, int nCurrent) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String strAm = "";
                double fc = nCurrent * 1.0 / 1000;
                if (fc >= 1) {
                    strAm = String.format("%dmA", ((int) fc));
                } else {
                    strAm = "< 1mA";
                }
                double fv = nVoltage * 1.0 / 1000;
                showstring.setText(String.format("当前：电压 %.1fV   电流:%s", fv, strAm));
            }
        });
    }

    /**
     * 提示用户可以起爆了
     */
    private void showBombDialog() {
        // 展示用户选择是否起爆的dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否进行起爆作业？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toastText.setText("请同时长按上下按钮1秒进行起爆操作！");
                isCanBomb = true;
                powerBank.setVisibility(View.GONE);
                if (checkDropOffTask != null) {
                    checkDropOffTask.cancel(true);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkDropOffTask != null) {
                    checkDropOffTask.cancel(true);
                }
                // 进行拉高操作
                StartSetBLTask(true);
            }
        });
        builder.setCancelable(false);
        startBombDialog = builder.create();
        startBombDialog.show();

        checkDropOffTask = new CheckDropOffTask(this);
        checkDropOffTask.execute();
    }

    /**
     * 进行BL拉高操作
     *
     * @param hight
     */
    private void StartSetBLTask(boolean hight) {
        SetBLTask setBLTask = new SetBLTask(this, hight);
        setBLTask.execute();
    }

    // 网络起爆失败,进行总线放电
    private void startDisChangeTask() {
        toastText.setText("总线放电中...");
        BusDisChargeTask busDisChargeTask = new BusDisChargeTask(this);
        busDisChargeTask.execute();
    }
}