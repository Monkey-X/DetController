package com.etek.controller.hardware.task;

public interface ITaskCallback {

    void showProgressDialog(String msg, int type);

    void setProgressValue(int value);

    void dissProgressDialog();

    void setDisplayText(String msg);

    void postResult(int result, int type);

    void setChargeData(int nVoltage, int nCurrent);


    int POWER_ON = 1;
    int CHARGE_TYPE = 2;
    int DROP_OFF = 3;
    int BL_FALSE = 4;
    int BL_TRUE = 5;
    int DETONATE = 6;
    int MIS_CHARGE = 7;
}
