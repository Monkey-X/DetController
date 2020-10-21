package com.etek.controller.dto;




import java.util.Date;


public class DetonatorDto  {



    private Date validTime;         //雷管有效期 yxq

    private String uid;     //雷管UID码 uid

    private String code;        //雷管发编号 fbh

    private String workCode;     //雷管工作码 gzm

    private String relay;     //雷管起爆延时时间 relay


    private int status;     //雷管工作码错误信息 0 正常 1 黑名单 2 已使用 3 不存在





    public DetonatorDto(Date validTime, String uid, String code, String workCode,
                        String relay, int status) {
        this.validTime = validTime;
        this.uid = uid;
        this.code = code;
        this.workCode = workCode;
        this.relay = relay;
        this.status = status;

    }

    public DetonatorDto() {
    }




    public Date getValidTime() {
        return this.validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWorkCode() {
        return this.workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getRelay() {
        return this.relay;
    }

    public void setRelay(String relay) {
        this.relay = relay;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "DetonatorEntity{" +
                "validTime=" + validTime +
                ", uid='" + uid + '\'' +
                ", code='" + code + '\'' +
                ", workCode='" + workCode + '\'' +
                ", relay='" + relay + '\'' +
                ", status=" + status +

                '}';
    }
}
