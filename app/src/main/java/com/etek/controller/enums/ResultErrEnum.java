package com.etek.controller.enums;

public enum ResultErrEnum {
    SUCCESS(0,"成功"),
    ILLEGAL_INFO(1,"非法的申请信息"),
    WITHOUT_CONTROLLER_INFO(2,"未找到该起爆器设备信息或起爆器未设置作业任务"),
    WITHOUT_WORK(3,"起爆器未设置作业任务"),
    BLACKLIST(4,"起爆器在黑名单中"),
    OUTOF_EXPLOSIVE_AREA(5,"起爆位置不在起爆区域内"),
    FORBIDDEN_AREA(6,"起爆位置在禁爆区域内"),
    CONTROLLER_DISABLE(7,"该起爆器已注销/报废"),
    FORBIDDEN_MISSION(8,"禁爆任务"),
    EXIST_PROJECT(9,"作业合同存在项目"),
     EXPLOSIVE_AREA_NOTSET(10,"作业任务未设置准爆区域"),
   OFFLINE_DOWNLOAD_NOTSUPPORT(11,"离线下载不支持生产厂家试爆"),
    MUST_SET_CONTRACT(12,"营业性单位必须设置合同或者项目"),
    NET_FAILED(99,"网络连接失败"),

    ;
    int code;
    String message;


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

    ResultErrEnum(int code, String message) {
        this.code = code;
        this.message = message;

    }
    public static ResultErrEnum getBycode(int code) {
        for (ResultErrEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getCode() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }




}
