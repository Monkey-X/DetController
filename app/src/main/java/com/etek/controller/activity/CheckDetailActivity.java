package com.etek.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import com.alibaba.fastjson.serializer.ValueFilter;
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
import com.etek.controller.utils.location.DLocationTools;
import com.etek.controller.utils.location.DLocationUtils;
import com.etek.controller.utils.location.OnLocationChangeListener;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
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

import static com.etek.controller.utils.location.DLocationWhat.NO_LOCATIONMANAGER;
import static com.etek.controller.utils.location.DLocationWhat.NO_PROVIDER;
import static com.etek.controller.utils.location.DLocationWhat.ONLY_GPS_WORK;

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
    private ProjectInfoEntity projectInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_detail);
        getProjectId();
        initView();
        getLocation();
        getWhiteBlackList();
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
        return true;
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
            //地标
            locationLongitude.setText("" + AppIntentString.strGratitude);
            locationLatitude.setText("" + AppIntentString.strLatitude);
            if (pendingProject.getLongitude() != 0 || pendingProject.getLatitude() != 0) {
                DecimalFormat df = new DecimalFormat("0.000000");
                String loc = df.format(pendingProject.getLongitude()) + "  ,  " + df.format(pendingProject.getLatitude());
            }
            controllerTime.setText(pendingProject.getDate());
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
     * 定位
     */
    private void getLocation() {
        int status = DLocationUtils.getInstance().register(locationChangeListener);
        XLog.e("status: " + status);
        switch (status) {
            case NO_LOCATIONMANAGER:
                //请求权限
                ToastUtils.show(this, "没有定位权限");
                DLocationTools.openAppSetting(mContext);
                break;
            case NO_PROVIDER:
                //打开定位
                ToastUtils.show(this, "尚未打开定位");
                DLocationTools.openGpsSettings(mContext, GO_TO_GPS);
                break;
            case ONLY_GPS_WORK:
                //切换定位模式到【高精确度】或【节电】
                ToastUtils.show(this, "切换定位模式到【高精确度】或【节电】");
                DLocationTools.openGpsSettings(mContext, GO_TO_GPS);
                break;
        }
    }

    /**
     * 定位监听器
     */
    private OnLocationChangeListener locationChangeListener = new OnLocationChangeListener() {
        @Override
        public void getLastKnownLocation(Location location) {
            updateGPSInfo(location);
        }

        @Override
        public void onLocationChanged(Location location) {
            updateGPSInfo(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 更新经纬度信息
     */
    public void updateGPSInfo(Location location) {
        if (location != null) {
            //地标
            XLog.e("DLocationUtils:  " + location.getLongitude() + "  ,  " + location.getLatitude());
            DecimalFormat df = new DecimalFormat("0.000000");
            String longitude = df.format(location.getLongitude());
            String latitude = df.format(location.getLatitude());
            locationLongitude.setText(longitude);
            locationLatitude.setText(latitude);
            pendingProject.setLongitude(Double.parseDouble(longitude));
            pendingProject.setLatitude(Double.parseDouble(latitude));
            XLog.e("DLocationUtils:  " + longitude + "  ,  " + latitude);
        }
    }

    /**
     * 注销
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DLocationUtils.getInstance().unregister();
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
                getVerifyResult2(pendingProject);
            } else if ("offline".equals(type)) {//离线检查
                offlineCheck2();
            }
        }
    }

    // TODO: 2020/12/20 演示
    private void getVerifyResult2(PendingProject pendingProject) {
        //todo 2020-12-20 演示
        showProDialog("规则检查中...");
        detonatorList.postDelayed(new Runnable() {
            @Override
            public void run() {
                missProDialog();
                changeDetStatus();
                goToBomb();
//                showStatusDialog(CheckRuleEnum.SUCCESS.getMessage());
            }
        },2000);
        return;
        //todo
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

    // 演示
    private void offlineCheck2() {
        //todo 2020-12-20 演示
        showProDialog("规则检查中...");
        detonatorList.postDelayed(new Runnable() {
            @Override
            public void run() {
                missProDialog();
                changeDetStatus();
                showStatusDialog(CheckRuleEnum.SUCCESS.getMessage());
            }
        },2000);
        return;
        //todo
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
                missProDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                missProDialog();
                String respStr = response.body().string();
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

                            showStatusDialog("完全匹配,允许爆破！");
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
        });
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

    public String getReportDto() {
        ProjectInfoDto projectInfoDto = new ProjectInfoDto();
        XLog.d("getReportDto:"+projectInfo.toString());

        try {
            BeanPropertiesUtil.copyProperties(projectInfo, projectInfoDto);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

//        XLog.d("from:", projectInfo);
        projectInfoDto.setCreateTime(new Date());
        projectInfoDto.addDetControllers(pendingProject);

//        XLog.v("to: ", projectInfoDto);
        ValueFilter filter = (Object object, String name, Object v) -> {
            if (v == null) return "";
            return v;
        };
        return JSON.toJSONString(projectInfoDto, filter);
    }

    private void uploadData(long projectId) {

        if (projectId!=0) {
            projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                    queryBuilder()
                    .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
        }

        String rptJson = getReportDto();
        if(rptJson==null){
            return;
        }

        String url = AppConstants.ETEKTestServer + AppConstants.CheckoutReport;

        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
                    Log.d(TAG, "onResponse: "+respStr);
                }
            }
        });
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
            showStatusDialog(CheckRuleEnum.SUCCESS.getMessage());
            uploadData(detInProjectId);
        }
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
                LocationUtil.LocationRange range = LocationUtil.getAround(permissibleZoneEntity.getLatitude(), permissibleZoneEntity.getLongitude(), permissibleZoneEntity.getRadius());
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


//    public class OffLineCheckTask extends AsyncTask<String, Integer, Integer> {
//
//
//        private final List<ProjectInfoEntity> projectInfoEntities;
//
//        public OffLineCheckTask(List<ProjectInfoEntity> projectInfoEntities) {
//            this.projectInfoEntities = projectInfoEntities;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showProDialog("检测中...");
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
//            missProDialog();
//            if (integer == -1) {
//                showStatusDialog("本地数据获取失败！");
//            } else if (integer == 0) {
//                checkDetailAdapter.notifyDataSetChanged();
//            } else {
//                showStatusDialog("存在已使用雷管" + integer + "个！");
//            }
//        }
//
//        @Override
//        protected Integer doInBackground(String... strings) {
//
//        }
//    }
}
