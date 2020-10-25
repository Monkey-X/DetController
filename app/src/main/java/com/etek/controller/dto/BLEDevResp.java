package com.etek.controller.dto;

import com.etek.controller.utils.CRCUtil;
import com.etek.controller.utils.SommerUtils;

public class BLEDevResp {
    byte cmd;
    byte[] data;
    byte crc8;
    byte[] code;

    boolean isValid;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public byte[] getCode() {
        return code;
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getCrc8() {
        return crc8;
    }

    public void setCrc8(byte crc8) {
        this.crc8 = crc8;
    }

    public BLEDevResp(byte cmd, byte[] data, byte crc8, byte[] code) {
        this.cmd = cmd;
        this.data = data;
        this.crc8 = crc8;
        this.code = code;
    }

    public BLEDevResp() {
    }

    public BLEDevResp(byte[] code) {
        cmd = code[0];
        this.code = code;
        data = new byte[code.length-2];
        crc8 = code[code.length-1];
        System.arraycopy(code,1,data,0,code.length-2);
        byte crc8temp = CRCUtil.calcCrc8(code, 0,code.length - 1);
        if(crc8==crc8temp){
            isValid = true;
        }else {
            isValid = false;
        }
    }

    public int getCountByData() {
        if (data == null)
            return 0;

        int len = data.length;
        if (len < 8) {
            return 0;
        }
        int count = data[len - 2] + data[len - 1];
        return count;
    }




    public void setCodeByData(){
        System.arraycopy(data,0, code,1,data.length);
    }

    public byte getCodeCrc(){
        crc8 = CRCUtil.calcCrc8(code, 0,code.length - 1);
        code[code.length-1] = crc8;
        return crc8;
    }

    @Override
    public String toString() {
        return "BLECmd{" +
                "cmd=" + cmd +
                ", data=" + SommerUtils.bytesToHexString(data) +
                ", crc8=" + crc8 +
                ", code=" + SommerUtils.bytesToHexString(code) +
                '}';
    }
}
