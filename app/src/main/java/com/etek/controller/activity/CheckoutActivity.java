package com.etek.controller.activity;


import android.app.Activity;

import android.bluetooth.BluetoothGattCharacteristic;

import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.location.Poi;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.elvishew.xlog.XLog;
import com.etek.controller.DetApplication;
import com.etek.controller.R;

import com.etek.controller.adapter.CheckOutAdapter;

import com.etek.controller.common.AppConstants;
import com.etek.controller.common.BleConstant;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.BLECmd;

import com.etek.controller.dto.BLEDevResp;

import com.etek.controller.dto.ProjectInfoDto;

import com.etek.controller.dto.WhiteBlackController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.entity.DetController;

import com.etek.controller.enums.CheckRuleEnum;
import com.etek.controller.enums.DevCommonEnum;

import com.etek.controller.exception.ScanExceptionHandler;

import com.etek.controller.adapter.ScanResultsAdapter;

import com.etek.controller.persistence.DBManager;

import com.etek.controller.persistence.entity.ChkControllerEntity;
import com.etek.controller.persistence.entity.ChkDetonatorEntity;
import com.etek.controller.persistence.entity.ControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ForbiddenZoneEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;


import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;


import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.BeanPropertiesUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.LocationPermission;
import com.etek.controller.utils.LocationUtil;

import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.MD5Util;

import com.etek.controller.utils.SommerUtils;

import com.etek.sommerlibrary.utils.ToastUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.jakewharton.rx.ReplayingShare;

import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;


import org.apache.commons.lang3.StringUtils;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckoutActivity extends BaseActivity {

    private static final int PAGE_SIZE = 10;

    private final int REQUEST_ENABLE_BT = 2;

    @BindView(R.id.rv_device)
    RecyclerView rvDevice;

    @BindView(R.id.det_location)
    TextView tvLocation;

    boolean isValid;

//    @BindView(R.id.rpt_info)
//    TextView rptInfo;


//    @BindView(R.id.checkout_info)
//    TextView checkoutInfo;

    //    @BindView(R.id.rv_det)
//    RecyclerView devDrv;
    @BindView(R.id.rv_project_info)
    RecyclerView prv;
    @BindView(R.id.sl_project_info)
    SwipeRefreshLayout psl;

    @BindView(R.id.scan_device)
    Button btConnect;

    @BindView(R.id.img_loading)
    ImageView img_loading;


    DetController detController;

    LocationClient mLocClient;

    ProjectInfoEntity projectInfo;

    BDLocation mLocation;

    CheckOutAdapter mProjectInfoAdapter;

    List<String> whiteList;

    List<String> blackList;

    // ble
    private RxBleClient rxBleClient;
    private RxBleDevice bleDevice;
    private Disposable scanDisposable;
    private boolean hasClickedScan;
    private ScanResultsAdapter resultsAdapter;
    private UUID rxCharUuid;
    private UUID txCharUuid;
    private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();
    private Observable<RxBleConnection> connectionObservable;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    int number = 0;
    private int mNextRequestPage = 1;
    int totalDet;
//    boolean isLocation = false;
//    long curProId = 0;
//    private SmartTable<Detonator> table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_activity_checkout);
        getUserCompanyCode();
        initData();
        initView();

        configureBleResultList();
        getLocationLocal();
        getWhiteBlackList();
    }

    void getLocationLocal() {
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
//        DBManager.getInstance().getChkControllerEntityDao().deleteAll();
//        DBManager.getInstance().getChkDetonatorEntityDao().deleteAll();
        // if the bluetooth adatper is not support and enabled
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            ToastUtils.showCustom(this, getString(R.string.ble_not_supported));
//            finish();
//        }
//        // request to open the bluetooth adapter
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }

    }


    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient = null;
        }
        if (scanDisposable != null) {
            scanDisposable.dispose();
        }
//        isLocation = false;
        super.onDestroy();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_ENABLE_BT:

                if (resultCode == Activity.RESULT_OK) {
                    ToastUtils.showCustom(this, "Bluetooth has turned on ");
                } else {

                    ToastUtils.showCustom(this, "Problem in BT Turning ON");
                }
                break;


            default:
                break;
        }
    }

    @OnClick(R.id.select_location)
    public void selectLocation() {
        Intent i = new Intent(this, SelectMapActivity.class);
        startActivity(i);
//        getBaiduLocation();
    }

