package com.etek.controller.yunnan.bean;

public class YunnanResponse {

    private int code;
    private String message;
    private long timestamp;
    private OfflineAuthBombBean result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public OfflineAuthBombBean getResult() {
        return result;
    }

    public void setResult(OfflineAuthBombBean result) {
        this.result = result;
    }
}
