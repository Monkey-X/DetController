package com.etek.controller.persistence.entity;



import org.greenrobot.greendao.annotation.Id;

public class BaseEntity {

    @Id(autoincrement = true)
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
