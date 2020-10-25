package com.etek.controller.dto;

public class ServerResult {
    String success;
    String cwxx;
    String cwxxms;
    public ServerResult() {
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

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
        return "ServerResult{" +
                "success='" + success + '\'' +
                ", cwxx='" + cwxx + '\'' +
                ", cwxxms='" + cwxxms + '\'' +
                '}';
    }
}
