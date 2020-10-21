package com.etek.controller.persistence.entity;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;



@Entity
public class DetonatorEntity extends BaseEntity{



    private Date validTime;         //雷管有效期 yxq

    private String uid;     //雷管UID码 uid

    private String code;        //雷管发编号 fbh

    private String workCode;     //雷管工作码 gzm


    private String relay;     //雷管起爆延时时间 relay


    private int status;     //雷管工作码错误信息 0 正常 1 黑名单 2 已使用 3 不存在

    private long projectInfoId;



    @Generated(hash = 1495567194)
    public DetonatorEntity(Date validTime, String uid, String code, String workCode,
            String relay, int status, long projectInfoId) {
        this.validTime = validTime;
        this.uid = uid;
        this.code = code;
        this.workCode = workCode;
        this.relay = relay;
        this.status = status;
        this.projectInfoId = projectInfoId;
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
}
