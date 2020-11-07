package com.etek.controller.hardware.test;

public interface InitialCheckCallBack {

    /***
     * 核心板初始化自检回调信息
     * @param strHardwareVer
     * @param strUpdateHardwareVer
     * @param strSNO
     * @param strConfig
     * @param bCheckResult
     */
    public void SetInitialCheckData(String strHardwareVer,
                                    String strUpdateHardwareVer,
                                    String strSoftwareVer,
                                    String strSNO,
                                    String strConfig,
                                    byte bCheckResult);
}
