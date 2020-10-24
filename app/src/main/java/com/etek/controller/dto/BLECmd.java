<<<<<<< HEAD
package com.etek.controller.dto;

import com.etek.controller.common.Globals;
import com.etek.controller.enums.DevCommonEnum;
import com.etek.controller.enums.SendCmdEnum;
import com.etek.controller.utils.CRCUtil;
import com.etek.controller.utils.SommerUtils;

import org.apache.commons.lang3.StringUtils;

public class BLECmd {
    byte cmd;
    byte[] data;
    byte crc8;
    byte[] code;

    public static BLECmd getUpdateStart() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.INIT.getCode();
        bleCmd.data = new byte[1];
        bleCmd.data[0] = 0x00;
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getEndCmd() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.END.getCode();
        bleCmd.data = new byte[1];
        bleCmd.data[0] = 0x00;
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getCmdTotal(int num) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.TOTAL.getCode();
        bleCmd.data = new byte[4];

        bleCmd.data[0] = (byte) num;
        bleCmd.data[1] = (byte)(num>>8);
        if(Globals.type==1){
            bleCmd.data[2] = (byte) 0x80;
        }else {
            bleCmd.data[2] = (byte) 0x10;
        }

        bleCmd.data[3] = (byte) 0x00;
        bleCmd.pushCmdToCode();

        return bleCmd;

    }

    public static BLECmd getCmdWriteByte(int num, byte[] data) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.DATAOne.getCode();
        bleCmd.data = new byte[18];
        bleCmd.data[0] = (byte) num;
        bleCmd.data[1] = (byte) (num >> 8);
        System.arraycopy(data, 0, bleCmd.data, 2, 16);
        bleCmd.pushCmdToCode();
//        code[1] = (byte) num;
//        code[2] = (byte) (num >> 8);
//        int sum = SommerUtils.ChkSum16Generate(code, code.length - 2);
//        code[3] = (byte) (sum >> 8);
//        code[4] = (byte) sum;
        return bleCmd;

    }

    public static BLECmd getLongWriteByte(int num, byte[] data) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.DATATwo.getCode();
        bleCmd.data = new byte[130];
        bleCmd.data[0] = (byte) num;
        bleCmd.data[1] = (byte) (num >> 8);
        System.arraycopy(data, 0, bleCmd.data, 2, 128);
        bleCmd.pushCmdToCode();
//        code[1] = (byte) num;
//        code[2] = (byte) (num >> 8);
//        int sum = SommerUtils.ChkSum16Generate(code, code.length - 2);
//        code[3] = (byte) (sum >> 8);
//        code[4] = (byte) sum;
        return bleCmd;

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

    public BLECmd(byte cmd, byte[] data, byte crc8, byte[] code) {
        this.cmd = cmd;
        this.data = data;
        this.crc8 = crc8;
        this.code = code;
    }

    public BLECmd() {
    }

    public static BLECmd getController() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.CONTROLLER.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'Q';
        bleCmd.data[1] = 'D';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getProId() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.DET_PRO.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'Q';
        bleCmd.data[1] = 'P';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getBlast() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.LOCATION.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'Q';
        bleCmd.data[1] = 'B';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getVerify(byte result, int unReg, int unUse) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.VERIF.getCode();
        bleCmd.data = new byte[5];
        bleCmd.data[0] = result;
        bleCmd.data[1] = (byte) unReg;
        bleCmd.data[2] = (byte) (unReg >> 8);
        bleCmd.data[3] = (byte) unUse;
        bleCmd.data[4] = (byte) (unUse >> 8);
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public void pushCmdToCode() {
        if (data != null && data.length > 0) {
            code = new byte[data.length + 2];
            code[0] = cmd;
            System.arraycopy(data, 0, code, 1, data.length);
            crc8 = CRCUtil.calcCrc8(code, 0, code.length - 1);
            code[code.length - 1] = crc8;
        }
    }

    public static BLECmd getOverCmd() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.OVER.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'O';
        bleCmd.data[1] = 'V';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getLocationCmd() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.LOCATION.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'Q';
        bleCmd.data[1] = 'B';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    /**
     * 得到要起爆雷管总数信息
     *
     * @return
     */
    public static BLECmd getBlastTotalCmd(String xmbh) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.TOEXP_TOTAL.getCode();
