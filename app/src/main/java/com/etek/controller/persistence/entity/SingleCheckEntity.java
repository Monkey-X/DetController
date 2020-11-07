package com.etek.controller.persistence.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SingleCheckEntity {

    @Id(autoincrement = true)
    Long id;

    private String uid;     //雷管UID码 uid

    private String relay;     //雷管起爆延时时间 relay

    private int detId;  // 雷管id

    private String DC;  // 管吗；

    private int testStatus;  // 连接检测状态码

    private long projectInfoId; // 工程id

    private  int batch;  // 批次

    private String date;  //日期

    @Generated(hash = 1148781066)
    public SingleCheckEntity(Long id, String uid, String relay, int detId,
            String DC, int testStatus, long projectInfoId, int batch, String date) {
        this.id = id;
        this.uid = uid;
        this.relay = relay;
        this.detId = detId;
        this.DC = DC;
        this.testStatus = testStatus;
        this.projectInfoId = projectInfoId;
        this.batch = batch;
        this.date = date;
    }

    @Generated(hash = 1967116580)
    public SingleCheckEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRelay() {
        return this.relay;
    }

    public void setRelay(String relay) {
        this.relay = relay;
    }

    public int getDetId() {
        return this.detId;
    }

    public void setDetId(int detId) {
        this.detId = detId;
    }

    public String getDC() {
        return this.DC;
    }

    public void setDC(String DC) {
        this.DC = DC;
    }

    public int getTestStatus() {
        return this.testStatus;
    }

    public void setTestStatus(int testStatus) {
        this.testStatus = testStatus;
    }

    public long getProjectInfoId() {
        return this.projectInfoId;
    }

    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }

    public int getBatch() {
        return this.batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
