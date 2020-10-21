package com.etek.controller.dto;



import java.util.Date;


public class PermissibleZoneDto {

    private  int radius ; // String zbqybj;  //准爆区域半径


    private  String name ; // String zbqymc;  //准爆区域名称

    private  double longitude ; // String zbqyjd;  //准爆区域中心位置经度

    private  double latitude ; // string zbqywd;  //准爆区域中心位置维度


    private  Date startTime ; //  String zbqssj;  //准爆起始时间

    private Date stopTime ; // String zbjzsj;  //准爆截止时间

    private long projectInfoId;

    public PermissibleZoneDto() {
    }

    public PermissibleZoneDto(int radius, double longitude, double latitude) {
        this.radius = radius;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public PermissibleZoneDto(int radius, String name, double longitude, double latitude, Date startTime, Date stopTime) {
        this.radius = radius;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }



    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }


    public long getProjectInfoId() {
        return this.projectInfoId;
    }

    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }

    @Override
    public String toString() {
        return "PermissibleZoneEntity{" +
                "radius=" + radius +
                ", name='" + name + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                '}';
    }
}
