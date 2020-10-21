package com.etek.controller.enums;

public enum SendCmdEnum {
    INIT((byte)0x30,"INIT",0),
    TOTAL((byte)0x31,"TOTAL",1),
    DATA((byte)0x33,"DATA",2),
    DATAOne((byte)0x34,"DATA",2),
    DATATwo((byte)0x33,"DATA2",3),
    END((byte)0x35,"END",3),
    ERROR((byte)0x3F,"ERROR",4),
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

    SendCmdEnum(byte code, String name, int index) {
        this.code = code;
        this.name = name;
        this.index = index;
    }
    public static SendCmdEnum getBycode(byte code) {
        for (SendCmdEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getCode() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }




}