//    @OnClick(R.id.rl_show_project)
//    public void showProjectInfo() {
//
//        Intent intent = new Intent(mContext, ProDetailActivity.class);
//
//        intent.putExtra("projectId", projectInfo.getId());
//        startActivity(intent);
//    }

    private void getUserCompanyCode() {


        if (Globals.user == null || StringUtils.isEmpty(Globals.user.getCompanyCode()) || StringUtils.isEmpty(Globals.user.getIdCode())) {
            showStatusDialog("公司代码或用户证件号为空，请去信息设置页面设置");
            delayAction(new Intent(mContext, UserInfoActivity.class), 1000);
        }

    }

    void initData() {
//        curProId = getLongInfo("projectId");
//        if(curProId<=0){
//            showToast("没有现成的项目，请申请项目！");
//            finish();
//            return;
//        }
        detController = new DetController();
        detController.setUserIDCode(Globals.user.getIdCode());
//        detList = new ArrayList<>();
//        nPage = 0;
        rxCharUuid = BleConstant.RX_CHAR_UUID;
        txCharUuid = BleConstant.TX_CHAR_UUID;
        rxBleClient = DetApplication.getRxBleClient(this);
        long proid = getLongInfo("projectId");
        projectInfo = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .where(ProjectInfoEntityDao.Properties.Id.eq(proid)).unique();
//        getReportDto();
    }

    private void initView() {
        prv.setLayoutManager(new LinearLayoutManager(mContext));
        psl.setColorSchemeColors(Color.rgb(47, 223, 189));
        psl.setRefreshing(true);
        mProjectInfoAdapter = new CheckOutAdapter();
        mProjectInfoAdapter.setOnLoadMoreListener(() -> new Handler().post(() -> loadMore()));
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        mAdapter.setPreLoadNumber(3);

        prv.setAdapter(mProjectInfoAdapter);
        prv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
//                ToastUtils.show(mContext, "position:" + position);
                // 获取itemView的位置

                Intent intent = new Intent(mContext, CheckoutDetailActivity.class);
                ChkControllerEntity chkControllerEntity = mProjectInfoAdapter.getData().get(position);
                XLog.d("projectInfoEntity:", chkControllerEntity);
//                showToast("proid:" + projectInfoEntity.getId());
                intent.putExtra("chkId", chkControllerEntity.getId());
                startActivity(intent);
            }
        });
        psl.setOnRefreshListener(() -> refresh());

    }


    private void refresh() {
//        showToast("数据更新！");
        XLog.v("数据更新! ");
        mNextRequestPage = 1;

        mProjectInfoAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        List<ChkControllerEntity> datas = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .orderDesc(ChkControllerEntityDao.Properties.Id)
                .limit(PAGE_SIZE)
                .build()
                .list();

        setChkData(true, datas);
        mProjectInfoAdapter.setEnableLoadMore(true);
//        mAdapter.setLoadMoreView(R.layout.item_load_more);
        psl.setRefreshing(false);


    }

    private void loadMore() {

        XLog.v("加载更多! ");
        int offset = (mNextRequestPage - 1) * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ChkControllerEntity> datas = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .orderDesc(ChkControllerEntityDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .build()
                .list();


        boolean isRefresh = mNextRequestPage == 1;
        setChkData(isRefresh, datas);

    }


    private void setChkData(boolean isRefresh, List datas) {
        mNextRequestPage++;
        final int size = datas == null ? 0 : datas.size();

//        XLog.v("iscomputing:", prv.isComputingLayout());
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
//            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
        } else {
            mProjectInfoAdapter.loadMoreComplete();
        }
//        for (ProjectInfoEntity datum : mProjectInfoAdapter.getData()) {
//            if (datum.getId() == curProId) {
//                datum.setSelect(true);
//                mProjectInfoAdapter.notifyDataSetChanged();
//            }
//        }
    }

    void getBaiduLocation() {

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
                    }else {
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
        XLog.i( sb.toString());
        return sb.toString();
    }


    private void configureBleResultList() {
        rvDevice.setHasFixedSize(true);
        rvDevice.setItemAnimator(null);

        rvDevice.setLayoutManager(new LinearLayoutManager(this));
        resultsAdapter = new ScanResultsAdapter(mContext);
        rvDevice.setAdapter(resultsAdapter);
//        resultsAdapter.setOnAdapterItemClickListener(view -> {
//            final int childAdapterPosition = rvDevice.getChildAdapterPosition(view);
//            final ScanResult itemAtPosition = resultsAdapter.getItemAtPosition(childAdapterPosition);
//            onAdapterItemClick(itemAtPosition);
//        });
        resultsAdapter.setOnDeviceClickListener(new ScanResultsAdapter.OnDeviceClickListener() {

            @Override
            public void onConnect(ScanResult scanResults) {
                if (scanDisposable != null) {
                    scanDisposable.dispose();
                }
//                if (!isLocation) {
//                    showLongToast("请等待定位完成！");
//                    return;
//                }
                bleDevice = scanResults.getBleDevice();
                connectionObservable = prepareConnectionObservable();
                onConnectToggleClick();
//                showToast("onConnect bleDevice"+bleDevice);
            }

            @Override
            public void onDisConnect(ScanResult scanResults) {
                onConnectToggleClick();
//                showToast("onDisConnect bleDevice" + bleDevice);
            }
        });


    }

    @OnClick(R.id.scan_device)
    public void onScanToggleClick() {

        if (isScanning()) {
            scanDisposable.dispose();
        } else {
            if (LocationPermission.isLocationPermissionGranted(this)) {
                scanBleDevices();
            } else {
                hasClickedScan = true;
                LocationPermission.requestLocationPermission(this);
            }
        }

        updateButtonUIState();
    }

    private void updateButtonUIState() {
        btConnect.setText(isScanning() ? R.string.stop_scan : R.string.start_scan);
        if (isScanning()) {
            startLoad();

        } else {
//            resultsAdapter.clearScanResults();
//            bleDevice = null;
            stopLoad();
        }
    }

    void startLoad() {
        Animation operatingAnim;
        operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        img_loading.startAnimation(operatingAnim);
        img_loading.setVisibility(View.VISIBLE);
    }

    private void stopLoad() {
        img_loading.clearAnimation();
        img_loading.setVisibility(View.GONE);
//        getDialog().dismiss();
    }

    private boolean isScanning() {
        return scanDisposable != null;
    }

    private void scanBleDevices() {
        scanDisposable = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build(),
                new ScanFilter.Builder()
//                            .setDeviceAddress("B4:99:4C:34:DC:8B")
                        // add custom filters if needed
                        .build()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::dispose)
                .subscribe(resultsAdapter::addScanResult, this::onScanFailure);
    }

    private void dispose() {

        scanDisposable = null;
//        resultsAdapter.clearScanResults();
        updateButtonUIState();
    }

    private void onScanFailure(Throwable throwable) {
        if (throwable instanceof BleScanException) {
            ScanExceptionHandler.handleException(this, (BleScanException) throwable);
        }
    }


    private Observable<RxBleConnection> prepareConnectionObservable() {
        return bleDevice
                .establishConnection(false)
                .takeUntil(disconnectTriggerSubject)
                .compose(ReplayingShare.instance());
    }

    public void onConnectToggleClick() {

        if (isConnected()) {
            triggerDisconnect();
        } else {
//            showToast("开始检验 请等待。。。。");
            final Disposable connectionDisposable = connectionObservable
                    .flatMapSingle(RxBleConnection::discoverServices)
                    .flatMapSingle(rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(rxCharUuid))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> connectUI())
                    .subscribe(
                            this::accept,
                            this::onConnectionFailure,
                            this::onConnectionFinished
                    );

            compositeDisposable.add(connectionDisposable);
        }
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void triggerDisconnect() {
        disconnectTriggerSubject.onNext(true);
    }

    private void connectUI() {

        resultsAdapter.notifyDataSetChanged();
//    connectButton.setText(R.string.connecting)
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        XLog.e(throwable);
//        Toast.makeText(mContext, "Connection error: " + throwable, Toast.LENGTH_SHORT).show();
        updateUI(null);
        dismissProgressBar();
    }

    private void onConnectionFinished() {
        XLog.i("BLE 连接完成！");
        updateUI(null);
    }


    private void accept(BluetoothGattCharacteristic characteristic) {
        updateUI(characteristic);
        XLog.i("BLE 连接已经建立！");
        onConnectionSuccessed();
    }

    public void onConnectionSuccessed() {
        final Disposable disposable = connectionObservable
                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(rxCharUuid))
                .doOnNext(notificationObservable -> runOnUiThread(this::notificationHasBeenSetUp))
                .flatMap(notificationObservable -> notificationObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNotificationReceived, this::onNotificationSetupFailure);

        compositeDisposable.add(disposable);
    }

    private void onNotificationReceived(byte[] bytes) {

        BLEDevResp devRsp = new BLEDevResp(bytes);
        if (devRsp.isValid()) {
            byte cmd = devRsp.getCmd();
            BLECmd bleCmd;
            DevCommonEnum devCheckoutEnum = DevCommonEnum.getBycode(cmd);
            XLog.v("onNotificationReceived ", devCheckoutEnum.toString());
            switch (devCheckoutEnum) {

                case CONTROLLER:
                    XLog.d("CONTROLLER ", SommerUtils.bytesToHexArrString(devRsp.getData()));
//                    byte[] d1 = devRsp.getData();
                    detController.clrDetonatorList();
                    detController.initData(devRsp.getData());
//                    detonatorAdapter.getData().clear();
                    totalDet = detController.getDetCount();
                    XLog.i(detController.toString());

                    showProgressBar("开始传输", detController.getDetCount());
//                    if (!StringUtils.isEmpty(detController.getContractId())) {
//                        bleCmd = BLECmd.getBlastTotalCmd(detController.getContractId());
//                        sendCmd2Controller(bleCmd);
//                    } else {
//                        bleCmd = BLECmd.getBlastTotalCmd("");
//                        sendCmd2Controller(bleCmd);
//                    }

//                    ControllerMultiItem controllerMultiItem = (ControllerMultiItem) detonatorAdapter.getData().get(0);
//                    controllerMultiItem.setContractId(detController.getContractId());
//
//                    detonatorAdapter.notifyDataSetChanged();
                    number = 1;
                    setProgressBar(number);
                    bleCmd = BLECmd.getDetCmd(number);
                    sendCmd2Controller(bleCmd);
                    break;
                case DET:
                    XLog.d("DET ", SommerUtils.bytesToHexArrString(devRsp.getData()));

                    Detonator detonator = new Detonator(devRsp.getData());
                    if (StringUtils.isEmpty(detonator.getDetCode())) {
//                        XLog.v("bytes ", SommerUtils.bytesToHexArrString(bytes));
                        return;
                    }
                    XLog.d("detonator ", detonator.toString());
                    if (!detController.isDetExist(detonator)) {

                        detController.addDetonator(detonator);

                        if (number < detController.getDetCount()) {
                            number++;

                            bleCmd = BLECmd.getDetCmd(number);
                            sendCmd2Controller(bleCmd);
                            setProgressBar(number);

                        } else {
                            XLog.d("isDetExist total");
                            if (isInWhiteList(detController.getSn())) {
//                                showToast("此设备" + detController.getSn() + "在白名单中！");
                                bleCmd = BLECmd.getVerify(CheckRuleEnum.SUCCESS.getCode(), 0, 0);
                                getToken();
                                sendCmd2Controller(bleCmd);
                                return;
                            }
                            if (isInBlackList(detController.getSn())) {

//                                showToast("此设备" + detController.getSn() + "在黑名单中！");
                                bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
                                sendCmd2Controller(bleCmd);
                                return;
                            }
                            long proId = isDetInProject();
                            if (proId > 0) {
                                projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                                        queryBuilder()
                                        .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
                                if (projectInfo == null) {
                                    showStatusDialog("没有找到雷管规则所对应的项目");
                                    quitCommunication();
                                    return;
                                }
//                                if (!StringUtils.isEmpty(projectInfo.getContractCode())) {
//                                    detController.setContractId(projectInfo.getContractCode());
//                                } else if (!StringUtils.isEmpty(projectInfo.getProCode())) {
//                                    detController.setContractId(projectInfo.getProCode());
//                                } else {
//                                    detController.setContractId("");
//                                }

                                bleCmd = getVerifyResult();
                                getToken();
//                                setLongInfo("projectId", proId);
                                sendCmd2Controller(bleCmd);
                            } else {
                                showStatusDialog("没有找到雷管规则所对应的项目");
                                quitCommunication();
                            }

//
                        }
                    }

                    break;
                case VERIF:
//                    XLog.d("token:",detController.getToken());

                    if(projectInfo==null){
                        showStatusDialog("项目文件出错！");
                        quitCommunication();
                        return;
                    }
                    String str =String.format  ("%015d", projectInfo.getId());
                    str += "0";
//                    if (!StringUtils.isEmpty(projectInfo.getContractCode())) {
//                        str +=  projectInfo.getContractCode();
//                    } else if (!StringUtils.isEmpty(projectInfo.getProCode())) {
//                        str += projectInfo.getProCode();
//                    } else {
//                        str += "00000000";
//                    }

                    bleCmd = BLECmd.getBlastTotalCmd(str);
                    sendCmd2Controller(bleCmd);

                    break;
                case TOEXP_TOTAL:
//                    XLog.d("TOEXP_TOTAL ", SommerUtils.bytesToHexArrString(devRsp.getData()));
                    bleCmd = BLECmd.getOverCmd();
                    sendCmd2Controller(bleCmd);

                    break;
                case OVER:
//                    projectInfo.setStatus(1);
//                    DBManager.getInstance().getProjectInfoEntityDao().update(projectInfo);
                    if (isValid) {
                        storeDetController();
                        refresh();
                    }

                    sendCheckoutReport();
                    quitCommunication();

                    break;
                default:
                    break;
            }
        } else {
            XLog.e("onReceive msg error: ", SommerUtils.bytesToHexArrString(bytes));
//            showToast("错误消息:" + SommerUtils.bytesToHexArrString(bytes));
        }
    }

    private void updateUI(BluetoothGattCharacteristic characteristic) {
//        btConnect.setText(characteristic != null ? R.string.disconnect : R.string.connect);
//        checkoutInfo.setText(characteristic != null ? R.string.disconnect : R.string.connect);
//
//        writeButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE));
//
//        writeButton.setEnabled(true);
//        notifyButton.setEnabled(true);
    }

    private void quitCommunication() {
        triggerDisconnect();

        dismissProgressBar();

        resultsAdapter.clearScanResults();
        bleDevice = null;
    }


    private void notificationHasBeenSetUp() {
        //noinspection ConstantConditions
//        Toast.makeText(mContext, "Notifications has been set up", Toast.LENGTH_SHORT).show();
        XLog.w("Notifications has been set up");
        BLECmd bleCmd = BLECmd.getController();
        sendCmd2Controller(bleCmd);
    }

    void sendCmd2Controller(BLECmd bleCmd) {
        final Disposable disposable = connectionObservable
                .firstOrError()
                .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(txCharUuid, bleCmd.getCode()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bytes -> onWriteSuccess(bytes),
                        this::onWriteFailure
                );

        compositeDisposable.add(disposable);
    }

    private void onNotificationSetupFailure(Throwable throwable) {
        dismissProgressBar();
        //noinspection ConstantConditions
        XLog.e("NotificationSetupFailure error: ", throwable);
//        Toast.makeText(mContext, "NotificationSetupFailure error: " + throwable, Toast.LENGTH_SHORT).show();
    }

    private void onWriteSuccess(byte[] bytes) {
        //noinspection ConstantConditions
        XLog.i("onWriteSuccess : ", SommerUtils.bytesToHexBlock(bytes));
//        Toast.makeText(mContext, "Write success", Toast.LENGTH_SHORT).show();
    }

    private void onWriteFailure(Throwable throwable) {
        //noinspection ConstantConditions
        XLog.w("Write error: " + throwable);
//        Toast.makeText(mContext, "Write error: " + throwable, Toast.LENGTH_SHORT).show();
    }


