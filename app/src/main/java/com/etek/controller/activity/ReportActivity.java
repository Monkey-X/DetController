package com.etek.controller.activity;


import android.bluetooth.BluetoothGattCharacteristic;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.elvishew.xlog.XLog;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.etek.controller.DetApplication;
import com.etek.controller.R;
import com.etek.controller.common.BleConstant;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.BLECmd;
import com.etek.controller.dto.BLEDevResp;

import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.DevCommonEnum;
import com.etek.controller.exception.ScanExceptionHandler;
import com.etek.controller.adapter.DetReportAdapter;
import com.etek.controller.adapter.ScanResultsAdapter;
import com.etek.controller.persistence.DBManager;


import com.etek.controller.persistence.entity.ChkControllerEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.controller.persistence.entity.ReportEntity;
import com.etek.controller.persistence.entity.RptDetonatorEntity;
import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.persistence.gen.ReportEntityDao;

import com.etek.controller.utils.LocationPermission;
import com.etek.controller.utils.SommerUtils;

import com.etek.controller.widget.DefineLoadMoreView;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.MD5Util;

import com.jakewharton.rx.ReplayingShare;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;


import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;


import java.util.List;
import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * 数据上报
 */
public class ReportActivity extends BaseActivity {


    @BindView(R.id.scan_toggle_btn)
    Button scanToggleButton;
    @BindView(R.id.scan_results)
    RecyclerView recyclerView;
    @BindView(R.id.img_loading)
    ImageView img_loading;

    private Animation operatingAnim;

    private SwipeRecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DetReportAdapter mAdapter;


    int number = 0;
    int nPage = 0;
    private static final int PAGE_SIZE = 10;

    //    ReportDao reportDao;
    List<DetController> rptCtlList;
    DetController cDetController;


    private int REQUESTCODE = 100;
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
    ProjectInfoEntity mProjectInfoEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_det_report);
        initSupportActionBar(R.string.title_activity_report);
        ButterKnife.bind(this);
        initData();
        initView();
//        if(Globals.isOnline){
//            showToast("在线模式！");
//        }else {
//            showToast("离线模式！");
//        }
    }

    private void configureResultList() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        resultsAdapter = new ScanResultsAdapter(mContext);
        recyclerView.setAdapter(resultsAdapter);
