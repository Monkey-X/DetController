package com.etek.controller.yunnan.bean;

public class YunUploadResponse {

    String ok;
    String msg;

    public boolean isOk() {
        if(ok.toUpperCase().equals("TRUE"))
            return true;

        return false;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
