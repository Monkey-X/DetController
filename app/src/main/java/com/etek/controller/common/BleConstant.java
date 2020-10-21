package com.etek.controller.common;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description: BLE常量
 * @author: sommer 190119
 * @date: 16/8/20 20:31.
 */
public class BleConstant {

    // UUID for the ble serial port client characteristic which is necessary for notifications.
    public final static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final UUID SERIAL_SERVICE_UUID_TX = UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb");
    public static final UUID SERIAL_SERVICE_UUID_RX = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_CHAR_UUID = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_CHAR_UUID = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");
    public static final int TIME_FOREVER = -1;

    public static final long SEND_TIME_OUT_MILLIS = TimeUnit.SECONDS.toMillis(2);
    public static final int COMMUNICATION_SUCCESS = 0;
    public static final int COMMUNICATION_TIMEOUT = -1;
    public static final int COMMUNICATION_WRITE_ERR = -2;
    public static final int COMMUNICATION_DATA_ERR = -3;

    public static final int DEFAULT_SCAN_TIME = 20000;
    public static final int DEFAULT_CONN_TIME = 10000;
    public static final int DEFAULT_OPERATE_TIME = 5000;

    public static final int DEFAULT_RETRY_INTERVAL = 1000;
    public static final int DEFAULT_RETRY_COUNT = 3;

    public static final int DEFAULT_MAX_CONNECT_COUNT = 5;

    public static final int MSG_CONNECT_TIMEOUT = 0x01;
    public static final int MSG_WRITE_DATA_TIMEOUT = 0x02;
    public static final int MSG_READ_DATA_TIMEOUT = 0x03;
    public static final int MSG_RECEIVE_DATA_TIMEOUT = 0x04;
    public static final int MSG_CONNECT_RETRY = 0x05;
    public static final int MSG_WRITE_DATA_RETRY = 0x06;
    public static final int MSG_READ_DATA_RETRY = 0x07;
    public static final int MSG_RECEIVE_DATA_RETRY = 0x08;

    //yankee
    public static final int DEFAULT_SCAN_REPEAT_INTERVAL = -1;

    public static final String EXTRA_CHARACTERISTIC_UUID = "extra_uuid";
    public static final String EXTRA_MAC_ADDRESS = "extra_mac_address";
}
