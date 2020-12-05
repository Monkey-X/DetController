package com.etek.controller.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.CheckDetailAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.dto.Jbqy;
import com.etek.controller.dto.Jbqys;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.Lgs;
import com.etek.controller.dto.OnlineCheckDto;
import com.etek.controller.dto.OnlineCheckResp;
import com.etek.controller.dto.ProjectDownLoadEntity;
import com.etek.controller.dto.ProjectFileDto;
import com.etek.controller.dto.Sbbhs;
import com.etek.controller.dto.Zbqy;
import com.etek.controller.dto.Zbqys;
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
import com.etek.controller.utils.JsonUtils;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.controller.utils.location.DLocationTools;
import com.etek.controller.utils.location.DLocationUtils;
import com.etek.controller.utils.location.OnLocationChangeListener;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.ToastUtils;
import com.google.gson.Gson;

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

    private long proId;
    private TextView contractCode;
    private TextView controllerId;
    private EditText locationLongitude;
    private EditText locationLatitude;
    private Button getLocation;
    private TextView controllerTime;
    private RecyclerView detonatorList;
    private CheckDetailAdapter checkDetailAdapter;
    private List<DetonatorEntity> detonatorEntityList;
    private ProjectInfoEntity projectInfoEntity;
    private int GO_TO_GPS = 150;
    private StringBuilder uid = new StringBuilder();
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_detail);
        getProjectId();
        initView();
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
            projectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
            detonatorEntityList = DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(proId)).list();
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
        checkDetailAdapter = new CheckDetailAdapter(R.layout.detonator_list_item, detonatorEntityList);
        detonatorList.setAdapter(checkDetailAdapter);

        if (projectInfoEntity != null) {
            //合同编号
            contractCode.setText(projectInfoEntity.getContractCode());
            //起爆器编号
            controllerId.setText(projectInfoEntity.getControllerId());
            //地标
            if (projectInfoEntity.getLongitude() != 0 || projectInfoEntity.getLatitude() != 0) {
                DecimalFormat df = new DecimalFormat("0.000000");
                String loc = df.format(projectInfoEntity.getLongitude()) + "  ,  " + df.format(projectInfoEntity.getLatitude());
                locationLongitude.setText("" + projectInfoEntity.getLongitude());
                locationLatitude.setText("" + projectInfoEntity.getLatitude());
            }
            //起爆器时间
            if (projectInfoEntity.getBlastTime() != null) {
                String timeStr = DateUtil.getDateStr(projectInfoEntity.getBlastTime());
                controllerTime.setText(timeStr);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.get_location) {
            getLocation();
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
            locationLongitude.setText("" + location.getLongitude());
            locationLatitude.setText("" + location.getLatitude());
            projectInfoEntity.setLongitude(location.getLongitude());
            projectInfoEntity.setLatitude(location.getLatitude());
            XLog.e(location.getLongitude() + "  ,  " + location.getLatitude());
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
            if (TextUtils.isEmpty(locationLongitude.getText().toString().trim())) {
                ToastUtils.show(mContext, "当前经度为空");
            } else if (TextUtils.isEmpty(locationLatitude.getText().toString().trim())) {
                ToastUtils.show(mContext, "当前纬度为空");
            } else {
                if ("online".equals(type)) {//在线检查
                    getVerifyResult(projectInfoEntity);
                } else if ("offline".equals(type)) {//离线检查
                    offlineCheck();
                }
            }
        }
        return true;
    }

    /**
     * 获取验证结果
     */
    private void getVerifyResult(ProjectInfoEntity projectInfoEntity) {
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
        onlineCheckDto.setUid(uid.toString());
        String rptJson = JSON.toJSONString(onlineCheckDto, SerializerFeature.WriteMapNullValue);
        XLog.e("rptJson: " + rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showToast("数据编码出错：" + result.getMessage());
            return;
        }
        String url = AppConstants.DanningServer + AppConstants.OnlineDownload;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());
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

    /**
     * 离线检查
     */
    private void offlineCheck() {
        String data = JsonUtils.getData();
        Gson gson = new Gson();
        ProjectDownLoadEntity projectDownLoadEntity = gson.fromJson(data, ProjectDownLoadEntity.class);
        if (!projectInfoEntity.getProCode().equals(projectDownLoadEntity.getXmbh())) {
            ToastUtils.show(this, "项目编号检查错误");
        }
        if (!projectInfoEntity.getProName().equals(projectDownLoadEntity.getXmmc())) {
            ToastUtils.show(this, "项目名称检查错误");
        }
        if (!projectInfoEntity.getCompanyCode().equals(projectDownLoadEntity.getDwdm())) {
            ToastUtils.show(this, "单位代码检查错误");
        }
        if (!projectInfoEntity.getCompanyName().equals(projectDownLoadEntity.getDwmc())) {
            ToastUtils.show(this, "单位名称检查错误");
        }
        if (!projectInfoEntity.getContractCode().equals(projectDownLoadEntity.getHtbh())) {
            ToastUtils.show(this, "合同编号检查错误");
        }
        if (!projectInfoEntity.getContractName().equals(projectDownLoadEntity.getHtmc())) {
            ToastUtils.show(this, "合同名称检查错误");
        }
        Result rptDecode = RptUtil.getRptDecode(projectDownLoadEntity.getMmwj());
        XLog.e(rptDecode);
        if (rptDecode.isSuccess()) {
            String data2 = (String) rptDecode.getData();
            OnlineCheckResp serverResult = JSON.parseObject(data2, OnlineCheckResp.class);
            if (serverResult.getCwxx().contains("0")) {
                int unRegDet = 0;
                boolean isUnreg = false;
                for (int i = 0; i < detonatorEntityList.size(); i++) {
                    for (Lg lg : serverResult.getLgs().getLg()) {
                        if(detonatorEntityList.get(i).getUid().equalsIgnoreCase(lg.getUid())){
                            if(lg.getGzmcwxx()!=0){
                                isUnreg = true;
                                unRegDet++;
                                detonatorEntityList.get(i).setStatus(lg.getGzmcwxx());
                                XLog.e("cwxx：" + lg.getGzmcwxx() + "  " + "lg：" + lg.getUid());
                            }else{
                                detonatorEntityList.get(i).setStatus(lg.getGzmcwxx());
                                XLog.e("cwxx：" + lg.getGzmcwxx() + "  " + "lg：" + lg.getUid());
                            }
                            checkDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if(isUnreg){
                    showStatusDialog("存在已使用雷管" + unRegDet + "个！");
                }
            }
        } else {
            XLog.e("解密失败：" + rptDecode.getMessage());
        }
    }
}
