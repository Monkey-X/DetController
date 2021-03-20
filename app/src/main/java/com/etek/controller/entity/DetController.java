package com.etek.controller.entity;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.etek.controller.dto.ReportDto2;
import com.etek.controller.model.User;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.entity.ReportEntity;

import com.etek.controller.persistence.entity.RptDetonatorEntity;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.MD5Util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import static com.etek.controller.utils.SommerUtils.bytesToInt;
import static com.etek.controller.utils.SommerUtils.getFloat;
import static java.lang.String.format;

/**
 * 起爆控制器
 */
public class DetController implements Serializable {
    private Date blastTime;
    private String company;
    private String companyCode;
    private String contractId;
    private int detCount;
    private long id;
    private double latitude;
    private double longitude;
    private String projectId;
    private String sn;
    private int status;
    private String token;
    private int type;
    private String userIDCode;
    private String uuid;
    private boolean valid;

    private List<Detonator> detList;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DetController() {
        detList = new ArrayList<>();
    }

    public DetController(ReportEntity det) {
        this.id = det.getId();
        this.sn = det.getControllerId();
        this.detCount = det.getDetonatorEntityList().size();
        this.contractId = det.getContractId();
        this.latitude = det.getLatitude();
        this.longitude = det.getLongitude();
        this.blastTime = det.getBlastTime();
        this.projectId = det.getProjectId();
        this.userIDCode = det.getIdCode();
        this.token = det.getToken();
        this.status = det.getStatus();
        this.companyCode = det.getCompanyCode();
        if (det.getDetonatorEntityList() != null && det.getDetonatorEntityList().size() > 0) {
            detList = new ArrayList<>();
            for (RptDetonatorEntity rptDetBean : det.getDetonatorEntityList()) {
                Detonator detonator = new Detonator(rptDetBean);
                detList.add(detonator);
            }


        }

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

//    public int getIndex() {
//        return index;
//    }
//
//    public void setIndex(int index) {
//        this.index = index;
//    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getBlastTime() {
        return blastTime;
    }

    public void setBlastTime(Date blastTime) {
        this.blastTime = blastTime;
    }

    public boolean initData(byte[] data) {
        if (data == null || data.length < 6 || data.length > 10) {
            valid = false;
            return false;
        }
        StringBuilder sb = new StringBuilder();
        String tmp;
        if (data[0] != 0x46) {
            valid = false;
            return false;
        }
        tmp = format("%C", data[0]);
        sb.append(tmp);
        if (data.length == 8) {


            int int1 = data[6] & 0xff;
            int int2 = (data[7] & 0xff) << 8;
            detCount = int1 | int2;

            tmp = format("%02x", data[1]);
            sb.append(tmp);
            tmp = format("%02x", data[2]);
            sb.append(tmp);
            tmp = format("%02x", data[3]);
            sb.append(tmp);
            tmp = format("%02x", data[4]);
            sb.append(tmp);
            tmp = format("%02x", data[5]);
            sb.append(tmp);

            sn = sb.toString().toUpperCase();
            valid = true;
            type = 1;
            return true;
        }
        if (data.length == 6) {


            int int1 = data[4] & 0xff;
            int int2 = (data[5] & 0xff) << 8;
            detCount = int1 | int2;
            tmp = format("%02x", data[1]);
            sb.append(tmp);
            tmp = format("%02x", data[2]);
            sb.append(tmp);
            tmp = format("%02x", data[3]);
            sb.append(tmp);

            sn = sb.toString().toUpperCase();
            valid = true;
            type = 2;
            return true;
        }
        if (data.length == 9) {


            int int1 = data[7] & 0xff;
            int int2 = (data[8] & 0xff) << 8;
            detCount = int1 | int2;

            tmp = format("%02x", data[1]);
            sb.append(tmp);
            tmp = format("%02x", data[2]);
            sb.append(tmp);
            tmp = format("%02x", data[3]);
            sb.append(tmp);
            tmp = format("%02x", data[4]);
            sb.append(tmp);
            tmp = format("%02x", data[5]);
            sb.append(tmp);
            tmp = format("%02x", data[6]);
            sb.append(tmp);
            sn = sb.toString().toUpperCase();
            valid = true;
            type = 1;
            return true;
        }

        return false;
    }

    public boolean setTimeLocation(byte[] data) {
        int date = bytesToInt(data, 0);
        String dateStr = String.format("%06d", date);
        int time = bytesToInt(data, 4);
        String timeStr = String.format("%06d-%06d", date, time);

        blastTime = DateUtil.parseDate("yyMMdd-HHmmss", timeStr);

        longitude = getFloat(data, 8);
        latitude = getFloat(data, 12);

        return true;
    }

