package com.etek.controller.persistence.entity;



import org.greenrobot.greendao.annotation.Entity;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class RptDetonatorEntity extends BaseEntity{


    private Date validTime;         //雷管有效期 yxq


    private int num;         // 指数


    private String chipID;         // 芯片内部ID


    private String detIDs;         //芯片内部ID


    private String source;         // 类型 0 ranyi new 1

    private String extId;         //额外ID


    private int type;         // 类型

    private String uid;     //雷管UID码 uid

    private String code;        //雷管发编号 fbh

    private String workCode;     //雷管工作码 gzm

    private int relay;     //雷管起爆延时时间 relay

    private int status;     //状态

    private long reportId;

    @Generated(hash = 72395419)
    public RptDetonatorEntity(Date validTime, int num, String chipID, String detIDs,
            String source, String extId, int type, String uid, String code,
            String workCode, int relay, int status, long reportId) {
        this.validTime = validTime;
        this.num = num;
        this.chipID = chipID;
        this.detIDs = detIDs;
        this.source = source;
        this.extId = extId;
        this.type = type;
        this.uid = uid;
        this.code = code;
        this.workCode = workCode;
        this.relay = relay;
        this.status = status;
        this.reportId = reportId;
    }

    @Generated(hash = 1817127554)
    public RptDetonatorEntity() {
    }

    public Date getValidTime() {
        return this.validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getChipID() {
        return this.chipID;
    }

    public void setChipID(String chipID) {
        this.chipID = chipID;
    }

    public String getDetIDs() {
        return this.detIDs;
    }

    public void setDetIDs(String detIDs) {
        this.detIDs = detIDs;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExtId() {
        return this.extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getRelay() {
        return this.relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getReportId() {
        return this.reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    @Override
    public String toString() {
        return "RptDetonatorEntity{" +
                "validTime=" + validTime +
                ", num=" + num +
                ", chipID='" + chipID + '\'' +
                ", detIDs='" + detIDs + '\'' +
                ", source='" + source + '\'' +
                ", extId='" + extId + '\'' +
                ", type=" + type +
                ", uid='" + uid + '\'' +
                ", code='" + code + '\'' +
                ", workCode='" + workCode + '\'' +
                ", relay=" + relay +
                ", status=" + status +
                ", reportId=" + reportId +
                '}';
    }
}
