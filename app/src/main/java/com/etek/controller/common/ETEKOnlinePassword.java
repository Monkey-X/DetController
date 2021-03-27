package com.etek.controller.common;

//{"code":0,"message":"成功","timestamp":1616723876701,"result":"752597"}
public class ETEKOnlinePassword {
    private String code;
    private String message;
    private long timestamp;
    private String result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ETEKOnlinePassword{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", result='" + result + '\'' +
                '}';
    }
}
