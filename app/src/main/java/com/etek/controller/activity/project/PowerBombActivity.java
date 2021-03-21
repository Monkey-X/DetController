package com.etek.controller.activity.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.activity.project.view.SudokuView;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.activity.project.dialog.SudokuDialog;
import com.etek.controller.hardware.command.DetApp;
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
import com.etek.controller.utils.DateStringUtils;
import com.etek.controller.utils.VibrateUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.List;

/**
 * 充电起爆
 */
public class PowerBombActivity extends BaseActivity implements View.OnClickListener, ITaskCallback {

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
    private String bombPassWord;

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
        bombPassWord = getPreInfo("BombPassWord");
        if (TextUtils.isEmpty(bombPassWord)) {
            //  Z字形输入
            bombPassWord ="1235789";
            setStringInfo("BombPassWord",bombPassWord);
            //startActivityForResult(new Intent(this, BombPassWordSettingActivity.class),200);
        }

        //  延时设置为5秒
        DetApp.getInstance().SetCommTimeout(5000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
           init();
        }
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
    private void playSound(boolean b){
        soundPoolHelp.playSound(b);
        VibrateUtil.vibrate(this, 1000);
    }
    boolean isCanBomb = false;

    private void showVerifyDialog(){
        SudokuDialog sudokuDialog = new SudokuDialog();
        sudokuDialog.setSudokuListener(new SudokuView.SudokuListener() {
            @Override
            public void onSbSelected(String result) {
                if (result.equals(bombPassWord)) {
                    sudokuDialog.dismiss();
                    DetonateAllDet();
                }else{
                    ToastUtils.show(PowerBombActivity.this,"手势密码不正确！");
                }
            }
        });
        sudokuDialog.setSudoCancelListener(new SudokuDialog.SudoCancelListenr() {
            @Override
            public void onSudoCancel() {
                StartSetBLTask(true);
            }
        });
        sudokuDialog.show(getSupportFragmentManager(),"");
    }

    private void DetonateAllDet() {
        //  检查是否脱落
        StringBuilder strData = new StringBuilder();
        int ret  = DetApp.getInstance().DetsCheckDropOff(strData);
        Log.d(TAG, String.format("DetsCheckDropOff: %d",ret));

        // 通信不通
        if(0!=ret){
            resultString = "检测到雷管脱落或短路！";
            postResult(0x0c,ITaskCallback.DROP_OFF);
            return;
        }

        //  返回数据不对
        Log.d(TAG, String.format("DetsCheckDropOff返回: %s",strData.toString()));
        String checkString = strData.substring(8);
        Log.d(TAG, String.format("DetsCheckDropOff checkString: %s",checkString.toString()));
        int checkInt = Integer.parseInt(checkString, 16);
        if (checkInt != 1) {
            resultString = "检测到雷管脱落或短路！";
            postResult(0x0c,ITaskCallback.DROP_OFF);
            return;
        }

        // 进行网络起爆
        isCanBomb = false;
        if (!isBombing) {
            // 蜂鸣+震动1秒钟
            playSound(true);

            isBombing = true;
            showProDialog("起爆中...");
            Log.d(TAG, "DetonateAllDet: ");
            DetnoateTask detnoateTask = new DetnoateTask(this);
            detnoateTask.execute();
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
        if(KeyEvent.KEYCODE_BACK==keyCode){
//            if(!isBombing){
//                finish();
//            }
            return true;
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
        android.app.AlertDialog.Builder builder = null;

        Log.d(TAG, "postResult: result = " + result + "   type = " + type);
        toastText.setText(resultString);
        dissProgressDialog();
        switch (type){
            case ITaskCallback.CHARGE_TYPE: //  雷管充电
                showstring.setText("");
                if (result == 0) {
                    //  等待用户选择是否起爆
                    showBombDialog();
                    break;
                }

                playSound(false);

                // 雷管充电失败
                builder = new android.app.AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(resultString);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 充电失败，进行拉高操作
                        StartSetBLTask(true);
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;

            case ITaskCallback.DETONATE:
                detonateResult(result);
                break;

            case ITaskCallback.MIS_CHARGE:      //  放电操作
                if(result!=0){
                    playSound(false);

                    builder = new android.app.AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    String str =resultString + "\n放电失败，请重新进行放电操作，若仍失败，请半小时之后再进入爆破场地";
                    builder.setMessage(str);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 充电失败，进行拉高操作
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

                powerBank.setVisibility(View.VISIBLE);
                break;

            case ITaskCallback.BL_TRUE:
                startDisChangeTask();
                break;

            case ITaskCallback.BL_FALSE:
                missProDialog();
                StartSetBLTask(true);
                break;

            case ITaskCallback.POWER_ON:        //  上电自检（总线充电）
                if (result == 0) {
                    StartChargeTask();
                    break;
                }

                playSound(false);

                // 上电自检失败
                builder = new android.app.AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(resultString);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                break;

            case ITaskCallback.DROP_OFF:
                Log.d(TAG,"进入到短路对话框！");
                playSound(false);
                builder = new android.app.AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(resultString);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 充电失败，进行拉高操作
                        dropOffResult(result);
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                break;
        }
        return;
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
            projectInfoEntity.setDate(DateStringUtils.getCurrentTime());        //  起爆时间
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
                toastText.setText("请验证手势密码进行起爆操作！");
                powerBank.setVisibility(View.GONE);
                showVerifyDialog();
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