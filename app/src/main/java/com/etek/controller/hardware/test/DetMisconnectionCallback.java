package com.etek.controller.hardware.test;

public interface DetMisconnectionCallback {

    public void DisplayText(String strText);

    /**
     *
     * @param nNo       第 n 颗雷管
     * @param nID       雷管ID
     * @param strDC      雷管管码
     */
    public void FindMisconnectDet(int nNo,int nID,String strDC);
}
