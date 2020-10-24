<<<<<<< HEAD
package com.etek.controller.persistence.entity;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class ControllerEntity extends BaseEntity{



    String name;

    private long projectInfoId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public long getProjectInfoId() {
        return this.projectInfoId;
    }

    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }

    public ControllerEntity() {
    }

    @Generated(hash = 1202671465)
    public ControllerEntity(String name, long projectInfoId) {
        this.name = name;
        this.projectInfoId = projectInfoId;
    }


}
=======
package com.etek.controller.persistence.entity;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class ControllerEntity extends BaseEntity{



    String name;

    private long projectInfoId;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public long getProjectInfoId() {
        return this.projectInfoId;
    }

    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }

    public ControllerEntity() {
    }

    @Generated(hash = 1202671465)
    public ControllerEntity(String name, long projectInfoId) {
        this.name = name;
        this.projectInfoId = projectInfoId;
    }


}
>>>>>>> 806c842... 雷管组网
