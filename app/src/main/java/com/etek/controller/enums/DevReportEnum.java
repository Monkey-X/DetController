package com.etek.controller.enums;

public enum DevReportEnum {
    DET_TOTAL((byte)0x51,"QUERY",0),       // 得到雷管总数
    TOEXP_TOTAL((byte)0x54,"BLAST",1),    //得到待起爆雷管总数
    DET((byte)0x53,"DET",2),                // 得到雷管
    VERIF((byte)0x58,"VERIF",3),             // 校验值下传
    OVER((byte)0x59,"OVER",4),              //雷管传输结束
    ;
    byte code;
    String name;
    int index;

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    DevReportEnum(byte code, String name, int index) {
        this.code = code;
        this.name = name;
        this.index = index;
    }
    public static DevReportEnum getBycode(byte code) {
        for (DevReportEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getCode() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }




}
