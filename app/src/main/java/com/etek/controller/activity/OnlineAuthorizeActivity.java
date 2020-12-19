package com.etek.controller.activity;


import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.elvishew.xlog.XLog;
import com.etek.controller.DetApplication;
import com.etek.controller.R;
import com.etek.controller.adapter.CheckOutAdapter;
import com.etek.controller.adapter.ScanResultsAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.BleConstant;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.BLECmd;
import com.etek.controller.dto.BLEDevResp;
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
import com.etek.controller.enums.CheckRuleEnum;
import com.etek.controller.enums.DevCommonEnum;
import com.etek.controller.exception.ScanExceptionHandler;
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
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;
import com.etek.sommerlibrary.utils.MD5Util;
import com.etek.sommerlibrary.utils.ToastUtils;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class OnlineAuthorizeActivity extends BaseActivity {

    private static final int PAGE_SIZE = 10;
    private static final int REQUEST_PRO_EDIT = 11;

    private final int REQUEST_ENABLE_BT = 2;

    @BindView(R.id.rv_device)
    RecyclerView rvDevice;

    @BindView(R.id.det_location)
    TextView tvLocation;

    @BindView(R.id.contract_id)
    TextView contractId;

    boolean isValid;

    int isTest = 0;

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

    BDLocation mLocation;

    CheckOutAdapter mProjectInfoAdapter;


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
    long proId = 0;
    String contractCode;
    String proCode;
    private ProjectInfoEntity projectInfo;

//    boolean isLocation = false;
//    long curProId = 0;
//    private SmartTable<Detonator> table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_authorize);
        ButterKnife.bind(this);
        initSupportActionBar(R.string.title_act_online_authorize);
        getUserCompanyCode();
        initData();
        initView();

        configureBleResultList();
        getLocationLocal();
        showDialog();

    }

    void getLocationLocal() {
        String longitudeStr = getStringInfo("Longitude");
        String latitudeStr = getStringInfo("Latitude");
        if (!StringUtils.isEmpty(longitudeStr) && !(StringUtils.isEmpty(latitudeStr))) {
            double longitude = Double.parseDouble(longitudeStr);
            double latitude = Double.parseDouble(latitudeStr);
            detController.setLongitude(longitude);
            detController.setLatitude(latitude);
            tvLocation.setText(longitude + " , " + latitude);
            tvLocation.setTextColor(getMyColor(R.color.darkgoldenrod));
        }


    }

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
//                showToast("你输入的是: " + edit.getText().toString());
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
//
                } else {
                    setStringInfo("proId", "");
                    Globals.proId = proCode;
                }
                showOnlineDocVerify();

            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isTest == 0) {
            getBaiduLocation();
        }

    }

    public void showOnlineDocVerify() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("是否手动添加雷管管码信息？");
        //设置对话框标题
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确认", (dialog1, which) -> {
//                showToast("你输入的是: " + edit.getText().toString());
            Intent intent = new Intent(mContext, OnlineEditActivity.class);
            intent.putExtra("Controller", detController);
            startActivityForResult(intent, REQUEST_PRO_EDIT);

        });
        dialog.setNegativeButton("取消", (dialog12, which) -> dialog12.dismiss());
        dialog.setCancelable(false);
        dialog.show();
    }

    public void changeLocationDialog() {
        if(!Globals.isTest){
            return;
        }
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
//                showToast("你输入的是: " + edit.getText().toString());
                double longitude = Double.parseDouble(etLongitude.getText().toString());
                double latitude = Double.parseDouble(etLatitude.getText().toString());
                detController.setLongitude(longitude);
                detController.setLatitude(latitude);
                tvLocation.setText(longitude + " , " + latitude);
                tvLocation.setTextColor(getMyColor(R.color.goldenrod));
//                if (!StringUtils.isEmpty(contractCode)) {
//                    contractId.setText((mContext.getString(R.string.contract_code_param, contractCode)));
////
//                }
//
//                if (!StringUtils.isEmpty(proCode)) {
//                    contractId.setText((mContext.getString(R.string.project_code_param, proCode)));
////
//                }

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
        if (scanDisposable != null) {
            scanDisposable.dispose();
        }
//        isLocation = false;
        super.onDestroy();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                ToastUtils.showCustom(this, "Bluetooth has turned on ");
            } else {

                ToastUtils.showCustom(this, "Problem in BT Turning ON");
            }
        } else if (requestCode == REQUEST_PRO_EDIT) {

//            if(resultCode==ProEditActivity.RESQUESTCODE){
//            detController = (DetController) data.getSerializableExtra("Controller");
//            XLog.i("ret:" + detController);
//            isTest = data.getIntExtra("Test", 1);
//            tvLocation.setText(detController.getLongitude() + " , " + detController.getLatitude());
//            tvLocation.setTextColor(getMyColor(R.color.gold));

//            }
        }

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

    void initData() {
//        curProId = getLongInfo("projectId");
//        if(curProId<=0){
//            showToast("没有现成的项目，请申请项目！");
//            finish();
//            return;
//        }
        detController = new DetController();
        detController.setUserIDCode(Globals.user.getIdCode());
        detController.setCompanyCode(Globals.user.getCompanyCode());
//        detList = new ArrayList<>();
//        nPage = 0;
        rxCharUuid = BleConstant.RX_CHAR_UUID;
        txCharUuid = BleConstant.TX_CHAR_UUID;
        rxBleClient = DetApplication.getRxBleClient(this);

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
                XLog.d("projectInfoEntity:"+chkControllerEntity);
//                showToast("proid:" + projectInfoEntity.getId());
                intent.putExtra("chkId", chkControllerEntity.getId());
                startActivity(intent);
            }
        });
        psl.setOnRefreshListener(() -> refresh());
        contractId.setOnClickListener(v -> {
            changeLocationDialog();
        });
    }


    private void refresh() {
//        showToast("数据更新！");
//        XLog.v("数据更新! ");
        mNextRequestPage = 1;

        mProjectInfoAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        List<ChkControllerEntity> datas = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .where(ChkControllerEntityDao.Properties.IsOnline.eq(1))
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

//        XLog.v("加载更多! ");
        int offset = (mNextRequestPage - 1) * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ChkControllerEntity> datas = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .where(ChkControllerEntityDao.Properties.IsOnline.eq(1))
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
//                    XLog.d(BDUtils.getLocation(location));
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
            resultsAdapter.clearScanResults();
            bleDevice = null;
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
            XLog.v("onNotificationReceived "+devCheckoutEnum.toString());
            switch (devCheckoutEnum) {

                case CONTROLLER:// 得到控制器序列号和雷管总数
                    XLog.d("CONTROLLER "+SommerUtils.bytesToHexArrString(devRsp.getData()));
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
                case DET: // 得到雷管
//                    XLog.d("DET: "+ SommerUtils.bytesToHexArrString(devRsp.getData()));

                    Detonator detonator = new Detonator(devRsp.getData());
                    if (StringUtils.isEmpty(detonator.getDetCode())) {
//                        XLog.v("bytes ", SommerUtils.bytesToHexArrString(bytes));
                        return;
                    }
                    XLog.d("detonator " + detonator.toString());
                    if (!detController.isDetExist(detonator)) {

                        detController.addDetonator(detonator);

                        if (number < detController.getDetCount()) {
                            number++;

                            bleCmd = BLECmd.getDetCmd(number);
                            sendCmd2Controller(bleCmd);
                            setProgressBar(number);

                        } else {

                                getToken();
                                 getVerifyResult();


                        }
                    }

                    break;
                case VERIF:// 校验值下传
//                    XLog.d("token:",detController.getToken());


                    String str = String.format("%015d", proId);
                    str += "0";
//                    projectInfo = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
//                            .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
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
                case TOEXP_TOTAL: //得到待起爆雷管总数
//                    XLog.d("TOEXP_TOTAL ", SommerUtils.bytesToHexArrString(devRsp.getData()));
                    bleCmd = BLECmd.getOverCmd();
                    sendCmd2Controller(bleCmd);

                    break;
                case OVER: //雷管传输结束
//                    projectInfo.setStatus(1);
//                    DBManager.getInstance().getProjectInfoEntityDao().update(projectInfo);
//                    XLog.d("over:"+);
                    if (isValid) {
                        storeDetController();
                        refresh();
                        sendCheckoutReport();
                    }


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
//        XLog.i("onWriteSuccess : "+SommerUtils.bytesToHexBlock(bytes));
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

        String token = MD5Util.md5(sb.toString());
        detController.setToken(token);

        return token;
    }


    void getVerifyResult() {
        isValid = false;
//        detController.setLongitude(105.6493);
//        detController.setLatitude(26.4922);
        OnlineCheckDto onlineCheckDto = new OnlineCheckDto();
        onlineCheckDto.setDetControllerWithoutDet(detController);
        onlineCheckDto.setDets(detController.getDetList());
        String rptJson = JSON.toJSONString(onlineCheckDto, SerializerFeature.WriteMapNullValue);
        XLog.v(rptJson);
        getToken();
        // jiangsheng
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showToast("数据编码出错：" + result.getMessage());
            BLECmd bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
            sendCmd2Controller(bleCmd);
            return;
        }
        String url = AppConstants.DanningServer + AppConstants.OnlineDownload;
//        XLog.v("url:", url);
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");


        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                dismissProgressBar();
                XLog.e("IOException:", e.getMessage());


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dismissProgressBar();
                String respStr = response.body().string();
                if (StringUtils.isEmpty(respStr)) {
                    XLog.w("respStr is null ");

                    return;
                }


                OnlineCheckResp serverResult = null;
                try {
                    Result rptDecode = RptUtil.getRptDecode(respStr);
                    if (rptDecode.isSuccess()) {
                        String data = (String) rptDecode.getData();
                        XLog.d("resp:" + data);
                        serverResult = JSON.parseObject(data, OnlineCheckResp.class);
                        XLog.d(serverResult.toString());
                        if (serverResult.getCwxx().contains("0")) {
                            ProjectFileDto projectFile = new ProjectFileDto();

                            projectFile.setCompany(Globals.user.getCompanyName());
                            projectFile.setDwdm(Globals.user.getCompanyCode());
                            projectFile.setXmbh(detController.getProjectId());
                            projectFile.setHtbh(detController.getContractId());



                            int unRegDet = 0;
                            boolean isUnreg = false;
                            for (Detonator detonator : detController.getDetList()) {
                                for (Lg lg : serverResult.getLgs().getLg()) {
                                    if(detonator.getUid().equalsIgnoreCase(lg.getUid())){
                                        if(lg.getGzmcwxx()!=0){
                                            detonator.setStatus(lg.getGzmcwxx());
                                            isUnreg = true;
                                            unRegDet++;
                                        }
                                    }
                                }
                            }
                            if(isUnreg){
                                XLog.w("unRegDet:" + unRegDet );
                                showStatusDialog("已存在已使用雷管！");
                                BLECmd bleCmd = BLECmd.getVerify(CheckRuleEnum.ERR_DET.getCode(), unRegDet, 0);
                                sendCmd2Controller(bleCmd);
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

                                BLECmd bleCmd = BLECmd.getVerify(CheckRuleEnum.SUCCESS.getCode(), 0, 0);
                                sendCmd2Controller(bleCmd);
                                isValid = true;
                                projectInfo = DBManager.getInstance().getProjectInfoEntityDao().
                                        queryBuilder()
                                        .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
                            } else {
                                showStatusDialog("已经存在有此项目");
                                BLECmd bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
                                sendCmd2Controller(bleCmd);
                            }


                        } else {
                            showStatusDialog(serverResult.getCwxxms());
                            BLECmd bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
                            sendCmd2Controller(bleCmd);
//                        result = ActivityResult.successOf("上传丹灵服务器成功!");
                        }
                    }


                } catch (Exception e) {
                    XLog.e("解析错误：" + e.getMessage());
//                    showLongToast("解析错误：" + e.getMessage());
                    BLECmd bleCmd = BLECmd.getVerify(CheckRuleEnum.OUT_CONTROLLER.getCode(), 0, 0);
                    sendCmd2Controller(bleCmd);

                }


            }
        });


    }

    private long storeProjectInfo(final ProjectFileDto projectFile, OnlineCheckResp onlineCheckResp) {

//        ThreadPoolUtils.getThreadPool().execute(()->{
//        ProInfoDto mDetInfoDto = projectFile.getProInfo();
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
        // get detonators to database by sommer 19.01.07
        Lgs lgs = onlineCheckResp.getLgs();
        if (!lgs.getLg().isEmpty()) {
            List<DetonatorEntity> detonatorEntityList = new ArrayList<>();
            for (Lg lg : lgs.getLg()) {

                DetonatorEntity detonatorBean = new DetonatorEntity();
                if (StringUtils.isEmpty(lg.getFbh())) {
                    for (Detonator detonator : detController.getDetList()) {
//                        XLog.d(detonator.toString());
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
//                                detonatorBean.set
//                            detonatorBean.setProInfoBean(proInfoBean);
                detonatorBean.setStatus(lg.getGzmcwxx());
//                                detonatorBean.set
//                               detonatorBean.setProInfoBean(detInfoDto);
                detonatorEntityList.add(detonatorBean);

//
            }
            DBManager.getInstance().getDetonatorEntityDao().insertInTx(detonatorEntityList);

        }


        Zbqys zbqys = onlineCheckResp.getZbqys();
        if (!zbqys.getZbqy().isEmpty()) {
            List<PermissibleZoneEntity> permissibleZoneEntityList = new ArrayList<>();
            for (Zbqy zbqy : zbqys.getZbqy()) {

//                                private String zbqssj;  //准爆起始时间
//
//                                private String zbjzsj;  //准爆截止时间
                PermissibleZoneEntity permissibleZone = new PermissibleZoneEntity();
//                            permissibleZoneBean.setProInfoBean(proInfoBean);
                permissibleZone.setName(zbqy.getZbqymc());
                permissibleZone.setLatitude(Double.parseDouble(zbqy.getZbqywd()));
                permissibleZone.setLongitude(Double.parseDouble(zbqy.getZbqyjd()));
                permissibleZone.setRadius(Integer.parseInt(zbqy.getZbqybj()));
                permissibleZone.setStartTime(zbqy.getZbqssj());
                permissibleZone.setStopTime(zbqy.getZbjzsj());
                permissibleZone.setProjectInfoId(proId);
                permissibleZoneEntityList.add(permissibleZone);
//                                Dao<PermissibleZoneBean, Long> permissibleZoneDao = DatabaseHelper.getInstance(mcontext).getDao(PermissibleZoneBean.class);
//                                permissibleZoneDao.create(permissibleZoneBean);
//                                permissibleZoneBean.setStartTime(zbqy.getZbqssj());
//                                permissibleZoneBean.setStopTime(zbqy.getZbjzsj());
            }
            DBManager.getInstance().getPermissibleZoneEntityDao().insertInTx(permissibleZoneEntityList);
        }
        Jbqys jbqys = onlineCheckResp.getJbqys();
        if (!jbqys.getJbqy().isEmpty()) {
            List<ForbiddenZoneEntity> forbiddenZoneEntityList = new ArrayList<>();
            for (Jbqy jbqy : jbqys.getJbqy()) {

//                                private String zbqssj;  //准爆起始时间
//
//                                private String zbjzsj;  //准爆截止时间
                ForbiddenZoneEntity forbiddenZoneEntity = new ForbiddenZoneEntity();

//                forbiddenZoneEntity.setName(jbqy.getJbjzsj());
                forbiddenZoneEntity.setLatitude(Double.parseDouble(jbqy.getJbqywd()));
                forbiddenZoneEntity.setLongitude(Double.parseDouble(jbqy.getJbqyjd()));
                forbiddenZoneEntity.setRadius(Integer.parseInt(jbqy.getJbqybj()));
                forbiddenZoneEntity.setStartTime(jbqy.getJbqssj());
                forbiddenZoneEntity.setStopTime(jbqy.getJbjzsj());
                forbiddenZoneEntity.setProjectInfoId(proId);
                forbiddenZoneEntityList.add(forbiddenZoneEntity);
//                                Dao<PermissibleZoneBean, Long> permissibleZoneDao = DatabaseHelper.getInstance(mcontext).getDao(PermissibleZoneBean.class);
//                                permissibleZoneDao.create(permissibleZoneBean);
//                                permissibleZoneBean.setStartTime(zbqy.getZbqssj());
//                                permissibleZoneBean.setStopTime(zbqy.getZbjzsj());
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
//                            detControllerBean.setProInfoBean(proInfoBean);

//                            Dao<DetControllerBean, Long> detControllerDao = DatabaseHelper.getInstance(mcontext).getDao(DetControllerBean.class);
//                            detControllerDao.create(detControllerBean);
            }
            DBManager.getInstance().getControllerEntityDao().insertInTx(controllerEntityList);
        }

//        });
        return proId;
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




    void storeDetController() {

        ChkControllerEntity chkControllerEntity = new ChkControllerEntity();
        XLog.i("storeDetController start");

        List<ChkControllerEntity> oldList = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
                .where(ChkControllerEntityDao.Properties.Token.eq(detController.getToken())).list();
        if(oldList!=null&&oldList.size()>0){
          return;
        }
//        if (oldControllerEntity != null) {
////            showStatusDialog("此规则检查已完成传输！");
//            return;
//        }
        XLog.i("storeDetController"+detController.toString());
        try {
            detController.setCompany("");
            BeanPropertiesUtil.copyProperties(detController, chkControllerEntity);
            chkControllerEntity.setProjectInfoId(proId);
            chkControllerEntity.setContractId(contractCode);
            chkControllerEntity.setProjectId(proCode);
            chkControllerEntity.setCompany(Globals.user.getCompanyCode());
            chkControllerEntity.setIsOnline(1);
            long chkId = DBManager.getInstance().getChkControllerEntityDao().insert(chkControllerEntity);
            for (Detonator detonator : detController.getDetList()) {
//                List<Lg> lgs = projectFile.getProInfo().getLgs().getLg();
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
//            XLog.i("copy: ", chkControllerEntity);
        } catch (Exception e) {
            XLog.e(e);
            e.printStackTrace();
        }
    }

    String getReportDto() {
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
//        projectInfoDto.addDetControllers(detController);

//        XLog.v("to: ", projectInfoDto);
        ValueFilter filter = (Object object, String name, Object v) -> {
            if (v == null) return "";
            return v;
        };
        return JSON.toJSONString(projectInfoDto, filter);
    }

    private void sendCheckoutReport() {

        String rptJson = getReportDto();
        if(rptJson==null){
            return;
        }
        String fdName = "report_info" + DateUtil.getDateDoc(new Date()) + ".json";
        FileUtils.saveFileToSDcard("detonator/json", fdName, rptJson);
//        showToast("保存完成！");
        String url = AppConstants.ETEKTestServer + AppConstants.CheckoutReport;

        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:" + e.getMessage());
//                closeDialog();
//                showStatusDialog("服务器报错");

//                sendCmdMessage(MSG_RPT_DANLING_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                closeDialog();
                String respStr = response.body().string();
                if (!StringUtils.isEmpty(respStr)) {
                    XLog.w("respStr is  " + respStr);
//                    showToast("上报返回值为空");

                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // sommer jiang

        if (Globals.isTest) {
            getMenuInflater().inflate(R.menu.menu_online_check_test, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_online_check, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar det_rpt_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_file) {
            Intent intent = new Intent(mContext, OnlineEditActivity.class);
            intent.putExtra("Controller", detController);
            startActivityForResult(intent, REQUEST_PRO_EDIT);

        }


        return super.onOptionsItemSelected(item);
    }


}
