/**
  * Copyright 2019 Sommer
  */
package com.etek.controller.dto;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Date;


public class ProInfoDto {

    private Lgs lgs;    //雷管组
    private Jbqys jbqys;   //警报区域
    private List<Sbbhs> sbbhs; // 起爆器设备编号组
    private Zbqys zbqys;    // 准爆区域组
    @JSONField(format="yyyy/MM/dd HH:mm:ss")
    private Date sqrq;      // 申请日期
    private String cwxx;    //申请错误信息
    public void setLgs(Lgs lgs) {
         this.lgs = lgs;
     }
     public Lgs getLgs() {
         return lgs;
     }

    public void setJbqys(Jbqys jbqys) {
         this.jbqys = jbqys;
     }
     public Jbqys getJbqys() {
         return jbqys;
     }

    public void setSbbhs(List<Sbbhs> sbbhs) {
         this.sbbhs = sbbhs;
     }
     public List<Sbbhs> getSbbhs() {
         return sbbhs;
     }

    public void setZbqys(Zbqys zbqys) {
         this.zbqys = zbqys;
     }
     public Zbqys getZbqys() {
         return zbqys;
     }

    public void setSqrq(Date sqrq) {
         this.sqrq = sqrq;
     }
     public Date getSqrq() {
         return sqrq;
     }

    public void setCwxx(String cwxx) {
         this.cwxx = cwxx;
     }
     public String getCwxx() {
         return cwxx;
     }

    @Override
    public String toString() {
        return "ProInfoDto{" +
                "lgs=" + lgs +
                ", jbqys=" + jbqys +
                ", sbbhs=" + sbbhs +
                ", zbqys=" + zbqys +
                ", sqrq=" + sqrq +
                ", cwxx='" + cwxx + '\'' +
                '}';
    }
}