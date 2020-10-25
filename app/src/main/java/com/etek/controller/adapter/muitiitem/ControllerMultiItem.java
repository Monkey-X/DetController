package com.etek.controller.adapter.muitiitem;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.etek.controller.adapter.DetControllerAdapter;
import com.etek.controller.entity.DetController;

import java.util.Date;


public class ControllerMultiItem implements MultiItemEntity {

    private long id;
    private String company;
    private String sn;
    private int detCount;
    private String contractId;
    private double latitude;
    private double longitude;
    private Date blastTime;
    private int type;
    private String projectId;
    private int status;
    private String userIDCode;
    private String token;
    private boolean isValid;


    public ControllerMultiItem(DetController detController) {
        this.id = detController.getId();

        this.company = detController.getCompany();
        this.sn = detController.getSn();
        this.detCount = detController.getDetCount();
        this.contractId = detController.getContractId();
        this.latitude = detController.getLatitude();
        this.longitude = detController.getLongitude();
        this.blastTime = detController.getBlastTime();
        this.type = detController.getType();
        this.projectId = detController.getProjectId();
        this.status = detController.getStatus();
        this.userIDCode = detController.getUserIDCode();
        this.token = detController.getToken();
        this.isValid = detController.isValid();

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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getDetCount() {
        return detCount;
    }

    public void setDetCount(int detCount) {
        this.detCount = detCount;
    }

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

    public Date getBlastTime() {
        return blastTime;
    }

    public void setBlastTime(Date blastTime) {
        this.blastTime = blastTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getUserIDCode() {
        return userIDCode;
    }

    public void setUserIDCode(String userIDCode) {
        this.userIDCode = userIDCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }


    @Override
    public int getItemType() {
        return DetControllerAdapter.TYPE_CONTROLLER;
    }
}
