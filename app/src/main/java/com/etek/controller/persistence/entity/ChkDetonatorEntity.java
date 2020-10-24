<<<<<<< HEAD
package com.etek.controller.persistence.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;


@Entity
public class ChkDetonatorEntity extends BaseEntity{


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

    private long chkId;

    @Generated(hash = 378001047)
    public ChkDetonatorEntity(Date validTime, int num, String chipID, String detIDs,
            String source, String extId, int type, String uid, String code,
            String workCode, int relay, int status, long chkId) {
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
        this.chkId = chkId;
    }

    @Generated(hash = 1395439422)
    public ChkDetonatorEntity() {
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

    public long getChkId() {
        return this.chkId;
    }

    public void setChkId(long chkId) {
        this.chkId = chkId;
    }

    @Override
    public String toString() {
        return "ChkDetonatorEntity{" +
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
                ", chkId=" + chkId +
                '}';
    }
}
=======
package com.etek.controller.persistence.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;


@Entity
public class ChkDetonatorEntity extends BaseEntity{


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

    private long chkId;

    @Generated(hash = 378001047)
    public ChkDetonatorEntity(Date validTime, int num, String chipID, String detIDs,
            String source, String extId, int type, String uid, String code,
            String workCode, int relay, int status, long chkId) {
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
        this.chkId = chkId;
    }

    @Generated(hash = 1395439422)
    public ChkDetonatorEntity() {
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

    public long getChkId() {
        return this.chkId;
    }

    public void setChkId(long chkId) {
        this.chkId = chkId;
    }

    @Override
    public String toString() {
        return "ChkDetonatorEntity{" +
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
                ", chkId=" + chkId +
                '}';
    }
}
>>>>>>> 806c842... 雷管组网
