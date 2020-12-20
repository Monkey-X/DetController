package com.etek.controller.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.hardware.task.BusDisChargeTask;
import com.etek.controller.hardware.task.CheckDropOffTask;
import com.etek.controller.hardware.task.DetnoateTask;
import com.etek.controller.hardware.task.DetsBusChargeTask;
import com.etek.controller.hardware.task.ITaskCallback;
import com.etek.controller.hardware.task.PowerOnSelfCheckTask;
import com.etek.controller.hardware.task.SetBLTask;
import com.etek.sommerlibrary.activity.BaseActivity;

/**
 * 充电起爆
 */
public class PowerBombActivity extends BaseActivity implements View.OnClickListener, ITaskCallback {

    private Context mContext;
    private int GO_TO_GPS = 150;
    private TextView toastText;
    private TextView showstring;
    private TextView powerBank;
    private String TAG = "PowerBombActivity";
    private PowerOnSelfCheckTask powerAsyncTask;
    private ProgressDialog progressValueDialog;

    private String resultString;
    private CheckDropOffTask checkDropOffTask;
    private DetsBusChargeTask detsBusChargeTask;
    private AlertDialog startBombDialog;

    private boolean isBombing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_bomb);
        initSupportActionBar(R.string.title_power_bomb);
        init();
        initView();
    }

    private void initView() {
        toastText = findViewById(R.id.toast_text);
        powerBank = findViewById(R.id.power_bank);
        showstring = findViewById(R.id.showString);
        powerBank.setOnClickListener(this);
    }

    /**
     * 初始化
     */
    private void init() {
        this.mContext = this;
    }

    int mBackKeyAction;
    long mActionTime;
    int mOkKeyAction;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (keyCode == 19 && action == KeyEvent.ACTION_DOWN) {
            mBackKeyAction = KeyEvent.ACTION_DOWN;  //记录按下状态
            if (mActionTime == 0) {
                mActionTime = System.currentTimeMillis();
            }
        }

        if (keyCode == 19 && action == KeyEvent.ACTION_UP) {
            mBackKeyAction = KeyEvent.ACTION_UP;  //记录松下状态
            mActionTime = 0;
        }

        if (keyCode == 20 && event.getAction() == KeyEvent.ACTION_DOWN) {
            mOkKeyAction = KeyEvent.ACTION_DOWN;   //记录按下状态
            if (mActionTime == 0) {
                mActionTime = System.currentTimeMillis();
            }
        }

        if (keyCode == 20 && event.getAction() == KeyEvent.ACTION_UP) {
            mOkKeyAction = KeyEvent.ACTION_UP;    //记录松下状态
            mActionTime = 0;
        }

        //长按，左右侧键  todo
        if (isLongPress() && mBackKeyAction == KeyEvent.ACTION_DOWN && mOkKeyAction == KeyEvent.ACTION_DOWN) {
            //  长按左右键之后进行起爆操作 todo  进行起爆操作

            DetonateAllDet();

        }

        return super.dispatchKeyEvent(event);

    }

    private void DetonateAllDet() {
        // 进行网络起爆
        if (!isBombing) {
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
        super.onDestroy();
        if (powerAsyncTask != null) {
            powerAsyncTask.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        toastText.setText("");
        powerAsyncTask = new PowerOnSelfCheckTask(this);
        powerAsyncTask.execute();
    }

    public void StartChargeTask() {
        detsBusChargeTask = new DetsBusChargeTask(this);
        detsBusChargeTask.execute();
    }


    public void showProgressDialog(String msg, int type) {
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
        progressValueDialog.show();
    }

    /**
     * 进行充电取消步骤
     */
    private void chargeCancel() {
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
        } else {
            // 网络起爆失败,进行总线放电
            startDisChangeTask();
        }
    }

    @Override
    public void setChargeData(int nVoltage, int nCurrent) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                float fc = nCurrent / 1000;
                if (fc >= 1) {
                    str = String.format("%dmA", fc);
                } else {
                    str = "< 1mA";
                }
                showstring.setText(String.format("当前：电压 %d   电流:%s", nVoltage, str));
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
                toastText.setText("请同时按下上下按钮进行起爆操作！");
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
        BusDisChargeTask busDisChargeTask = new BusDisChargeTask(this);
        busDisChargeTask.execute();
    }
}