//        byte[] bytes = SommerUtils.hexStringToBytes(xmbh);
        bleCmd.data = new byte[16];

        System.arraycopy(xmbh.getBytes(), 0, bleCmd.data, 0, 16);

//        bleCmd.data[15] = 0x00;
        bleCmd.pushCmdToCode();

        return bleCmd;

    }

    /**
     * 得到要起爆雷管总数信息
     *
     * @return
     */
    public static BLECmd getLocationInfo(String xmbh) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.LOCATION.getCode();
//        bleCmd.data = new byte[16];
//        System.arraycopy(xmbh.getBytes(),0,bleCmd.data,0,15);
//        bleCmd.data[15]= 0x00;
        bleCmd.pushCmdToCode();

        return bleCmd;

    }

    public static BLECmd getDetCmd(int num) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.DET.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = (byte) num;
        bleCmd.data[1] = (byte) (num >> 8);
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public void setCodeByData() {
        System.arraycopy(data, 0, code, 1, data.length);
    }

    public byte getCodeCrc() {
        crc8 = CRCUtil.calcCrc8(code, 0, code.length - 1);
        code[code.length - 1] = crc8;
        return crc8;
    }

    @Override
    public String toString() {
        return "BLECmd{" +
                "cmd=" + DevCommonEnum.getBycode(cmd) + "" +
                ", data=" + SommerUtils.bytesToHexString(data) +
                ", crc8=" + crc8 +
                ", code=" + SommerUtils.bytesToHexString(code) +
                '}';
    }
}
=======
package com.etek.controller.dto;

import com.etek.controller.common.Globals;
import com.etek.controller.enums.DevCommonEnum;
import com.etek.controller.enums.SendCmdEnum;
import com.etek.controller.utils.CRCUtil;
import com.etek.controller.utils.SommerUtils;

import org.apache.commons.lang3.StringUtils;

public class BLECmd {
    byte cmd;
    byte[] data;
    byte crc8;
    byte[] code;

    public static BLECmd getUpdateStart() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.INIT.getCode();
        bleCmd.data = new byte[1];
        bleCmd.data[0] = 0x00;
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getEndCmd() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.END.getCode();
        bleCmd.data = new byte[1];
        bleCmd.data[0] = 0x00;
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getCmdTotal(int num) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.TOTAL.getCode();
        bleCmd.data = new byte[4];

        bleCmd.data[0] = (byte) num;
        bleCmd.data[1] = (byte)(num>>8);
        if(Globals.type==1){
            bleCmd.data[2] = (byte) 0x80;
        }else {
            bleCmd.data[2] = (byte) 0x10;
        }

        bleCmd.data[3] = (byte) 0x00;
        bleCmd.pushCmdToCode();

        return bleCmd;

    }

    public static BLECmd getCmdWriteByte(int num, byte[] data) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.DATAOne.getCode();
        bleCmd.data = new byte[18];
        bleCmd.data[0] = (byte) num;
        bleCmd.data[1] = (byte) (num >> 8);
        System.arraycopy(data, 0, bleCmd.data, 2, 16);
        bleCmd.pushCmdToCode();
//        code[1] = (byte) num;
//        code[2] = (byte) (num >> 8);
//        int sum = SommerUtils.ChkSum16Generate(code, code.length - 2);
//        code[3] = (byte) (sum >> 8);
//        code[4] = (byte) sum;
        return bleCmd;

    }

    public static BLECmd getLongWriteByte(int num, byte[] data) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = SendCmdEnum.DATATwo.getCode();
        bleCmd.data = new byte[130];
        bleCmd.data[0] = (byte) num;
        bleCmd.data[1] = (byte) (num >> 8);
        System.arraycopy(data, 0, bleCmd.data, 2, 128);
        bleCmd.pushCmdToCode();
