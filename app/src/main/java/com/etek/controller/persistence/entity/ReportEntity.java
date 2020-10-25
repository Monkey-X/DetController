package com.etek.controller.persistence.entity;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.etek.controller.entity.DetController;
import com.etek.controller.persistence.gen.DaoSession;
import com.etek.controller.persistence.gen.RptDetonatorEntityDao;
import com.etek.controller.persistence.gen.ReportEntityDao;

@Entity
public class ReportEntity  implements  Cloneable{

    @Id(autoincrement = true)
    Long id;

    private  String controllerId ; //   String Sbbh;     //	起爆器设备编号

    private  double longitude ; //   String Jd;      //	经度

    private  double latitude ; // String Wd;      //	纬度

    private Date blastTime ; //     String Bpsj;    //	爆破时间	精确到时分秒    YYYY-MM-DD HH:MM:SS

    private String idCode ; //  Date jbjzsj;       String Bprysfz;        //爆破人员身份证

    private String contractId ;  //    String Htid;    // 合同ID

    private String projectId ;  //    String Xmbh;         //	项目编号

    private int status ;  //   状态

    private String token ;  //   token 唯一序列号

    private String companyCode ;  //   companyCode 唯一序列号

    @ToMany(referencedJoinProperty= "reportId")
    private List<RptDetonatorEntity> detonatorEntityList; // //雷管

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 467901170)
    private transient ReportEntityDao myDao;

    @Generated(hash = 1684928871)
    public ReportEntity(Long id, String controllerId, double longitude, double latitude,
            Date blastTime, String idCode, String contractId, String projectId, int status,
            String token, String companyCode) {
        this.id = id;
        this.controllerId = controllerId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.blastTime = blastTime;
        this.idCode = idCode;
        this.contractId = contractId;
        this.projectId = projectId;
        this.status = status;
        this.token = token;
        this.companyCode = companyCode;
    }

    @Generated(hash = 683167796)
    public ReportEntity() {
    }

    public ReportEntity(DetController detController) {
//        this.id = detController.getId();
        this.controllerId = detController.getSn();
        this.longitude = detController.getLongitude();
        this.latitude = detController.getLatitude();
        this.blastTime = detController.getBlastTime();
        this.idCode = detController.getUserIDCode();
        this.contractId = detController.getContractId();
        this.projectId = detController.getProjectId();
        this.status = detController.getStatus();
        this.token = detController.getToken();
        this.companyCode = detController.getCompanyCode();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getControllerId() {
        return this.controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getBlastTime() {
        return this.blastTime;
    }

    public void setBlastTime(Date blastTime) {
        this.blastTime = blastTime;
    }

    public String getIdCode() {
        return this.idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getContractId() {
        return this.contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCompanyCode() {
        return this.companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 786399843)
    public List<RptDetonatorEntity> getDetonatorEntityList() {
        if (detonatorEntityList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RptDetonatorEntityDao targetDao = daoSession.getRptDetonatorEntityDao();
            List<RptDetonatorEntity> detonatorEntityListNew = targetDao
                    ._queryReportEntity_DetonatorEntityList(id);
            synchronized (this) {
                if (detonatorEntityList == null) {
                    detonatorEntityList = detonatorEntityListNew;
                }
            }
        }
        return detonatorEntityList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 296310361)
    public synchronized void resetDetonatorEntityList() {
        detonatorEntityList = null;
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
    @Generated(hash = 337068885)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getReportEntityDao() : null;
    }

    @Override
    public String toString() {
        return "ReportEntity{" +
                "id=" + id +
                ", controllerId='" + controllerId + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", blastTime=" + blastTime +
                ", idCode='" + idCode + '\'' +
                ", contractId='" + contractId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", status=" + status +
                ", token='" + token + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", detonatorEntityList=" + detonatorEntityList +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                '}';
    }
}
