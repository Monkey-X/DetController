package com.etek.controller.entity;

/**
 * 离线下载的请求参数
 */
public class OfflineDownloadBean {
    // 箱条码
    String Xtm;
    // 盒条码
    String Htm;
    // 发编码
    String Fbh;
    // 合同编号
    String Htid;
    // 项目编号
    String Xmbh;
    // 起爆器编号
    String Sbbh;
    // 单位代码
    String dwdm;

    public String getXtm() {
        return Xtm;
    }

    public void setXtm(String xtm) {
        Xtm = xtm;
    }

    public String getHtm() {
        return Htm;
    }

    public void setHtm(String htm) {
        Htm = htm;
    }

    public String getFbh() {
        return Fbh;
    }

    public void setFbh(String fbh) {
        Fbh = fbh;
    }

    public String getHtid() {
        return Htid;
    }

    public void setHtid(String htid) {
        Htid = htid;
    }

    public String getXmbh() {
        return Xmbh;
    }

    public void setXmbh(String xmbh) {
        Xmbh = xmbh;
    }

    public String getSbbh() {
        return Sbbh;
    }

    public void setSbbh(String sbbh) {
        Sbbh = sbbh;
    }

    public String getDwdm() {
        return dwdm;
    }

    public void setDwdm(String dwdm) {
        this.dwdm = dwdm;
    }
}
