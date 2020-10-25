package com.etek.controller.entity;

import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.utils.DateUtil;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

public class DetReportInfo implements Serializable {
    private  int index;

    private String device;
    private String detonatorid;
    //    @JSONField(format="yyyy/MM/dd HH:mm:ss")
    private Date detTime;
    private double latitude;
    private double longitude;
    private int count;
    private String address;

    private String msg;

    public DetReportInfo() {
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDetonatorid() {
        return detonatorid;
    }

    public void setDetonatorid(String detonatorid) {
        this.detonatorid = detonatorid;
    }

    public Date getDetTime() {
        return detTime;
    }

    public void setDetTime(Date detTime) {
        this.detTime = detTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        DecimalFormat g = new DecimalFormat("0.00000000"); //固定的格式
        String newDevice = "";
        if(device.length()>8){
          newDevice   = device.substring(3,device.length());
        }else {
          newDevice = device;
        }

        msg = "DETMSG"
                + "-" + newDevice
                + "-" + g.format(this.latitude)
                + "-" + g.format(this.longitude)
                + "-" + this.count
                + "-" + DateUtil.getDateDStr(this.detTime) +
                "$";
        return msg;
    }

    @Override
    public String toString() {
        return "DetReportInfo{" +
                "device='" + device + '\'' +
                ", detonatorid='" + detonatorid + '\'' +
                ", detTime=" + detTime +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", count=" + count +
                '}';
    }
}
