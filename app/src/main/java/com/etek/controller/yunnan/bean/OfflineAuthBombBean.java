package com.etek.controller.yunnan.bean;

import java.util.List;

/**
 * 云南离线准爆文件的下载数据
 */
public class OfflineAuthBombBean {
    /**
     * mc : 元阳马山公路隧道爆破项目
     * id : 53252820210208D0001
     * bpcs : 0
     * qbq : ["FTA821010123","FTA821010124","FTA821010125","FTA821010126"]
     * kssj : 2021-02-22 08:00:00
     * jssj : 2021-02-25 18:00:00
     * zbbj : 12000
     * zbqy : [[102.832031,23.045632],[102.892456,23.025396]]
     * lgm : ["6100102D58501","6100102D58502","6100102D58503","6100102D58504","6100102D58505","6100102D58506","6100102D58507","6100102D58508","6100102D58509"]
     */

    private int qbqCount;
    private int zbqyCount;
    private int lgmCount;
    //作业名称
    private String mc;
    // 规则文件id
    private String id;
    // 爆破次数
    private int bpcs;
    // 开始时间
    private String kssj;
    // 结束时间
    private String jssj;
    //准爆半径
    private int zbbj;
    // 起爆器
    private List<String> qbq;
    //准爆中心
    private List<List<Double>> zbqy;
    // 雷管码
    private List<String> lgm;

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

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getQbq() {
        return qbq;
    }

    public void setQbq(List<String> qbq) {
        this.qbq = qbq;
    }

    public List<List<Double>> getZbqy() {
        return zbqy;
    }

    public void setZbqy(List<List<Double>> zbqy) {
        this.zbqy = zbqy;
    }

    public List<String> getLgm() {
        return lgm;
    }

    public void setLgm(List<String> lgm) {
        this.lgm = lgm;
    }
}
