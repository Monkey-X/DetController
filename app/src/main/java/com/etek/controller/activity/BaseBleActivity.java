package com.etek.controller.activity;



import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.elvishew.xlog.XLog;
import com.etek.controller.DetApplication;
import com.etek.controller.R;
import com.etek.controller.adapter.ScanResultsAdapter;
import com.etek.controller.common.BleConstant;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.BLECmd;
import com.etek.controller.dto.BLEDevResp;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.enums.DevCommonEnum;
import com.etek.controller.exception.ScanExceptionHandler;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import com.etek.controller.utils.LocationPermission;
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

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;


public class BaseBleActivity extends BaseActivity {

    RecyclerView rvDevice;

    ImageView img_loading;

    private final int REQUEST_ENABLE_BT = 10;
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
//        btConnect.setText(isScanning() ? R.string.stop_scan : R.string.start_scan);
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

    @Override
    protected void onDestroy() {

        if (scanDisposable != null) {
            scanDisposable.dispose();
        }
//        isLocation = false;
        super.onDestroy();


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


}
