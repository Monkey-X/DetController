<<<<<<< HEAD
package com.etek.controller.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.elvishew.xlog.XLog;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.Base64Utils;
import com.etek.sommerlibrary.utils.DES3Utils;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.StringTool;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@JSONType(orders = {"sbbh","jd","wd","bpsj","bprysfz","uid","xmbh","htid","dwdm"})
public class ReportDto {

    String sbbh;     //	起爆器设备编号
    String jd;      //	经度
    String wd;      //	纬度
    String bpsj;    //	爆破时间	精确到时分秒    YYYY-MM-DD HH:MM:SS
    String bprysfz;        //爆破人员身份证
    String uid;            //雷管UID	 多个雷管之间用逗号分隔
    String htid;    // 合同ID
    String xmbh;         //	项目编号
    String dwdm;        //单位代码

    public ReportDto() {
    }

    public  void setDetController(DetController detController){
        sbbh = detController.getSn();
//        htid = detController.getProjectId();
//        xmbh = detController.getContractId();
        htid = detController.getContractId();
        xmbh = detController.getProjectId();


        jd = StringTool.getDoubleStr(detController.getLongitude());
        wd = StringTool.getDoubleStr(detController.getLatitude());
        uid = "";
        if(!detController.getDetList().isEmpty()){
            List<String> dets = new ArrayList<>();
//            dets.add(detController.getDetList().get(0).getUid());
            for (Detonator detonator : detController.getDetList()) {
                dets.add(detonator.getUid());

            }
//            uid =  detController.getDetList().get(0).getUid();
            uid =  StringUtils.join(dets, ",");

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




    public void setDetControllerWithoutDet(DetController detController) {
        sbbh = detController.getSn();
//        htid = detController.getProjectId();
//        xmbh = detController.getContractId();
        htid = detController.getContractId();
        xmbh = detController.getProjectId();


        jd = StringTool.getDoubleStr(detController.getLongitude());
        wd = StringTool.getDoubleStr(detController.getLatitude());
        uid = "";
        bpsj = DateUtil.getDateStr(detController.getBlastTime());
        bprysfz = detController.getUserIDCode();
        dwdm = detController.getCompanyCode();
    }

    public void setDets(List<Detonator> detonators) {
        List<String> dets = new ArrayList<>();
//            dets.add(detController.getDetList().get(0).getUid());
        for (Detonator detonator : detonators) {
            dets.add(detonator.getUid());

        }
//            uid =  detController.getDetList().get(0).getUid();
        uid =  StringUtils.join(dets, ",");
    }
}
=======
package com.etek.controller.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.elvishew.xlog.XLog;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.Base64Utils;
import com.etek.sommerlibrary.utils.DES3Utils;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.StringTool;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@JSONType(orders = {"sbbh","jd","wd","bpsj","bprysfz","uid","xmbh","htid","dwdm"})
public class ReportDto {

    String sbbh;     //	起爆器设备编号
    String jd;      //	经度
    String wd;      //	纬度
    String bpsj;    //	爆破时间	精确到时分秒    YYYY-MM-DD HH:MM:SS
    String bprysfz;        //爆破人员身份证
    String uid;            //雷管UID	 多个雷管之间用逗号分隔
    String htid;    // 合同ID
    String xmbh;         //	项目编号
    String dwdm;        //单位代码

    public ReportDto() {
    }

    public  void setDetController(DetController detController){
        sbbh = detController.getSn();
//        htid = detController.getProjectId();
//        xmbh = detController.getContractId();
        htid = detController.getContractId();
        xmbh = detController.getProjectId();


        jd = StringTool.getDoubleStr(detController.getLongitude());
        wd = StringTool.getDoubleStr(detController.getLatitude());
        uid = "";
        if(!detController.getDetList().isEmpty()){
            List<String> dets = new ArrayList<>();
//            dets.add(detController.getDetList().get(0).getUid());
            for (Detonator detonator : detController.getDetList()) {
                dets.add(detonator.getUid());

            }
//            uid =  detController.getDetList().get(0).getUid();
            uid =  StringUtils.join(dets, ",");

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




    public void setDetControllerWithoutDet(DetController detController) {
        sbbh = detController.getSn();
//        htid = detController.getProjectId();
//        xmbh = detController.getContractId();
        htid = detController.getContractId();
        xmbh = detController.getProjectId();


        jd = StringTool.getDoubleStr(detController.getLongitude());
        wd = StringTool.getDoubleStr(detController.getLatitude());
        uid = "";
        bpsj = DateUtil.getDateStr(detController.getBlastTime());
        bprysfz = detController.getUserIDCode();
        dwdm = detController.getCompanyCode();
    }

    public void setDets(List<Detonator> detonators) {
        List<String> dets = new ArrayList<>();
//            dets.add(detController.getDetList().get(0).getUid());
        for (Detonator detonator : detonators) {
            dets.add(detonator.getUid());

        }
//            uid =  detController.getDetList().get(0).getUid();
        uid =  StringUtils.join(dets, ",");
    }
}
>>>>>>> 806c842... 雷管组网
