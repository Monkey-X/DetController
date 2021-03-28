package com.etek.controller.yunnan.enetity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 云南greenDao数据存储
 */
@Entity
public class YunnanAuthBobmEntity {

    @Id(autoincrement = true)
    Long id;

    //作业名称
    private String mc;
    // 规则文件id
    private String FileId;
    // 爆破次数
    private int bpcs;
    // 开始时间
    private String kssj;
    // 结束时间
    private String jssj;
    //准爆半径
    private int zbbj;
    // 起爆器
    private String qbqStr;
    //准爆中心
    private String zbqyStr;
    // 雷管码
    private String lgmStr;

    private Long date;

    private int qbqCount;

    private int zbqyCount;

    private int lgmCount;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    private String authCode;

    @Generated(hash = 1559485046)
    public YunnanAuthBobmEntity(Long id, String mc, String FileId, int bpcs,
            String kssj, String jssj, int zbbj, String qbqStr, String zbqyStr,
            String lgmStr, Long date, int qbqCount, int zbqyCount, int lgmCount,
            String authCode) {
        this.id = id;
        this.mc = mc;
        this.FileId = FileId;
        this.bpcs = bpcs;
        this.kssj = kssj;
        this.jssj = jssj;
        this.zbbj = zbbj;
        this.qbqStr = qbqStr;
        this.zbqyStr = zbqyStr;
        this.lgmStr = lgmStr;
        this.date = date;
        this.qbqCount = qbqCount;
        this.zbqyCount = zbqyCount;
        this.lgmCount = lgmCount;
        this.authCode = authCode;
    }

    @Generated(hash = 112868987)
    public YunnanAuthBobmEntity() {
    }

    public int getQbqCount() {
        return qbqCount;
    }

    public void setQbqCount(int qbqCount) {
        this.qbqCount = qbqCount;
    }

    public int getZbqyCount() {
        return zbqyCount;
    }

    public void setZbqyCount(int zbqyCount) {
        this.zbqyCount = zbqyCount;
    }

    public int getLgmCount() {
        return lgmCount;
    }

    public void setLgmCount(int lgmCount) {
        this.lgmCount = lgmCount;
    }
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getFileId() {
        return FileId;
    }

    public void setFileId(String fileId) {
        FileId = fileId;
    }

    public int getBpcs() {
        return bpcs;
    }

    public void setBpcs(int bpcs) {
        this.bpcs = bpcs;
    }

    public String getKssj() {
        return kssj;
    }

    public void setKssj(String kssj) {
        this.kssj = kssj;
    }

    public String getJssj() {
        return jssj;
    }

    public void setJssj(String jssj) {
        this.jssj = jssj;
    }

    public int getZbbj() {
        return zbbj;
    }

    public void setZbbj(int zbbj) {
        this.zbbj = zbbj;
    }

    public String getQbqStr() {
        return qbqStr;
    }

    public void setQbqStr(String qbqStr) {
        this.qbqStr = qbqStr;
    }

    public String getZbqyStr() {
        return zbqyStr;
    }

    public void setZbqyStr(String zbqyStr) {
        this.zbqyStr = zbqyStr;
    }

    public String getLgmStr() {
        return lgmStr;
    }

    public void setLgmStr(String lgmStr) {
        this.lgmStr = lgmStr;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
