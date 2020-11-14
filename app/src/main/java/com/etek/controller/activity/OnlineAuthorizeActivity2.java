package com.etek.controller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.CheckOutAdapter2;
import com.etek.controller.common.AppConstants;
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
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OnlineAuthorizeActivity2 extends BaseActivity implements View.OnClickListener, CheckOutAdapter2.OnItemClickListener {

    private String TAG = "OnlineAuthorizeActivity2";
    private static final int REQUEST_PRO_EDIT = 11;
    private TextView contractId;
    private TextView detLocation;
    private Button selectLocation;
    private ImageView imgLoading;
    private SwipeRefreshLayout slProjectInfo;
    private RecyclerView rvProjectInfo;
    private CheckOutAdapter2 mProjectInfoAdapter;
    private static final int PAGE_SIZE = 10;
    private int mNextRequestPage = 1;
    private DetController detController;
    private ProjectInfoEntity projectInfo;
    private LocationClient mLocClient;
    private BDLocation mLocation;
    private int isTest = 0;
    private String contractCode;
    private String proCode;
    private long proId = 0;
    private boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_authorize2);
        initSupportActionBar(R.string.title_act_online_authorize);
        initView();
        initData();
        getUserCompanyCode();
        getLocationLocal();
        showDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isTest == 0) {
            getBaiduLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient = null;
        }
        super.onDestroy();
    }

    /**
     * 初始化view
     */
    private void initView() {
        contractId = findViewById(R.id.contract_id);
        detLocation = findViewById(R.id.det_location);
        selectLocation = findViewById(R.id.select_location);
        imgLoading = findViewById(R.id.img_loading);
        slProjectInfo = findViewById(R.id.sl_project_info);
        rvProjectInfo = findViewById(R.id.rv_project_info);
        contractId.setOnClickListener(this);
        selectLocation.setOnClickListener(this);
        rvProjectInfo.setLayoutManager(new LinearLayoutManager(mContext));
        slProjectInfo.setColorSchemeColors(Color.rgb(47, 223, 189));
        slProjectInfo.setRefreshing(true);
        mProjectInfoAdapter = new CheckOutAdapter2();
        mProjectInfoAdapter.setOnLoadMoreListener(() -> new Handler().post(() -> loadMore()));
        rvProjectInfo.setAdapter(mProjectInfoAdapter);
        mProjectInfoAdapter.setOnItemClickListener(this);
        slProjectInfo.setOnRefreshListener(() -> refresh());
    }

    /**
     * 条目的点击事件
     */
    @Override
    public void onItemCLick(ProjectInfoEntity projectInfoEntity, int position) {
        Intent intent = new Intent(mContext, CheckoutDetailActivity2.class);
        XLog.d("projectInfoEntity:" + projectInfoEntity);
        intent.putExtra("chkId", projectInfoEntity.getId());
        intent.putExtra("longitude",getStringInfo("Longitude"));
        intent.putExtra("latitude",getStringInfo("Latitude"));
        startActivity(intent);
    }

    /**
     * 校验的点击事件
     */
    @Override
    public void onCheckOutClick(ProjectInfoEntity projectInfoEntity, int position) {
        getVerifyResult(projectInfoEntity);
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contract_id:
                changeLocationDialog();
                break;

            case R.id.select_location:
                Intent i = new Intent(this, SelectMapActivity.class);
                startActivity(i);
                break;
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        detController = new DetController();
        detController.setUserIDCode(Globals.user.getIdCode());
        detController.setCompanyCode(Globals.user.getCompanyCode());
    }

    /**
     * 如果用户信息为空则跳转至用户信息页面
     */
    private void getUserCompanyCode() {
        if (Globals.user == null || StringUtils.isEmpty(Globals.user.getCompanyCode()) || StringUtils.isEmpty(Globals.user.getIdCode())) {
            showStatusDialog("公司代码或用户证件号为空，请去信息设置页面设置");
            delayAction(new Intent(mContext, UserInfoActivity.class), 1000);
        }
    }

    /**
     * 获取位置信息
     */
    private void getLocationLocal() {
        String longitudeStr = getStringInfo("Longitude");
        String latitudeStr = getStringInfo("Latitude");
        if (!StringUtils.isEmpty(longitudeStr) && !(StringUtils.isEmpty(latitudeStr))) {
            double longitude = Double.parseDouble(longitudeStr);
            double latitude = Double.parseDouble(latitudeStr);
            detController.setLongitude(longitude);
            detController.setLatitude(latitude);
            detLocation.setText(longitude + " , " + latitude);
            detLocation.setTextColor(getMyColor(R.color.darkgoldenrod));
        }
    }

    private void getBaiduLocation() {
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerNotifyLocationListener(
                location -> {
                    mLocClient.stop();
                    if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                        mLocation = location;
                        detLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        detLocation.setTextColor(getMyColor(R.color.colorPrimary));
                        detController.setLatitude(mLocation.getLatitude());
                        detController.setLongitude(mLocation.getLongitude());
                        setStringInfo("Longitude", location.getLongitude() + "");
                        setStringInfo("Latitude", location.getLatitude() + "");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                        mLocation = location;
                        detLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        detLocation.setTextColor(getMyColor(R.color.colorPrimary));
                        detController.setLatitude(mLocation.getLatitude());
                        detController.setLongitude(mLocation.getLongitude());
                        setStringInfo("Longitude", location.getLongitude() + "");
                        setStringInfo("Latitude", location.getLatitude() + "");
                    } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                        mLocation = location;
                        detLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        detLocation.setTextColor(getMyColor(R.color.colorPrimary));
                        detController.setLatitude(mLocation.getLatitude());
                        detController.setLongitude(mLocation.getLongitude());
                        setStringInfo("Longitude", location.getLongitude() + "");
                        setStringInfo("Latitude", location.getLatitude() + "");
                    } else if (location.getLocType() == BDLocation.TypeServerError) {
                        showStatusDialog("定位失败：" + location.getLocType());
                    } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                        showStatusDialog("定位失败：" + location.getLocType());
                    } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                        showStatusDialog("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    } else {
                        showStatusDialog("定位失败：" + location.getLocType());
                    }
                });

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");// 坐标类型
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 打开Gps
        option.setScanSpan(1000);// 1000毫秒定位一次
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 弹框输入项目信息
     */
    public void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_input_checkout, null, false);
        EditText etProCode = view.findViewById(R.id.et_pro_code);
        EditText etContractCode = view.findViewById(R.id.et_contract_code);
        etProCode.setText(Globals.proId);
        etContractCode.setText(Globals.contractId);
        dialog.setView(view);
        dialog.setTitle("请输入项目信息！");
        dialog.setCancelable(false);
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contractCode = etContractCode.getText().toString();
                proCode = etProCode.getText().toString();
                detController.setContractId(contractCode);
                detController.setProjectId(proCode);

                if (!StringUtils.isEmpty(contractCode)) {
                    contractId.setText((mContext.getString(R.string.contract_code_param, contractCode)));
                    setStringInfo("contractId", contractCode);
                    Globals.contractId = contractCode;
                } else {
                    setStringInfo("contractId", "");
                    Globals.contractId = contractCode;
                }

                if (!StringUtils.isEmpty(proCode)) {
                    contractId.setText((mContext.getString(R.string.project_code_param, proCode)));
                    setStringInfo("proId", proCode);
                    Globals.proId = proCode;
                } else {
                    setStringInfo("proId", "");
                    Globals.proId = proCode;
                }
                showOnlineDocVerify();
            }
        });
        dialog.show();
    }

    public void changeLocationDialog() {
//        if (!Globals.isTest) {
//            return;
//        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.dialog_input_location, null, false);
        EditText etLongitude = view.findViewById(R.id.et_longitude);
        EditText etLatitude = view.findViewById(R.id.et_latitude);

        etLongitude.setText(detController.getLongitude() + "");
        etLatitude.setText(detController.getLatitude() + "");
        dialog.setView(view);
        dialog.setTitle("请输入项目信息！");
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double longitude = Double.parseDouble(etLongitude.getText().toString());
                double latitude = Double.parseDouble(etLatitude.getText().toString());
                detController.setLongitude(longitude);
                detController.setLatitude(latitude);
                detLocation.setText(longitude + " , " + latitude);
                detLocation.setTextColor(getMyColor(R.color.goldenrod));
            }
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
     * 下拉刷下
     */
    private void refresh() {
        mNextRequestPage = 1;
        mProjectInfoAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        List<ProjectInfoEntity> datas = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.Id)
                .limit(PAGE_SIZE)
                .build()
                .list();
        Log.v(TAG,"数据更新! " + datas.size());
        setChkData(true, datas);
        mProjectInfoAdapter.setEnableLoadMore(true);
        slProjectInfo.setRefreshing(false);
    }

    /**
     * 上拉加载
     */
    private void loadMore() {
        int offset = (mNextRequestPage - 1) * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ProjectInfoEntity> datas = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .build()
                .list();
        boolean isRefresh = mNextRequestPage == 1;
        Log.v(TAG,"加载更多! " + datas.size());
        setChkData(isRefresh, datas);
    }

    /**
     * 设置数据并刷新页面
     */
    private void setChkData(boolean isRefresh, List datas) {
        mNextRequestPage++;
        final int size = datas == null ? 0 : datas.size();

        if (rvProjectInfo.isComputingLayout())
            return;
        if (isRefresh) {
            mProjectInfoAdapter.setNewData(datas);
        } else {
            if (size > 0) {
                mProjectInfoAdapter.addData(datas);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mProjectInfoAdapter.loadMoreEnd(isRefresh);
        } else {
            mProjectInfoAdapter.loadMoreComplete();
        }
    }

    /**
     * 在线验证
     */
    public void showOnlineDocVerify() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("是否手动添加雷管管码信息？");
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确认", (dialog1, which) -> {
            Intent intent = new Intent(mContext, OnlineEditActivity.class);
            intent.putExtra("Controller", detController);
            startActivityForResult(intent, REQUEST_PRO_EDIT);
        });
        dialog.setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss());
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 获取TOKEN
     */
    private String getToken() {
        StringBuilder sb = new StringBuilder();
        for (Detonator detonator : detController.getDetList()) {
            sb.append(detonator.getDetCode());
        }
        String token = MD5Util.md5(sb.toString());
        detController.setToken(token);
        return token;
    }

    private void getVerifyResult(ProjectInfoEntity projectInfoEntity) {
        isValid = false;
        OnlineCheckDto onlineCheckDto = new OnlineCheckDto();
        onlineCheckDto.setDwdm(projectInfoEntity.getCompanyCode());
        onlineCheckDto.setHtid(projectInfoEntity.getContractCode());
        onlineCheckDto.setJd(getStringInfo("Longitude"));
        onlineCheckDto.setWd(getStringInfo("Latitude"));
        onlineCheckDto.setXmbh(projectInfoEntity.getProCode());
        onlineCheckDto.setSbbh("61000255");//无数据，暂时写死
        onlineCheckDto.setUid("61000001028324");//无数据，暂时写死
        String rptJson = JSON.toJSONString(onlineCheckDto, SerializerFeature.WriteMapNullValue);
        Log.e(TAG, "rptJson: " + rptJson);
        getToken();
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showToast("数据编码出错：" + result.getMessage());
            return;
        }
        String url = AppConstants.DanningServer + AppConstants.OnlineDownload;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");

        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e( TAG, "IOException: " + e.getMessage());
                dismissProgressBar();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dismissProgressBar();
                String respStr = response.body().string();
                if (StringUtils.isEmpty(respStr)) {
                    Log.e(TAG, "respStr is null ");
                    return;
                }

                OnlineCheckResp serverResult = null;
                try {
                    Result rptDecode = RptUtil.getRptDecode(respStr);
                    if (rptDecode.isSuccess()) {
                        String data = (String) rptDecode.getData();
                        Log.e(TAG, "resp:" + data);
                        serverResult = JSON.parseObject(data, OnlineCheckResp.class);
                        Log.e(TAG, serverResult.toString());
                        if (serverResult.getCwxx().contains("0")) {
                            ProjectFileDto projectFile = new ProjectFileDto();

//                            ProInfoDto   detInfoDto = JSON.parseObject(data, ProInfoDto.class);
//                            Log.d("TAG",detInfoDto.toString());
//                            projectFile.setProInfo(detInfoDto);

                            projectFile.setCompany(Globals.user.getCompanyName());
                            projectFile.setDwdm(Globals.user.getCompanyCode());
                            projectFile.setXmbh(detController.getProjectId());
                            projectFile.setHtbh(detController.getContractId());

                            int unRegDet = 0;
                            boolean isUnreg = false;
                            for (Detonator detonator : detController.getDetList()) {
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
                                Log.w(TAG, "unRegDet:" + unRegDet);
                                showStatusDialog("已存在已使用雷管！");
                                proId = storeProjectInfo(projectFile, serverResult);
                                if (proId != 0) {
                                    isValid = true;
                                    projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                                            queryBuilder()
                                            .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
                                }
                                return;
                            }

                            proId = storeProjectInfo(projectFile, serverResult);
                            if (proId != 0) {
                                isValid = true;
                                projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                                        queryBuilder()
                                        .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
                            } else {
                                showStatusDialog("已经存在有此项目");
                            }
                        } else {
                            showStatusDialog(serverResult.getCwxxms());
//                        result = ActivityResult.successOf("上传丹灵服务器成功!");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "解析错误：" + e.getMessage());
                }
            }
        });
    }


    private long storeProjectInfo(final ProjectFileDto projectFile, OnlineCheckResp onlineCheckResp) {

//        ThreadPoolUtils.getThreadPool().execute(()->{
//        ProInfoDto mDetInfoDto = projectFile.getProInfo();
        Log.v(TAG, "onlineCheckResp:" + onlineCheckResp);
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
        Log.v(TAG, "proid:" + proId);
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

    /**
     * 导航栏菜单按钮
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Globals.isTest) {
            getMenuInflater().inflate(R.menu.menu_online_check_test, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_online_check, menu);
        }
        return true;
    }

    /**
     * 菜单按钮选中事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_file) {
            Intent intent = new Intent(mContext, OnlineEditActivity.class);
            intent.putExtra("Controller", detController);
            startActivityForResult(intent, REQUEST_PRO_EDIT);
        }else if (id == R.id.send_checkout_report){
            //发送检测报告
//            if (projectInfo == null){ //假数据projectInfo
//                projectInfo = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().limit(1).unique();
//            }
//            sendCheckoutReport();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 雷管传输结束后调用
     */
    private void sendCheckoutReport() {

        String rptJson = getReportDto();
        if(rptJson==null){
            return;
        }
        String fdName = "report_info" + DateUtil.getDateDoc(new Date()) + ".json";
        FileUtils.saveFileToSDcard("detonator/json", fdName, rptJson);
        String url = AppConstants.ETEKTestServer + AppConstants.CheckoutReport;
        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"IOException:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
                    Log.e(TAG,"respStr is  " + respStr);
                }
            }
        });
    }

    private String getReportDto() {
        ProjectInfoDto projectInfoDto = new ProjectInfoDto();
        Log.d(TAG,"getReportDto:"+projectInfo.toString());
        try {
            BeanPropertiesUtil.copyProperties(projectInfo, projectInfoDto);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        projectInfoDto.setCreateTime(new Date());
        projectInfoDto.addDetControllers(detController);
        ValueFilter filter = (Object object, String name, Object v) -> {
            if (v == null) return "";
            return v;
        };
        return JSON.toJSONString(projectInfoDto, filter);
    }
}
