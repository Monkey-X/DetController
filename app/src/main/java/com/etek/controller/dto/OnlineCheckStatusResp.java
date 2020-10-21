package com.etek.controller.dto;



public class OnlineCheckStatusResp {

    private String cwxx;

    String cwxxms;

    public String getCwxx() {
        return cwxx;
    }

    public void setCwxx(String cwxx) {
        this.cwxx = cwxx;
    }

    public String getCwxxms() {
        return cwxxms;
    }

    public void setCwxxms(String cwxxms) {
        this.cwxxms = cwxxms;
    }

    @Override
    public String toString() {
        return "OnlineCheckStatusResp{" +
                "cwxx='" + cwxx + '\'' +
                ", cwxxms='" + cwxxms + '\'' +
                '}';
    }
}