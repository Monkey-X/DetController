package com.etek.controller.dto;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ProjectDownLoadEntity {
    @Id(autoincrement = true)
    Long id;
    private String xmbh;    //项目编号
    private String xmmc; //项目名称
    private String dwdm; //单位代码
    private String dwmc; //单位名称
    private String htbh; //合同编号
    private String htmc;    //合同名称
    private int result; //结果
    private String mmwj; // 下发雷管数据
    private String fileSn;// 文件序列号

    @Generated(hash = 1093860034)
    public ProjectDownLoadEntity(Long id, String xmbh, String xmmc, String dwdm,
            String dwmc, String htbh, String htmc, int result, String mmwj,
            String fileSn) {
        this.id = id;
        this.xmbh = xmbh;
        this.xmmc = xmmc;
        this.dwdm = dwdm;
        this.dwmc = dwmc;
        this.htbh = htbh;
        this.htmc = htmc;
        this.result = result;
        this.mmwj = mmwj;
        this.fileSn = fileSn;
    }

    @Generated(hash = 2019426020)
    public ProjectDownLoadEntity() {
    }

    public String getXmbh() {
        return xmbh;
    }

    public void setXmbh(String xmbh) {
        this.xmbh = xmbh;
    }

    public String getXmmc() {
        return xmmc;
    }

    public void setXmmc(String xmmc) {
        this.xmmc = xmmc;
    }

    public String getDwdm() {
        return dwdm;
    }

    public void setDwdm(String dwdm) {
        this.dwdm = dwdm;
    }

    public String getDwmc() {
        return dwmc;
    }

    public void setDwmc(String dwmc) {
        this.dwmc = dwmc;
    }

    public String getHtbh() {
        return htbh;
    }

    public void setHtbh(String htbh) {
        this.htbh = htbh;
    }

    public String getHtmc() {
        return htmc;
    }

    public void setHtmc(String htmc) {
        this.htmc = htmc;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMmwj() {
        return mmwj;
    }

    public void setMmwj(String mmwj) {
        this.mmwj = mmwj;
    }

    public String getFileSn() {
        return fileSn;
    }

    public void setFileSn(String fileSn) {
        this.fileSn = fileSn;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}





