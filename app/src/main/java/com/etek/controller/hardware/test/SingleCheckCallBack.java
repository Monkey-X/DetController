package com.etek.controller.hardware.test;

public interface SingleCheckCallBack {

    void DisplayText(String strText);

    void SetSingleModuleCheckData(int nID, byte[] szDC, int nDT, byte bCheckResult);
}
