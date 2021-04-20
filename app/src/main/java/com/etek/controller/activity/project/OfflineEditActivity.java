package com.etek.controller.activity.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.adapter.OfflineEditAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.common.HandsetWorkMode;
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
import com.etek.controller.entity.DetCacheInput;
import com.etek.controller.entity.Detonator;
import com.etek.controller.entity.OfflineDownloadBean;
import com.etek.controller.hardware.test.HttpCallback;
import com.etek.controller.hardware.util.DetIDConverter;
import com.etek.controller.hardware.util.DetLog;
import com.etek.controller.hardware.util.SoundPoolHelp;
import com.etek.controller.model.User;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.scan.ScannerBase;
import com.etek.controller.scan.ScannerFactory;
import com.etek.controller.utils.AppUtils;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.JsonUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.VibrateUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Response;
import com.etek.controller.utils.SommerUtils;

public class OfflineEditActivity extends BaseActivity implements View.OnClickListener, OfflineEditAdapter.OnItemClickListener {

    private EditText proCode;
    private EditText contractCode;
    private TextView companyCode;
    private EditText controllerSn;
    private TextView numPostion;
    private TextView detCode;
    private TextView detStatus;
    private RecyclerView offlineRecycleView;
    private ArrayList<Detonator> detList;
    private OfflineEditAdapter offlineEditAdapter;

    private static final String SN_REGEX = "^[A-Z0-9]{15}";
    private static final String DATE_REGEX = "^(0?[1-9]|1[012])(0?[1-9]|[12][0-9]|3[01])";

    private String TAG = "OfflineEditActivity";
    private long proId;

    private DetCacheInput detCacheInput;
    private OfflineDownloadBean offlineDownloadBean;
    private boolean bSuccess=false;

