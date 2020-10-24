<<<<<<< HEAD
package com.etek.controller.dto;

import java.util.Date;

public class Jbqy {
    String jbqyjd;  //禁爆区域中心位置经度
    String jbqywd;  //禁爆区域中心位置纬度
    String jbqybj;  //禁爆区域半径
    Date jbqssj;    //禁爆起始时间
    Date jbjzsj;    //禁爆截止时间


    public Jbqy(String jbqyjd, String jbqywd, String jbqybj, Date jbqssj, Date jbjzsj) {
        this.jbqyjd = jbqyjd;
        this.jbqywd = jbqywd;
        this.jbqybj = jbqybj;
        this.jbqssj = jbqssj;
        this.jbjzsj = jbjzsj;
    }

    public Jbqy() {
    }

    public String getJbqyjd() {
        return jbqyjd;
    }

    public void setJbqyjd(String jbqyjd) {
        this.jbqyjd = jbqyjd;
    }

    public String getJbqywd() {
        return jbqywd;
    }

    public void setJbqywd(String jbqywd) {
        this.jbqywd = jbqywd;
    }

    public String getJbqybj() {
        return jbqybj;
    }

    public void setJbqybj(String jbqybj) {
        this.jbqybj = jbqybj;
    }

    public Date getJbqssj() {
        return jbqssj;
    }

    public void setJbqssj(Date jbqssj) {
        this.jbqssj = jbqssj;
    }

    public Date getJbjzsj() {
        return jbjzsj;
    }

    public void setJbjzsj(Date jbjzsj) {
        this.jbjzsj = jbjzsj;
    }

    @Override
    public String toString() {
        return "Jbqy{" +
                "jbqyjd='" + jbqyjd + '\'' +
                ", jbqywd='" + jbqywd + '\'' +
                ", jbqybj='" + jbqybj + '\'' +
                ", jbqssj=" + jbqssj +
                ", jbjzsj=" + jbjzsj +
                '}';
    }
}
=======
package com.etek.controller.dto;

import java.util.Date;

public class Jbqy {
    String jbqyjd;  //禁爆区域中心位置经度
    String jbqywd;  //禁爆区域中心位置纬度
    String jbqybj;  //禁爆区域半径
    Date jbqssj;    //禁爆起始时间
    Date jbjzsj;    //禁爆截止时间


    public Jbqy(String jbqyjd, String jbqywd, String jbqybj, Date jbqssj, Date jbjzsj) {
        this.jbqyjd = jbqyjd;
        this.jbqywd = jbqywd;
        this.jbqybj = jbqybj;
        this.jbqssj = jbqssj;
        this.jbjzsj = jbjzsj;
    }

    public Jbqy() {
    }

    public String getJbqyjd() {
        return jbqyjd;
    }

    public void setJbqyjd(String jbqyjd) {
        this.jbqyjd = jbqyjd;
    }

    public String getJbqywd() {
        return jbqywd;
    }

    public void setJbqywd(String jbqywd) {
        this.jbqywd = jbqywd;
    }

    public String getJbqybj() {
        return jbqybj;
    }

    public void setJbqybj(String jbqybj) {
        this.jbqybj = jbqybj;
    }

    public Date getJbqssj() {
        return jbqssj;
    }

    public void setJbqssj(Date jbqssj) {
        this.jbqssj = jbqssj;
    }

    public Date getJbjzsj() {
        return jbjzsj;
    }

    public void setJbjzsj(Date jbjzsj) {
        this.jbjzsj = jbjzsj;
    }

    @Override
    public String toString() {
        return "Jbqy{" +
                "jbqyjd='" + jbqyjd + '\'' +
                ", jbqywd='" + jbqywd + '\'' +
                ", jbqybj='" + jbqybj + '\'' +
                ", jbqssj=" + jbqssj +
                ", jbjzsj=" + jbjzsj +
                '}';
    }
}
>>>>>>> 806c842... 雷管组网
