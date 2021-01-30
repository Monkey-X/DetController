package com.etek.controller.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.etek.controller.R;
import com.etek.controller.adapter.SingleCheckAdapter;
import com.etek.controller.hardware.command.DetApp;
import com.etek.controller.hardware.test.SingleCheckCallBack;
import com.etek.controller.hardware.util.DataConverter;
import com.etek.controller.hardware.util.DetIDConverter;
import com.etek.controller.hardware.util.SoundPoolHelp;
import com.etek.controller.persistence.entity.SingleCheckEntity;
import com.etek.controller.utils.VibrateUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 单线检测
 */
public class SingleCheckActivity extends BaseActivity implements View.OnClickListener {

    private TextView mPipeCode;
    private TextView mUid;
    private TextView mDelayed;
    private RecyclerView testRecycleView;
    private List<SingleCheckEntity> singleCheckEntityList;
    private TextView checkNum;
    private String TAG = "SingleCheckActivity";
    private SingleCheckAdapter singleCheckAdapter;
    private SoundPoolHelp soundPoolHelp;

    private boolean cancelSingleCheck = false;

    private boolean singleClick = false;

    private int m_nLastDetID = -1;
    private boolean m_bLastDetRemoved = true;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_check);
        initSupportActionBar(R.string.title_act_single_check);
        initView();
        initSound();
    }

    /**
     * 初始化音效
     */
    private void initSound() {
        soundPoolHelp = new SoundPoolHelp(this);
        soundPoolHelp.initSound();
    }
    private void releaseSound() {
        if (soundPoolHelp!=null) {
            soundPoolHelp.releaseSound();
        }
    }
    private void playSound(boolean b) {
        if (soundPoolHelp != null) {
            soundPoolHelp.playSound(b);
            VibrateUtil.vibrate(SingleCheckActivity.this, 150);
        }
    }

    @Override
    protected void onDestroy() {
        cancelSingleCheck = true;
        releaseSound();


        super.onDestroy();
    }

    /**
     * 初始化View
     */
    private void initView() {
        View clickTest = findViewById(R.id.clickTest);
        checkNum = findViewById(R.id.checkNum);
        clickTest.setOnClickListener(this);

        singleCheckEntityList = new ArrayList<>();
        testRecycleView = findViewById(R.id.testRecycleView);
        testRecycleView.setLayoutManager(new LinearLayoutManager(SingleCheckActivity.this));
        singleCheckAdapter = new SingleCheckAdapter(R.layout.item_single_check, singleCheckEntityList);
        testRecycleView.setAdapter(singleCheckAdapter);
    }

    @Override
    public void onClick(View v) {
        switchButtonText();

        //  进入单颗检测 点击检测后才开始进行轮训操作
        if (singleClick) {
            return;
        }

        Log.d(TAG, "开始检测...");

        singleClick = true;
        m_nLastDetID=-1;m_bLastDetRemoved = true;
        // 调用接口进行检测
        new Thread() {
            @Override
            public void run() {
                while (true){
                    if (cancelSingleCheck) {
                        singleClick=false;
                        // 必须总线下电
                        DetApp.getInstance().MainBoardBusPowerOff();
                        Log.d(TAG, "下电退出，停止检测");
                        return;
                    }

                    if(!m_bLastDetRemoved){
                        int ret = DetApp.getInstance().ModuleSetIOStatus(m_nLastDetID,(byte)0x02);
                        Log.d(TAG, String.format("ModuleSetIOStatus返回：%d",ret));
                        if(0==ret){
                            m_bLastDetRemoved = false;

                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        m_bLastDetRemoved = true;
                    }
                    Log.d(TAG, "总线短路和漏电检测");
                    // 总线短路和漏电检测
                    StringBuilder strData = new StringBuilder();
                    int i = DetApp.getInstance().CheckBusShortCircuit(strData);
                    showBusShortResult(i,strData.toString());

                    Log.d(TAG, "获取雷管信息...");

                    int result = DetApp.getInstance().CheckSingleModule(new SingleCheckCallBack() {
                        @Override
                        public void DisplayText(String strText) {
                            Log.d(TAG, "DisplayText: "+strText);
                        }
                        @Override
                        public void SetSingleModuleCheckData(int nID, byte[] szDC, int nDT, byte bCheckResult) {
                            Log.d(TAG,String.format("上一个棵雷管ID:%08X",m_nLastDetID));
                            if(m_nLastDetID==nID){
                                Log.d(TAG, "showSingleCheckData: 同一颗雷管");
                                return;
                            }
                            //  缓存上一颗
                            m_nLastDetID = nID;
                            m_bLastDetRemoved =false;

                            SingleCheckEntity singleCheckEntity = new SingleCheckEntity();
                            singleCheckEntity.setRelay(String.valueOf(0xffffffffL&nDT));
                            singleCheckEntity.setDetId(nID);
                            singleCheckEntity.setDC(DetIDConverter.GetDisplayDC(szDC));
                            int checkResult = DataConverter.getByteValue(bCheckResult);
                            singleCheckEntity.setTestStatus(checkResult);
                            Log.d(TAG, "SetSingleModuleCheckData: checkResult=" + singleCheckEntity.toString());
                            showSingleCheckData(singleCheckEntity);
                        }

                        @Override
                        public void SetProgressbarValue(int npos){
                            // 显示“检测中...”和百分比
                            showCheckSingleProgress(npos);

                        }
                    });
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    // 展示进度
    private void showCheckSingleProgress(int npos) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null ) {
                    progressDialog = new ProgressDialog(SingleCheckActivity.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage("检测中...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                }

                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
                progressDialog.setProgress(npos);

                if (npos >=100) {
                    if (progressDialog !=null) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showBusShortResult(int ret, String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ret != 0) {
                    Log.d(TAG, "获取电压电流 失败 " + ret);
                    ToastUtils.show(SingleCheckActivity.this, "获取电压电流 失败 " + ret);
                    return;
                }

                Log.d(TAG, String.format("返回数据:%s",data));
                if(data.length()<18){
                    ToastUtils.show(SingleCheckActivity.this, "返回数据错误，长度不足!");
                    return;
                }
                String strResult = data.substring(16,18).toUpperCase();
                Log.d(TAG, String.format("检测结果:%s",strResult));
                if(strResult.equals("00")){
                    playSound(false);
                    ToastUtils.show(SingleCheckActivity.this, "未检测");
                    return;
                }

                if(strResult.equals("0A")){
                    playSound(false);
                    ToastUtils.show(SingleCheckActivity.this, "总线漏电");
                    return;
                }

                if(strResult.equals("0F")){
                    playSound(false);
                    ToastUtils.show(SingleCheckActivity.this, "总线短路");
                    return;
                }

            }
        });
    }

    private void showSingleCheckData(SingleCheckEntity singleCheckEntity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (singleCheckEntity != null) {
                    // 去除重复扫描的
                    if (singleCheckEntityList != null && singleCheckEntityList.size()!=0) {
                        for (SingleCheckEntity checkEntity : singleCheckEntityList) {
                            if (checkEntity.getDetId() == singleCheckEntity.getDetId()) {
                                playSound(false);
                                ToastUtils.showShort(SingleCheckActivity.this,"检测成功  该发雷管已检测！");
                                return;
                            }
                        }
                    }
                    playSound(true);
                    singleCheckEntityList.add(singleCheckEntity);
                    singleCheckAdapter.notifyDataSetChanged();
                    checkNum.setText(singleCheckEntityList.size() + "");
//                SingleCheckEntityDao.
//                DBManager.getInstance().getSingleCheckEntityDao().insert(singleCheckEntity);
                }
            }
        });
    }


    private boolean m_bChecking =false;
    private void switchButtonText(){
        TextView tv = findViewById(R.id.clickTest);
        if(!m_bChecking){
            m_bChecking=true;
            cancelSingleCheck=false;
            tv.setText("检测中,点击停止...");
            return;
        }

        m_bChecking=false;
        cancelSingleCheck =true;
        singleClick =true;
        Log.d(TAG, "准备停止检测");

        try {
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        tv.setText("点 击 检 测");
    }

}