package com.etek.controller.activity.project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
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

    private RecyclerView testRecycleView;
    private List<SingleCheckEntity> singleCheckEntityList;
    private TextView checkNum;
    private String TAG = "SingleCheckActivity";
    private SingleCheckAdapter singleCheckAdapter;
    private SoundPoolHelp soundPoolHelp;

    private boolean cancelSingleCheck = false;

    private boolean singleClick = false;

    private int m_nLastDetID = -1;
    private boolean m_bFirstTime = true;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_check);
        initSupportActionBar(R.string.title_act_single_check);
        initView();
        initSound();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        m_bFirstTime = true;

        releaseSound();

        if(null!=m_sthd){
            try {
                m_sthd.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"单颗检测线程退出");
        }
        m_sthd = null;

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

    class SingleCheckThread extends Thread{
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

                //  第一次不执行
                if(!m_bFirstTime){
                    int ret = DetApp.getInstance().ModuleSetIOStatus(m_nLastDetID,(byte)0x02);
                    Log.d(TAG, String.format("ModuleSetIOStatus返回：%d",ret));
                    if(0==ret){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    m_nLastDetID = -1;
                }

                Log.d(TAG, "检查是否短路...");
                StringBuilder strData = new StringBuilder();
                int result = DetApp.getInstance().CheckBusShortCircuit(strData);
                result = showBusShortResult(result,strData.toString());
                if(0!=result){
                    Log.d(TAG,"短路检测失败");
                    m_nLastDetID = -1;
                    continue;
                }

                Log.d(TAG, "获取雷管信息...");
                result = DetApp.getInstance().CheckSingleModule(new SingleCheckCallBack() {
                    private String m_strmsg;
                    @Override
                    public void DisplayText(String strText) {
                        Log.d(TAG, "DisplayText: "+strText);
                        m_strmsg =  strText;
                    }
                    @Override
                    public void SetSingleModuleCheckData(int nID, byte[] szDC, int nDT, byte bCheckResult) {
                        m_bFirstTime = false;

                        if (cancelSingleCheck) {
                            singleClick=false;
                            return;
                        }

                        Log.d(TAG,String.format("上一个棵雷管ID:%08X",m_nLastDetID));
                        if(m_nLastDetID==nID){
                            Log.d(TAG, "showSingleCheckData: 同一颗雷管");
                            return;
                        }
                        //  缓存上一颗
                        m_nLastDetID = nID;

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
                        Log.d(TAG,String.format("进度：%d",npos));
                        showCheckSingleProgress(npos);
                        if(npos<=110) {
                            // 显示“检测中...”和百分比
                            return;
                        }
                        showCheckErrorMessage(npos,m_strmsg);
                        // 失败
                        playSound(false);
                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private SingleCheckThread m_sthd=null;

    @Override
    public void onClick(View v) {
        switchButtonText();

        //  进入单颗检测 点击检测后才开始进行轮训操作
        if (singleClick) {
            return;
        }

        Log.d(TAG, "开始检测...");

        singleClick = true;
        m_nLastDetID = -1;


        if (null != m_sthd) {
            try {
                m_sthd.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            m_sthd = null;
        }

        m_sthd = new SingleCheckThread();
        m_sthd.start();
    }

    // 展示进度
    private void showCheckSingleProgress(int npos) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (cancelSingleCheck) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    return;
                }

                if (progressDialog == null ) {
                    progressDialog = new ProgressDialog(SingleCheckActivity.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage("检测中...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
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

    private int showBusShortResult(int ret, String data) {
        if (ret != 0) {
            Log.d(TAG, "获取电压电流 失败 " + ret);
            showCheckErrorMessage(-1, "获取电压电流 失败 " + ret);
            return -1;
        }

        Log.d(TAG, String.format("返回数据:%s",data));
        if(data.length()<18){
            showCheckErrorMessage(-1,  "返回数据错误，长度不足!");
            return -1;
        }
        String strResult = data.substring(16,18).toUpperCase();
        Log.d(TAG, String.format("检测结果:%s",strResult));
        if(strResult.equals("00")){
            showCheckErrorMessage(-1,  "未检测");
            return -1;
        }

        if(strResult.equals("0A")){
            showCheckErrorMessage(-1, "总线漏电");
            return -1;
        }

        if(strResult.equals("0F")){
            showCheckErrorMessage(-1, "总线短路");
            return -1;
        }

        return 0;
    }


    private void showCheckErrorMessage(int ret, String strmsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playSound(false);
                ToastUtils.show(SingleCheckActivity.this, strmsg);
                return;
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
                        for (int i = 0; i < singleCheckEntityList.size(); i++) {
                            SingleCheckEntity singleCheckEntity1 = singleCheckEntityList.get(i);
                            if (singleCheckEntity1.getDetId() == singleCheckEntity.getDetId()) {
                                playSound(false);
                                singleCheckAdapter.setSelectedPostion(i);
                                singleCheckAdapter.notifyDataSetChanged();
                                testRecycleView.scrollToPosition(i);
                                ToastUtils.showShort(SingleCheckActivity.this,"检测成功  该发雷管已检测！");
                                return;
                            }
                        }
                    }
                    playSound(true);
                    singleCheckEntityList.add(singleCheckEntity);
                    singleCheckAdapter.setSelectedPostion(singleCheckEntityList.size()-1);
                    singleCheckAdapter.notifyDataSetChanged();
                    testRecycleView.scrollToPosition(singleCheckEntityList.size()-1);
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

        if(null!=m_sthd){
            try {
                m_sthd.join();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        m_sthd = null;
        tv.setText("点 击 检 测");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,String.format("KeyCode:%d",keyCode));

        //  右下角的退出键
        if(4==keyCode){
            if(singleClick) {
                ToastUtils.show(this, "按停止检测后才能退出");
                return true;
            }
            else{
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}