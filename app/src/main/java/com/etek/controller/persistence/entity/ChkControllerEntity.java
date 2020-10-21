package com.etek.controller.persistence.entity;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;


import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.ToOne;

import com.etek.controller.persistence.gen.DaoSession;
import com.etek.controller.persistence.gen.ChkDetonatorEntityDao;
import com.etek.controller.persistence.gen.ChkControllerEntityDao;
import com.etek.controller.persistence.gen.ProjectInfoEntityDao;
import org.greenrobot.greendao.annotation.NotNull;


@Entity
public class ChkControllerEntity{


    @Id(autoincrement = true)
    Long id;

    private long projectInfoId;

    String name;

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

    private Integer isOnline;


    @ToOne(joinProperty = "projectInfoId")
    ProjectInfoEntity projectInfoEntity;


    @ToMany(referencedJoinProperty= "chkId")
    List<ChkDetonatorEntity> chkDetonatorList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2020531068)
    private transient ChkControllerEntityDao myDao;


    @Generated(hash = 328966683)
    public ChkControllerEntity(Long id, long projectInfoId, String name,
            String company, String sn, int detCount, String contractId,
            double latitude, double longitude, Date blastTime, int type,
            String projectId, int status, String userIDCode, String token,
            boolean isValid, Integer isOnline) {
        this.id = id;
        this.projectInfoId = projectInfoId;
        this.name = name;
        this.company = company;
        this.sn = sn;
        this.detCount = detCount;
        this.contractId = contractId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.blastTime = blastTime;
        this.type = type;
        this.projectId = projectId;
        this.status = status;
        this.userIDCode = userIDCode;
        this.token = token;
        this.isValid = isValid;
        this.isOnline = isOnline;
    }


    @Generated(hash = 1232964725)
    public ChkControllerEntity() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public long getProjectInfoId() {
        return this.projectInfoId;
    }


    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getCompany() {
        return this.company;
    }


    public void setCompany(String company) {
        this.company = company;
    }


    public String getSn() {
        return this.sn;
    }


    public void setSn(String sn) {
        this.sn = sn;
    }


    public int getDetCount() {
        return this.detCount;
    }


    public void setDetCount(int detCount) {
        this.detCount = detCount;
    }


    public String getContractId() {
        return this.contractId;
    }


    public void setContractId(String contractId) {
        this.contractId = contractId;
    }


    public double getLatitude() {
        return this.latitude;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return this.longitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public Date getBlastTime() {
        return this.blastTime;
    }


    public void setBlastTime(Date blastTime) {
        this.blastTime = blastTime;
    }


    public int getType() {
        return this.type;
    }


    public void setType(int type) {
        this.type = type;
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


    public String getUserIDCode() {
        return this.userIDCode;
    }


    public void setUserIDCode(String userIDCode) {
        this.userIDCode = userIDCode;
    }


    public String getToken() {
        return this.token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public boolean getIsValid() {
        return this.isValid;
    }


    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }


    public Integer getIsOnline() {
        return this.isOnline;
    }


    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }


    @Generated(hash = 455676698)
    private transient Long projectInfoEntity__resolvedKey;


    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1343056332)
    public ProjectInfoEntity getProjectInfoEntity() {
        long __key = this.projectInfoId;
        if (projectInfoEntity__resolvedKey == null
                || !projectInfoEntity__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProjectInfoEntityDao targetDao = daoSession.getProjectInfoEntityDao();
            ProjectInfoEntity projectInfoEntityNew = targetDao.load(__key);
            synchronized (this) {
                projectInfoEntity = projectInfoEntityNew;
                projectInfoEntity__resolvedKey = __key;
            }
        }
        return projectInfoEntity;
    }


    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1998519546)
    public void setProjectInfoEntity(@NotNull ProjectInfoEntity projectInfoEntity) {
        if (projectInfoEntity == null) {
            throw new DaoException(
                    "To-one property 'projectInfoId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.projectInfoEntity = projectInfoEntity;
            projectInfoId = projectInfoEntity.getId();
            projectInfoEntity__resolvedKey = projectInfoId;
        }
    }


    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 19696400)
    public List<ChkDetonatorEntity> getChkDetonatorList() {
        if (chkDetonatorList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChkDetonatorEntityDao targetDao = daoSession.getChkDetonatorEntityDao();
            List<ChkDetonatorEntity> chkDetonatorListNew = targetDao
                    ._queryChkControllerEntity_ChkDetonatorList(id);
            synchronized (this) {
                if (chkDetonatorList == null) {
                    chkDetonatorList = chkDetonatorListNew;
                }
            }
        }
        return chkDetonatorList;
    }


    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1965754572)
    public synchronized void resetChkDetonatorList() {
        chkDetonatorList = null;
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
    @Generated(hash = 2116297980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChkControllerEntityDao() : null;
    }


}
