package com.etek.controller.persistence.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.etek.controller.persistence.gen.DaoSession;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.persistence.gen.PendingProjectDao;

@Entity
public class PendingProject {

    @Id(autoincrement = true)
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public int getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(int projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProCode() {
        return proCode;
    }

    public void setProCode(String proCode) {
        this.proCode = proCode;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getFileSn() {
        return fileSn;
    }

    public void setFileSn(String fileSn) {
        this.fileSn = fileSn;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1380334193)
    public List<ProjectDetonator> getDetonatorList() {
        if (detonatorList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProjectDetonatorDao targetDao = daoSession.getProjectDetonatorDao();
            List<ProjectDetonator> detonatorListNew = targetDao
                    ._queryPendingProject_DetonatorList(id);
            synchronized (this) {
                if (detonatorList == null) {
                    detonatorList = detonatorListNew;
                }
            }
        }
        return detonatorList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1029473687)
    public synchronized void resetDetonatorList() {
        detonatorList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1947995175)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPendingProjectDao() : null;
    }

    String date;//爆破日期

    String projectCode;// 工程编号

    int projectStatus;// 项目状态

    private String proCode;           //xmbh;    //项目编号

    private String proName;         //xmmc; //项目名称

    private String companyCode;       //dwdm; //单位代码

    private String companyName;     //dwmc; //单位名称

    private String contractCode;         //htbh; //合同编号

    private String contractName;        //htmc;    //合同名称

    private String fileSn;        //fileSn;    //合同名称

    private double longitude ; //经度
    private double latitude ; //纬度
    private String reportStatus ; //上报状态
    private String controllerId ; //起爆器设备编号(Sbbh)

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    private long locationTime;// 定位的时间

    private String fileId;// 规则文件的id

    private String createTime; // 项目创建时间

    private String authCode;// 授权码


    public long getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(long locationTime) {
        this.locationTime = locationTime;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getControllerId() {
        return controllerId;
    }
    public String getShortSn(){
        if (controllerId.length() > 8) {
            String sSn = controllerId.substring(3, controllerId.length());
            return sSn;
        } else {
            return controllerId;
        }

    }
    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    @ToMany(referencedJoinProperty= "projectInfoId")
    List<ProjectDetonator> detonatorList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 796773759)
    private transient PendingProjectDao myDao;

    @Generated(hash = 1930657479)
    public PendingProject(Long id, String date, String projectCode, int projectStatus, String proCode,
            String proName, String companyCode, String companyName, String contractCode,
            String contractName, String fileSn, double longitude, double latitude, String reportStatus,
            String controllerId, long locationTime, String fileId, String createTime, String authCode) {
        this.id = id;
        this.date = date;
        this.projectCode = projectCode;
        this.projectStatus = projectStatus;
        this.proCode = proCode;
        this.proName = proName;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.contractCode = contractCode;
        this.contractName = contractName;
        this.fileSn = fileSn;
        this.longitude = longitude;
        this.latitude = latitude;
        this.reportStatus = reportStatus;
        this.controllerId = controllerId;
        this.locationTime = locationTime;
        this.fileId = fileId;
        this.createTime = createTime;
        this.authCode = authCode;
    }

    @Generated(hash = 1532219714)
    public PendingProject() {
    }





}
