package com.etek.controller.utils;

import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.entity.DetonatorEntity;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Locale;

public class DetUtil {

    public static String getAcCodeFromDet(DetonatorEntity detonator) {
        byte[] d2 = new byte[7];
        byte[] uidBytes = SommerUtils.hexStringToBytes(detonator.getUid());

        byte[] acCode = new byte[4];
        d2[0] = uidBytes[0];
        d2[1] = uidBytes[1];
        d2[2] = uidBytes[2];
        d2[3] = uidBytes[6];
        d2[4] = uidBytes[5];
        d2[5] = uidBytes[4];
        d2[6] = uidBytes[3];
//
        byte[] crcs = CRCUtil.CRC16(d2);
        byte[] extId = getExtid(detonator.getCode());
        byte[] crcs2 = CRCUtil.CRC16(extId);
//            System.out.println("crc:"+SommerUtils.bytesToHexString(crcs2));
        acCode[0] = crcs[0];
        acCode[1] = crcs[1];
        acCode[2] = crcs2[0];
        acCode[3] = crcs2[1];
        return SommerUtils.bytesToHexString(acCode);
    }

    public static byte[] getExtid(String code) {
//        code = "6100309D34263";
//        String eID = "3d0003094456013f";
//        byte[] beid = SommerUtils.hexStringToBytes(eID);
        byte[] extid = new byte[8];

        extid[0] = (byte) Integer.parseInt(code.substring(0, 2));

        extid[1] = (byte) Integer.parseInt(code.substring(2, 3));

        extid[2] = (byte) Integer.parseInt(code.substring(3, 5));
        extid[3] = (byte) Integer.parseInt(code.substring(5, 7));
        extid[4] = code.substring(7, 8).getBytes()[0];
        String numStr = code.substring(8, 11);
        int d1 = Integer.parseInt(numStr);
        extid[5] = (byte) d1;
        extid[6] = (byte) (d1 >> 8);
        extid[7] = (byte) Integer.parseInt(code.substring(11, 13));
//        System.out.println(d1);
//        System.out.println(SommerUtils.bytesToHexString(extid));
        return extid;
    }

    public static boolean isValidFbh(String fbh) {
        if (fbh.length() != 13) {
            return false;
        }
        int cCodeType = 0;
        String companyCode = fbh.substring(0, 2);

        int cCode = Integer.parseInt(companyCode);
        if (cCode == Detonator.RanYiCode) {
            cCodeType = 0;
        } else if (cCode == Detonator.QianJingCode) {
            cCodeType = 1;
        } else if (cCode == Detonator.QingHuaCode) {
            cCodeType = 2;
        } else if (cCode == Detonator.LeiMingCode) {
            cCodeType = 3;
        } else if (cCode == Detonator.JiuLianCode) {
            cCodeType = 4;
        } else {
            return false;
        }
        String numfirst = fbh.substring(0, 7);
        if (!StringUtils.isNumeric(numfirst)) {
            return false;
        }
        String numsecond = fbh.substring(8, 13);
        if (!StringUtils.isNumeric(numsecond)) {
            return false;
        }
        return true;
    }
}
