package com.etek.controller.dto;




import com.etek.controller.entity.DetController;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ProjectInfoDto {


    private String proCode;           //xmbh;    //项目编号

    private String proName;         //xmmc; //项目名称

    private String companyCode;       //dwdm; //单位代码

    private String companyName;     //dwmc; //单位名称

    private String contractCode;         //htbh; //合同编号

    private String contractName;        //htmc;    //合同名称

    private String fileSn;        //fileSn;    //合同名称

    private Date createTime;


    private Date applyDate;      // 申请日期


    List<DetonatorDto> detonatorList;

    List<ForbiddenZoneDto> forbiddenZoneList;

    List<ControllerDto> controllerList;

    List<PermissibleZoneDto> permissibleZoneList;

    List<DetController>  detControllers;


    public ProjectInfoDto() {
    }

    public List<DetController> getDetControllers() {
        return detControllers;
    }

    public void setDetControllers(List<DetController> detControllers) {
        this.detControllers = detControllers;
    }
    public void addDetControllers(DetController detController) {
        if(this.detControllers==null){
            this.detControllers = new ArrayList<>();
        }
        this.detControllers.add(detController);

    }


    public String getFileSn() {
        return fileSn;
    }

    public void setFileSn(String fileSn) {
        this.fileSn = fileSn;
    }

    public List<DetonatorDto> getDetonatorList() {
        return detonatorList;
    }

    public void setDetonatorList(List<DetonatorDto> detonatorList) {
        this.detonatorList = detonatorList;
    }

    public List<ForbiddenZoneDto> getForbiddenZoneList() {
        return forbiddenZoneList;
    }

    public void setForbiddenZoneList(List<ForbiddenZoneDto> forbiddenZoneList) {
        this.forbiddenZoneList = forbiddenZoneList;
    }

    public List<ControllerDto> getControllerList() {
        return controllerList;
    }

    public void setControllerList(List<ControllerDto> controllerList) {
        this.controllerList = controllerList;
    }

    public List<PermissibleZoneDto> getPermissibleZoneList() {
        return permissibleZoneList;
    }

    public void setPermissibleZoneList(List<PermissibleZoneDto> permissibleZoneList) {
        this.permissibleZoneList = permissibleZoneList;
    }


    public String getProCode() {
        return this.proCode;
    }

    public void setProCode(String proCode) {
        this.proCode = proCode;
    }

    public String getProName() {
        return this.proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getCompanyCode() {
        return this.companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContractCode() {
        return this.contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getContractName() {
        return this.contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getApplyDate() {
        return this.applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }



    @Override
    public String toString() {
        return "ProjectInfoDto{" +
                "proCode='" + proCode + '\'' +
                ", proName='" + proName + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", contractCode='" + contractCode + '\'' +
                ", contractName='" + contractName + '\'' +
                ", fileSn='" + fileSn + '\'' +
                ", createTime=" + createTime +
                ", applyDate=" + applyDate +
                ", detonatorList=" + detonatorList +
                ", forbiddenZoneList=" + forbiddenZoneList +
                ", controllerList=" + controllerList +
                ", permissibleZoneList=" + permissibleZoneList +
                ", detController=" + detControllers +
                '}';
    }
}
