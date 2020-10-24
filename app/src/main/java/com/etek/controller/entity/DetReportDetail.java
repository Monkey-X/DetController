<<<<<<< HEAD
package com.etek.controller.entity;

import java.util.Date;

public class DetReportDetail {
  private   String device;
    private  String detonatorid;
    private Date detTime;
    private double latitude;
    private double longitude;

    public DetReportDetail() {
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

    @Override
    public String toString() {
        return "DetReportDetail{" +
                "device='" + device + '\'' +
                ", detonatorid='" + detonatorid + '\'' +
                ", detTime=" + detTime +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
=======
package com.etek.controller.entity;

import java.util.Date;

public class DetReportDetail {
  private   String device;
    private  String detonatorid;
    private Date detTime;
    private double latitude;
    private double longitude;

    public DetReportDetail() {
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

    @Override
    public String toString() {
        return "DetReportDetail{" +
                "device='" + device + '\'' +
                ", detonatorid='" + detonatorid + '\'' +
                ", detTime=" + detTime +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
>>>>>>> 806c842... 雷管组网
