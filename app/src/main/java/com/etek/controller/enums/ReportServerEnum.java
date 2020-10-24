<<<<<<< HEAD
package com.etek.controller.enums;

public enum ReportServerEnum {
    ZB_QIANNAN(0,"中爆黔南","113.140.1.135",9903),       // 中爆黔南
    ZB_QIANDONGNAN(1,"中爆黔东南","113.140.1.137",8608),       // 中爆黔东南
    ZB_GUANGXI(2,"中爆广西","119.29.111.172",6088),       // 中爆广西
    ZB_GUIYANG(3,"中爆贵阳","119.29.111.172",6089),       // 中爆贵阳
    ETEK(0,"ETEK","222.191.229.234",1089),       // ETEK
    DL_OFFICIAL(0,"丹灵正式"," qq.mbdzlg.com",80),       // 丹灵正式
    DL_TEST(0,"丹灵测试","139.129.216.133",8080),       // 丹灵测试
    ;
    int code;
    String name;
    String address;
    int port;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    ReportServerEnum(int code, String name, String address, int port) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public static ReportServerEnum getBycode(int code) {
        for (ReportServerEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getCode() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }

    public static ReportServerEnum getByName(String name) {
        for (ReportServerEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getName().equalsIgnoreCase(name)) {
                return sendCmdEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ReportServerEnum{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
=======
package com.etek.controller.enums;

public enum ReportServerEnum {
    ZB_QIANNAN(0,"中爆黔南","113.140.1.135",9903),       // 中爆黔南
    ZB_QIANDONGNAN(1,"中爆黔东南","113.140.1.137",8608),       // 中爆黔东南
    ZB_GUANGXI(2,"中爆广西","119.29.111.172",6088),       // 中爆广西
    ZB_GUIYANG(3,"中爆贵阳","119.29.111.172",6089),       // 中爆贵阳
    ETEK(0,"ETEK","222.191.229.234",1089),       // ETEK
    DL_OFFICIAL(0,"丹灵正式"," qq.mbdzlg.com",80),       // 丹灵正式
    DL_TEST(0,"丹灵测试","139.129.216.133",8080),       // 丹灵测试
    ;
    int code;
    String name;
    String address;
    int port;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    ReportServerEnum(int code, String name, String address, int port) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public static ReportServerEnum getBycode(int code) {
        for (ReportServerEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getCode() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }

    public static ReportServerEnum getByName(String name) {
        for (ReportServerEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getName().equalsIgnoreCase(name)) {
                return sendCmdEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ReportServerEnum{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
>>>>>>> 806c842... 雷管组网
