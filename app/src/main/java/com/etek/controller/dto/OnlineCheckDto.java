
package com.etek.controller.dto;

import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.StringTool;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OnlineCheckDto {

    private String sbbh; //起爆器设备编号
    private String jd;  //经度
    private String wd;  //纬度
    private String uid; //	雷管UID(多个雷管逗号分隔)
    private String xmbh;    //合同ID
    private String htid;    //项目编号
    private String dwdm;    //单位代码
    public void setSbbh(String sbbh) {
         this.sbbh = sbbh;
     }
     public String getSbbh() {
         return sbbh;
     }

    public void setJd(String jd) {
         this.jd = jd;
     }
     public String getJd() {
         return jd;
     }

    public void setWd(String wd) {
         this.wd = wd;
     }
     public String getWd() {
         return wd;
     }

    public void setUid(String uid) {
         this.uid = uid;
     }
     public String getUid() {
         return uid;
     }

    public void setXmbh(String xmbh) {
         this.xmbh = xmbh;
     }
     public String getXmbh() {
         return xmbh;
     }

    public void setHtid(String htid) {
         this.htid = htid;
     }
     public String getHtid() {
         return htid;
     }

    public void setDwdm(String dwdm) {
         this.dwdm = dwdm;
     }
     public String getDwdm() {
         return dwdm;
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