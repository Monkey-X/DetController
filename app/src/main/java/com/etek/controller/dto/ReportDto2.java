package com.etek.controller.dto;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.elvishew.xlog.XLog;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.model.User;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.StringTool;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


@JSONType(orders = {"sbbh", "jd", "wd", "bpsj", "bprysfz", "uid", "xmbh", "htid", "dwdm"})
public class ReportDto2 {

    String sbbh;     //	起爆器设备编号
    String jd;      //	经度
    String wd;      //	纬度
    String bpsj;    //	爆破时间	精确到时分秒    YYYY-MM-DD HH:MM:SS
    String bprysfz;        //爆破人员身份证
    String uid;            //雷管UID	 多个雷管之间用逗号分隔
    String htid;    // 合同ID
    String xmbh;         //	项目编号
    String dwdm;        //单位代码

    public ReportDto2() {
    }

    public void setDetController(DetController detController) {
        sbbh = detController.getSn();
//        htid = detController.getProjectId();
//        xmbh = detController.getContractId();
        htid = detController.getContractId();
        xmbh = detController.getProjectId();


        jd = StringTool.getDoubleStr(detController.getLongitude());
        wd = StringTool.getDoubleStr(detController.getLatitude());
        uid = "";
        if (!detController.getDetList().isEmpty()) {
            List<String> dets = new ArrayList<>();
//            dets.add(detController.getDetList().get(0).getUid());
            for (Detonator detonator : detController.getDetList()) {
                dets.add(detonator.getUid());

            }
//            uid =  detController.getDetList().get(0).getUid();
            uid = StringUtils.join(dets, ",");

        }

        bpsj = DateUtil.getDateStr(detController.getBlastTime());
        bprysfz = detController.getUserIDCode();
        dwdm = detController.getCompanyCode();
//        Bprysfz = detController.ge
    }

    public String getSbbh() {
        return sbbh;
    }

    public void setSbbh(String sbbh) {
        this.sbbh = sbbh;
    }

    public String getJd() {
        return jd;
    }

    public void setJd(String jd) {
        this.jd = jd;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    public String getBpsj() {
        return bpsj;
    }

    public void setBpsj(String bpsj) {
        this.bpsj = bpsj;
    }

    public String getBprysfz() {
        return bprysfz;
    }

    public void setBprysfz(String bprysfz) {
        this.bprysfz = bprysfz;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHtid() {
        return htid;
    }

    public void setHtid(String htid) {
        this.htid = htid;
    }

    public String getXmbh() {
        return xmbh;
    }

    public void setXmbh(String xmbh) {
        this.xmbh = xmbh;
    }

    public String getDwdm() {
        return dwdm;
    }

    public void setDwdm(String dwdm) {
        this.dwdm = dwdm;
    }


    public void setDetControllerWithoutDet2(String userInfo, ProjectInfoEntity projectInfoEntity) {
        sbbh = projectInfoEntity.getControllerId();
//        htid = detController.getProjectId();
//        xmbh = detController.getContractId();
        htid = projectInfoEntity.getContractCode();
        xmbh = projectInfoEntity.getProCode();


        jd = StringTool.getDoubleStr(projectInfoEntity.getLongitude());
        wd = StringTool.getDoubleStr(projectInfoEntity.getLatitude());
        uid = "";
        if (projectInfoEntity.getBlastTime() != null) {
            bpsj = DateUtil.getDateStr(projectInfoEntity.getBlastTime());
        }
//        bprysfz = projectInfoEntity.getUserIDCode();
        User user = JSON.parseObject(userInfo, User.class);
        bprysfz = user.getIdCode();
        XLog.e("IdCode: " + userInfo);
        dwdm = projectInfoEntity.getCompanyCode();
    }

    public void setDets2(List<DetonatorEntity> detonatorEntities) {
        List<String> dets = new ArrayList<>();
        for (DetonatorEntity detonatorEntity : detonatorEntities) {
            dets.add(detonatorEntity.getUid());
        }
        uid = StringUtils.join(dets, ",");
    }
}
