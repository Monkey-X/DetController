package com.etek.controller.dto;




import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.etek.controller.entity.DetController;
import com.etek.controller.enums.ResultErrEnum;

import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.Base64Utils;
import com.etek.sommerlibrary.utils.DES3Utils;


public class ProjectFileDto {


    private String xmbh;    //项目编号
    private String xmmc; //项目名称
    private String dwdm; //单位代码
    private String dwmc; //单位名称
    private String htbh; //合同编号
    private String htmc;    //合同名称
    private String mmwj;    //文档详情
    private int result; //结果
    private String fileSn;

    private String mingma;

    private String company;

    private String companyName;

    private String userId;

    private ProInfoDto proInfo;

    private DetController detController;

    public ProjectFileDto(ProjectInfoEntity projectInfo) {
        xmbh = projectInfo.getProCode();
        xmmc = projectInfo.getProName();

    }

    public ProjectFileDto() {
    }

    public ProInfoDto getProInfo() {
        return proInfo;
    }

    public String getFileSn() {
        return fileSn;
    }


    public void setFileSn(String fileSn) {
        this.fileSn = fileSn;
    }

    public void setProInfo(ProInfoDto proInfo) {
        this.proInfo = proInfo;
    }

    public void setXmbh(String xmbh) {
        this.xmbh = xmbh;
    }

    public String getXmbh() {
        return xmbh;
    }

    public void setXmmc(String xmmc) {
        this.xmmc = xmmc;
    }

    public String getXmmc() {
        return xmmc;
    }

    public void setDwdm(String dwdm) {
        this.dwdm = dwdm;
    }

    public String getDwdm() {
        return dwdm;
    }

    public void setDwmc(String dwmc) {
        this.dwmc = dwmc;
    }

    public String getDwmc() {
        return dwmc;
    }

    public void setHtbh(String htbh) {
        this.htbh = htbh;
    }

    public String getHtbh() {
        return htbh;
    }

    public void setHtmc(String htmc) {
        this.htmc = htmc;
    }

    public String getHtmc() {
        return htmc;
    }

    public void setMmwj(String mmwj) {
        this.mmwj = mmwj;
    }

    public String getMmwj() {
        return mmwj;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMingma() {
        return mingma;
    }

    public void setMingma(String mingma) {
        this.mingma = mingma;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public DetController getDetController() {
        return detController;
    }

    public void setDetController(DetController detController) {
        this.detController = detController;
    }

    public Result parseContentAndSave(String content) {
        ProInfoDto detInfoDto = new ProInfoDto();
        if (result == ResultErrEnum.SUCCESS.getCode()) {

            try {
                byte[] decode1 = Base64Utils.getDecodeBytes(mmwj);
                byte[] decode2 = DES3Utils.decryptMode(decode1, DES3Utils.CRYPT_KEY_FRONT + content);
                if (decode2 != null && decode2.length > 0) {
                    String detInfoStr = new String(decode2);
                    mingma = detInfoStr;
                    detInfoDto = JSON.parseObject(detInfoStr, ProInfoDto.class);

                    XLog.d(detInfoDto.toString());
                    if (detInfoDto == null) {
                        return Result.errorMsg("解析数据为空");
                    }
                    this.proInfo = detInfoDto;
                }

            } catch (Exception e) {
                XLog.e(e.getMessage());

                return Result.errorMsg("数据错误：" + e.getMessage());

            }

        } else {
            return Result.errorMsg(ResultErrEnum.getBycode(result).getMessage());
        }

        return Result.successOf(this);
    }

    @Override
    public String toString() {
        return "ProjectFileDto{" +
                "xmbh='" + xmbh + '\'' +
                ", xmmc='" + xmmc + '\'' +
                ", dwdm='" + dwdm + '\'' +
                ", dwmc='" + dwmc + '\'' +
                ", htbh='" + htbh + '\'' +
                ", htmc='" + htmc + '\'' +
                ", mmwj='" + mmwj + '\'' +
                ", mingma='" + mingma + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}