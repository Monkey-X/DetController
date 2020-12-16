package com.etek.controller.persistence.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ProjectDetonator {

    @Id(autoincrement = true)
    Long id;

    private String uid;     //雷管UID码 uid

    private String code;        //雷管管吗 fbh {可能是管吗}

    private String detId;    // 管吗内部id

    private String relay;     //雷管起爆延时时间 relay


    private int status = -1;     //雷管工作码错误信息 0 正常 1 黑名单 2 已使用 3 不存在

    private String holePosition; // 雷管孔位

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetId() {
        return detId;
    }

    public void setDetId(String detId) {
        this.detId = detId;
    }

    public String getRelay() {
        return relay;
    }

    public void setRelay(String relay) {
        this.relay = relay;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHolePosition() {
        return holePosition;
    }

    public void setHolePosition(String holePosition) {
        this.holePosition = holePosition;
    }

    public int getDownLoadStatus() {
        return downLoadStatus;
    }

    public void setDownLoadStatus(int downLoadStatus) {
        this.downLoadStatus = downLoadStatus;
    }

    public int getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(int testStatus) {
        this.testStatus = testStatus;
    }

    public long getProjectInfoId() {
        return projectInfoId;
    }

    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }

    private int downLoadStatus = -1; // 延时下载的状态吗

    private int testStatus = -1;  // 连接检测状态码

    private long projectInfoId;

    @Generated(hash = 920499162)
    public ProjectDetonator(Long id, String uid, String code, String detId,
            String relay, int status, String holePosition, int downLoadStatus,
            int testStatus, long projectInfoId) {
        this.id = id;
        this.uid = uid;
        this.code = code;
        this.detId = detId;
        this.relay = relay;
        this.status = status;
        this.holePosition = holePosition;
        this.downLoadStatus = downLoadStatus;
        this.testStatus = testStatus;
        this.projectInfoId = projectInfoId;
    }

    @Generated(hash = 24602496)
    public ProjectDetonator() {
    }

}
