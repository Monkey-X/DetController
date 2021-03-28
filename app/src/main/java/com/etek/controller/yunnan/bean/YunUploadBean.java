package com.etek.controller.yunnan.bean;

import java.util.List;

/**
 * 云南项目的数据上报
 */
public class YunUploadBean {

    //规则文件id
    String id;
    // 开始时间
    String qssj;
     // 起爆器
    String qbq;
    // 起爆位置
    List<Double> zbqy;
    // 有效定位时间
    String dwsj;
    // 雷管吗清单
    List<YunDetInfoBean> lgm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQssj() {
        return qssj;
    }

    public void setQssj(String qssj) {
        this.qssj = qssj;
    }

    public String getQbq() {
        return qbq;
    }

    public void setQbq(String qbq) {
        this.qbq = qbq;
    }

    public List<Double> getZbqy() {
        return zbqy;
    }

    public void setZbqy(List<Double> zbqy) {
        this.zbqy = zbqy;
    }

    public String getDwsj() {
        return dwsj;
    }

    public void setDwsj(String dwsj) {
        this.dwsj = dwsj;
    }

    public List<YunDetInfoBean> getLgm() {
        return lgm;
    }

    public void setLgm(List<YunDetInfoBean> lgm) {
        this.lgm = lgm;
    }

}
