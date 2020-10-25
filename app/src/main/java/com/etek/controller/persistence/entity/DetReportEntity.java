package com.etek.controller.persistence.entity;





import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class DetReportEntity extends BaseEntity implements  Cloneable{



    private  String controllerId ; //   String Sbbh;     //	起爆器设备编号

    private  double longitude ; //   String Jd;      //	经度

    private  double latitude ; // String Wd;      //	纬度

    private Date blastTime ; //     String Bpsj;    //	爆破时间	精确到时分秒    YYYY-MM-DD HH:MM:SS

    private String idCode ; //  Date jbjzsj;       String Bprysfz;        //爆破人员身份证


    private String contractId ;  //    String Htid;    // 合同ID


    private String projectId ;  //    String Xmbh;         //	项目编号


    private int status ;  //   状态


    private String token ;  //   token 唯一序列号


//    private List<RptDetBean> rptDetBeans; // //雷管

    public DetReportEntity() {
    }

    @Generated(hash = 1752267467)
    public DetReportEntity(String controllerId, double longitude, double latitude,
            Date blastTime, String idCode, String contractId, String projectId, int status,
            String token) {
        this.controllerId = controllerId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.blastTime = blastTime;
        this.idCode = idCode;
        this.contractId = contractId;
        this.projectId = projectId;
        this.status = status;
        this.token = token;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getBlastTime() {
        return blastTime;
    }

    public void setBlastTime(Date blastTime) {
        this.blastTime = blastTime;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    @Override
    protected Object clone() throws CloneNotSupportedException {

        DetReportEntity report = (DetReportEntity) super.clone();

        return report;
    }



}