//    private void onAdapterItemClick(ScanResult scanResults) {
//
////        bleDevice =  scanResults.getBleDevice();
//    }


    private String getToken() {
        StringBuilder sb = new StringBuilder();
        for (Detonator detonator : detController.getDetList()) {
            sb.append(detonator.getDetCode());
        }
//        XLog.i(JSON.toJSONString(detList));
        XLog.i("sb:" + sb.toString());
        String token = MD5Util.md5(sb.toString());
        detController.setToken(token);
        XLog.i("token:" + token);
        return token;
    }

    long isDetInProject() {
//        ProjectInfoEntity projectInfoEntity
        List<ProjectInfoEntity> projectInfoEntityList = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                .orderDesc(ProjectInfoEntityDao.Properties.CreateTime)
                .limit(100).list();

        for (ProjectInfoEntity projectInfoEntity : projectInfoEntityList) {
            for (DetonatorEntity detonatorEntity : projectInfoEntity.getDetonatorList()) {
                for (Detonator detonator : detController.getDetList()) {
                    if (detonator.getDetCode().equalsIgnoreCase(detonatorEntity.getCode())) {
                        return projectInfoEntity.getId();
                    }
                }
            }

        }
        return 0;
    }

    BLECmd getVerifyResult() {
        isValid = false;
        BLECmd bleCmd;
        bleCmd = BLECmd.getVerify(CheckRuleEnum.SUCCESS.getCode(), 0, 0);


        boolean isControllerValid = false;

        for (ControllerEntity detControllerValid : projectInfo.getControllerList()) {
            if (detController.getSn().equalsIgnoreCase(detControllerValid.getName())) {
//                XLog.w("detController: controll==" + detController.getSn() + "  rule==" + detControllerValid.getName());
                isControllerValid = true;
            }
        }
        if (!isControllerValid) {
            bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
//            ToastUtils.showCustom(mContext, CheckRuleEnum.OUT_CONTROLLER.toString());
            showStatusDialog(CheckRuleEnum.OUT_CONTROLLER.getMessage());
//            checkoutInfo.setText(CheckRuleEnum.OUT_CONTROLLER.getMessage());
            return bleCmd;
        }


        // detect forbidden  zone
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
//                ToastUtils.showCustom(mContext, CheckRuleEnum.IN_FORBIDDEN.toString());
                return bleCmd;
            }

        }
        // detect permission zone
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
//                ToastUtils.showCustom(mContext, CheckRuleEnum.IN_FORBIDDEN.toString());
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
//                XLog.v("verfiryCode: ",conDet.getDetCode(),infoDet.getCode());
                String cUid = conDet.getUid().substring(conDet.getUid().length() - 8, conDet.getUid().length());
                String iUid = conDet.getUid().substring(infoDet.getUid().length() - 8, infoDet.getUid().length());
