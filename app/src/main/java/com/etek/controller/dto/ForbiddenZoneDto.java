<<<<<<< HEAD
package com.etek.controller.dto;



import java.util.Date;


public class ForbiddenZoneDto {



    private  int radius ; //  String jbqybj;  //禁爆区域半径

    private  double longitude ; //  String jbqyjd;  //禁爆区域中心位置经度

    private  double latitude ; // String jbqywd;  //禁爆区域中心位置纬度

    private  Date startTime ; //    Date jbqssj;    //禁爆起始时间

    private Date stopTime ; //  Date jbjzsj;    //禁爆截止时间



    public ForbiddenZoneDto(int radius, double longitude,
            double latitude, Date startTime, Date stopTime) {

        this.radius = radius;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public ForbiddenZoneDto() {
    }




    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
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

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }


}
=======
package com.etek.controller.dto;



import java.util.Date;


public class ForbiddenZoneDto {



    private  int radius ; //  String jbqybj;  //禁爆区域半径

    private  double longitude ; //  String jbqyjd;  //禁爆区域中心位置经度

    private  double latitude ; // String jbqywd;  //禁爆区域中心位置纬度

    private  Date startTime ; //    Date jbqssj;    //禁爆起始时间

    private Date stopTime ; //  Date jbjzsj;    //禁爆截止时间



    public ForbiddenZoneDto(int radius, double longitude,
            double latitude, Date startTime, Date stopTime) {

        this.radius = radius;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public ForbiddenZoneDto() {
    }




    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
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

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }


}
>>>>>>> 806c842... 雷管组网
