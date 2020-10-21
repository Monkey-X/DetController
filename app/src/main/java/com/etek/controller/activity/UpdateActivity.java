package com.etek.controller.activity;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.elvishew.xlog.XLog;
import com.etek.controller.DetApplication;
import com.etek.controller.R;
import com.etek.controller.adapter.ScanResultsAdapter;
import com.etek.controller.common.BleConstant;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.BLECmd;
import com.etek.controller.dto.BLEDevResp;
import com.etek.controller.entity.Firmware;
import com.etek.controller.entity.HardwareInfo;
import com.etek.controller.enums.SendCmdEnum;
import com.etek.controller.exception.ScanExceptionHandler;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.utils.ToastUtils;

import com.jakewharton.rx.ReplayingShare;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;

import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;


public class UpdateActivity extends BaseActivity {



    private final int REQUEST_ENABLE_BT = 2;

    boolean isPermission = false;

    @BindView(R.id.rv_device)
    RecyclerView rvDevice;


    @BindView(R.id.scan_device)
    Button btConnect;

    @BindView(R.id.img_loading)
    ImageView img_loading;

    @BindView(R.id.det_info)
    TextView detInfo;


    Firmware firmware;

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
    private int num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        initToolBar(R.string.title_activity_dfu);
//        initSupportActionBar(R.string.title_activity_checkout);
        initData();
        initView();
        configureBleResultList();
        String firmName = "v2.3.62_v2.bin";
        getFirmwareFromSource(firmName);
        detInfo.setText(firmName);

    }

    private void getFirmwareFromSource(String res) {
        XLog.i("getFirmwareFromSource:" + res);
        byte[] bytes = readFileFromAssets(mContext, null, res);
        firmware = new Firmware(bytes);
        XLog.i("firmware:" + firmware);
//        Iterator iter = firmware.getPkts().entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            Object key = entry.getKey();
//            byte[] val = (byte[]) entry.getValue();
//            XLog.d("key:"+key+ " value:"+SommerUtils.bytesToHexArrString(val));
//        }

    }

    private  byte[] readFileFromAssets(Context context, String groupPath, String filename) {
        byte[] buffer = null;
        AssetManager am = context.getAssets();
        try {
            InputStream inputStream = null;
            if (groupPath != null) {
                inputStream = am.open(groupPath + "/" + filename);
            } else {
                inputStream = am.open(filename);
            }

            int length = inputStream.available();

            buffer = new byte[length];
            inputStream.read(buffer);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return buffer;
    }




    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {

        if (scanDisposable != null) {
            scanDisposable.dispose();
        }

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


    void initData() {


        rxCharUuid = BleConstant.RX_CHAR_UUID;
        txCharUuid = BleConstant.TX_CHAR_UUID;
        rxBleClient = DetApplication.getRxBleClient(this);

    }

    private void initView() {
//        prv.setLayoutManager(new LinearLayoutManager(mContext));
//        psl.setColorSchemeColors(Color.rgb(47, 223, 189));
//        psl.setRefreshing(true);


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

                scanBleDevices();

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

    private void notificationHasBeenSetUp() {
        //noinspection ConstantConditions
//        Toast.makeText(mContext, "Notifications has been set up", Toast.LENGTH_SHORT).show();
//        XLog.w("Notifications has been set up");
        BLECmd bleCmd = BLECmd.getUpdateStart();
        sendCmd2Controller(bleCmd);
    }

    private void onNotificationReceived(byte[] bytes) {

        BLEDevResp devRsp = new BLEDevResp(bytes);
        if (devRsp.isValid()) {
            byte cmd = devRsp.getCmd();
            BLECmd bleCmd;
            SendCmdEnum devCheckoutEnum = SendCmdEnum.getBycode(cmd);
//            XLog.v("onNotificationReceived " + devCheckoutEnum.toString());
            switch (devCheckoutEnum) {

                case INIT:
                    XLog.d("INIT " + SommerUtils.bytesToHexArrString(devRsp.getData()));
                    HardwareInfo hardwareInfo = new HardwareInfo(devRsp.getData());
                    XLog.d("hardwareInfo:" + hardwareInfo.toString());
                    detInfo.setText(hardwareInfo.toString());
                    bleCmd = BLECmd.getCmdTotal(firmware.getpNum());
                    sendCmd2Controller(bleCmd);
                    num = 1;
                    showProgressBar("开始传输", firmware.getpNum());
                    break;
                case TOTAL:
                    XLog.d("TOTAL "+SommerUtils.bytesToHexArrString(devRsp.getData()));
//                    code = sendCmd.getCmdData(1, firmware.getPkts().get(0));
                    XLog.d("data "+SommerUtils.bytesToHexArrString(firmware.getPkts().get(num - 1)));
                    if(Globals.type==1){
                        bleCmd = BLECmd.getLongWriteByte(num, firmware.getPkts().get(num - 1));
                        sendLongCmd2Controller(bleCmd);
                    }else {
                        bleCmd = BLECmd.getCmdWriteByte(num, firmware.getPkts().get(num - 1));
                        sendCmd2Controller(bleCmd);
                    }

                    setProgressBar(num);
                    num++;
                    break;
                case DATATwo:
                    XLog.d("DATATwo: " +SommerUtils.bytesToHexArrString(devRsp.getData()));

                    if(num>firmware.getpNum()){
                        bleCmd = BLECmd.getEndCmd();
                        sendCmd2Controller(bleCmd);
                    }else {
                        setProgressBar(num);
                        bleCmd = BLECmd.getLongWriteByte(num, firmware.getPkts().get(num - 1));
                        sendLongCmd2Controller(bleCmd);
                        num++;
                    }

                    break;
                case DATAOne:
                    XLog.d("DATAOne: " +SommerUtils.bytesToHexArrString(devRsp.getData()));

                    if(num>firmware.getpNum()){
                        bleCmd = BLECmd.getEndCmd();
                        sendCmd2Controller(bleCmd);
                    }else {
                        setProgressBar(num);
                        XLog.d("num: "+num + "firmware:"+SommerUtils.bytesToHexArrString(firmware.getPkts().get(num - 1)));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bleCmd = BLECmd.getCmdWriteByte(num, firmware.getPkts().get(num - 1));
                        sendCmd2Controller(bleCmd);
                        num++;
                    }

                    break;
                case END:
//                    XLog.d("TOEXP_TOTAL ", SommerUtils.bytesToHexArrString(devRsp.getData()));

                    quitCommunication();
                    break;
                case ERROR:
//

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


//    private byte[] bytesToWrite = new byte[1024]; // a kilobyte array

    void sendLongCmd2Controller(BLECmd bleCmd) {
        final Disposable disposable = connectionObservable
                .flatMap(rxBleConnection -> rxBleConnection.createNewLongWriteBuilder()
                        .setCharacteristicUuid(txCharUuid) // required or the .setCharacteristic()
                        // .setCharacteristic() alternative if you have a specific BluetoothGattCharacteristic
                        .setBytes(bleCmd.getCode())
                        // .setMaxBatchSize(maxBatchSize) // optional -> default 20 or current MTU
                        // .setWriteOperationAckStrategy(ackStrategy) // optional to postpone writing next batch
                        .build()
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        byteArray -> {
                            onWriteSuccess(byteArray);
                            // Written data.
                        },
                        this::onWriteFailure
                );

        compositeDisposable.add(disposable);
    }

    private void onNotificationSetupFailure(Throwable throwable) {
        dismissProgressBar();
        //noinspection ConstantConditions
        XLog.e("NotificationSetupFailure error: "+throwable);
//        Toast.makeText(mContext, "NotificationSetupFailure error: " + throwable, Toast.LENGTH_SHORT).show();
    }

    private void onWriteSuccess(byte[] bytes) {
        //noinspection ConstantConditions
        XLog.i("onWriteSuccess : "+SommerUtils.bytesToHexBlock(bytes));
//        Toast.makeText(mContext, "Write success", Toast.LENGTH_SHORT).show();
    }

    private void onWriteFailure(Throwable throwable) {
        //noinspection ConstantConditions
        XLog.w("Write error: " + throwable);
//        Toast.makeText(mContext, "Write error: " + throwable, Toast.LENGTH_SHORT).show();
    }





}
