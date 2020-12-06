package com.etek.controller.activity;


import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.dto.Jbqy;
import com.etek.controller.dto.Jbqys;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.Lgs;
import com.etek.controller.dto.OnlineCheckDto;
import com.etek.controller.dto.OnlineCheckResp;
import com.etek.controller.dto.ProjectFileDto;
import com.etek.controller.dto.Sbbhs;
import com.etek.controller.dto.Zbqy;
import com.etek.controller.dto.Zbqys;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.controller.utils.location.DLocationTools;
import com.etek.controller.utils.location.DLocationUtils;
import com.etek.controller.utils.location.OnLocationChangeListener;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;
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
 * 在线授权页
 */
public class OnlineAuthorizeActivity2 extends BaseActivity implements View.OnClickListener {

    private LinearLayout project;
    private TextView companyName;
    private TextView companyCode;
    private TextView projectName;
    private TextView projectCode;
    private TextView contractName;
    private TextView contractCode;
    private TextView controllerId;
    private TextView implementStates;
    private TextView blastTime;
    private EditText longitude;
    private EditText latitude;
    private TextView getLocation;
    private TextView verify;
    private int GO_TO_GPS = 150;
    private long proIds;
    private ProjectInfoEntity projectInfoEntity;
    private List<DetonatorEntity> detonatorEntityList;
    private StringBuilder uid = new StringBuilder();
    private ProjectInfoEntity projectInfo;
    private DetController detController;
    private long proId = 0;
    private boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_authorize2);
        initSupportActionBar(R.string.title_act_online_authorize);
        getProjectId();
        initView();
        initData();
    }

    /**
     * 获取项目id
     */
    private void getProjectId() {
        proIds = getIntent().getLongExtra(AppIntentString.PROJECT_ID, -1);
        XLog.d("proIds: " + proIds);
    }

    /**
     * 初始化view
     */
    private void initView() {
        project = findViewById(R.id.project);
        companyName = findViewById(R.id.company_name);
        companyCode = findViewById(R.id.company_code);
        projectName = findViewById(R.id.project_name);
        projectCode = findViewById(R.id.project_code);
        contractName = findViewById(R.id.contract_name);
        contractCode = findViewById(R.id.contract_code);
        controllerId = findViewById(R.id.controller_id);
        implementStates = findViewById(R.id.project_mplement_states);
        blastTime = findViewById(R.id.blast_time);
        longitude = findViewById(R.id.location_longitude);
        latitude = findViewById(R.id.location_latitude);
        getLocation = findViewById(R.id.get_location);
        verify = findViewById(R.id.verify);
        verify.setOnClickListener(this);
        getLocation.setOnClickListener(this);
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
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    /**
     * 更新经纬度信息
     */
    public void updateGPSInfo(Location location) {
        if (location != null) {
            DecimalFormat df = new DecimalFormat("0.000000");
            String longitude2 = df.format(location.getLongitude());
            String latitude2 = df.format(location.getLatitude());
            longitude.setText(longitude2);
            latitude.setText(latitude2);
            projectInfoEntity.setLongitude(Double.parseDouble(longitude2));
            projectInfoEntity.setLatitude(Double.parseDouble(latitude2));
            XLog.e("updateGPSInfo:  " + longitude + "  ,  " + latitude);
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (proIds >= 0) {
            projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proIds)).unique();
            detonatorEntityList = DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(proIds)).list();
            companyName.setText(projectInfoEntity.getCompanyName());
            companyCode.setText(projectInfoEntity.getCompanyCode());
            projectName.setText(projectInfoEntity.getProName());
            projectCode.setText(projectInfoEntity.getProCode());
            contractName.setText(projectInfoEntity.getContractName());
            contractCode.setText(projectInfoEntity.getContractCode());
            controllerId.setText(projectInfoEntity.getControllerId());
            if (0 != projectInfoEntity.getLongitude()) {
                longitude.setText(projectInfoEntity.getLongitude() + "");
            }
            if (0 != projectInfoEntity.getLatitude()) {
                latitude.setText(projectInfoEntity.getLatitude() + "");
            }
            if (AppIntentString.PROJECT_IMPLEMENT_CONNECT_TEST.equals(projectInfoEntity.getProjectImplementStates())) {
                implementStates.setText(getString(R.string.title_activity_connecttest));
            } else if (AppIntentString.PROJECT_IMPLEMENT_DELAY_DOWNLOAD.equals(projectInfoEntity.getProjectImplementStates())) {
                implementStates.setText(getString(R.string.activity_delay_download));
            } else if (AppIntentString.PROJECT_IMPLEMENT_ONLINE_AUTHORIZE.equals(projectInfoEntity.getProjectImplementStates())) {
                implementStates.setText(getString(R.string.check_authorize));
            } else if (AppIntentString.PROJECT_IMPLEMENT_POWER_BOMB.equals(projectInfoEntity.getProjectImplementStates())) {
                implementStates.setText(getString(R.string.title_power_bomb));
            } else if (AppIntentString.PROJECT_IMPLEMENT_DATA_REPORT.equals(projectInfoEntity.getProjectImplementStates())) {
                implementStates.setText(getString(R.string.data_report));
            }
            if (projectInfoEntity.getBlastTime() != null) {
                blastTime.setText(DateUtil.getDateStr(projectInfoEntity.getBlastTime()));
            }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify://校验
                if (TextUtils.isEmpty(longitude.getText().toString().trim())) {
                    ToastUtils.show(mContext, "当前经度为空");
                    return;
                }

                if (TextUtils.isEmpty(latitude.getText().toString().trim())) {
                    ToastUtils.show(mContext, "当前纬度为空");
                    return;
                }
                getVerifyResult(projectInfoEntity);
                break;

            case R.id.get_location://获取当前位置
                getLocation();
                break;
        }
    }

    /**
     * 获取验证结果
     */
    private void getVerifyResult(ProjectInfoEntity projectInfoEntity) {
        isValid = false;
        OnlineCheckDto onlineCheckDto = new OnlineCheckDto();
        onlineCheckDto.setDwdm(projectInfoEntity.getCompanyCode());
        onlineCheckDto.setHtid(projectInfoEntity.getContractCode());
        onlineCheckDto.setJd(projectInfoEntity.getLongitude() + "");
        onlineCheckDto.setWd(projectInfoEntity.getLatitude() + "");
        onlineCheckDto.setXmbh(projectInfoEntity.getProCode());
        onlineCheckDto.setSbbh(projectInfoEntity.getControllerId());
        for (int i = 0; i < detonatorEntityList.size(); i++) {
            if (i == 0) {
                uid.append(detonatorEntityList.get(i).getUid());
            } else {
                uid.append("," + detonatorEntityList.get(i).getUid());
            }
        }
        XLog.e("uid: " + uid.toString());
        onlineCheckDto.setUid(uid.toString());//无数据，暂时写死
        String rptJson = JSON.toJSONString(onlineCheckDto, SerializerFeature.WriteMapNullValue);
        XLog.e("rptJson: " + rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showToast("数据编码出错：" + result.getMessage());
            return;
        }
        String url = AppConstants.DanningServer + AppConstants.OnlineDownload;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");
        XLog.e("newUrl: " + newUrl);
        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException: " + e.getMessage());
                dismissProgressBar();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dismissProgressBar();
                String respStr = response.body().string();
                if (StringUtils.isEmpty(respStr)) {
                    XLog.e("respStr is null ");
                    return;
                }
                XLog.e("respStr: " + respStr);
                OnlineCheckResp serverResult = null;
                try {
                    Result rptDecode = RptUtil.getRptDecode(respStr);
                    XLog.e("respStr: " + rptDecode);
                    if (rptDecode.isSuccess()) {
                        String data = (String) rptDecode.getData();
                        XLog.e("resp:" + data);
                        serverResult = JSON.parseObject(data, OnlineCheckResp.class);
                        XLog.e("serverResult: " + serverResult.toString());
                        if (serverResult.getCwxx().contains("0")) {
                            ProjectFileDto projectFile = new ProjectFileDto();

//                            ProInfoDto   detInfoDto = JSON.parseObject(data, ProInfoDto.class);
//                            Log.d("TAG",detInfoDto.toString());
//                            projectFile.setProInfo(detInfoDto);

                            projectFile.setCompany(projectInfoEntity.getCompanyName());
                            projectFile.setDwdm(projectInfoEntity.getCompanyCode());
                            projectFile.setXmbh(projectInfoEntity.getProCode());
                            projectFile.setHtbh(projectInfoEntity.getControllerId());

                            int unRegDet = 0;
                            boolean isUnreg = false;
                            for (DetonatorEntity detonator : detonatorEntityList) {
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
                            if (isUnreg) {
                                XLog.w("unRegDet:" + unRegDet);
                                showStatusDialog("已存在已使用雷管！");
//                                proId = storeProjectInfo(projectFile, serverResult);
//                                if (proId != 0) {
//                                    projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
//                                            queryBuilder()
//                                            .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
//                                }
                                return;
                            }

//                            proId = storeProjectInfo(projectFile, serverResult);
//                            if (proId != 0) {
//                                projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
//                                        queryBuilder()
//                                        .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
//                            } else {
//                                showStatusDialog("已经存在有此项目");
//                            }
                        } else {
                            showStatusDialog(serverResult.getCwxxms());
//                        result = ActivityResult.successOf("上传丹灵服务器成功!");
                        }
                    }
                } catch (Exception e) {
                    XLog.e("解析错误：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 存储数据
     */
    private long storeProjectInfo(final ProjectFileDto projectFile, OnlineCheckResp onlineCheckResp) {

//        ThreadPoolUtils.getThreadPool().execute(()->{
//        ProInfoDto mDetInfoDto = projectFile.getProInfo();
        XLog.v("onlineCheckResp:" + onlineCheckResp);
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
        XLog.v("proid:" + proId);
        // get detonators to database by sommer 19.01.07
        Lgs lgs = onlineCheckResp.getLgs();
        if (!lgs.getLg().isEmpty()) {
            List<DetonatorEntity> detonatorEntityList = new ArrayList<>();
            for (Lg lg : lgs.getLg()) {

                DetonatorEntity detonatorBean = new DetonatorEntity();
                if (StringUtils.isEmpty(lg.getFbh())) {
                    for (Detonator detonator : detController.getDetList()) {
//                        Log.d(TAG, detonator.toString());
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
//              detonatorBean.setdetonatorBean.setProInfoBean(proInfoBean);
                detonatorBean.setStatus(lg.getGzmcwxx());
//               detonatorBean.setdetonatorBean.setProInfoBean(detInfoDto);
                detonatorEntityList.add(detonatorBean);
            }
            DBManager.getInstance().getDetonatorEntityDao().insertInTx(detonatorEntityList);
        }

        Zbqys zbqys = onlineCheckResp.getZbqys();
        if (!zbqys.getZbqy().isEmpty()) {
            List<PermissibleZoneEntity> permissibleZoneEntityList = new ArrayList<>();
            for (Zbqy zbqy : zbqys.getZbqy()) {
//              private String zbqssj;  //准爆起始时间
//              private String zbjzsj;  //准爆截止时间
                PermissibleZoneEntity permissibleZone = new PermissibleZoneEntity();
//              permissibleZoneBean.setProInfoBean(proInfoBean);
                permissibleZone.setName(zbqy.getZbqymc());
                permissibleZone.setLatitude(Double.parseDouble(zbqy.getZbqywd()));
                permissibleZone.setLongitude(Double.parseDouble(zbqy.getZbqyjd()));
                permissibleZone.setRadius(Integer.parseInt(zbqy.getZbqybj()));
                permissibleZone.setStartTime(zbqy.getZbqssj());
                permissibleZone.setStopTime(zbqy.getZbjzsj());
                permissibleZone.setProjectInfoId(proId);
                permissibleZoneEntityList.add(permissibleZone);
//              Dao<PermissibleZoneBean, Long> permissibleZoneDao = DatabaseHelper.getInstance(mcontext).getDao(PermissibleZoneBean.class);
//              permissibleZoneDao.create(permissibleZoneBean);
//              permissibleZoneBean.setStartTime(zbqy.getZbqssj());
//              permissibleZoneBean.setStopTime(zbqy.getZbjzsj());
            }
            DBManager.getInstance().getPermissibleZoneEntityDao().insertInTx(permissibleZoneEntityList);
        }
        Jbqys jbqys = onlineCheckResp.getJbqys();
        if (!jbqys.getJbqy().isEmpty()) {
            List<ForbiddenZoneEntity> forbiddenZoneEntityList = new ArrayList<>();
            for (Jbqy jbqy : jbqys.getJbqy()) {
//              private String zbqssj;  //准爆起始时间
//              private String zbjzsj;  //准爆截止时间
                ForbiddenZoneEntity forbiddenZoneEntity = new ForbiddenZoneEntity();
//              forbiddenZoneEntity.setName(jbqy.getJbjzsj());
                forbiddenZoneEntity.setLatitude(Double.parseDouble(jbqy.getJbqywd()));
                forbiddenZoneEntity.setLongitude(Double.parseDouble(jbqy.getJbqyjd()));
                forbiddenZoneEntity.setRadius(Integer.parseInt(jbqy.getJbqybj()));
                forbiddenZoneEntity.setStartTime(jbqy.getJbqssj());
                forbiddenZoneEntity.setStopTime(jbqy.getJbjzsj());
                forbiddenZoneEntity.setProjectInfoId(proId);
                forbiddenZoneEntityList.add(forbiddenZoneEntity);
//              Dao<PermissibleZoneBean, Long> permissibleZoneDao = DatabaseHelper.getInstance(mcontext).getDao(PermissibleZoneBean.class);
//              permissibleZoneDao.create(permissibleZoneBean);
//              permissibleZoneBean.setStartTime(zbqy.getZbqssj());
//              permissibleZoneBean.setStopTime(zbqy.getZbjzsj());
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
//              etControllerBean.setProInfoBean(proInfoBean);
//              Dao<DetControllerBean, Long> detControllerDao = DatabaseHelper.getInstance(mcontext).getDao(DetControllerBean.class);
//              detControllerDao.create(detControllerBean);
            }
            DBManager.getInstance().getControllerEntityDao().insertInTx(controllerEntityList);
        }
//        });
        return proId;
    }
}