//        resultsAdapter.setOnAdapterItemClickListener(view -> {
//            final int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
//            final ScanResult itemAtPosition = resultsAdapter.getItemAtPosition(childAdapterPosition);
//            onAdapterItemClick(itemAtPosition);
//        });
        resultsAdapter.setOnDeviceClickListener(new ScanResultsAdapter.OnDeviceClickListener() {

            @Override
            public void onConnect(ScanResult scanResults) {
                if (scanDisposable != null) {
                    scanDisposable.dispose();
                }

                bleDevice = scanResults.getBleDevice();
                connectionObservable = prepareConnectionObservable();
                onConnectToggleClick();
//                showToast("onConnect bleDevice"+bleDevice);
            }

            @Override
            public void onDisConnect(ScanResult scanResults) {
                onConnectToggleClick();
                showToast("onDisConnect bleDevice" + bleDevice);
            }
        });


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
            showToast("开始传输 请等待。。。。");
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


    private void connectUI() {

        resultsAdapter.notifyDataSetChanged();
//    connectButton.setText(R.string.connecting)
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        showToast("Connection error: " + throwable);
//        Toast.makeText(mContext, "Connection error: " + throwable, Toast.LENGTH_SHORT).show();
        dismissProgressBar();
        updateUI(null);
    }

    private void onConnectionFinished() {
        updateUI(null);
    }

    private void triggerDisconnect() {
        disconnectTriggerSubject.onNext(true);
    }

    private void accept(BluetoothGattCharacteristic characteristic) {
        updateUI(characteristic);
        XLog.i("Hey, connection has been established!");
        onConnectionSuccessed();
    }

    private void updateUI(BluetoothGattCharacteristic characteristic) {
//        connectButton.setText(characteristic != null ? R.string.disconnect : R.string.connect);
//
//        writeButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE));
//
//        writeButton.setEnabled(true);
//        notifyButton.setEnabled(true);
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

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void onNotificationReceived(byte[] bytes) {
        //noinspection ConstantConditions
//        XLog.d(TAG, HexString.bytesToHex(bytes));
//        Toast.makeText(mContext, "Change: " + HexString.bytesToHex(bytes), Toast.LENGTH_SHORT).show();
        BLEDevResp devRsp = new BLEDevResp(bytes);
        if (devRsp.isValid()) {
            byte cmd = devRsp.getCmd();
            byte[] datas = devRsp.getData();
            DevCommonEnum devCheckoutEnum = DevCommonEnum.getBycode(cmd);
            switch (devCheckoutEnum) {
                case DET_PRO:
                    XLog.d("DET_PRO " + SommerUtils.bytesToHexArrString(datas) + new String(datas));
//                    XLog.d("cDetController Token:  ",SommerUtils.bytesToHexArrString(devRsp.getData()));

                    String contractOne = new String(datas).trim();
                    contractOne = contractOne.substring(0,15);
                    XLog.d("contractOne:  "+ contractOne);
                    if(StringUtils.isEmpty(contractOne)|| !StringUtils.isNumeric(contractOne)){

                        triggerDisconnect();
                        dismissProgressBar();
                        refreshInit();
                        resultsAdapter.clearScanResults();
                        showToast("项目编号有问题："+contractOne);
                    }
                    long proId = Long.parseLong(contractOne);
                    mProjectInfoEntity = DBManager.getInstance().getProjectInfoEntityDao().queryBuilder()
                            .where(ProjectInfoEntityDao.Properties.Id.eq(proId)).unique();
                    if(mProjectInfoEntity==null){

                        triggerDisconnect();
                        dismissProgressBar();
                        refreshInit();
                        resultsAdapter.clearScanResults();
                        showToast("项目编号有问题："+contractOne);
                    }
//                    if(contractOne.getBytes().length==1){
//                        if(contractOne.equalsIgnoreCase("2"))
//                        cDetController.setContractId("");
//                    }else {
//                        String type = contractOne.substring(15, 16);
//                        if (type.equalsIgnoreCase("0")) {
//                            contractOne = contractOne.substring(0, 15);
//                            cDetController.setContractId(contractOne);
//                        } else if (type.equalsIgnoreCase("1")) {
//                            contractOne = contractOne.substring(0, 15);
//                            cDetController.setProjectId(contractOne);
//                        }
//                    }
                    XLog.d("mProjectInfoEntity:  "+ mProjectInfoEntity);
                    cDetController.setProjectId(mProjectInfoEntity.getProCode());
                    cDetController.setContractId(mProjectInfoEntity.getContractCode());
                    cDetController.setCompanyCode(mProjectInfoEntity.getCompanyCode());
                    cDetController.setCompany(mProjectInfoEntity.getCompanyName());
                    if(!mProjectInfoEntity.getPermissibleZoneList().isEmpty()){
                        PermissibleZoneEntity permissibleZoneEntity = mProjectInfoEntity.getPermissibleZoneList().get(0);
                        XLog.d("permissibleZoneEntity:  "+ permissibleZoneEntity);
                        DecimalFormat df = new DecimalFormat("0.00");
                        String longitude = df.format(permissibleZoneEntity.getLongitude());

                        Random random = new Random();
                        int ends = random.nextInt(99);
//                        String.format("%02d",ends);
                        longitude+=String.format("%02d",ends);

                        cDetController.setLongitude(Double.parseDouble(longitude));

                        DecimalFormat df2 = new DecimalFormat("0.000");
                        String latitude = df2.format(permissibleZoneEntity.getLatitude());


                        ends = random.nextInt(99);
//                        String.format("%02d",ends);
                        latitude+=String.format("%02d",ends);
                        XLog.d("latitude:  "+ latitude +"  longitude:  "+ longitude);
                        cDetController.setLongitude(Double.parseDouble(longitude));
                        cDetController.setLatitude(Double.parseDouble(latitude));

                    }

//                    cDetController.setLongitude(mProjectInfoEntity.get);
//                    cDetController.setCompanyCode(Globals.user.getCompanyCode());
                    XLog.d("cDetController:  "+ cDetController);
//                    cDetController.setContractId(new String(devRsp.getData()).trim());
//                    XLog.d("cDetController ContractId:  ",cDetController.getContractId());
                    BLECmd bleCmd = BLECmd.getController();
                    sendCmd2Controller(bleCmd);

                    break;

                case CONTROLLER:
//                    XLog.d("CONTROLLER " + SommerUtils.bytesToHexArrString(devRsp.getData()));
//                    byte[] d1 = devRsp.getData();
                    cDetController.initData(datas);
                    XLog.i(cDetController.toString());

                    showProgressBar("开始传输", cDetController.getDetCount());
                    bleCmd = BLECmd.getBlast();
                    sendCmd2Controller(bleCmd);

                    break;

                case LOCATION:
//                    XLog.d("BLAST " + HexString.bytesToHexArrString(devRsp.getData()) );

//                    cDetController.setTimeLocation(devRsp.getData());
                    cDetController.setTime(datas);
                    cDetController.clrDetonatorList();
//                    XLog.d(curRptCtl.toString());
                    number = 1;
                    setProgressBar(number);
                    bleCmd = BLECmd.getDetCmd(number);
                    sendCmd2Controller(bleCmd);
                    break;
                case DET:
//                    XLog.d("DET " + SommerUtils.bytesToHexArrString(datas));
                    Detonator detonator = new Detonator(datas);
                    XLog.d("detonator " + detonator);
                    if(!mProjectInfoEntity.getDetonatorList().isEmpty()){
                        for (DetonatorEntity detonatorEntity : mProjectInfoEntity.getDetonatorList()) {
                           if(detonatorEntity.getCode().equalsIgnoreCase(detonator.getDetCode())) {
                               XLog.d("detonatorEntity " + detonatorEntity);
                               detonator.setUid(detonatorEntity.getUid());
                           }
                        }
                    }

//                    List<DetonatorEntity> entities = DBManager.getInstance().getDetonatorEntityDao().queryBuilder()
//                            .where(DetonatorEntityDao.Properties.Code.eq(detonator.getDetCode())).list();
//                    if(entities!=null&&!entities.isEmpty()){
//                        DetonatorEntity entity =entities.get(0);
//                        XLog.d("entity " + entity);
//                        detonator.setUid(entity.getUid());
//                    }else {
//                        showToast("此雷管Code没有对应的授权Code");
//                    }

                    if (!cDetController.isDetExist(detonator)) {
                        cDetController.addDetonator(detonator);


                        if (number < cDetController.getDetCount()) {
                            number++;
                            bleCmd = BLECmd.getDetCmd(number);
                            sendCmd2Controller(bleCmd);
                            setProgressBar(number);

                        } else {

                            bleCmd = BLECmd.getOverCmd();
                            sendCmd2Controller(bleCmd);
//
                        }
                    } else {
                        dismissProgressBar();
                        triggerDisconnect();
                        XLog.w("DET " + detonator.toString() + "isexist");
//                        sendCmdMessage(MSG_ERROR);
                    }

                    break;
                case OVER:

                    triggerDisconnect();
                    storeDetController(cDetController);
                    dismissProgressBar();
                    refreshInit();
                    resultsAdapter.clearScanResults();
                    break;
                default:
                    break;
            }
        } else {
            XLog.e("onReceive msg error:" + SommerUtils.bytesToHexArrString(bytes));
//            showToast("错误消息:" + SommerUtils.bytesToHexArrString(bytes));
        }
    }


    private void onNotificationSetupFailure(Throwable throwable) {
        //noinspection ConstantConditions
        XLog.e("NotificationSetupFailure error: ", throwable);


        triggerDisconnect();
        dismissProgressBar();
//        refreshInit();
        resultsAdapter.clearScanResults();

    }

    private void notificationHasBeenSetUp() {
        //noinspection ConstantConditions
//        Toast.makeText(mContext, "Notifications has been set up", Toast.LENGTH_SHORT).show();
        BLECmd bleCmd = BLECmd.getProId();
        sendCmd2Controller(bleCmd);
    }

    private boolean isScanning() {
        return scanDisposable != null;
    }

    @OnClick(R.id.scan_toggle_btn)
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

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (LocationPermission.isRequestLocationPermissionGranted(requestCode, permissions, grantResults)
                && hasClickedScan) {
            hasClickedScan = false;
            scanBleDevices();
        }
    }


    void initData() {
//        reportDao = new ReportDao(mContext);
        cDetController = new DetController();
        rptCtlList = new ArrayList<>();
        nPage = 0;
        rxCharUuid = BleConstant.RX_CHAR_UUID;
        txCharUuid = BleConstant.TX_CHAR_UUID;

    }


    void initView() {


        mRecyclerView = findViewById(R.id.recycler_view);
//        mRecyclerView.addItemDecoration(new MyItemDecoration(mContext.getResources(), R.color.divider_color, R.dimen.divider_normal, 1));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        mRecyclerView.setOnItemClickListener(mItemClickListener);

        // 自定义的核心就是DefineLoadMoreView类。
        DefineLoadMoreView loadMoreView = new DefineLoadMoreView(this);
        mRecyclerView.addFooterView(loadMoreView); // 添加为Footer。
        mRecyclerView.setLoadMoreView(loadMoreView); // 设置LoadMoreView更新监听。
        mRecyclerView.setLoadMoreListener(mLoadMoreListener); // 加载更多的监听。
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);

        mSwipeRefreshLayout = findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                com.etek.sommerlibrary.R.color.colorAccent,
                com.etek.sommerlibrary.R.color.activated,
                com.etek.sommerlibrary.R.color.colorPrimary,
                com.etek.sommerlibrary.R.color.colorPrimaryDark);


        mAdapter = new DetReportAdapter(mContext, rptCtlList);

