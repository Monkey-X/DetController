package com.etek.controller.dto;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class OnlineCheckResp {

    private String cwxx;
    private List<Sbbhs> sbbhs; // 起爆器设备编号组
    private Zbqys zbqys;    // 准爆区域组
    @JSONField(format="yyyy/MM/dd HH:mm:ss")
    private Date sqrq;      // 申请日期


    private Jbqys jbqys;
    private Lgs lgs;
    String cwxxms;
    public void setCwxx(String cwxx) {
         this.cwxx = cwxx;
     }
     public String getCwxx() {
         return cwxx;
     }

    public void setSqrq(Date sqrq) {
         this.sqrq = sqrq;
     }
     public Date getSqrq() {
         return sqrq;
     }

    public List<Sbbhs> getSbbhs() {
        return sbbhs;
    }

    public void setSbbhs(List<Sbbhs> sbbhs) {
        this.sbbhs = sbbhs;
    }

    public void setZbqys(Zbqys zbqys) {
         this.zbqys = zbqys;
     }
     public Zbqys getZbqys() {
         return zbqys;
     }

    public void setJbqys(Jbqys jbqys) {
         this.jbqys = jbqys;
     }
     public Jbqys getJbqys() {
         return jbqys;
     }

    public void setLgs(Lgs lgs) {
         this.lgs = lgs;
     }
     public Lgs getLgs() {
         return lgs;
     }

    public String getCwxxms() {
        return cwxxms;
    }

    public void setCwxxms(String cwxxms) {
        this.cwxxms = cwxxms;
    }

    @Override
    public String toString() {
        return "OnlineCheckResp{" +
                "cwxx='" + cwxx + '\'' +
                ", sqrq='" + sqrq + '\'' +
                ", sbbhs=" + sbbhs +
                ", zbqys=" + zbqys +
                ", jbqys=" + jbqys +
                ", lgs=" + lgs +
                ", cwxxms='" + cwxxms + '\'' +
                '}';
    }
}