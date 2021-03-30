package com.etek.controller.dto;

public class LocationBean {

    double longitude;// 经度
    double latitude;// 纬度
    double radius; // 半径
    public LocationBean(double longitude, double latitude,double rds) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = rds;
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }


    @Override
    public String toString() {
        return "LocationBean{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", radius=" + radius +
                '}';
    }
}
