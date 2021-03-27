package com.etek.controller.enums;



//         1 非法的申请信息
//         2 未找到该起爆器设备信息或起爆器未设置作业任务
//         3 该起爆器未设置作业任务
//         4 起爆器在黑名单中
//         5 起爆位置不在起爆区域内
//         6 起爆位置在禁爆区域内
//         7 该起爆器已注销/报废
//         8 禁爆任务
//         9 作业合同存在项目
//         10 作业任务未设置准爆区域
//         11 离线下载不支持生产厂家试爆
//         12 营业性单位必须设置合同或者项目
//         99	网络连接失败




public enum CheckRuleEnum {
    SUCCESS((byte)0,"完全匹配,允许爆破"),
    OUT_PERMISSION((byte)1,"不在准爆区域"),
    IN_FORBIDDEN((byte)2,"在禁爆区域"),
    TOO_EARLY((byte)3,"未到起爆开始时间"),
    TOO_LATER((byte)4,"超过起爆结束时间"),
    UNREG_DET((byte)5,"存在未注册的雷管"),
    UNUSED_DET((byte)6,"存在未使用的雷管"),
    ERR_DET((byte)7,"存在已使用[%d]和未注册的雷管[%d]"),
    OUT_CONTROLLER((byte)0X0A,"起爆器未注册，不允许起爆 "),
    USED_DET((byte)0X0B,"存在已使用雷管"),
    ;
    byte code;
    String message;


    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    CheckRuleEnum(byte code, String message) {
        this.code = code;
        this.message = message;

    }
    public static CheckRuleEnum getBycode(int code) {
        for (CheckRuleEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getCode() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }




}
