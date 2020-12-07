package com.etek.controller.persistence.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;


@Entity
public class DetonatorEntity {

    @Id(autoincrement = true)
    Long id;

    private Date validTime;         //雷管有效期 yxq

    private String uid;     //雷管UID码 uid

    private String code;        //雷管管吗 fbh {可能是管吗}

    private String detId;    // 管吗内部id

    private String workCode;     //雷管工作码 gzm


    private String relay;     //雷管起爆延时时间 relay


    private int status = -1;     //雷管工作码错误信息 0 正常 1 黑名单 2 已使用 3 不存在

    private String holePosition; // 雷管孔位

    private int downLoadStatus; // 延时下载的状态吗

    private int testStatus;  // 连接检测状态码

    private long projectInfoId;


    private int serialNum;


    @Generated(hash = 1280044808)
    public DetonatorEntity(Long id, Date validTime, String uid, String code,
                           String detId, String workCode, String relay, int status,
                           String holePosition, int downLoadStatus, int testStatus,
                           long projectInfoId, int serialNum) {
        this.id = id;
        this.validTime = validTime;
        this.uid = uid;
        this.code = code;
        this.detId = detId;
        this.workCode = workCode;
        this.relay = relay;
        this.status = status;
        this.holePosition = holePosition;
        this.downLoadStatus = downLoadStatus;
        this.testStatus = testStatus;
        this.projectInfoId = projectInfoId;
        this.serialNum = serialNum;
    }

    @Generated(hash = 442328053)
    public DetonatorEntity() {
    }


    public Date getValidTime() {
        return this.validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWorkCode() {
        return this.workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getRelay() {
        return this.relay;
    }

    public void setRelay(String relay) {
        this.relay = relay;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getProjectInfoId() {
        return this.projectInfoId;
    }

    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }

    @Override
    public String toString() {
        return "DetonatorEntity{" +
                "validTime=" + validTime +
                ", uid='" + uid + '\'' +
                ", code='" + code + '\'' +
                ", workCode='" + workCode + '\'' +
                ", relay='" + relay + '\'' +
                ", status=" + status +
                ", projectInfoId=" + projectInfoId +
                '}';
    }

    public String getHolePosition() {
        return this.holePosition;
    }

    public void setHolePosition(String holePosition) {
        this.holePosition = holePosition;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDownLoadStatus() {
        return this.downLoadStatus;
    }

    public void setDownLoadStatus(int downLoadStatus) {
        this.downLoadStatus = downLoadStatus;
    }

    public int getTestStatus() {
        return this.testStatus;
    }

    public void setTestStatus(int testStatus) {
        this.testStatus = testStatus;
    }

    public int getSerialNum() {
        return this.serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public String getDetId() {
        return this.detId;
    }

    public void setDetId(String detId) {
        this.detId = detId;
    }
}
