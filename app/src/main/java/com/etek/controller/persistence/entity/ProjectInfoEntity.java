package com.etek.controller.persistence.entity;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import java.util.List;

import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Transient;


import com.etek.controller.persistence.gen.DaoSession;
import com.etek.controller.persistence.gen.PermissibleZoneEntityDao;
import com.etek.controller.persistence.gen.ControllerEntityDao;
import com.etek.controller.persistence.gen.ForbiddenZoneEntityDao;
import com.etek.controller.persistence.gen.DetonatorEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;


@Entity
public class ProjectInfoEntity{

    @Id(autoincrement = true)
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFileSn() {
        return this.fileSn;
    }

    public void setFileSn(String fileSn) {
        this.fileSn = fileSn;
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

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 575863904)
    public List<DetonatorEntity> getDetonatorList() {
        if (detonatorList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DetonatorEntityDao targetDao = daoSession.getDetonatorEntityDao();
            List<DetonatorEntity> detonatorListNew = targetDao
                    ._queryProjectInfoEntity_DetonatorList(id);
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
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1020891890)
    public List<ForbiddenZoneEntity> getForbiddenZoneList() {
        if (forbiddenZoneList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ForbiddenZoneEntityDao targetDao = daoSession
                    .getForbiddenZoneEntityDao();
            List<ForbiddenZoneEntity> forbiddenZoneListNew = targetDao
                    ._queryProjectInfoEntity_ForbiddenZoneList(id);
            synchronized (this) {
                if (forbiddenZoneList == null) {
                    forbiddenZoneList = forbiddenZoneListNew;
                }
            }
        }
        return forbiddenZoneList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 977078997)
    public synchronized void resetForbiddenZoneList() {
        forbiddenZoneList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 875701686)
    public List<ControllerEntity> getControllerList() {
        if (controllerList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ControllerEntityDao targetDao = daoSession.getControllerEntityDao();
            List<ControllerEntity> controllerListNew = targetDao
                    ._queryProjectInfoEntity_ControllerList(id);
            synchronized (this) {
                if (controllerList == null) {
                    controllerList = controllerListNew;
                }
            }
        }
        return controllerList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1011895069)
    public synchronized void resetControllerList() {
        controllerList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 622717552)
    public List<PermissibleZoneEntity> getPermissibleZoneList() {
        if (permissibleZoneList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PermissibleZoneEntityDao targetDao = daoSession
                    .getPermissibleZoneEntityDao();
            List<PermissibleZoneEntity> permissibleZoneListNew = targetDao
                    ._queryProjectInfoEntity_PermissibleZoneList(id);
            synchronized (this) {
                if (permissibleZoneList == null) {
                    permissibleZoneList = permissibleZoneListNew;
                }
            }
        }
        return permissibleZoneList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 301594637)
    public synchronized void resetPermissibleZoneList() {
        permissibleZoneList = null;
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
    @Generated(hash = 1360761605)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProjectInfoEntityDao() : null;
    }

    private String proCode;           //xmbh;    //项目编号

    private String proName;         //xmmc; //项目名称

    private String companyCode;       //dwdm; //单位代码

    private String companyName;     //dwmc; //单位名称

    private String contractCode;         //htbh; //合同编号

    private String contractName;        //htmc;    //合同名称

    private String fileSn;        //fileSn;    //合同名称

    private Date createTime;


    private Date applyDate;      // 申请日期

    private int status;

    private Boolean isOnline;



    @Transient
    private boolean isSelect;

    @ToMany(referencedJoinProperty= "projectInfoId")
    List<DetonatorEntity> detonatorList;

    @ToMany(referencedJoinProperty= "projectInfoId")
    List<ForbiddenZoneEntity> forbiddenZoneList;

    @ToMany(referencedJoinProperty= "projectInfoId")
    List<ControllerEntity> controllerList;

    @ToMany(referencedJoinProperty= "projectInfoId")
    List<PermissibleZoneEntity> permissibleZoneList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1931631268)
    private transient ProjectInfoEntityDao myDao;

    @Generated(hash = 891869920)
    public ProjectInfoEntity(Long id, String proCode, String proName, String companyCode,
            String companyName, String contractCode, String contractName, String fileSn,
            Date createTime, Date applyDate, int status, Boolean isOnline) {
        this.id = id;
        this.proCode = proCode;
        this.proName = proName;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.contractCode = contractCode;
        this.contractName = contractName;
        this.fileSn = fileSn;
        this.createTime = createTime;
        this.applyDate = applyDate;
        this.status = status;
        this.isOnline = isOnline;
    }

    @Generated(hash = 479712204)
    public ProjectInfoEntity() {
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "ProjectInfoEntity{" +
                "id=" + id +
                ", proCode='" + proCode + '\'' +
                ", proName='" + proName + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", contractCode='" + contractCode + '\'' +
                ", contractName='" + contractName + '\'' +
                ", fileSn='" + fileSn + '\'' +
                ", createTime=" + createTime +
                ", applyDate=" + applyDate +
                ", status=" + status +
                ", isSelect=" + isSelect +
                ", detonatorList=" + detonatorList +
                ", forbiddenZoneList=" + forbiddenZoneList +
                ", controllerList=" + controllerList +
                ", permissibleZoneList=" + permissibleZoneList +
                '}';
    }

    public Boolean getIsOnline() {
        return this.isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }
}
