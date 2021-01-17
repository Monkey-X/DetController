package com.etek.controller.activity.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.activity.UserInfoActivity;
import com.etek.controller.adapter.OfflineEditAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.Jbqy;
import com.etek.controller.dto.Jbqys;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.Lgs;
import com.etek.controller.dto.OnlineCheckResp;
import com.etek.controller.dto.OnlineCheckStatusResp;
import com.etek.controller.dto.ProInfoDto;
import com.etek.controller.dto.ProjectFileDto;
import com.etek.controller.dto.Sbbhs;
import com.etek.controller.dto.Zbqy;
import com.etek.controller.dto.Zbqys;
import com.etek.controller.entity.Detonator;
import com.etek.controller.entity.OfflineDownloadBean;
import com.etek.controller.hardware.test.HttpCallback;
import com.etek.controller.model.User;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.JsonUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

public class OfflineEditActivity extends BaseActivity implements View.OnClickListener {

    private EditText proCode;
    private EditText contractCode;
    private EditText companyCode;
    private EditText controllerSn;
    private TextView numPostion;
    private TextView detCode;
    private TextView detStatus;
    private RecyclerView offlineRecycleView;
    private ArrayList<Detonator> detList;
    private OfflineEditAdapter offlineEditAdapter;

    private String TAG = "OfflineEditActivity";
    private long proId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_edit);
        initView();
        initData();
    }

    private void initView() {
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        View backImg = findViewById(R.id.back_img);
        textTitle.setText("离线项目编辑");
        textBtn.setText("离线校验");
        backImg.setOnClickListener(this);
        textBtn.setOnClickListener(this);
        proCode = findViewById(R.id.pro_code);
        contractCode = findViewById(R.id.contract_code);
        companyCode = findViewById(R.id.company_code);
        controllerSn = findViewById(R.id.controller_sn);
        View addDet = findViewById(R.id.add_det);
        addDet.setOnClickListener(this);
        numPostion = findViewById(R.id.num_position);
        detCode = findViewById(R.id.det_code);
        detStatus = findViewById(R.id.det_status);
        numPostion.setText("序号");
        detCode.setText("管码");
        detStatus.setText("状态");
        offlineRecycleView = findViewById(R.id.offline_recycleView);

        // 设置界面的数据
        detList = new ArrayList<>();
        offlineEditAdapter = new OfflineEditAdapter(this, detList);
        offlineRecycleView.setLayoutManager(new LinearLayoutManager(OfflineEditActivity.this));
        offlineRecycleView.setAdapter(offlineEditAdapter);
    }

    private void initData() {
        // 回填单位代码
        String userStr = getPreInfo("userInfo");
        if (TextUtils.isEmpty(userStr)) {
            Intent intent = new Intent(this, UserInfoActivity.class);
            startActivity(intent);
            return;
        }else{
            Globals.user = JSON.parseObject(userStr, User.class);
        }
        companyCode.setText(Globals.user.getCompanyCode());
        controllerSn.setText(getStringInfo(getString(R.string.controller_sno)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.text_btn:
                // 提示生成校验
                createOfflineData();
                break;
            case R.id.add_det:
                showDetOfflineDialog();
                break;
        }
    }

    /**
     * 生成离线文件
     */
    private void createOfflineData() {
        OfflineDownloadBean offlineDownloadBean = new OfflineDownloadBean();
        if (!TextUtils.isEmpty(contractCode.getText().toString()) || !TextUtils.isEmpty(proCode.getText().toString())) {
            offlineDownloadBean.setHtid(contractCode.getText().toString());
            offlineDownloadBean.setXmbh(proCode.getText().toString());
        } else {
            showToast("请输入项目编号或者合同编码！");
            return;
        }
        if (!StringUtils.isEmpty(companyCode.getText().toString())) {
            offlineDownloadBean.setDwdm(companyCode.getText().toString());
        } else {
            showToast("请输入有效的单位编码");
            return;
        }
        offlineDownloadBean.setXtm("");
        offlineDownloadBean.setHtm("");
        String controller = controllerSn.getText().toString();
        if (StringUtils.isEmpty(controller) || controller.length() != 11 || controller.contains(" ")) {
            showToast("请输入有效的起爆器编号");
            return;
        } else {
            offlineDownloadBean.setSbbh(controller.toUpperCase());
        }
        if (detList == null || detList.isEmpty()) {
            showToast("请输入雷管数");
            return;
        } else {
            offlineDownloadBean.setDets(detList);
        }
        showProDialog("正在得到检验数据中。。。");
        String rptJson = JSON.toJSONString(offlineDownloadBean, SerializerFeature.WriteMapNullValue);
        XLog.v(rptJson);
//        rptJson = " {\"dwdm\":\"5227224300086\",\"fbh\":\"\",\"htid\":\"522722320120002\",\"htm\":\"\",\"sbbh\":\"F61A8190423\",\"xmbh\":\"\",\"xtm\":\"I610c01K201014\"}";
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            missProDialog();
            showStatusDialog("数据编码出错：" + result.getMessage());
            return;
        }
        String url;
        // 测试服务器
        if (Globals.isTest) {
            url = AppConstants.DanningTestServer + AppConstants.OfflineDownload;
        } else {
            url = AppConstants.DanningServer + AppConstants.OfflineDownload;
        }
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");
        AsyncHttpCilentUtil.httpPostNew(this, newUrl, null, new HttpCallback() {

            @Override
            public void onFaile(IOException e) {
                missProDialog();
                showStatusDialog("校验服务器出错：" + e.getMessage());
            }

            @Override
            public void onSuccess(Response response) {
                missProDialog();
                String respStr = null;
                try {
                    respStr = response.body().string();
                } catch (IOException e) {
                }
                if (StringUtils.isEmpty(respStr)) {
                    showStatusDialog("返回数据为空！");
                    return;
                }

                if (JsonUtil.isHtml(respStr)) {
                    showStatusDialog("返回数据HTML！" + respStr);
                    return;
                }

                try {
                    Result rptDecode = RptUtil.getRptDecode(respStr);
                    if (rptDecode.isSuccess()) {
                        String data = (String) rptDecode.getData();
                        OnlineCheckStatusResp onlineCheckStatusResp = JSON.parseObject(data, OnlineCheckStatusResp.class);

                        if (onlineCheckStatusResp.getCwxx().equals("0")) {
                            OnlineCheckResp serverResult = JSON.parseObject(data, OnlineCheckResp.class);

                            if (serverResult.getLgs().getLg() == null || serverResult.getLgs().getLg().isEmpty()) {
                                showStatusDialog("雷管信息为空！");
                                return;
                            }

                            int isUsed = 0;
                            for (Detonator detonator : detList) {
                                for (Lg lg : serverResult.getLgs().getLg()) {

                                    if (lg.getFbh().equalsIgnoreCase(detonator.getDetCode())) {
                                        detonator.setStatus(lg.getGzmcwxx());
                                    }
                                    if (lg.getGzmcwxx() != 0) {
                                        isUsed = 1;
                                    }
                                }
                            }
                            if (isUsed > 0) {
                                showStatusDialog("雷管信息异常！");
                                offlineEditAdapter.notifyDataSetChanged();
                            }
                            ProjectFileDto projectFile = new ProjectFileDto();
                            projectFile.setCompany(Globals.user.getCompanyName());
                            projectFile.setDwdm(Globals.user.getCompanyCode());
                            projectFile.setXmbh(offlineDownloadBean.getXmbh());
                            projectFile.setHtbh(offlineDownloadBean.getHtid());
                            ProInfoDto proInfoDto = new ProInfoDto();
                            BeanPropertiesUtil.copyProperties(serverResult, proInfoDto);
                            projectFile.setProInfo(proInfoDto);

                            String strInfo = JSON.toJSONString(projectFile, AppUtils.filter);

                            updateETEKData(strInfo);

                            proId = storeProjectInfo(projectFile, serverResult);
                            if (proId != 0) {
                                showStatusDialog("项目保存成功！");

                            } else {
                                showStatusDialog("项目保存失败！");
                            }
                        } else {
                            if (!StringUtils.isEmpty(onlineCheckStatusResp.getCwxxms())) {
                                showStatusDialog(onlineCheckStatusResp.getCwxxms());
                            } else {
                                showStatusDialog("服务器返回错误，数据错误 请调整！");
                            }


                        }
                    }
                } catch (Exception e) {
                    showLongToast("解析错误：" + e.getMessage());
                }

            }
        });

    }


    private long storeProjectInfo(final ProjectFileDto projectFile, OnlineCheckResp onlineCheckResp) {

        XLog.v("onlineCheckResp:" + onlineCheckResp);
        ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
        projectInfoEntity.setApplyDate(onlineCheckResp.getSqrq());
        projectInfoEntity.setProCode(projectFile.getXmbh());
        projectInfoEntity.setProName(projectFile.getXmmc());
        projectInfoEntity.setCompanyCode(projectFile.getDwdm());
        projectInfoEntity.setCompanyName(projectFile.getDwmc());
        projectInfoEntity.setContractCode(projectFile.getHtbh());
        projectInfoEntity.setContractName(projectFile.getHtmc());
        projectInfoEntity.setIsOnline(false);
        projectInfoEntity.setStatus(0);
        projectInfoEntity.setCreateTime(new Date());

        long proId = DBManager.getInstance().getProjectInfoEntityDao().insert(projectInfoEntity);
        if (proId == 0) {
            return 0;
        }
        XLog.v("proid:" + proId);

        Lgs lgs = onlineCheckResp.getLgs();
        if (!lgs.getLg().isEmpty()) {
            List<DetonatorEntity> detonatorEntityList = new ArrayList<>();
            for (Lg lg : lgs.getLg()) {
//                XLog.v("lg:" + lg);
                DetonatorEntity detonatorBean = new DetonatorEntity();
                if (StringUtils.isEmpty(lg.getFbh())) {
                    for (Detonator detonator : detList) {
//                        debug(detonator.toString());
                        if (detonator.getUid().equalsIgnoreCase(lg.getUid())) {
                            lg.setFbh(detonator.getDetCode());
                            detonator.setStatus(lg.getGzmcwxx());
                        }
                    }
                }
                detonatorBean.setCode(lg.getFbh());
                detonatorBean.setWorkCode(lg.getGzm());
                detonatorBean.setUid(lg.getUid());
                detonatorBean.setValidTime(lg.getYxq());
                detonatorBean.setProjectInfoId(proId);
                detonatorBean.setStatus(lg.getGzmcwxx());
                detonatorEntityList.add(detonatorBean);
            }
            DBManager.getInstance().getDetonatorEntityDao().insertInTx(detonatorEntityList);

        }


        Zbqys zbqys = onlineCheckResp.getZbqys();
        if (!zbqys.getZbqy().isEmpty()) {
            List<PermissibleZoneEntity> permissibleZoneEntityList = new ArrayList<>();
            for (Zbqy zbqy : zbqys.getZbqy()) {
                PermissibleZoneEntity permissibleZone = new PermissibleZoneEntity();
                permissibleZone.setName(zbqy.getZbqymc());
                permissibleZone.setLatitude(Double.parseDouble(zbqy.getZbqywd()));
                permissibleZone.setLongitude(Double.parseDouble(zbqy.getZbqyjd()));
                permissibleZone.setRadius(Integer.parseInt(zbqy.getZbqybj()));
                permissibleZone.setStartTime(zbqy.getZbqssj());
                permissibleZone.setStopTime(zbqy.getZbjzsj());
                permissibleZone.setProjectInfoId(proId);
                permissibleZoneEntityList.add(permissibleZone);
            }
            DBManager.getInstance().getPermissibleZoneEntityDao().insertInTx(permissibleZoneEntityList);
        }
        Jbqys jbqys = onlineCheckResp.getJbqys();
        if (!jbqys.getJbqy().isEmpty()) {
            List<ForbiddenZoneEntity> forbiddenZoneEntityList = new ArrayList<>();
            for (Jbqy jbqy : jbqys.getJbqy()) {
                ForbiddenZoneEntity forbiddenZoneEntity = new ForbiddenZoneEntity();
                forbiddenZoneEntity.setLatitude(Double.parseDouble(jbqy.getJbqywd()));
                forbiddenZoneEntity.setLongitude(Double.parseDouble(jbqy.getJbqyjd()));
                forbiddenZoneEntity.setRadius(Integer.parseInt(jbqy.getJbqybj()));
                forbiddenZoneEntity.setStartTime(jbqy.getJbqssj());
                forbiddenZoneEntity.setStopTime(jbqy.getJbjzsj());
                forbiddenZoneEntity.setProjectInfoId(proId);
                forbiddenZoneEntityList.add(forbiddenZoneEntity);
            }
            DBManager.getInstance().getForbiddenZoneEntityDao().insertInTx(forbiddenZoneEntityList);
        }
        List<Sbbhs> sbbhs = onlineCheckResp.getSbbhs();

        if (!sbbhs.isEmpty()) {
            List<ControllerEntity> controllerEntityList = new ArrayList<>();
            for (Sbbhs sbbh : sbbhs) {
                ControllerEntity controller = new ControllerEntity();
                controller.setName(sbbh.getSbbh());
                controller.setProjectInfoId(proId);
                controllerEntityList.add(controller);
            }
            DBManager.getInstance().getControllerEntityDao().insertInTx(controllerEntityList);
        }

        return proId;
    }

    /**
     * 上传数据到ETEK
     *
     * @param strInfo
     */
    private void updateETEKData(String strInfo) {
        if (TextUtils.isEmpty(strInfo)) {
            return;
        }
        String url = AppConstants.ETEKTestServer + AppConstants.DETUnCheck;

        AsyncHttpCilentUtil.httpPostJson(url, strInfo, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "update to ETEK onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "update to ETEK onResponse: " + response.body().string());
            }
        });
    }

    // 离线雷管编辑的
    private void showDetOfflineDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_offline_edit, null, false);
        EditText etDetCode = view.findViewById(R.id.et_det_code);
        EditText etDetNum = view.findViewById(R.id.et_det_num);
        Spinner spDetSpinner = view.findViewById(R.id.sp_det_type);
        dialog.setCancelable(false);
        dialog.setView(view);
        //设置对话框标题
        dialog.setPositiveButton("确认", (dialog1, which) -> {
            String detCodeStr = etDetCode.getText().toString().trim();
            String detNumStr = etDetNum.getText().toString().trim();
            int selectedItemPosition = spDetSpinner.getSelectedItemPosition();
            createDetInfo(detCodeStr, detNumStr, selectedItemPosition);
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * 根据输入的雷管信息，雷管数量，递增类型创建雷管的码
     *
     * @param detCodeStr
     * @param detNumStr
     * @param selectedItemPosition
     */
    private void createDetInfo(String detCodeStr, String detNumStr, int selectedItemPosition) {
        if (TextUtils.isEmpty(detCodeStr) || !DetUtil.isValidFbh(detCodeStr)) {
            showToast("请输入有效的雷管发编号！");
            return;
        }
        if (TextUtils.isEmpty(detNumStr)) {
            showToast("请输入有效的雷管数！");
            return;
        }
        int num = Integer.parseInt(detNumStr);
        if (num <= 0) {
            showToast("请输入有效的雷管数！");
            return;
        }
        int tube = Integer.parseInt(detCodeStr.substring(11, 13));
        boolean isOdd = false;
        if (tube % 2 != 0) {
            isOdd = true;
        }
        for (int i = 0; i < num; i++) {
            if (tube > 100) {
                showToast("一次编辑雷管少于100！");
                break;
            }
            String newFbh = detCodeStr.substring(0, 11) + String.format(Locale.CHINA, "%02d", tube);
            Detonator detonator = new Detonator(newFbh);

            int status = detonator.getDetonatorByFbh(newFbh);

            if (status == 1) {
                detonator.setStatus(0);
            } else {
                showToast("请输入有效的雷管编号！");
                break;
            }
            if (selectedItemPosition == 0) {
                tube++;
            } else if (selectedItemPosition == 1) {
                if (isOdd) {
                    tube += 2;
                } else {
                    tube += 1;
                }

            } else if (selectedItemPosition == 2) {
                if (isOdd) {
                    tube += 1;
                } else {
                    tube += 2;
                }
            }
            detList.add(detonator);
        }
        offlineEditAdapter.notifyDataSetChanged();
    }
}