    private ScannerBase scanner;
    private static final String RES_ACTION = "android.intent.action.SCANRESULT";
    private ScannerResultReceiver scanReceiver;
    private SoundPoolHelp soundPoolHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_edit);
        initView();
        initData();

        getSPData();

        initSoundPool();
    }

    // 获取sp中缓存的数据
    private void getSPData() {
        detList = new ArrayList<>();

        String offlineEditInfo = getStringInfo("offlineEditInfo");
        Log.d(TAG,"授权下载缓存:"+offlineEditInfo);
        if (!TextUtils.isEmpty(offlineEditInfo)) {
            offlineDownloadBean = JSON.parseObject(offlineEditInfo, OfflineDownloadBean.class);
            if (offlineDownloadBean!=null) {
                proCode.setText(offlineDownloadBean.getXmbh());
                contractCode.setText(offlineDownloadBean.getHtid());

                if(null!=offlineDownloadBean.getDets())
                    detList = offlineDownloadBean.getDets();
            }
        }
        if(null==offlineDownloadBean){
            offlineDownloadBean = new OfflineDownloadBean();
        }

        Intent intent = getIntent();
        String  strMode =intent.getStringExtra("projectMode");
        if(null!=strMode){
            if(strMode.equals("NEW")){
                detList.clear();
            }
        }

        Log.d(TAG,"缓存雷管数量："+detList.size());
        offlineEditAdapter = new OfflineEditAdapter(this, detList);
        offlineEditAdapter.setOnItemClickListener(this);
        offlineRecycleView.setLayoutManager(new LinearLayoutManager(OfflineEditActivity.this));
        offlineRecycleView.setAdapter(offlineEditAdapter);

        offlineEditInfo = getStringInfo("detCacheInput");
        if (!TextUtils.isEmpty(offlineEditInfo)) {
            detCacheInput = JSON.parseObject(offlineEditInfo, DetCacheInput.class);
            if (detCacheInput==null) {
                detCacheInput = new DetCacheInput();
                detCacheInput.init();
            }
        }

    }

    private void initView() {
        TextView textTitle = findViewById(R.id.text_title);
        TextView textBtn = findViewById(R.id.text_btn);
        View backImg = findViewById(R.id.back_img);
        textTitle.setText("离线授权编辑");
        textBtn.setText("校验");
        backImg.setOnClickListener(this);
        textBtn.setOnClickListener(this);
        proCode = findViewById(R.id.pro_code);
        // 项目编号可以输入英文字母
        //proCode.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        contractCode = findViewById(R.id.contract_code);
        //contractCode.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        companyCode = findViewById(R.id.company_code);
        controllerSn = findViewById(R.id.controller_sn);
        if(HandsetWorkMode.MODE_TEST>HandsetWorkMode.getInstance().getWorkMode()){
            controllerSn.setKeyListener(null);
        }

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
//        detList = new ArrayList<>();
//        offlineEditAdapter = new OfflineEditAdapter(this, detList);
//        offlineEditAdapter.setOnItemClickListener(this);
//        offlineRecycleView.setLayoutManager(new LinearLayoutManager(OfflineEditActivity.this));
//        offlineRecycleView.setAdapter(offlineEditAdapter);

        View clearDet = findViewById(R.id.clear_det);
        clearDet.setOnClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        showPopuWindow(view, position);
    }

    private void showPopuWindow(View view, int position) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        View popuView = getLayoutInflater().inflate(R.layout.popuwindow_view, null, false);
        PopupWindow mPopupWindow = new PopupWindow(popuView, 150, 60);
        popuView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detList.remove(position);
                offlineEditAdapter.notifyDataSetChanged();
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
        popuView.findViewById(R.id.insert_item).setVisibility(View.GONE);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, location[1] + 25);
    }

    private void initData() {
        // 回填单位代码
        String userStr = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        if (TextUtils.isEmpty(userStr)) {
            Intent intent = new Intent(this, UserInfoActivity2.class);
            startActivity(intent);
            return;
        } else {
            Globals.user = JSON.parseObject(userStr, User.class);
        }
        companyCode.setText(Globals.user.getCompanyCode());
        controllerSn.setText(getStringInfo(getString(R.string.controller_sno)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                areYouQuit();
                break;
            case R.id.text_btn:
                // 提示生成校验
                createOfflineData();
                break;
            case R.id.add_det:
                bSuccess = false;
                showDetOfflineDialog();
                break;

            case  R.id.clear_det:
                if(null==detList)
                    break;
                if(detList.size()>0){
                    clearAllDets();
                }
                break;
        }
    }

    private boolean strIsLength(String str) {
        if (str.length() > 0 && str.length() < 15) {
           return false;
        }
        return true;
    }

    private boolean strMageRegex(String  str){
        if (str.length() > 0) {
            Pattern compile = Pattern.compile(SN_REGEX);
            Matcher matcher = compile.matcher(str);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断是否含有空格
     * @param input
     * @return
     */
    public static boolean containSpace(CharSequence input){
        return Pattern.compile("\\s+").matcher(input).find();
    }

    /**
     * 判断字符串是否都是数字
     * @param str
     * @return
     */
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    private  boolean isDateString(String dateString){
        Pattern pattern = Pattern.compile(DATE_REGEX);
        Matcher isNum = pattern.matcher(dateString);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        saveData();
        releaseSound();
        super.onDestroy();
    }

    private void saveData() {
        String strContractCode = contractCode.getText().toString();
        String strProCode = proCode.getText().toString().toUpperCase();
        offlineDownloadBean.setHtid(strContractCode);
        offlineDownloadBean.setXmbh(strProCode);

        offlineDownloadBean.setDets(detList);

        setStringInfo("offlineEditInfo",JSON.toJSONString(offlineDownloadBean));

        setStringInfo("detCacheInput",JSON.toJSONString(detCacheInput));
    }

    /**
     * 生成离线文件
     */
    private void createOfflineData() {
        offlineDownloadBean.setDwdm(companyCode.getText().toString());
        String strContractCode = contractCode.getText().toString();
        String strProCode = proCode.getText().toString();

        //  长度为15位，数字开头，数字和字母组合
        String regex = "^[0-9][0-9A-Za-z]{14}$";
        boolean bOk = false;
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

        if (!TextUtils.isEmpty(strContractCode) || !TextUtils.isEmpty(strProCode)) {
            if (!TextUtils.isEmpty(strProCode)) {
                bOk = pattern.matcher(strProCode).matches();
                if(!bOk){
                    showDialogMessage("项目编号不符合规定！");
                    return;
                }
            }

            if (!TextUtils.isEmpty(strContractCode)) {
                bOk = pattern.matcher(strContractCode).matches();
                if(!bOk){
                    showDialogMessage("合同备案序号不符合规定！");
                    return;
                }
            }
            offlineDownloadBean.setHtid(strContractCode);
            offlineDownloadBean.setXmbh(strProCode);
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
//        // sp缓存编辑的信息
//        setStringInfo("offlineEditInfo",JSON.toJSONString(offlineDownloadBean));

        if (detList == null || detList.isEmpty()) {
            showToast("请输入雷管数");
            return;
        } else {
            offlineDownloadBean.setDets(detList);
        }
        showProDialog("正在得到检验数据中。。。");
        String rptJson = JSON.toJSONString(offlineDownloadBean, SerializerFeature.WriteMapNullValue);
        DetLog.writeLog(TAG,String.format("授权下载申请:%s",rptJson));

        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            missProDialog();
            showStatusDialog("数据编码出错：" + result.getMessage());
            return;
        }
        String url;
        // 测试服务器

        url = AppConstants.DanningServer + AppConstants.OfflineDownload;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());

        AsyncHttpCilentUtil.httpPostNew(this, url, params, new HttpCallback() {

            @Override
            public void onFaile(IOException e) {
                missProDialog();
                DetLog.writeLog(TAG,String.format("校验服务器出错：%s",e.getMessage()));
                showStatusDialog("与服务器通信失败！");
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
                    DetLog.writeLog(TAG,String.format("返回数据为空！"));
                    showStatusDialog("返回数据为空！");
                    return;
                }

                if (JsonUtil.isHtml(respStr)) {
                    Log.d(TAG,respStr);
                    DetLog.writeLog(TAG,String.format("返回数据HTML：%s",respStr));
                    showStatusDialog("返回数据HTML！" + respStr);
                    return;
                }

                try {
                    Result rptDecode = RptUtil.getRptDecode(respStr);
                    DetLog.writeLog(TAG,String.format("授权下载应答：%s",rptDecode));
                    if (rptDecode.isSuccess()) {
                        String data = (String) rptDecode.getData();
                        OnlineCheckStatusResp onlineCheckStatusResp = JSON.parseObject(data, OnlineCheckStatusResp.class);

                        if (onlineCheckStatusResp.getCwxx().equals("0")) {
                            OnlineCheckResp serverResult = JSON.parseObject(data, OnlineCheckResp.class);

                            if (serverResult.getLgs().getLg() == null || serverResult.getLgs().getLg().isEmpty()) {
                                DetLog.writeLog(TAG,String.format("雷管信息为空！"));
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
                                DetLog.writeLog(TAG,String.format("雷管信息异常！"));
                                showStatusDialog("雷管信息异常！");
                                offlineEditAdapter.notifyDataSetChanged();
                            }else{
                                bSuccess = true;
                            }
                            ProjectFileDto projectFile = new ProjectFileDto();
                            projectFile.setCompany(Globals.user.getCompanyName());
                            projectFile.setDwdm(Globals.user.getCompanyCode());
                            projectFile.setXmbh(offlineDownloadBean.getXmbh());
                            projectFile.setHtbh(offlineDownloadBean.getHtid());
                            ProInfoDto proInfoDto = new ProInfoDto();
                            BeanPropertiesUtil.copyProperties(serverResult, proInfoDto);

                            // 设置申请日期为当前日期
                            //proInfoDto.setSqrq(new Date());
                            projectFile.setProInfo(proInfoDto);

                            String strInfo = JSON.toJSONString(projectFile, AppUtils.filter);

                            updateETEKData(strInfo);

                            proId = storeProjectInfo(projectFile, serverResult);
                            if (proId != 0) {
                                showStatusDialog("项目保存成功！");
                            } else {
                                showStatusDialog("项目保存失败！");
                            }

                            // 缓存输入
                            if(!TextUtils.isEmpty(strContractCode))
                                detCacheInput.appendContractCode(strContractCode);
                            if(!TextUtils.isEmpty(strProCode))
                                detCacheInput.appendUnitCode(strProCode);

                        } else {
                            if (!StringUtils.isEmpty(onlineCheckStatusResp.getCwxxms())) {
                                showStatusDialog(onlineCheckStatusResp.getCwxxms());
                            } else {
                                DetLog.writeLog(TAG,String.format("服务器返回错误，数据错误 请调整！"));
                                showStatusDialog("服务器返回错误，数据错误 请调整！");
                            }
                        }
                    }
                } catch (Exception e) {
                    DetLog.writeLog(TAG,String.format("解析错误：",e.getMessage()));
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
        String spDetCodeStr = getPreInfo(AppSpSaveConstant.OFFLINE_EDIT_DET_CODE);
        etDetCode.setText(spDetCodeStr);
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
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String detCodeStr = etDetCode.getText().toString().trim();
                setStringInfo(AppSpSaveConstant.OFFLINE_EDIT_DET_CODE,detCodeStr);
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
        if (TextUtils.isEmpty(detCodeStr)) {
            showToast("请输入有效的雷管发编号！");
            return;
        }
        if (detCodeStr.length() != 13) {
            showToast("请输入有效的13位雷管发编号！");
            return;
        }

        if (containSpace(detCodeStr)) {
            showToast("雷管发编号不能包含空格！");
            return;
        }

        String substring = detCodeStr.substring(0, 7);
        if (!isNumeric(substring)) {
            showToast("请输入正确的雷管发编号！");
            return;
        }

        String subDatestring = detCodeStr.substring(3, 7);
        if (!isDateString(subDatestring)) {
            showToast("请输入正确的有效期内雷管发编号！");
            return;
        }

        if(!DetIDConverter.isValidMID(detCodeStr.substring(0,2))){
            showToast("不支持的雷管厂商："+detCodeStr.substring(0,2));
            return;
        }

        if (TextUtils.isEmpty(detNumStr)) {
            showToast("请输入有效的雷管数！");
            return;
        }
        if(detNumStr.length()>3){
            showToast("请输入有效的雷管数！");
            return;
        }

        int num = Integer.parseInt(detNumStr);
        if (num <= 0) {
            showToast("请输入有效的雷管数！");
            return;
        }
        int tube = Integer.parseInt(detCodeStr.substring(9, 13));
        Log.d(TAG,"detCodeStr.substring(9, 13)"+detCodeStr.substring(9, 13));
        Log.d(TAG,"tube is "+tube);

        boolean isOdd = false;
        if (tube % 2 != 0) {
            isOdd = true;
        }

        int nrepeatnum = 0;
        for (int i = 0; i < num; i++) {
            if (num >= 1000) {
                showToast("一次编辑雷管少于1000！");
                break;
            }
            String newFbh = detCodeStr.substring(0, 9) + String.format(Locale.CHINA, "%04d", tube);
            Detonator detonator = new Detonator(newFbh);
            detonator.getDetonatorByFbh(newFbh);

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

            if(isDetonatorExist(detonator.getDetCode())){
                Log.d(TAG,"雷管已经存在："+detonator.getDetCode());
                nrepeatnum++;
            }else{
                detList.add(detonator);
            }
        }

        offlineEditAdapter.notifyDataSetChanged();
        if(nrepeatnum>0){
            playSound(false);
            showDialogMessage(String.format("%d 颗雷管号重复！",nrepeatnum));
        }
    }

    /**
     * 清除所有输入的雷管信息
     */
    private void clearAllDets(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("是否要清除所有雷管？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                detList.clear();
                offlineEditAdapter.notifyDataSetChanged();
            }
        });
        builder.create().show();
        return;
    }

    private void areYouQuit(){
        //  如果校验成功or没有雷管信息，直接退出
        if(bSuccess||detList.size()==0){
            finish();
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("离线授权编辑完成？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,String.format("KeyCode=%d",keyCode));

        //  右下角的退出键
        if(KeyEvent.KEYCODE_BACK==keyCode){
            areYouQuit();
            return true;
        }

        int nAction = event.getAction();

        // 如果项目已经起爆，就不能修改
        if((KeyEvent.ACTION_DOWN==nAction)){
            // 只处理Key_DOWNW消息
            // 左边189 右边190  中间188
            if ((keyCode == 189) || ( 284 == keyCode)){
                scanner.doScan();
                return true;
            }
            // 中间按钮
            if ((keyCode == 188)||(288 == keyCode)) {
                scanner.doScan();
                return true;
            }
            // 右边按钮
            if ((keyCode == 190)||( 285 == keyCode)) {
                scanner.doScan();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    private void initSoundPool() {
        soundPoolHelp = new SoundPoolHelp(this);
        soundPoolHelp.initSound();
    }

    private void playSound(boolean b) {
        if (soundPoolHelp != null ) {
            soundPoolHelp.playSound(b);
        }

        //  成功也要震动
        VibrateUtil.vibrate(this,150);
        return;
    }


    private void releaseSound() {
        if (soundPoolHelp != null) {
            soundPoolHelp.releaseSound();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initScanner();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //取消接收扫描广播，并恢复输出模式为默认

        if (scanReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(scanReceiver);
            unregisterReceiver(scanReceiver);
        }

        Log.d(TAG,"scanner close");
        if (scanner != null) {
            scanner.unlockScanKey();
            scanner.setOutputMode(0);
            scanner.close();
        }
    }

    private void initScanner() {
        Log.d(TAG,"initScanner");
        scanner = ScannerFactory.getScannerObject(this);
        scanner.open();
        scanner.setOutputMode(1);
        scanner.lockScanKey();
        //  扫描失败是否发送广播
        scanner.SetErrorBroadCast(false);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RES_ACTION);

        //注册广播接受者
        scanReceiver = new ScannerResultReceiver();
        registerReceiver(scanReceiver, intentFilter);

        LocalBroadcastManager.getInstance(this).registerReceiver(scanReceiver,intentFilter);
    }


    /**
     * 扫描结果广播接收
     */
    //*********重要
    private class ScannerResultReceiver extends BroadcastReceiver {
        public synchronized void onReceive(Context context, Intent intent) {
            Log.d(TAG, "intent.getAction()-->" + intent.getAction());//

            //*******重要，注意Extral为"value"
            final String scanResult = intent.getStringExtra("value");

            Log.d(TAG, "onReceive: scanResult = " + scanResult);

            //*******重要
            if (!intent.getAction().equals(RES_ACTION)) {
                return;
            }

            //获取扫描结果
            if (scanResult.length() > 0 && DetIDConverter.VerifyQRCheckValue(scanResult)) { //如果条码长度>0，解码成功。如果条码长度等于0解码失败。
                // 扫描成功
                String strgm="";
                //  12位条码
                if(scanResult.length()==12){
                    byte[] dc = DetIDConverter.GetDCByOldQRString(scanResult);
                    strgm = DetIDConverter.GetDisplayDC(dc);
                } else{
                    strgm = scanResult.substring(0, 13);
                }

                Log.d(TAG,"扫描得到："+strgm);

                Detonator detonator = new Detonator(strgm);
                detonator.getDetonatorByFbh(strgm);

                if(isDetonatorExist(detonator.getDetCode())){
                    showToast(String.format("雷管【%s】已经存在列表中！",strgm));
                    playSound(false);
                    return;
                }else{
                    playSound(true);
                    detList.add(detonator);
                }
                offlineEditAdapter.notifyDataSetChanged();

                setStringInfo(AppSpSaveConstant.OFFLINE_EDIT_DET_CODE,strgm);
                return;
            }

            Log.d(TAG,String.format("扫描结果:%s",scanResult));
            if (scanResult.length() > 0){
                playSound(false);
            }
            return;
        }
    }

    /**
     * 雷管是否已经存在于列表中
     * @param detcode
     * @return
     */
    private boolean isDetonatorExist(String detcode){
        for (Detonator detonator : detList) {
            if(detcode.equals(detonator.getDetCode()))
                return true;
        }
        return false;
    }
}