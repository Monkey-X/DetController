package com.etek.controller.persistence.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ForbiddenZoneEntity extends BaseEntity{


    private long projectInfoId;

    private  int radius ; //  String jbqybj;  //禁爆区域半径

    private  double longitude ; //  String jbqyjd;  //禁爆区域中心位置经度

    private  double latitude ; // String jbqywd;  //禁爆区域中心位置纬度

    private  Date startTime ; //    Date jbqssj;    //禁爆起始时间

    private Date stopTime ; //  Date jbjzsj;    //禁爆截止时间


    @Generated(hash = 774785509)
    public ForbiddenZoneEntity(long projectInfoId, int radius, double longitude,
            double latitude, Date startTime, Date stopTime) {
        this.projectInfoId = projectInfoId;
        this.radius = radius;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    @Generated(hash = 1679253033)
    public ForbiddenZoneEntity() {
    }




    public long getProjectInfoId() {
        return this.projectInfoId;
    }

    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }


}
