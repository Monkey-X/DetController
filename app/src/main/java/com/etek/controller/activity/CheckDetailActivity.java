package com.etek.controller.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.activity.project.MapActivity;
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
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.LocationUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.NetUtil;
import com.etek.sommerlibrary.utils.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

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
    private TextView controllerTime;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_detail);
        getProjectId();
        initView();
        getWhiteBlackList();
        localLocationInit();
    }

    /**
     * 初始化原生的定位功能
     */
    @SuppressLint("MissingPermission")
    private void localLocationInit() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String content = "";
        List<String> providerList = locationManager.getProviders(true);
        String locationProvider;
        if (providerList.contains(LocationManager.NETWORK_PROVIDER) && NetUtil.getNetType(mContext) == 0) {//Google服务被墙不可用
            //网络定位的精准度稍差，但耗电量比较少。
            Log.d(TAG, "localLocationInit: =====NETWORK_PROVIDER=====");
            locationProvider = LocationManager.NETWORK_PROVIDER;
            content = "网络开始定位！";
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            //GPS 定位的精准度比较高，但是非常耗电。
            Log.d(TAG, "localLocationInit: =====GPS_PROVIDER=====");
            locationProvider = LocationManager.GPS_PROVIDER;
            content = "GPS开始定位！";
        } else {
            Log.d(TAG, "localLocationInit: ======NO_PROVIDER=====");
            // 当没有可用的位置提供器时，弹出Toast提示用户
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
            return;
        }
        showProDialog(content);

        locationManager.requestLocationUpdates(locationProvider, 5000, 0, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                missProDialog();
                Log.d(TAG, "onLocationChanged: " + location.getLongitude() + "," + location.getLatitude() + "," + location.getProvider());
                updateLocation(location);

                //只执行一次就停了自己  否则这个方法会根据参数不断的执行。
                locationManager.removeUpdates(this);
            }

            @Override
            public void onProviderDisabled(String provider) {
                missProDialog();
                Log.d(TAG, "onProviderDisabled" + provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                missProDialog();
                Log.d(TAG, "onProviderEnabled" + provider);
                updateLocation(locationManager
                        .getLastKnownLocation(provider));

            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                missProDialog();
                Log.d(TAG, "onStatusChanged" + provider);
            }
        });
    }

    private void updateLocation(Location location) {
        if (location == null) {
            return;
        }
        DecimalFormat df = new DecimalFormat("#.000000");
        String strLong = df.format(location.getLongitude());
        setStringInfo("Longitude", strLong);
        String strLat = df.format(location.getLatitude());
        setStringInfo("Latitude", strLat);

        locationLongitude.setText(strLong);
        locationLatitude.setText(strLat);
        if (pendingProject != null) {
            pendingProject.setLatitude(location.getLatitude());
            pendingProject.setLongitude(location.getLongitude());
        }
        if (location.getProvider().contains("network")) {
            locationLongitude.setTextColor(getMyColor(R.color.color_ac74ff));
            locationLatitude.setTextColor(getMyColor(R.color.color_ac74ff));
        } else {
            locationLongitude.setTextColor(getMyColor(R.color.colorPrimary));
            locationLatitude.setTextColor(getMyColor(R.color.colorPrimary));
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
     * @return
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
        if ("online".equals(type)) {
            initSupportActionBar(R.string.online_detail);
        } else if ("offline".equals(type)) {
            initSupportActionBar(R.string.offline_detail);
        }
        proId = intent.getLongExtra(AppIntentString.PROJECT_ID, -1);
        XLog.d("proId: " + proId);
        if (proId >= 0) {
            pendingProject = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.Id.eq(proId)).unique();
            projectDetonatorList = pendingProject.getDetonatorList();
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        contractCode = findViewById(R.id.contract_code);
        controllerId = findViewById(R.id.ctrl_id);
        locationLongitude = findViewById(R.id.ctrl_location_longitude);
        locationLatitude = findViewById(R.id.ctrl_location_latitude);
        getLocation = findViewById(R.id.get_location);
        controllerTime = findViewById(R.id.ctrl_time);
        detonatorList = findViewById(R.id.check_detonator_list);
        getLocation.setOnClickListener(this);
        detonatorList.setLayoutManager(new LinearLayoutManager(this));
        checkDetailAdapter = new CheckDetailAdapter(R.layout.detonator_list_item, projectDetonatorList);
        detonatorList.setAdapter(checkDetailAdapter);

        if (pendingProject != null) {
            //合同编号
            contractCode.setText(pendingProject.getContractCode());
            //起爆器编号
            controllerId.setText(getStringInfo(getString(R.string.controller_sno)));

            controllerTime.setText(pendingProject.getDate());
            //地标
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.get_location) {
            // 跳转地图界面
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 注销
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 右上角检查按钮
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check_detail, menu);
        return true;
    }

    /**
     * 检查按钮选中事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();//返回按钮
        } else if (item.getItemId() == R.id.action_check) {//检查
            // 点击进行检查操作
            projectCheckData();
        }
        return true;
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
            if ("online".equals(type)) {//在线检查
                getVerifyResult(pendingProject);
            } else if ("offline".equals(type)) {//离线检查
                offlineCheck();
            }
        }
    }

    private void goToBomb() {
        PendingProject projectInfoEntity = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.Id.eq(proId)).unique();
        if (projectInfoEntity != null) {
            projectInfoEntity.setProjectStatus(AppIntentString.PROJECT_IMPLEMENT_POWER_BOMB1);
            DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
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

        OnlineCheckDto onlineCheckDto = new OnlineCheckDto();
        onlineCheckDto.setDwdm(projectInfoEntity.getCompanyCode());
        onlineCheckDto.setHtid(projectInfoEntity.getContractCode());
        onlineCheckDto.setJd(projectInfoEntity.getLongitude() + "");
        onlineCheckDto.setWd(projectInfoEntity.getLatitude() + "");
        onlineCheckDto.setXmbh(projectInfoEntity.getProCode());
        onlineCheckDto.setSbbh(projectInfoEntity.getControllerId());
        onlineCheckDto.setProjectDets(projectDetonatorList);
        String rptJson = JSON.toJSONString(onlineCheckDto, SerializerFeature.WriteMapNullValue);
        XLog.e("rptJson: " + rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showToast("数据编码出错：" + result.getMessage());
            return;
        }
        showProDialog("在线检查中...");
        String url = AppConstants.DanningServer + AppConstants.OnlineDownload;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");
        Log.d(TAG, "newUrl: " + newUrl);
        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        missProDialog();
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
            Log.d(TAG, "respStr is null");
            return;
        }
        Log.d(TAG, "respStr: " + respStr);
        OnlineCheckResp serverResult = null;
        try {
            Result rptDecode = RptUtil.getRptDecode(respStr);
            Log.d(TAG, "respStr: " + rptDecode);
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
                    if (isUnreg) {
                        Log.d(TAG, "unRegDet:" + unRegDet);
                        showStatusDialog("已存在已使用雷管！");
                        long projectId = storeProjectInfo(projectFile, serverResult);
                        uploadData(projectId);
                        return;
                    }

                    goToBomb();
                    long projectId = storeProjectInfo(projectFile, serverResult);
                    uploadData(projectId);
                } else {
                    showStatusDialog(serverResult.getCwxxms());
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "解析错误：" + e.getMessage());
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
                    for (ProjectDetonator detonator : pendingProject.getDetonatorList()) {
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
        String controllerId = pendingProject.getControllerId();
        if (isInBlackList(controllerId)) {
            showStatusDialog("起爆器未注册，不允许起爆");
            return;
        }

        // 如果起爆器在白名单中直接允许起爆
        if (isInWhiteList(controllerId)) {
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
            showStatusDialog("没有找到雷管规则所对应的项目");
            return;
        }
        ProjectInfoEntity projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                queryBuilder()
                .where(ProjectInfoEntityDao.Properties.Id.eq(detInProjectId)).unique();
        if (projectInfo == null) {
            showStatusDialog("没有找到雷管规则所对应的项目");
            return;
        }

        if (!checkControllerData(projectInfo)) {
            showStatusDialog("起爆器未注册，不允许起爆");
            return;
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

    // todo 刷新雷管状态，演示
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
                    projectDetonator.setStatus(1);
                    unRegiestCount++;
                }
            }

            refreshData();
            if (unRegiestCount > 0) {
                showStatusDialog(CheckRuleEnum.UNREG_DET.getMessage() + unRegiestCount);
                return;
            }
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
        String rptJson = getReportDto(projectInfoEntity);
        String url = AppConstants.ETEKTestServer + AppConstants.CheckoutReport;
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
        List<PermissibleZoneEntity> permissibleZoneList = projectInfo.getPermissibleZoneList();
        if (permissibleZoneList != null && permissibleZoneList.size() != 0) {
            for (PermissibleZoneEntity permissibleZoneEntity : permissibleZoneList) {
                LocationUtil.LocationRange range = LocationUtil.getAround(permissibleZoneEntity.getLatitude(), permissibleZoneEntity.getLongitude(), 30000);
//                LocationUtil.LocationRange range = LocationUtil.getAround(permissibleZoneEntity.getLatitude(), permissibleZoneEntity.getLongitude(), permissibleZoneEntity.getRadius());
                if (pendingProject.getLatitude() > range.getMinLat()
                        && pendingProject.getLatitude() < range.getMaxLat()
                        && pendingProject.getLongitude() > range.getMinLng()
                        && pendingProject.getLongitude() < range.getMaxLng()) {
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
                if (controllerEntity.getName().equalsIgnoreCase(pendingProject.getControllerId())) {
                    return true;
                }
            }
        }
        return false;
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
                if (pendingProject.getLatitude() > range.getMinLat()
                        && pendingProject.getLatitude() < range.getMaxLat()
                        && pendingProject.getLongitude() > range.getMinLng()
                        && pendingProject.getLongitude() < range.getMinLng()) {
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
}
