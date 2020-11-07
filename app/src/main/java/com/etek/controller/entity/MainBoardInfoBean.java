package com.etek.controller.entity;

/**
 * 主板信息类
 */
public class MainBoardInfoBean {

    //硬件版本号
    String strHardwareVer;
    //升级固件版本号
    String strUpdateHardwareVer;

    //软件版本号
    String strSoftwareVer;

    //序列号
    String strSNO;

    //配置信息
    String strConfig;

    public String getStrHardwareVer() {
        return strHardwareVer;
    }

    public void setStrHardwareVer(String strHardwareVer) {
        this.strHardwareVer = strHardwareVer;
    }

    public String getStrUpdateHardwareVer() {
        return strUpdateHardwareVer;
    }

    public void setStrUpdateHardwareVer(String strUpdateHardwareVer) {
        this.strUpdateHardwareVer = strUpdateHardwareVer;
    }

    public String getStrSoftwareVer() {
        return strSoftwareVer;
    }

    public void setStrSoftwareVer(String strSoftwareVer) {
        this.strSoftwareVer = strSoftwareVer;
    }

    public String getStrSNO() {
        return strSNO;
    }

    public void setStrSNO(String strSNO) {
        this.strSNO = strSNO;
    }

    public String getStrConfig() {
        return strConfig;
    }

    public void setStrConfig(String strConfig) {
        this.strConfig = strConfig;
    }
}
