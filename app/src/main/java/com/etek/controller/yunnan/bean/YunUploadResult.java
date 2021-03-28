package com.etek.controller.yunnan.bean;


// {"code":40000,"message":"查询成功","timestamp":1616937622467,"result":{"ok":"true","msg":"上传成功！若起爆文件上传完毕，请手动将此条作业登记变更为已完成！"}}
public class YunUploadResult {
    private String code;
    private String message;
    private long timestamp;
    private YunUploadResponse result;

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

    public YunUploadResponse getResult() {
        return result;
    }

    public void setResult(YunUploadResponse result) {
        this.result = result;
    }
}
