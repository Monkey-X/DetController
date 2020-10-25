/**
  * Copyright 2019 Sommer
  */
package com.etek.controller.dto;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;


public class Lg {

    private int gzmcwxx; //雷管工作码错误信息
    private String uid;     //雷管UID码
    private String fbh;     //雷管发编号
    @JSONField(format="yyyy/MM/dd HH:mm:ss")
    private Date yxq;       //雷管有效期
    private String gzm;     //雷管工作码

    public void setGzmcwxx(int gzmcwxx) {
         this.gzmcwxx = gzmcwxx;
     }
     public int getGzmcwxx() {
         return gzmcwxx;
     }

    public void setUid(String uid) {
         this.uid = uid;
     }
     public String getUid() {
         return uid;
     }

    public void setFbh(String fbh) {
         this.fbh = fbh;
     }
     public String getFbh() {
         return fbh;
     }

    public void setYxq(Date yxq) {
         this.yxq = yxq;
     }
     public Date getYxq() {
         return yxq;
     }

    public void setGzm(String gzm) {
         this.gzm = gzm;
     }
     public String getGzm() {
         return gzm;
     }

    @Override
    public String toString() {
        return "Lg{" +
                "gzmcwxx='" + gzmcwxx + '\'' +
                ", uid='" + uid + '\'' +
                ", fbh='" + fbh + '\'' +
                ", yxq=" + yxq +
                ", gzm='" + gzm + '\'' +
                '}';
    }
}