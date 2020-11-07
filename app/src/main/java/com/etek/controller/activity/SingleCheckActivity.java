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
import com.etek.controller.hardware.test.DetCallback;
import com.etek.controller.hardware.util.DataConverter;
import com.etek.controller.hardware.util.DetIDConverter;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.SingleCheckEntity;
import com.etek.controller.persistence.gen.SingleCheckEntityDao;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_check);
        initSupportActionBar(R.string.title_act_single_check);
        initView();
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
        // 调用接口进行检测
        showProgressDialog("检测中...");
        int result = DetApp.getInstance().CheckSingleModule(new DetCallback() {
            @Override
            public void DisplayText(String strText) {
                Log.d(TAG, "DisplayText: " + strText);
//                ToastUtils.show(SingleCheckActivity.this,strText);

            }

            @Override
            public void StartProgressbar() {

            }

            @Override
            public void SetProgressbarValue(int nVal) {

            }

            @Override
            public void SetSingleModuleCheckData(int nID, byte[] szDC, int nDT, byte bCheckResult) {
                Log.d(TAG, "SetSingleModuleCheckData: nID="+nID);
                Log.d(TAG, "SetSingleModuleCheckData: szDC="+nID);
                Log.d(TAG, "SetSingleModuleCheckData: nDT="+nDT);
                Log.d(TAG, "SetSingleModuleCheckData: checkResult="+DataConverter.getByteValue(bCheckResult));

                SingleCheckEntity singleCheckEntity = new SingleCheckEntity();
                singleCheckEntity.setRelay(String.valueOf(nDT));
                singleCheckEntity.setDetId(nID);
                singleCheckEntity.setDC(new DetIDConverter().GetDisplayDC(szDC));
                int checkResult = DataConverter.getByteValue(bCheckResult);
                singleCheckEntity.setTestStatus(checkResult);

                singleCheckEntityList.add(singleCheckEntity);
                singleCheckAdapter.notifyDataSetChanged();
                checkNum.setText(singleCheckEntityList.size()+"");
//                SingleCheckEntityDao.
//                DBManager.getInstance().getSingleCheckEntityDao().insert(singleCheckEntity);
            }

            @Override
            public void SetInitialCheckData(String strHardwareVer, String strUpdateHardwareVer, String strSoftwareVer, String strSNO, String strConfig, byte bCheckResult) {

            }
        });
        Log.d(TAG, "onClick: CheckSingleModule = "+result);
        missProDialog();
    }
}