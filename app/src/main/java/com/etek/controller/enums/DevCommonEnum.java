package com.etek.controller.enums;

import com.etek.controller.utils.SommerUtils;

public enum DevCommonEnum {
    CONTROLLER((byte)0x52,"CONTROLLER",2),       // 得到控制器序列号和雷管总数
    TOEXP_TOTAL((byte)0x50,"TOEXP_TOTAL",0),    //得到待起爆雷管总数
    DET((byte)0x53,"DET",3),                // 得到雷管
    VERIF((byte)0x58,"VERIF",8),             // 校验值下传
    OVER((byte)0x59,"OVER",9),              //雷管传输结束
    DET_PRO((byte)0x51,"PRO",1),       // 得到雷管项目号
//    BLAST((byte)0x54,"BLAST",1),    //得到时间地点
    LOCATION((byte)0x54,"LOCATION",1),    //得到时间地点
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

    DevCommonEnum(byte code, String name, int index) {
        this.code = code;
        this.name = name;
        this.index = index;
    }
    public static DevCommonEnum getBycode(byte code) {
        for (DevCommonEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getCode() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DevCommonEnum{" +
                "code=" + code +
                "byte=" + SommerUtils.byteToHexString(code) +
                ", name='" + name + '\'' +
                ", index=" + index +
                '}';
    }
}