//        mAdapter.setPreLoadNumber(3);
        mRecyclerView.setAdapter(mAdapter);


        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));

//        refresh(0);
//        handler = new MyHandler();

        rxBleClient = DetApplication.getRxBleClient(this);
        configureResultList();
    }


    /**
     * 加载更多。
     */
    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener = new SwipeRecyclerView.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    XLog.v("mLoadMoreListener");
                    refresh(nPage);
//                    List<String> strings = createDataList(mAdapter.getItemCount());
//                    mDataList.addAll(strings);
//                    // notifyItemRangeInserted()或者notifyDataSetChanged().
//                    mAdapter.notifyItemRangeInserted(mDataList.size() - strings.size(), strings.size());
//                        showToast("更新吧");
                    // 数据完更多数据，一定要掉用这个方法。
                    // 第一个参数：表示此次数据是否为空。
                    // 第二个参数：表示是否还有更多数据。
                    mRecyclerView.loadMoreFinish(false, true);

                    // 如果加载失败调用下面的方法，传入errorCode和errorMessage。
                    // errorCode随便传，你自定义LoadMoreView时可以根据errorCode判断错误类型。
                    // errorMessage是会显示到loadMoreView上的，用户可以看到。
                    // mRecyclerView.loadMoreError(0, "请求网络失败");
                }
            }, 1000);
        }
    };

    /**
     * Item点击监听。
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
//            Toast.makeText(mContext, "第" + position + "个", Toast.LENGTH_SHORT).show();
            cDetController = rptCtlList.get(position);
            Intent intent = new Intent(mContext, ReportDetailActivity.class);
            intent.putExtra("DetController", cDetController);
//        delayAction(intent,1000);
            startActivityForResult(intent, REQUESTCODE);
        }
    };
    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = (swipeLeftMenu, swipeRightMenu, position) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_72);

        int height = ViewGroup.LayoutParams.MATCH_PARENT;


        // 添加右侧的，如果不添加，则右侧不会出现菜单。

        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext).setBackground(R.drawable.selector_red)
                .setImage(R.mipmap.delete)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。


    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
//                showToast("list第" + position + "; 右侧菜单第" + menuPosition);
                if (menuPosition == 0) {
                    showRemoveDialog(position);

                }
            }
        }
    };

    void showRemoveDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("是否删除此数据！");
        //设置对话框标题
        builder.setIcon(R.mipmap.ic_launcher);


        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                reportDao.deleteController(rptCtlList.get(position));
                ReportEntity reportEntity = rptCtlList.get(position).getReportEntity();
                reportEntity.setId(rptCtlList.get(position).getId());
                DBManager.getInstance().getReportEntityDao().delete(reportEntity);
//                DBManager.getInstance().getDetReportEntityDao().delete(rptCtlList.get(position));
                rptCtlList.remove(position);
                mAdapter.notifyItemRemoved(position);

            }
        });
        builder.setNegativeButton("取消", null);
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }


    void showSameDetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("此次雷管传输已经存在！");
        //设置对话框标题
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setPositiveButton("确认", null);
//        builder.setNegativeButton("取消", null);
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    String getToken(DetController detController) {
        StringBuilder sb = new StringBuilder();
        for (Detonator detonator : detController.getDetList()) {
            sb.append(detonator.getDetCode());
        }
//        XLog.i("sb:" + sb.toString());
        String token = MD5Util.md5(sb.toString());
        return token;
    }

    long storeDetController(DetController detController) {

        detController.setStatus(0);
//        detController.setProjectId(proId);
        detController.setUserIDCode(Globals.user.getIdCode());
//        detController.setContractId(contractId);

//        XLog.i(" old token :", detController.getToken());

        String token = getToken(detController);
//        XLog.i(" new token :", token);
        detController.setToken(token);
//        ChkControllerEntity chkControllerEntity = DBManager.getInstance().getChkControllerEntityDao().queryBuilder()
//                .where(ChkControllerEntityDao.Properties.Token.eq(detController.getToken())).unique();
//        if(chkControllerEntity==null){
//            showStatusDialog("没有此对应的规则检查文件！");
//            return 0;
//        }
//        XLog.i(" chkControllerEntity :", chkControllerEntity);
//        detController.setContractId(chkControllerEntity.getContractId());
//        detController.setProjectId(chkControllerEntity.getProjectId());
        return storeReport(detController);


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshInit();
    }

    @Override
    protected void onDestroy() {

        if (scanDisposable != null) {
            scanDisposable.dispose();
            scanDisposable = null;
        }
        super.onDestroy();


    }

    void refresh(int page) {
        int offset = page * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;
        List<ReportEntity> datas = DBManager.getInstance().getReportEntityDao().queryBuilder()
                .orderDesc(ReportEntityDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .build()
                .list();

        if (datas != null && !datas.isEmpty()) {
            for (ReportEntity data : datas) {
                DetController detCtrl = new DetController(data);
                rptCtlList.add(detCtrl);
            }
            nPage++;
            mAdapter.dataChange(rptCtlList);
        }
        mRecyclerView.loadMoreFinish(false, true);
        mSwipeRefreshLayout.setRefreshing(false);
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

    private void onWriteSuccess(byte[] bytes) {
        //noinspection ConstantConditions

//        Toast.makeText(mContext, "Write success", Toast.LENGTH_SHORT).show();
    }

    private void onWriteFailure(Throwable throwable) {
        //noinspection ConstantConditions
        showToast("Write error: " + throwable);
//        Toast.makeText(mContext, "Write error: " + throwable, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        XLog.w("requestCode:" + requestCode + " resultCode:" + resultCode);
//        if (requestCode == REQUESTCODE) {
////            refreshInit();
////            if (resultCode == Activity.RESULT_OK) {
////                curRptCtl.setStatus(1);
////                mAdapter.notifyDataSetChanged();
////            }
//        }


    }


    private void updateButtonUIState() {
        scanToggleButton.setText(isScanning() ? R.string.stop_scan : R.string.start_scan);
        if (isScanning()) {
            startLoad();
        } else {
            stopLoad();
        }
    }

    void startLoad() {
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

//    private void onAdapterItemClick(ScanResult scanResults) {
//
////        bleDevice =  scanResults.getBleDevice();
//
//    }

    private void refreshInit() {
        rptCtlList.clear();
        nPage = 0;
        refresh(nPage);
    }

    private long storeReport(final DetController detController) {
        ReportEntity reportEntity = detController.getReportEntity();
        ReportEntity oldController = DBManager.getInstance().getReportEntityDao().queryBuilder()
                .where(ReportEntityDao.Properties.Token.eq(detController.getToken())).unique();
        if (oldController != null) {
            showSameDetDialog();
            return 0;
        }
//        detController.setContractId();
        if (detController.getDetList() == null || detController.getDetList().size() == 0) {
            return -10;
        }

        long rptId = DBManager.getInstance().getReportEntityDao().insert(reportEntity);


        for (Detonator detonator : detController.getDetList()) {
            RptDetonatorEntity rptDet = new RptDetonatorEntity();
            rptDet.setSource(SommerUtils.bytesToHexString(detonator.getSource()));
            rptDet.setChipID(detonator.getChipID());
            rptDet.setDetIDs(SommerUtils.bytesToHexString(detonator.getIds()));
            rptDet.setStatus(detonator.getStatus());
            rptDet.setType(detonator.getType());
            rptDet.setNum(detonator.getNum());
            rptDet.setValidTime(detonator.getTime());
            rptDet.setCode(detonator.getDetCode());
            rptDet.setWorkCode(SommerUtils.bytesToHexString(detonator.getAcCode()));
            rptDet.setUid(detonator.getUid());
            rptDet.setRelay(detonator.getRelay());
            rptDet.setReportId(rptId);
//            rptDet.setId(SommerUtils.bytesToLong(detonator.getIds()));
            DBManager.getInstance().getRptDetonatorEntityDao().insertOrReplace(rptDet);

        }

        return rptId;

    }


}
