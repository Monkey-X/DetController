package com.etek.controller.hardware.test;

public interface BusChargeCallback {

     void SetProgressbarValue(int nVal);

     void DisplayText(String strText);

    void setChargeData(int nVoltage,int nCurrent);

}