//        code[1] = (byte) num;
//        code[2] = (byte) (num >> 8);
//        int sum = SommerUtils.ChkSum16Generate(code, code.length - 2);
//        code[3] = (byte) (sum >> 8);
//        code[4] = (byte) sum;
        return bleCmd;

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

    public BLECmd(byte cmd, byte[] data, byte crc8, byte[] code) {
        this.cmd = cmd;
        this.data = data;
        this.crc8 = crc8;
        this.code = code;
    }

    public BLECmd() {
    }

    public static BLECmd getController() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.CONTROLLER.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'Q';
        bleCmd.data[1] = 'D';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getProId() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.DET_PRO.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'Q';
        bleCmd.data[1] = 'P';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getBlast() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.LOCATION.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'Q';
        bleCmd.data[1] = 'B';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getVerify(byte result, int unReg, int unUse) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.VERIF.getCode();
        bleCmd.data = new byte[5];
        bleCmd.data[0] = result;
        bleCmd.data[1] = (byte) unReg;
        bleCmd.data[2] = (byte) (unReg >> 8);
        bleCmd.data[3] = (byte) unUse;
        bleCmd.data[4] = (byte) (unUse >> 8);
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public void pushCmdToCode() {
        if (data != null && data.length > 0) {
            code = new byte[data.length + 2];
            code[0] = cmd;
            System.arraycopy(data, 0, code, 1, data.length);
            crc8 = CRCUtil.calcCrc8(code, 0, code.length - 1);
            code[code.length - 1] = crc8;
        }
    }

    public static BLECmd getOverCmd() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.OVER.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'O';
        bleCmd.data[1] = 'V';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public static BLECmd getLocationCmd() {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.LOCATION.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = 'Q';
        bleCmd.data[1] = 'B';
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    /**
     * 得到要起爆雷管总数信息
     *
     * @return
     */
    public static BLECmd getBlastTotalCmd(String xmbh) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.TOEXP_TOTAL.getCode();
//        byte[] bytes = SommerUtils.hexStringToBytes(xmbh);
        bleCmd.data = new byte[16];

        System.arraycopy(xmbh.getBytes(), 0, bleCmd.data, 0, 16);

//        bleCmd.data[15] = 0x00;
        bleCmd.pushCmdToCode();

        return bleCmd;

    }

    /**
     * 得到要起爆雷管总数信息
     *
     * @return
     */
    public static BLECmd getLocationInfo(String xmbh) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.LOCATION.getCode();
//        bleCmd.data = new byte[16];
//        System.arraycopy(xmbh.getBytes(),0,bleCmd.data,0,15);
//        bleCmd.data[15]= 0x00;
        bleCmd.pushCmdToCode();

        return bleCmd;

    }

    public static BLECmd getDetCmd(int num) {
        BLECmd bleCmd = new BLECmd();
        bleCmd.cmd = DevCommonEnum.DET.getCode();
        bleCmd.data = new byte[2];
        bleCmd.data[0] = (byte) num;
        bleCmd.data[1] = (byte) (num >> 8);
        bleCmd.pushCmdToCode();
        return bleCmd;

    }

    public void setCodeByData() {
        System.arraycopy(data, 0, code, 1, data.length);
    }

    public byte getCodeCrc() {
        crc8 = CRCUtil.calcCrc8(code, 0, code.length - 1);
        code[code.length - 1] = crc8;
        return crc8;
    }

    @Override
    public String toString() {
        return "BLECmd{" +
                "cmd=" + DevCommonEnum.getBycode(cmd) + "" +
                ", data=" + SommerUtils.bytesToHexString(data) +
                ", crc8=" + crc8 +
                ", code=" + SommerUtils.bytesToHexString(code) +
                '}';
    }
}
>>>>>>> 806c842... 雷管组网