    public boolean setTime(byte[] data) {
        int date = bytesToInt(data, 0);
        String dateStr = String.format("%06d", date);
        int time = bytesToInt(data, 4);
        String timeStr = String.format("%06d-%06d", date, time);

        blastTime = DateUtil.parseDate("yyMMdd-HHmmss", timeStr);

        return true;
    }

    public int getDetCount() {
        return detCount;
    }

    public void setDetCount(int detCount) {
        this.detCount = detCount;
    }

//    public String getSource() {
//        return source;
//    }
//
//    public void setSource(String source) {
//        this.source = source;
//    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        valid = valid;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public List<Detonator> getDetList() {
        return detList;
    }

    public void setDetList(List<Detonator> detList) {
        this.detList = detList;
    }


    public String getSn() {
        return sn;
    }

    @JSONField(serialize = false)
    public String getShortSn() {
        if (sn.length() > 8) {
            String sSn = sn.substring(3, sn.length());
            return sSn;
        } else {
            return sn;
        }


    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void addDetonator(Detonator detonator) {
        detList.add(detonator);
    }

    public void clrDetonatorList() {
        detList.clear();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserIDCode() {
        return userIDCode;
    }

    public void setUserIDCode(String userIDCode) {
        this.userIDCode = userIDCode;
    }


    @JSONField(serialize = false)
    public ReportEntity getReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setBlastTime(blastTime);
        reportEntity.setContractId(contractId);
        reportEntity.setControllerId(sn);
        reportEntity.setProjectId(projectId);
        reportEntity.setIdCode(userIDCode);
        reportEntity.setLatitude(latitude);
        reportEntity.setLongitude(longitude);
        reportEntity.setStatus(status);
        reportEntity.setToken(token);
        reportEntity.setCompanyCode(companyCode);
        return reportEntity;
    }

    @JSONField(serialize = false)
    public String getTokenByDetList() {
        StringBuilder sb = new StringBuilder();
        for (Detonator detonator : detList) {
            sb.append(detonator.getDetCode());
        }

        String token = MD5Util.md5(sb.toString());
        this.token = token;
        return token;
    }


//    public CheckBean getCheckBean() {
//        CheckBean checkBean = new CheckBean();
//        checkBean.setBlastTime(blastTime);
//        checkBean.setContractId(contractId);
//        checkBean.setControllerId(sn);
//        checkBean.setProjectId(projectId);
//        checkBean.setIdCode(userIDCode);
//        checkBean.setLatitude(latitude);
//        checkBean.setLongitude(longitude);
//        checkBean.setStatus(status);
//        checkBean.setToken(token);
//        return checkBean;
//    }

    @Override
    public String toString() {
        return "DetController{" +
                "company=" + company +
                "companyCode=" + companyCode +
                ", sn='" + sn + '\'' +
                ", detCount=" + detCount +
                ", contractId='" + contractId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", blastTime=" + blastTime +
                ", type=" + type +
                ", projectId='" + projectId + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", userIDCode='" + userIDCode + '\'' +
                ", token='" + token + '\'' +
                ", isValid=" + valid +
                ", detList=" + detList +
                '}';
    }

    public boolean isDetExist(Detonator detonator) {
        if (detList == null || detList.size() < 1) {
            return false;
        }
        for (Detonator det : detList) {
            if (det.getDetCode().equalsIgnoreCase(detonator.getDetCode())) {
                return true;
            }
        }
        return false;
    }


    public DetController(String userInfo,PendingProject prj) {
        String strTime = prj.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            this.blastTime = simpleDateFormat.parse(strTime);
        }catch (Exception e){
            this.blastTime = new Date();
        }
        this.company = prj.getCompanyName();
        this.companyCode = prj.getCompanyCode();
        this.contractId = prj.getContractCode();
        this.detCount = prj.getDetonatorList().size();
        this.id = prj.getId();
        this.latitude = prj.getLatitude();
        this.longitude = prj.getLongitude();
        this.projectId = prj.getProjectCode();
        this.sn = prj.getControllerId();
        this.status = prj.getProjectStatus();
        this.token = "";
        this.type = 0;
        User user = JSON.parseObject(userInfo, User.class);
        this.userIDCode = user.getIdCode();
        this.uuid = "";
        valid = false;

        int n = 0;
        if (prj.getDetonatorList() != null && prj.getDetonatorList().size() > 0) {
            detList = new ArrayList<>();
            for (ProjectDetonator rptDetBean : prj.getDetonatorList()) {
                Detonator detonator = new Detonator(rptDetBean);

                n++;
                detonator.setNum(n);
                detonator.setRelay(n);
                detonator.setTime(blastTime);

                detList.add(detonator);
            }
        }

        this.token = getTokenByDetList();

    }
}
