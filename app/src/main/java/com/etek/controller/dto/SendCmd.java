package com.etek.controller.dto;

import com.etek.controller.enums.SendCmdEnum;
import com.etek.controller.utils.CRCUtil;
import com.etek.controller.utils.SommerUtils;

import java.util.Arrays;


public class SendCmd {

    byte cmd;

    byte[] data;

    byte crc;

    byte[] code;

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

    public byte getCrc() {
        return crc;
    }

    public void setCrc(byte crc) {
        this.crc = crc;
    }

    public byte[] getCmdInit() {

        this.cmd = SendCmdEnum.INIT.getCode();


        code = new byte[4];
        code[0] = cmd;
        code[1] = 0x00;
        int sum = SommerUtils.ChkSum16Generate(code, code.length - 2);
        code[2] = (byte) (sum >> 8);
        code[3] = (byte) sum;
        return code;

    }

    public byte[] getCmdTotal(int num) {

        this.cmd = SendCmdEnum.TOTAL.getCode();
//        this.data = new byte[2];
//        data[0] =         (byte)num;
//        data[1] =         (byte)(num>>8);
        code = new byte[5];
        code[0] = cmd;
        code[1] = (byte) num;
        code[2] = (byte) (num >> 8);
        int sum = SommerUtils.ChkSum16Generate(code, code.length - 2);
        code[3] = (byte) (sum >> 8);
        code[4] = (byte) sum;
        return code;

    }

    public byte[] getCmdData(int num, byte[] soData) {

        this.cmd = SendCmdEnum.DATA.getCode();
//        this.data = new byte[1026];
//        data[0] =         (byte)num;
//        data[1] =         (byte)(num>>8);
//        System.arraycopy(soData,0,data,2,soData.length);
//        int sum =  SommerUtils.ChkSum16Generate(data,data.length);
        code = new byte[1029];
        code[0] = cmd;
        code[1] = (byte) num;
        code[2] = (byte) (num >> 8);
        System.arraycopy(soData, 0, code, 3, 1024);
        int sum = SommerUtils.ChkSum16Generate(code, code.length - 2);
        code[1027] = (byte) (sum >> 8);
        code[1028] = (byte) sum;
        return code;

    }

    public byte[] getCmdBlockData(byte[] soData) {


        this.data = new byte[20];
        System.arraycopy(soData, 0, data, 0, 20);
        code = new byte[20];
        code[0] = cmd;
        System.arraycopy(data, 0, code, 1, 18);
        code[19] = CRCUtil.calcCrc8(code);
        return code;

    }


    public byte[] getCmdEnd() {

        this.cmd = SendCmdEnum.END.getCode();
//        this.data = new byte[1];
//        data[0] = 0x00;
        code = new byte[4];
        code[0] = cmd;
        code[1] = 0x00;
        int sum = SommerUtils.ChkSum16Generate(code, code.length - 2);
        code[2] = (byte) (sum >> 8);
        code[3] = (byte) sum;
        return code;


    }

    @Override
    public String toString() {
        return "SendCmd{" +
                "cmd=" + cmd +
                ", data=" +SommerUtils.bytesToHexString(data) +
                ", crc=" + crc +
                ", code=" + SommerUtils.bytesToHexString(code) +
                '}';
    }
}
