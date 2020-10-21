package com.etek.controller.entity;

import com.etek.controller.utils.SommerUtils;

public class HardwareInfo {
    String sn;
    int HardwareVersion;
    int BlueToothVersion;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getHardwareVersion() {
        return HardwareVersion;
    }

    public void setHardwareVersion(int hardwareVersion) {
        HardwareVersion = hardwareVersion;
    }

    public int getBlueToothVersion() {
        return BlueToothVersion;
    }

    public void setBlueToothVersion(int blueToothVersion) {
        BlueToothVersion = blueToothVersion;
    }

    public HardwareInfo(byte[] code) {
        byte[] snByte = new byte[5];
        System.arraycopy(code,1,snByte,0,5);
        this.sn = SommerUtils.bytesToHexString(snByte);
        HardwareVersion = (int)(code[6]<<8)+code[7];
        BlueToothVersion = (int)(code[8]<<8)+code[9];;
    }

    @Override
    public String toString() {
        return "HardwareInfo{" +
                "sn='" + sn + '\'' +
                ", HardwareVersion=" + HardwareVersion +
                ", BlueToothVersion=" + BlueToothVersion +
                '}';
    }
}
