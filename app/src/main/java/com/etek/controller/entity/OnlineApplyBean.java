package com.etek.controller.entity;

//  在线详情信息
public class OnlineApplyBean {
    // 项目编号
    String xmbh;
    // 合同编号
    String htid;
    // 起爆器编号
    String sbbh;
    //  经度
    Double longtitude;
    //  纬度
    Double latitude;

    public String getXmbh() {
        return xmbh;
    }

    public void setXmbh(String xmbh) {
        this.xmbh = xmbh;
    }

    public String getHtid() {
        return htid;
    }

    public void setHtid(String htid) {
        this.htid = htid;
    }

    public String getSbbh() {
        return sbbh;
    }

    public void setSbbh(String sbbh) {
        this.sbbh = sbbh;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "OnlineApplyBean{" +
                "xmbh='" + xmbh + '\'' +
                ", htid='" + htid + '\'' +
                ", sbbh='" + sbbh + '\'' +
                ", longtitude=" + longtitude +
                ", latitude=" + latitude +
                '}';
    }
}