//                XLog.v("verfiryUid: ",cUid,iUid);

                if (conDet.getDetCode().equalsIgnoreCase(infoDet.getCode())
                        && cUid.equalsIgnoreCase(iUid)) {
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
//            ToastUtils.showCustom(mContext, CheckRuleEnum.ERR_DET.toString());
            showStatusDialog(CheckRuleEnum.UNREG_DET.getMessage() + unRegDet);

//            XLog.w(CheckRuleEnum.ERR_DET.getMessage() + " : " + unUsedDet + " || " + unRegDet);
            bleCmd = BLECmd.getVerify(CheckRuleEnum.ERR_DET.getCode(), unRegDet, 0);
            return bleCmd;
        }
        isValid = true;
        showStatusDialog(CheckRuleEnum.SUCCESS.getMessage());

        return bleCmd;
    }

    private boolean isInBlackList(String sn) {
        if (blackList != null && !blackList.isEmpty()) {
            for (String s : blackList) {
                if (s.equalsIgnoreCase(sn))
                    return true;
            }
        }
        return false;
    }

    private boolean isInWhiteList(String sn) {
        if (whiteList != null && !whiteList.isEmpty()) {
            for (String s : whiteList) {
                if (s.equalsIgnoreCase(sn))
                    return true;
            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (LocationPermission.isRequestLocationPermissionGranted(requestCode, permissions, grantResults)
                && hasClickedScan) {
            hasClickedScan = false;
            scanBleDevices();
        }
    }


    String getReportDto() {
        ProjectInfoDto projectInfoDto = new ProjectInfoDto();
        try {
            BeanPropertiesUtil.copyProperties(projectInfo, projectInfoDto);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        XLog.d("from:", projectInfo);
        projectInfoDto.setCreateTime(new Date());
        detController.setContractId("");
//        projectInfoDto.addDetControllers(detController);

//        XLog.v("to: ", projectInfoDto);
        return JSON.toJSONString(projectInfoDto);
    }

    void storeDetController() {

        ChkControllerEntity chkControllerEntity = new ChkControllerEntity();
        XLog.i("storeDetController start");

        ChkControllerEntity oldControllerEntity = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .where(ChkControllerEntityDao.Properties.Token.eq(detController.getToken())).unique();
        if (oldControllerEntity != null) {
//            showStatusDialog("此规则检查已完成传输！");
            return;
        }
        try {
            BeanPropertiesUtil.copyProperties(detController, chkControllerEntity);
            chkControllerEntity.setProjectInfoId(projectInfo.getId());
            chkControllerEntity.setContractId(projectInfo.getContractCode());
            chkControllerEntity.setProjectId(projectInfo.getProCode());
            chkControllerEntity.setCompany(projectInfo.getCompanyName());
            long chkId = DBManager.getInstance().getChkControllerEntityDao().insert(chkControllerEntity);
            for (Detonator detonator : detController.getDetList()) {
                ChkDetonatorEntity chkDet = new ChkDetonatorEntity();
                chkDet.setSource(SommerUtils.bytesToHexString(detonator.getSource()));
                chkDet.setChipID(detonator.getChipID());
                chkDet.setDetIDs(SommerUtils.bytesToHexString(detonator.getIds()));
                chkDet.setStatus(detonator.getStatus());
                chkDet.setType(detonator.getType());
                chkDet.setNum(detonator.getNum());
                chkDet.setValidTime(detonator.getTime());
                chkDet.setCode(detonator.getDetCode());
                chkDet.setWorkCode(SommerUtils.bytesToHexString(detonator.getAcCode()));
                chkDet.setUid(detonator.getUid());
                chkDet.setRelay(detonator.getRelay());
                chkDet.setChkId(chkId);
                DBManager.getInstance().getChkDetonatorEntityDao().insert(chkDet);
            }
            XLog.i("copy: ", chkControllerEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCheckoutReport() {

        String rptJson = getReportDto();
        String fdName = "report_info" + DateUtil.getDateDoc(new Date()) + ".json";
        FileUtils.saveFileToSDcard("detonator/json", fdName, rptJson);
//        showToast("保存完成！");
        String url = AppConstants.ETEKTestServer + AppConstants.DETUnCheck;
//        showDialog(getMyString(R.string.report));
        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
//                closeDialog();
//                showStatusDialog("服务器报错");

//                sendCmdMessage(MSG_RPT_DANLING_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                closeDialog();
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
                    XLog.w("respStr is  ", respStr);
//                    showToast("上报返回值为空");

                }

            }
        });


    }


    private void getWhiteBlackList() {
        whiteList = new ArrayList<>();
        blackList = new ArrayList<>();

        String url = AppConstants.ETEKTestServer + AppConstants.WhiteBlackList;
//        showDialog(getMyString(R.string.report));
        AsyncHttpCilentUtil.getOkHttpClient(url, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
//                closeDialog();
//                showStatusDialog("服务器报错");

//                sendCmdMessage(MSG_RPT_DANLING_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                closeDialog();
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
//                    XLog.w("respStr is  ", respStr);
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
                    XLog.d("white:"+whiteList);
                    XLog.d("black:"+blackList);
                }

            }
        });


    }

}
