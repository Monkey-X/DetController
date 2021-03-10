package com.etek.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.activity.project.MapActivity;
import com.etek.controller.activity.project.PowerBombActivity;
import com.etek.controller.adapter.CheckDetailAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.Jbqy;
import com.etek.controller.dto.Jbqys;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.Lgs;
import com.etek.controller.dto.OnlineCheckDto;
import com.etek.controller.dto.OnlineCheckResp;
import com.etek.controller.dto.ProjectFileDto;
import com.etek.controller.dto.ProjectInfoDto;
import com.etek.controller.dto.Sbbhs;
import com.etek.controller.dto.WhiteBlackController;
import com.etek.controller.dto.Zbqy;
import com.etek.controller.dto.Zbqys;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.EntryCopyUtil;
import com.etek.controller.enums.CheckRuleEnum;
import com.etek.controller.hardware.util.DetLog;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.LocationUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 检查详情页
 */
public class CheckDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CheckDetailActivity";
    private long proId;
    private TextView contractCode;
    private TextView controllerId;
    private EditText locationLongitude;
    private EditText locationLatitude;
    private Button getLocation;
    private EditText proCode;
    private RecyclerView detonatorList;
    private CheckDetailAdapter checkDetailAdapter;
    private List<ProjectDetonator> projectDetonatorList;
    private PendingProject pendingProject;
    private int GO_TO_GPS = 150;
    private StringBuilder uid = new StringBuilder();
    private String type;

    private List<String> whiteList;
    private List<String> blackList;
    private LocationManager locationManager;

    private boolean m_bChecking =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_detail);
        getProjectId();
        initTitle();
        initView();
        getLocationLocal();
        getWhiteBlackList();
        initBaiduLocation();
    }

    private void initTitle() {
        View backImg = findViewById(R.id.back_img);
        backImg.setOnClickListener(this);
        TextView textTitle = findViewById(R.id.text_title);
        TextView textbtn = findViewById(R.id.text_btn);
        textbtn.setText("检查");
        textbtn.setOnClickListener(this);
        if ("online".equals(type)) {
            textTitle.setText("在线详情");
        } else if ("offline".equals(type)) {
            textTitle.setText("离线详情");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_location:
                // 跳转地图界面
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.back_img:
                finish();
                break;
            case R.id.text_btn:
                projectCheckData();
                break;

        }
    }

    private void initBaiduLocation() {
        LocationClient locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setWifiCacheTimeOut(2 * 60 * 1000);
        option.setNeedNewVersionRgc(true);
        locationClient.setLocOption(option);
        locationClient.start();
    }

    /**
     * 得到本地缓存中的地址
     */
    void getLocationLocal() {
        locationLongitude.setText("");
        locationLatitude.setText("");

//        String longitudeStr = getStringInfo("Longitude");
//        String latitudeStr = getStringInfo("Latitude");
//        if (!StringUtils.isEmpty(longitudeStr) && !(StringUtils.isEmpty(latitudeStr))) {
//            double longitude = Double.valueOf(longitudeStr);
//            double latitude = Double.valueOf(latitudeStr);
//            setCacheLongitude(longitude);
//            setCacheLatitude(latitude);
//
//            locationLongitude.setText("" + longitude);
//            locationLatitude.setText("" + latitude);

//            if (pendingProject != null) {
//                pendingProject.setLongitude(longitude);
//                pendingProject.setLatitude(latitude);
//            }
//        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //  如果正在检查中，不更新经纬度
            if(m_bChecking) return;

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息

            setCacheLongitude(longitude);
            setCacheLatitude(latitude);

            //  百度获取到的经纬度，只是显示在TextView里，不缓存
            locationLongitude.setText(String.format("%.4f" ,longitude));
            locationLatitude.setText(String.format("%.4f",latitude));

//            setStringInfo("Longitude", String.format("%.4f" ,longitude));
//            setStringInfo("Latitude", String.format("%.4f",latitude));
//            if (pendingProject != null) {
//                pendingProject.setLongitude(longitude);
//                pendingProject.setLatitude(latitude);
//            }
        }
    }

    // 获取黑白名单
    private void getWhiteBlackList() {
        whiteList = new ArrayList<>();
        blackList = new ArrayList<>();
        String url = AppConstants.ETEKTestServer + AppConstants.WhiteBlackList;
        AsyncHttpCilentUtil.getOkHttpClient(url, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
                    WhiteBlackController whiteBlackController = JSON.parseObject(respStr, WhiteBlackController.class);
                    List<WhiteBlackController.Hbmd> hbmds = whiteBlackController.getHbmd();
                    if (hbmds != null && !hbmds.isEmpty()) {
                        for (WhiteBlackController.Hbmd hbmd : hbmds) {
                            if (hbmd.getStatus() == 2) {
                                whiteList.add(hbmd.getSbbh());
                            } else if (hbmd.getStatus() == 1) {
                                blackList.add(hbmd.getSbbh());
                            }
                        }
                    }
                    XLog.d("white:" + whiteList);
                    XLog.d("black:" + blackList);
                }
            }
        });
    }

    /**
     * 检查起爆是否在黑名单中
     *
     * @param sn 起爆器编号
     * @return true表示在黑名单中，false表示不在
     */
    private boolean isInBlackList(String sn) {
        if (blackList != null && !blackList.isEmpty()) {
            for (String s : blackList) {
                if (s.equalsIgnoreCase(sn))
                    return true;
            }
        }
        return false;
    }

    // 检查起爆器是否在白名单中
    private boolean isInWhiteList(String sn) {
        if (whiteList != null && !whiteList.isEmpty()) {
            for (String s : whiteList) {
                if (s.equalsIgnoreCase(sn))
                    return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 获取项目id
     */
    private void getProjectId() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        proId = intent.getLongExtra(AppIntentString.PROJECT_ID, -1);
        XLog.d("proId: " + proId);
        if (proId >= 0) {
            pendingProject = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.Id.eq(proId)).unique();
            pendingProject.refresh();
            projectDetonatorList= DBManager.getInstance().getProjectDetonatorDao().queryBuilder().where(ProjectDetonatorDao.Properties.ProjectInfoId.eq(proId)).list();
        }

        //  起爆器编号使用全局设置信息
        pendingProject.setControllerId(getStringInfo(getString(R.string.controller_sno)));
    }

    /**
     * 初始化View
     */
    private void initView() {
        LinearLayout layoutPro = findViewById(R.id.layout_pro);
        LinearLayout layoutContract = findViewById(R.id.layout_contract);
        if ("offline".equals(type)) {
            layoutContract.setVisibility(View.GONE);
            layoutPro.setVisibility(View.GONE);
        }
        contractCode = findViewById(R.id.contract_code);
        //contractCode.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        controllerId = findViewById(R.id.ctrl_id);
        locationLongitude = findViewById(R.id.ctrl_location_longitude);
        locationLatitude = findViewById(R.id.ctrl_location_latitude);
        //  经纬度禁止输入
//        locationLongitude.setKeyListener(null);
//        locationLatitude.setKeyListener(null);

        getLocation = findViewById(R.id.get_location);
        proCode = findViewById(R.id.pro_code);
        //proCode.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        detonatorList = findViewById(R.id.check_detonator_list);
        getLocation.setOnClickListener(this);
        detonatorList.setLayoutManager(new LinearLayoutManager(this));
        checkDetailAdapter = new CheckDetailAdapter(R.layout.detonator_list_item, projectDetonatorList);
        detonatorList.setAdapter(checkDetailAdapter);

        if (pendingProject != null) {
            //合同编号
            contractCode.setText(pendingProject.getContractCode());
            //项目编号
            proCode.setText(pendingProject.getProCode());
            //地标
        }
        //起爆器编号
        controllerId.setText(getStringInfo(getString(R.string.controller_sno)));
    }

    /***
     * 缓存界面输入
     */
    private void saveData(){
        if (pendingProject == null)
            return;

        //合同编号
        pendingProject.setContractCode(contractCode.getText().toString());
        //起爆器编号
        pendingProject.setControllerId(controllerId.getText().toString());
        //项目编号
        pendingProject.setProCode(proCode.getText().toString());

        DBManager.getInstance().getPendingProjectDao().save(pendingProject);
    }
    /**
     * 注销
     */
    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    /**
     * 进行规则的检查
     */
    private void projectCheckData() {
        if (TextUtils.isEmpty(locationLongitude.getText().toString().trim())) {
            ToastUtils.show(mContext, "当前经度为空");
        } else if (TextUtils.isEmpty(locationLatitude.getText().toString().trim())) {
            ToastUtils.show(mContext, "当前纬度为空");
        } else {
            //  规则检查时pendingProject的经纬度根据界面上的调整
            double longitude = Double.valueOf(locationLongitude.getText().toString().trim());
            double latitude = Double.valueOf(locationLatitude.getText().toString().trim());

            setCacheLongitude(longitude);
            setCacheLatitude(latitude);

            //  缓存当前地址（就算所有检查不合格，项目还有经纬度）
            if (pendingProject != null) {
                pendingProject.setLongitude(getEmuLongLatitude(longitude));
                pendingProject.setLatitude(getEmuLongLatitude(latitude));

                DetLog.writeLog(TAG,String.format("工程经纬度:%s,%s",longitude,latitude));
            }

            m_bChecking = true;
            if ("online".equals(type)) {//在线检查
                getVerifyResult(pendingProject);
            } else if ("offline".equals(type)) {//离线检查
                offlineCheck();
            }
            m_bChecking = false;

        }
    }

    private void goToBomb() {
        if (pendingProject != null) {
            pendingProject.setProjectStatus(AppIntentString.PROJECT_IMPLEMENT_POWER_BOMB1);
            DBManager.getInstance().getPendingProjectDao().save(pendingProject);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("检查成功，请进行充电起爆！");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CheckDetailActivity.this, PowerBombActivity.class);
                intent.putExtra(AppIntentString.PROJECT_ID, proId);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 获取验证结果
     */
    private void getVerifyResult(PendingProject projectInfoEntity) {

//        String longitude = locationLongitude.getText().toString().trim();
//        String latitue = locationLatitude.getText().toString().trim();
//        projectInfoEntity.setLongitude(Double.parseDouble(longitude));
//        projectInfoEntity.setLatitude(Double.parseDouble(latitue));

        String strContractCode = contractCode.getText().toString().trim();
        String strProCode = proCode.getText().toString().trim();

        projectInfoEntity.setContractCode(strContractCode);
        projectInfoEntity.setProCode(strProCode);

        OnlineCheckDto onlineCheckDto = new OnlineCheckDto();
        onlineCheckDto.setDwdm(projectInfoEntity.getCompanyCode());
        onlineCheckDto.setHtid(projectInfoEntity.getContractCode());

//        onlineCheckDto.setJd(projectInfoEntity.getLongitude() + "");
//        onlineCheckDto.setWd(projectInfoEntity.getLatitude() + "");

        // 使用界面获取到的经纬度
        onlineCheckDto.setJd(getCacheLongitude() + "");
        onlineCheckDto.setWd(getCacheLatitude() + "");

        onlineCheckDto.setXmbh(projectInfoEntity.getProCode());
        onlineCheckDto.setSbbh(controllerId.getText().toString());
        onlineCheckDto.setProjectDets(projectDetonatorList);

        String rptJson = JSON.toJSONString(onlineCheckDto, SerializerFeature.WriteMapNullValue);
        DetLog.writeLog("在线检查","rptJson: " + rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showToast("数据编码出错：" + result.getMessage());
            return;
        }
        showProDialog("在线检查中...");
        String url = AppConstants.DanningServer + AppConstants.OnlineDownload;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());
        //String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");
        //Log.d(TAG, "newUrl: " + newUrl);
        AsyncHttpCilentUtil.httpPost(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        missProDialog();
                        DetLog.writeLog(TAG,"请求服务器失败，" + e.toString());
                        showStatusDialog("请求服务器失败，" + e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 展示在线检查的结果
                        showOnlineCheck(response);
                    }
                });
            }
        });
    }

    private void showOnlineCheck(Response response) {
        missProDialog();
        String respStr = null;
        try {
            respStr = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(respStr)) {
            DetLog.writeLog(TAG, "respStr is null");
            showToast("服务器返回数据失败！");
            return;
        }
        Log.d(TAG, "respStr: " + respStr);
        OnlineCheckResp serverResult = null;
        try {
            Result rptDecode = RptUtil.getRptDecode(respStr);
            DetLog.writeLog(TAG, "在线检查应答: " + rptDecode);
            if (rptDecode.isSuccess()) {
                String data = (String) rptDecode.getData();
                Log.d(TAG, "resp:" + data);
                serverResult = JSON.parseObject(data, OnlineCheckResp.class);
                Log.d(TAG, "serverResult: " + serverResult.toString());
                if (serverResult.getCwxx().contains("0")) {
                    ProjectFileDto projectFile = new ProjectFileDto();
                    projectFile.setCompany(Globals.user.getCompanyName());
                    projectFile.setDwdm(Globals.user.getCompanyCode());
                    projectFile.setXmbh(pendingProject.getProCode());
                    projectFile.setHtbh(pendingProject.getContractCode());
                    int unRegDet = 0;
                    boolean isUnreg = false;
                    for (ProjectDetonator detonator : projectDetonatorList) {
                        for (Lg lg : serverResult.getLgs().getLg()) {
                            if (detonator.getUid().equalsIgnoreCase(lg.getUid())) {
                                if (lg.getGzmcwxx() != 0) {
                                    detonator.setStatus(lg.getGzmcwxx());
                                    isUnreg = true;
                                    unRegDet++;
                                }
                            }
                        }
                    }
                    refreshData();

                    long projectId = storeProjectInfo(projectFile, serverResult);

                    Zbqys zbqys = serverResult.getZbqys();
                    List<Zbqy> zbqy = zbqys.getZbqy();
                    if (zbqy != null && zbqy.size() != 0) {
                        Zbqy zbqy1 = zbqy.get(0);
                        pendingProject.setLongitude(getEmuLongLatitude(Double.parseDouble(zbqy1.getZbqyjd())));
                        pendingProject.setLatitude(getEmuLongLatitude(Double.parseDouble(zbqy1.getZbqywd())));

                        Log.d(TAG,String.format("工程经纬度:%s,%s",zbqy1.getZbqyjd(),zbqy1.getZbqywd()));
                    }

                    if (isUnreg) {
                        DetLog.writeLog(TAG, "已使用雷管:" + unRegDet);
                        showStatusDialog("已存在已使用雷管！");
                        uploadData(projectId);
                        return;
                    }

                    //  在线检查数据上报时，使用当前经纬度
//                    Zbqys zbqys = serverResult.getZbqys();
//                    List<Zbqy> zbqy = zbqys.getZbqy();
//                    if (zbqy != null && zbqy.size() != 0) {
//                        Zbqy zbqy1 = zbqy.get(0);
//                        DecimalFormat df = new DecimalFormat("0.00");
//                        String longitude = df.format(Double.parseDouble(zbqy1.getZbqyjd()));
//                        Random random = new Random();
//                        int ends = random.nextInt(99);
//                        longitude += String.format(Locale.CHINA, "%02d", ends);
//                        DecimalFormat df2 = new DecimalFormat("0.000");
//                        String latitude = df2.format(Double.parseDouble(zbqy1.getZbqywd()));
//                        ends = random.nextInt(99);
//                        latitude += String.format(Locale.CHINA, "%02d", ends);
//                        pendingProject.setLongitude(Double.parseDouble(longitude));
//                        pendingProject.setLatitude(Double.parseDouble(latitude));

//                        pendingProject.setLongitude(Double.parseDouble(zbqy1.getZbqyjd()));
//                        pendingProject.setLatitude(Double.parseDouble(zbqy1.getZbqywd()));
//
//                        Log.d(TAG,String.format("工程经纬度:%s,%s",zbqy1.getZbqyjd(),zbqy1.getZbqywd()));
//                    }
                    goToBomb();
                    uploadData(projectId);
                } else {
                    showStatusDialog(serverResult.getCwxxms());
                }
            }
        } catch (Exception e) {
            DetLog.writeLog(TAG, "解析错误：" + e.getMessage());
        }
    }

    /**
     *
     * @param projectFile
     * @param onlineCheckResp
     */
    private long storeProjectInfo(ProjectFileDto projectFile, OnlineCheckResp onlineCheckResp) {
        XLog.v("onlineCheckResp:"+ onlineCheckResp);
        ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
        projectInfoEntity.setApplyDate(onlineCheckResp.getSqrq());
        projectInfoEntity.setProCode(projectFile.getXmbh());
        projectInfoEntity.setProName(projectFile.getXmmc());
        projectInfoEntity.setCompanyCode(projectFile.getDwdm());
        projectInfoEntity.setCompanyName(projectFile.getDwmc());
        projectInfoEntity.setContractCode(projectFile.getHtbh());
        projectInfoEntity.setContractName(projectFile.getHtmc());
        projectInfoEntity.setIsOnline(true);
        projectInfoEntity.setStatus(0);
        projectInfoEntity.setCreateTime(new Date());

        long proId = DBManager.getInstance().getProjectInfoEntityDao().insert(projectInfoEntity);
        if (proId == 0) {
            return 0;
        }
        XLog.v("proid:"+proId);
        Lgs lgs = onlineCheckResp.getLgs();
        if (!lgs.getLg().isEmpty()) {
            List<DetonatorEntity> detonatorEntityList = new ArrayList<>();
            for (Lg lg : lgs.getLg()) {

                DetonatorEntity detonatorBean = new DetonatorEntity();
                if (StringUtils.isEmpty(lg.getFbh())) {
                    for (ProjectDetonator detonator : projectDetonatorList) {
                        if (detonator.getUid().equalsIgnoreCase(lg.getUid())) {
                            lg.setFbh(detonator.getCode());
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

    String getReportDto(ProjectInfoEntity projectInfoEntity) {
        ProjectInfoDto projectInfoDto = new ProjectInfoDto();
        try {
            BeanPropertiesUtil.copyProperties(projectInfoEntity, projectInfoDto);

        } catch (Exception e) {
            e.printStackTrace();
        }
        projectInfoDto.setCreateTime(new Date());
        DetController detController = new DetController();
        detController.setContractId("");
        DetController detController1 = EntryCopyUtil.copyInfoToDetController(detController, pendingProject);
        // 经纬度不能用pendingProject的，使用参数projectInfoEntity的经纬度
        detController1.setLongitude(projectInfoEntity.getLongitude());
        detController1.setLatitude(projectInfoEntity.getLatitude());

        projectInfoDto.addDetControllers(detController1);
        return JSON.toJSONString(projectInfoDto);
    }

    private void refreshData() {
        DBManager.getInstance().getProjectDetonatorDao().saveInTx(projectDetonatorList);
        checkDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 离线检查
     */
    private void offlineCheck() {
        String strControllerId = controllerId.getText().toString().trim();

        if (isInBlackList(strControllerId)) {
            showStatusDialog(String.format("起爆器[%s]未注册，不允许起爆1",strControllerId));
            return;
        }

        // 如果起爆器在白名单中直接允许起爆
        if (isInWhiteList(strControllerId)) {
            changeDetStatus();
            goToBomb();
            return;
        }

        List<ProjectInfoEntity> projectInfoEntityList = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.CreateTime)
                .limit(100).list();
        if (projectInfoEntityList.size() == 0) {
            showStatusDialog("请下载离线文件！");
            return;
        }
        Long detInProjectId = isDetInProject(projectInfoEntityList);
        if (detInProjectId == -1) {
            showStatusDialog("没有找到雷管规则所对应的项目1");
            return;
        }
        ProjectInfoEntity projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                queryBuilder()
                .where(ProjectInfoEntityDao.Properties.Id.eq(detInProjectId)).unique();
        if (projectInfo == null) {
            showStatusDialog("没有找到雷管规则所对应的项目2");
            return;
        }

        //  离线不检查F99起爆器
        Log.d(TAG,"起爆器厂商："+strControllerId.substring(0,3));
        if(!strControllerId.substring(0,3).equals("F99")){
            if (!checkControllerData(projectInfo)) {
                showStatusDialog(String.format("起爆器[%s]未注册，不允许起爆2",strControllerId));
                return;
            }
        }

        if (checkForbiddenZone(projectInfo)) {
            showStatusDialog("在禁爆区域");
            return;
        }
        if (!checkPermissibleZone(projectInfo)) {
            showStatusDialog("不在准爆区域");
            return;
        }

        // 最后检查雷管的数量
        checkDetonatorData(projectInfo,detInProjectId);

    }

    private void changeDetStatus() {
        for (int i = 0; i < projectDetonatorList.size(); i++) {
            ProjectDetonator projectDetonator = projectDetonatorList.get(i);
            projectDetonator.setStatus(0);
        }
        checkDetailAdapter.notifyDataSetChanged();
        DBManager.getInstance().getProjectDetonatorDao().saveInTx(projectDetonatorList);
    }

    /**
     * 检查项目中的雷管信息
     *
     * @param projectInfo
     * @param detInProjectId
     */
    private void checkDetonatorData(ProjectInfoEntity projectInfo, Long detInProjectId) {
        List<DetonatorEntity> detonatorList = projectInfo.getDetonatorList();
        if (detonatorList != null && detonatorList.size() != 0) {
            int unUserCount = detonatorList.size();
            int unRegiestCount = 0;
            boolean isUnUsed;
            for (ProjectDetonator projectDetonator : projectDetonatorList) {
                isUnUsed = true;
                for (DetonatorEntity detonatorEntity : detonatorList) {
                    if (projectDetonator.getCode().equalsIgnoreCase(detonatorEntity.getCode())
                            && projectDetonator.getUid().equalsIgnoreCase(detonatorEntity.getUid())) {
                        if (!DetUtil.getAcCodeFromDet(detonatorEntity).equalsIgnoreCase(detonatorEntity.getWorkCode()))
                            break;
                        unUserCount--;
                        if (unUserCount < 0) {
                            unUserCount = 0;
                        }
                        isUnUsed = false;
                        projectDetonator.setStatus(0);
                    }
                }
                if (isUnUsed) {
                    projectDetonator.setStatus(2);
                    unRegiestCount++;
                }
            }

            refreshData();
            if (unRegiestCount > 0) {
                showStatusDialog(CheckRuleEnum.USED_DET.getMessage() + unRegiestCount);
                return;
            }

            //  离线检查数据上报时，获取当前经纬度
//            List<PermissibleZoneEntity> permissibleZoneList = projectInfo.getPermissibleZoneList();
//            if (permissibleZoneList != null && permissibleZoneList.size() != 0) {
//                PermissibleZoneEntity permissibleZoneEntity = permissibleZoneList.get(0);
//                DecimalFormat df = new DecimalFormat("0.00");
//                String longitude = df.format(permissibleZoneEntity.getLongitude());
//                Random random = new Random();
//                int ends = random.nextInt(99);
//                longitude += String.format(Locale.CHINA, "%02d", ends);
//                DecimalFormat df2 = new DecimalFormat("0.000");
//                String latitude = df2.format(permissibleZoneEntity.getLatitude());
//                ends = random.nextInt(99);
//                latitude += String.format(Locale.CHINA, "%02d", ends);
//                pendingProject.setLongitude(Double.parseDouble(longitude));
//                pendingProject.setLatitude(Double.parseDouble(latitude));
//            }
            goToBomb();
            uploadData(projectInfo);
        }
    }

    private void uploadData(long proId) {
        ProjectInfoEntity projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                queryBuilder()
                .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
        if (projectInfo == null) {
            return;
        }
        uploadData(projectInfo);
    }

    private void uploadData(ProjectInfoEntity projectInfoEntity) {
        Log.d(TAG,String.format("缓存[%.4f,%.4f]",getCacheLongitude(),getCacheLatitude()));

        //  2021-02-19  用当前界面上的经纬度来在线/离线检查上报
        projectInfoEntity.setLongitude(getCacheLongitude());
        projectInfoEntity.setLatitude(getCacheLatitude());


        Log.d(TAG,String.format("上报ETEK 经纬度:%.5f,%.5f",projectInfoEntity.getLongitude(),projectInfoEntity.getLatitude()));

        String rptJson = getReportDto(projectInfoEntity);
        String url = AppConstants.ETEKTestServer + AppConstants.CheckoutReport;

        Log.d(TAG,String.format("上报ETEK JSON:%s",rptJson));

        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().toString());
            }
        });
    }

    /**
     * 检查是否在准爆区域
     *
     * @param projectInfo
     */
    private boolean checkPermissibleZone(ProjectInfoEntity projectInfo) {
//        String longitude = locationLongitude.getText().toString().trim();
//        String latitue = locationLatitude.getText().toString().trim();
//        setCacheLongitude(Double.parseDouble(longitude));
//        setCacheLatitude(Double.parseDouble(latitue));

        List<PermissibleZoneEntity> permissibleZoneList = projectInfo.getPermissibleZoneList();
        if (permissibleZoneList != null && permissibleZoneList.size() != 0) {
            for (PermissibleZoneEntity permissibleZoneEntity : permissibleZoneList) {
                LocationUtil.LocationRange range = LocationUtil.getAround(permissibleZoneEntity.getLatitude(), permissibleZoneEntity.getLongitude(), 30000);
//                LocationUtil.LocationRange range = LocationUtil.getAround(permissibleZoneEntity.getLatitude(), permissibleZoneEntity.getLongitude(), permissibleZoneEntity.getRadius());
                Log.d(TAG,String.format("缓存[%.4f,%.4f], 范围[%.4f,%.4f]",
                        getCacheLongitude(),getCacheLatitude(),
                        permissibleZoneEntity.getLongitude(),permissibleZoneEntity.getLatitude()));

                if (getCacheLatitude() > range.getMinLat()
                        && getCacheLatitude() < range.getMaxLat()
                        && getCacheLongitude() > range.getMinLng()
                        && getCacheLongitude() < range.getMaxLng()) {
                    //  离线：缓存准爆区域
                    pendingProject.setLongitude(getEmuLongLatitude(permissibleZoneEntity.getLongitude()));
                    pendingProject.setLatitude(getEmuLongLatitude(permissibleZoneEntity.getLatitude()));

                    Log.d(TAG,String.format("工程经纬度:%s,%s",permissibleZoneEntity.getLongitude(),permissibleZoneEntity.getLatitude()));

                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查起爆器的是否存在文件中
     *
     * @param projectInfo
     */
    private boolean checkControllerData(ProjectInfoEntity projectInfo) {
        List<ControllerEntity> controllerList = projectInfo.getControllerList();
        if (controllerList != null && controllerList.size() != 0) {
            for (ControllerEntity controllerEntity : controllerList) {
                if (controllerEntity.getName().equalsIgnoreCase(controllerId.getText().toString().trim())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 是否在禁止区域
     *
     * @param projectInfo
     * @return
     */
    private boolean checkForbiddenZone(ProjectInfoEntity projectInfo) {
        List<ForbiddenZoneEntity> forbiddenZoneList = projectInfo.getForbiddenZoneList();
        if (forbiddenZoneList != null && forbiddenZoneList.size() != 0) {
            for (ForbiddenZoneEntity forbiddenZoneEntity : forbiddenZoneList) {
                LocationUtil.LocationRange range = LocationUtil.getAround(forbiddenZoneEntity.getLatitude(), forbiddenZoneEntity.getLongitude(), forbiddenZoneEntity.getRadius());
                if (getCacheLatitude() > range.getMinLat()
                        && getCacheLatitude() < range.getMaxLat()
                        && getCacheLongitude() > range.getMinLng()
                        && getCacheLongitude() < range.getMinLng()) {
                    return true;
                }
            }
        }
        return false;
    }

    // 检查雷管是否在下载的离线文件中
    private long isDetInProject(List<ProjectInfoEntity> projectInfoEntityList) {
        for (ProjectInfoEntity projectInfoEntity : projectInfoEntityList) {
            for (DetonatorEntity detonatorEntity : projectInfoEntity.getDetonatorList()) {
                for (ProjectDetonator detonator : projectDetonatorList) {
                    if (detonator.getCode().equalsIgnoreCase(detonatorEntity.getCode())) {
                        return projectInfoEntity.getId();
                    }
                }
            }

        }
        return -1;
    }

    private Double m_cacheLongitude;
    private Double m_cacheLatitude;

    private void setCacheLongitude(Double longitude){
        m_cacheLongitude = longitude;
        Log.d(TAG,String.format("缓存经度:%.4f",m_cacheLongitude));
    }
    private Double getCacheLongitude(){
        return m_cacheLongitude;
    }
    private void setCacheLatitude(Double latitude){
        m_cacheLatitude = latitude;
        Log.d(TAG,String.format("缓存维度:%.4f",m_cacheLatitude));
    }
    private Double getCacheLatitude(){
        return m_cacheLatitude;
    }

    //  修改经纬度后4,5位
    private Double getEmuLongLatitude(Double dval) {
        int n0 = (int)(dval*1000);
        n0= n0*100;

        Random random = new Random();
        int ends = random.nextInt(99);
        n0 = n0+ends;

        Double d = (n0*1.00)/(1000*100);
        return d;
    }
}
