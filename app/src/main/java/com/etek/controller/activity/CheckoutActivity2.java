package com.etek.controller.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.CheckOutAdapter2;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.BLECmd;
import com.etek.controller.dto.ProjectInfoDto;
import com.etek.controller.dto.WhiteBlackController;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.CheckRuleEnum;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.LocationUtil;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CheckoutActivity2 extends BaseActivity implements CheckOutAdapter2.OnItemClickListener {
    private static final int PAGE_SIZE = 10;

    @BindView(R.id.det_location)
    TextView tvLocation;
    @BindView(R.id.rv_project_info)
    RecyclerView prv;
    @BindView(R.id.sl_project_info)
    SwipeRefreshLayout psl;
    @BindView(R.id.img_loading)
    ImageView img_loading;

    boolean isValid;
    private DetController detController;
    private LocationClient mLocClient;
    private ProjectInfoEntity projectInfo;
    private BDLocation mLocation;
    private CheckOutAdapter2 mProjectInfoAdapter;
    private List<String> whiteList;
    private List<String> blackList;
    private int mNextRequestPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout2);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_checkout);
        getUserCompanyCode();
        initData();
        initView();
        getLocationLocal();
        getWhiteBlackList();
    }

    private void getLocationLocal() {
        String longitudeStr = getStringInfo("Longitude");
        String latitudeStr = getStringInfo("Latitude");
        if (!StringUtils.isEmpty(longitudeStr) && !(StringUtils.isEmpty(latitudeStr))) {
            double longitude = Double.valueOf(longitudeStr);
            double latitude = Double.valueOf(latitudeStr);
            detController.setLongitude(longitude);
            detController.setLatitude(latitude);
            tvLocation.setText(longitude + " , " + latitude);
            tvLocation.setTextColor(getMyColor(R.color.darkgoldenrod));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        getBaiduLocation();
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient = null;
        }
        super.onDestroy();
    }

    @OnClick(R.id.select_location)
    public void selectLocation() {
        Intent i = new Intent(this, SelectMapActivity.class);
        startActivity(i);
    }

    private void getUserCompanyCode() {
        if (Globals.user == null || StringUtils.isEmpty(Globals.user.getCompanyCode()) || StringUtils.isEmpty(Globals.user.getIdCode())) {
            showStatusDialog("公司代码或用户证件号为空，请去信息设置页面设置");
            delayAction(new Intent(mContext, UserInfoActivity.class), 1000);
        }
    }

    private void initData() {
        detController = new DetController();
        detController.setUserIDCode(Globals.user.getIdCode());
        long proid = getLongInfo("projectId");
        projectInfo = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder().where(ProjectInfoEntityDao.Properties.Id.eq(proid)).unique();
    }

    private void initView() {
        prv.setLayoutManager(new LinearLayoutManager(mContext));
        psl.setColorSchemeColors(Color.rgb(47, 223, 189));
        psl.setRefreshing(true);
        mProjectInfoAdapter = new CheckOutAdapter2();
        mProjectInfoAdapter.setOnLoadMoreListener(() -> new Handler().post(() -> loadMore()));
        prv.setAdapter(mProjectInfoAdapter);
        mProjectInfoAdapter.setOnItemClickListener(this);
        psl.setOnRefreshListener(() -> refresh());
    }

    /**
     * 条目点击事件
     */
    @Override
    public void onItemCLick(ProjectInfoEntity projectInfoEntity, int position) {
        Intent intent = new Intent(mContext, CheckoutDetailActivity2.class);
        intent.putExtra("chkId", projectInfoEntity.getId());
        intent.putExtra("longitude",getStringInfo("Longitude"));
        intent.putExtra("latitude",getStringInfo("Latitude"));
        startActivity(intent);
    }

    /**
     * 校验点击事件
     */
    @Override
    public void onCheckOutClick(ProjectInfoEntity projectInfoEntity, int position) {
        getVerifyResult();//该验证方式后续可能会改变
    }

    /**
     * 下拉刷新
     */
    private void refresh() {
        XLog.v("数据更新! ");
        mNextRequestPage = 1;
        mProjectInfoAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        List<ProjectInfoEntity> datas = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.Id)
                .limit(PAGE_SIZE)
                .build()
                .list();
        setChkData(true, datas);
        mProjectInfoAdapter.setEnableLoadMore(true);
        psl.setRefreshing(false);
    }

    /**
     * 上拉加载
     */
    private void loadMore() {
        XLog.v("加载更多! ");
        int offset = (mNextRequestPage - 1) * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ProjectInfoEntity> datas = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .build()
                .list();
        boolean isRefresh = mNextRequestPage == 1;
        setChkData(isRefresh, datas);
    }

    /**
     * 设置数据并刷新页面
     */
    private void setChkData(boolean isRefresh, List datas) {
        mNextRequestPage++;
        final int size = datas == null ? 0 : datas.size();
        if (prv.isComputingLayout())
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

    private void getBaiduLocation() {
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerNotifyLocationListener(
                location -> {
                    XLog.d(getLocation(location));
                    mLocClient.stop();
                    if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果

                        mLocation = location;
                        tvLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        tvLocation.setTextColor(getMyColor(R.color.colorPrimary));
                        detController.setLatitude(mLocation.getLatitude());
                        detController.setLongitude(mLocation.getLongitude());

                        setStringInfo("Longitude", location.getLongitude() + "");
                        setStringInfo("Latitude", location.getLatitude() + "");

                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                        mLocation = location;
                        tvLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        tvLocation.setTextColor(getMyColor(R.color.colorPrimary));
                        detController.setLatitude(mLocation.getLatitude());
                        detController.setLongitude(mLocation.getLongitude());

                        setStringInfo("Longitude", location.getLongitude() + "");
                        setStringInfo("Latitude", location.getLatitude() + "");
                    } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                        mLocation = location;
                        tvLocation.setText(location.getLongitude() + " , " + location.getLatitude());
                        tvLocation.setTextColor(getMyColor(R.color.colorPrimary));
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
//                    isLocation = true;
//                        XLog.d(LOG_TAG,location.getCity() + "location" + location.getStreet() + "--" + location.getAddrStr() + "---" + location.getStreetNumber());
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

    private String getLocation(BDLocation location) {
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
//        List<Poi> list = location.getPoiList();// POI数据
//        if (list != null) {
//            sb.append("\npoilist size = : ");
//            sb.append(list.size());
//            for (Poi p : list) {
//                sb.append("\npoi= : ");
//                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
//            }
//        }
        XLog.i(sb.toString());
        return sb.toString();
    }


    /**
     * 校验
     */
    private BLECmd getVerifyResult() {
        isValid = false;
        BLECmd bleCmd;
        bleCmd = BLECmd.getVerify(CheckRuleEnum.SUCCESS.getCode(), 0, 0);
        boolean isControllerValid = false;
//        for (ControllerEntity detControllerValid : projectInfo.getControllerList()) {
//            if (detController.getSn().equalsIgnoreCase(detControllerValid.getName())) {
////                XLog.w("detController: controll==" + detController.getSn() + "  rule==" + detControllerValid.getName());
//                isControllerValid = true;
//            }
//        }
        if (!isControllerValid) {
            bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
            showStatusDialog(CheckRuleEnum.OUT_CONTROLLER.getMessage());
            return bleCmd;
        }

        for (ForbiddenZoneEntity forbiddenZoneBean : projectInfo.getForbiddenZoneList()) {
            LocationUtil.LocationRange range = LocationUtil.getAround(forbiddenZoneBean.getLatitude(), forbiddenZoneBean.getLongitude(), forbiddenZoneBean.getRadius());
            if (detController.getLatitude() > range.getMinLat()
                    && detController.getLatitude() < range.getMaxLat()
                    && detController.getLongitude() > range.getMinLng()
                    && detController.getLongitude() < range.getMinLng()) {
                bleCmd = BLECmd.getVerify(CheckRuleEnum.IN_FORBIDDEN.getCode(), 0, 0);
//                XLog.w("forbiddenZoneBean: lat==" + mLocation.getLatitude() + "  lng==" + mLocation.getLongitude()
//                        + "  MinLat==" + range.getMinLat() + "  MaxLat==" + range.getMaxLat()
//                        + "  MinLng==" + range.getMinLng() + "  MaxLng==" + range.getMinLng());
                return bleCmd;
            }
        }

        boolean isPermisssion = true;
        for (PermissibleZoneEntity permissibleZoneBean : projectInfo.getPermissibleZoneList()) {
            isPermisssion = false;
            XLog.d(permissibleZoneBean.toString());
            LocationUtil.LocationRange range = LocationUtil.getAround(permissibleZoneBean.getLatitude(), permissibleZoneBean.getLongitude(), permissibleZoneBean.getRadius());
            if (detController.getLatitude() > range.getMinLat()
                    && detController.getLatitude() < range.getMaxLat()
                    && detController.getLongitude() > range.getMinLng()
                    && detController.getLongitude() < range.getMaxLng()) {
//                XLog.d("permissibleZoneBean: lat==" + mLocation.getLatitude() + "  lng==" + mLocation.getLongitude()
//                        + "  MinLat==" + range.getMinLat() + "  MaxLat==" + range.getMaxLat()
//                        + "  MinLng==" + range.getMinLng() + "  MaxLng==" + range.getMinLng());
                isPermisssion = true;
                break;
            }
        }
//
        if (!isPermisssion) {
            showStatusDialog(CheckRuleEnum.OUT_PERMISSION.getMessage());
            bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_PERMISSION.getCode(), 0, 0);
            return bleCmd;
        }

        int unUsedDet = projectInfo.getDetonatorList().size();
        int unRegDet = 0;
        boolean isUnreg;

        for (Detonator conDet : detController.getDetList()) {
            isUnreg = true;
            for (DetonatorEntity infoDet : projectInfo.getDetonatorList()) {
//                XLog.v(infoDet.toString());
                String cUid = conDet.getUid().substring(conDet.getUid().length() - 8, conDet.getUid().length());
                String iUid = conDet.getUid().substring(infoDet.getUid().length() - 8, infoDet.getUid().length());
//                XLog.v("verfiryUid: ",cUid,iUid);

                if (conDet.getDetCode().equalsIgnoreCase(infoDet.getCode()) && cUid.equalsIgnoreCase(iUid)) {
//                    XLog.d("conDet: ",conDet);
                    XLog.v("verfiryCode conDet:  infoDet:", conDet, infoDet);
                    if (!DetUtil.getAcCodeFromDet(infoDet).equalsIgnoreCase(infoDet.getWorkCode()))
                        break;
                    unUsedDet--;
                    if (unUsedDet < 0) {
                        unUsedDet = 0;
                    }
                    isUnreg = false;
                    conDet.setStatus(0);
                }
            }
            if (isUnreg) {
                conDet.setStatus(1);
                unRegDet++;
            }

        }

        XLog.w("unRegDet:" + unRegDet + " unUsedDet:" + unUsedDet);
        if (unRegDet > 0) {
            isValid = true;
            showStatusDialog(CheckRuleEnum.UNREG_DET.getMessage() + unRegDet);
//            XLog.w(CheckRuleEnum.ERR_DET.getMessage() + " : " + unUsedDet + " || " + unRegDet);
            bleCmd = BLECmd.getVerify(CheckRuleEnum.ERR_DET.getCode(), unRegDet, 0);
            return bleCmd;
        }
        isValid = true;
        showStatusDialog(CheckRuleEnum.SUCCESS.getMessage());

        return bleCmd;
    }

    /**
     * 发送检测报告
     */
    private void sendCheckoutReport() {
        String rptJson = getReportDto();
        String fdName = "report_info" + DateUtil.getDateDoc(new Date()) + ".json";
        FileUtils.saveFileToSDcard("detonator/json", fdName, rptJson);
        String url = AppConstants.ETEKTestServer + AppConstants.CheckoutReport;
        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
//                sendCmdMessage(MSG_RPT_DANLING_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
                    XLog.w("respStr is  ", respStr);
                }
            }
        });
    }


    private String getReportDto() {
        ProjectInfoDto projectInfoDto = new ProjectInfoDto();
        try {
            BeanPropertiesUtil.copyProperties(projectInfo, projectInfoDto);

        } catch (Exception e) {
            e.printStackTrace();
        }
        projectInfoDto.setCreateTime(new Date());
        detController.setContractId("");
//        projectInfoDto.addDetControllers(detController);
        return JSON.toJSONString(projectInfoDto);
    }

    /**
     * 获取黑/白名单列表
     */
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
}
