package com.etek.controller.entity;

import com.elvishew.xlog.XLog;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 离线下载的请求参数
 */
public class OfflineDownloadBean {
    // 箱条码
    String xtm;
    // 盒条码
    String htm;
    // 发编码
    String fbh;
    // 合同编号
    String htid;
    // 项目编号
    String xmbh;
    // 起爆器编号
    String sbbh;
    // 单位代码
    String dwdm;

    ArrayList<Detonator> dets;

    public String getXtm() {
        return xtm;
    }

    public void setXtm(String xtm) {
        this.xtm = xtm;
    }

    public String getHtm() {
        return htm;
    }

    public void setHtm(String htm) {
        this.htm = htm;
    }

    public String getFbh() {
        return fbh;
    }

    public void setFbh(String fbh) {
        this.fbh = fbh;
    }

    public String getHtid() {
        return htid;
    }

    public void setHtid(String htid) {
        this.htid = htid;
    }

    public String getXmbh() {
        return xmbh;
    }

    public void setXmbh(String xmbh) {
        this.xmbh = xmbh;
    }

    public String getSbbh() {
        return sbbh;
    }

    public void setSbbh(String sbbh) {
        this.sbbh = sbbh;
    }

    public String getDwdm() {
        return dwdm;
    }

    public void setDwdm(String dwdm) {
        this.dwdm = dwdm;
    }

    public void setDets(ArrayList<Detonator> detonators) {
        if(detonators ==null || detonators.isEmpty()){
            fbh = "";
            dets = new ArrayList<Detonator>();
            return;
        }
        dets = detonators;

        List<String> detcodes = new ArrayList<>();
        for (Detonator detonator : detonators) {
            XLog.d(detonator);
            detcodes.add(detonator.getDetCode());
        }
        fbh =  StringUtils.join(detcodes, ",");
    }

    public void clearDets(){
        fbh ="";
        if(null==dets){
            dets = new ArrayList<Detonator>();
            return;
        }
        dets.clear();
    }

    public ArrayList<Detonator> getDets(){
        return dets;
    }


    @Override
    public String toString() {
        return "OfflineDownloadBean{" +
                "xtm='" + xtm + '\'' +
                ", htm='" + htm + '\'' +
                ", fbh='" + fbh + '\'' +
                ", htid='" + htid + '\'' +
                ", xmbh='" + xmbh + '\'' +
                ", sbbh='" + sbbh + '\'' +
                ", dwdm='" + dwdm + '\'' +
                '}';
    }
}
