package com.etek.controller.entity;


import com.etek.controller.enums.DetStatusEnum;
import com.etek.controller.persistence.entity.ChkDetonatorEntity;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.RptDetonatorEntity;
import com.etek.controller.utils.CRCUtil;
import com.etek.controller.utils.SommerUtils;


import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Detonator implements Serializable {

    final public static int RanYiCode = 61;
    final public static int QianJingCode = 38;
    final public static int QingHuaCode = 64;
    final public static int LeiMingCode = 28;
    final public static int JiuLianCode = 60;
    public final static Map<String, Integer> RanYiCodeMap = new HashMap<String, Integer>() {{
        put("D", 0);
        put("2", 1);
        put("3", 2);
        put("4", 3);
        put("5", 4);
        put("6", 5);
        put("7", 6);
        put("8", 7);
        put("9", 8);

    }};

    public final static Map<String, Integer> QianJingCodeMap = new HashMap<String, Integer>() {{
        put("6", 0);
        put("1", 1);
        put("2", 2);
        put("3", 3);
        put("4", 4);
        put("5", 5);
        put("7", 6);
        put("8", 7);
        put("9", 8);

    }};

    public final static Map<String, Integer> QingHuaCodeMap = new HashMap<String, Integer>() {{
        put("A", 0);
        put("R", 1);
        put("P", 2);
        put("N", 3);
        put("T", 4);
        put("H", 5);
        put("K", 6);
        put("S", 7);
        put("1", 8);

    }};

    public final static Map<String, Integer> LeiMingCodeMap = new HashMap<String, Integer>() {{
        put("B", 0);
        put("C", 1);
        put("D", 2);
        put("E", 3);
        put("F", 4);
        put("G", 5);
        put("H", 6);
        put("I", 7);
        put("J", 8);

    }};

    public final static Map<String, Integer> JiuLianCodeMap = new HashMap<String, Integer>() {{
        put("5", 0);
        put("4", 1);
        put("6", 2);
        put("7", 3);
        put("8", 4);
        put("9", 5);
        put("a", 6);
        put("b", 7);
        put("c", 8);

    }};

    private int num; // 指数
    private byte[] ids;          //芯片内部ID
    private String chipID;      //芯片内部ID
    private byte[] source;      //雷管原始上传数据
    private String uid;         //雷管码 uid
    private String detCode;     //雷管发编号 fbh
    private int relay;          //雷管起爆延时时间 relay
    private Date time;           //雷管有效期 yxq
    private boolean isValid;    //是否有效
    private byte[] acCode;      //雷管工作码 gzm
    private int status;         //状态码
    private byte[] extId;       //额外ID
    private String zbDetCode;  // 中爆管码
    private int type;           // 类型 0 ranyi new 1

    private String statusName;           // 类型 0 ranyi new 1

    public Detonator(ChkDetonatorEntity detonatorEntity) {
        detCode = detonatorEntity.getCode();
        uid = detonatorEntity.getUid();

        relay = detonatorEntity.getRelay();
        time = detonatorEntity.getValidTime();
        ids = SommerUtils.hexStringToBytes(detonatorEntity.getUid());
        acCode = SommerUtils.hexStringToBytes(detonatorEntity.getWorkCode());
        status = detonatorEntity.getStatus();
        if (status == 0) {
            statusName = "正常";
        } else if (status == 1) {
            statusName = "未注册";
        } else if (status == 2) {
            statusName = "已使用";
        } else if (status == 3) {
            statusName = "不存在";
        } else {
            statusName = "异常";
        }
    }
/*
ExtID 说明 例子
[0] 厂家代码（HEX） 燃一：代码是 61，存储为 0x3D
前进：代码是 38，存储为 0x26
[1] 生产年份尾数（0~9） 2019 年，存储为 0x09
2020 年，存储为 0x00
[2] 生产月份（1~12） 1 月，存储为 0x01
12 月，存储为 0x0C
[3] 生产日期（1~31） 1 号，存储为 0x01
31 号，存储为 0x1F
[4] 特征码/机台号（ASCII） 燃一：当前是‘D’ ，存储为 0x44
前进：当前是‘6’ ，存储为 0x36
[5][6] 当前的盒号（0~999） [5]为盒号低字节， [6]为高字节
如：盒号 666=0x029A，则
[5]=0x9A， [6]=0x02
[7] 当前的管号（0~99） 如：管号 78=0x4E，则[7]=0x4E
 */
    public Detonator(String fbh) {


    }

    public int getDetonatorByFbh(String fbh){
        int cCodeType = 0;
        if(fbh.length()!=13){
            return 0;
        }
        String companyCode = fbh.substring(0,2);
        try {
            int cCode = Integer.parseInt(companyCode);
            if(cCode ==RanYiCode){
                cCodeType = 0;
            }else if(cCode ==QianJingCode){
                cCodeType = 1;
            }else if(cCode ==QingHuaCode){
                cCodeType = 2;
            }else if(cCode ==LeiMingCode){
                cCodeType = 3;
            }else if(cCode ==JiuLianCode){
                cCodeType = 4;
            }else {
                return 0;
            }
            String nianStr = fbh.substring(2,3);
            int nian = Integer.parseInt(nianStr);
            String yueStr = fbh.substring(3,5);
            int yue = Integer.parseInt(yueStr);
            String riStr = fbh.substring(5,7);
            int ri = Integer.parseInt(riStr);
            int count = yue*1000+ri*10+nian;
            String spc = fbh.substring(7,8);
            int spcNum = 0;
            if(cCodeType==0){
              spcNum =    RanYiCodeMap.get(spc);

            }else if( cCodeType ==1){
                spcNum =    QianJingCodeMap.get(spc);
            }else if( cCodeType ==2){
                spcNum =    QingHuaCodeMap.get(spc);
            }else if( cCodeType ==3){
                spcNum =    LeiMingCodeMap.get(spc);
            }else if( cCodeType ==4){
                spcNum =    JiuLianCodeMap.get(spc);
            }
//            String boxStr =fbh.substring(8,11);
//            int box = Integer.parseInt(boxStr);
//            String tubeStr =fbh.substring(11,13);
//            int tube = Integer.parseInt(tubeStr);
            String numStr =fbh.substring(8,13);
            int num = Integer.parseInt(numStr);
            int dc = (count << 17) +num + (spcNum << 28);
            byte[] ids = SommerUtils.intToBytes2(dc);
            this.ids = ids;
            this.time= new Date();
            isValid = true;
            byte[] c1 = SommerUtils.intToBytes(cCode);
            String uidStr = String.format(Locale.CHINA, "%02d", c1[0]);
            if(nian==0){
                uidStr += String.format(Locale.CHINA, "%02d", 20);
            }else {
                uidStr += String.format(Locale.CHINA, "%02d", 10+nian);
            }
            uidStr += "A8";
            uidStr += SommerUtils.bytesToHexString(ids);
            this.uid = uidStr.toUpperCase();
        } catch (NullPointerException | NumberFormatException e){
            e.printStackTrace();
            return 0;
        }
        this.detCode = fbh;
        return 1;
    }

    public int getTube(String detCode){
        if(detCode==null){
            return  0;
        }
        String tubeStr =detCode.substring(11,13);
            return  Integer.parseInt(tubeStr);
    }


    public String getZbDetCode() {
        return zbDetCode;
    }

    public void setZbDetCode(String zbDetCode) {
        this.zbDetCode = zbDetCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Detonator() {
    }

    public Detonator(byte[] encode) {
        if (encode == null || encode.length < 18) {
            isValid = false;
            return;
        }
        ids = new byte[4];
        extId = new byte[8];
        source = new byte[18];

        System.arraycopy(encode, 0, source, 0, 18);
        System.arraycopy(encode, 2, ids, 0, 4);
        System.arraycopy(encode, 6, extId, 0, 8);

        detCode = getDetCodeStr();
//        num = (encode[1] * 256 + encode[0])&0xffff;
        num = ((encode[0] & 0xFF)
                | ((encode[1] & 0xFF) << 8));
        relay = ((encode[14] & 0xFF)
                | ((encode[15] & 0xFF) << 8) | ((encode[16] & 0xFF) << 16) | ((encode[17] & 0xFF) << 24));
        time = new Date();

        getChipId();
        getUidStr();
        getAcCodeFromCode();
        getZBDetCodeStr();
        isValid = true;
        type = 0;
        status = 0;
        getStatusNameByStatus();
//        if (status == 0) {
//            statusName = "正常";
//        } else if (status == 1) {
//            statusName = "黑名单";
//        } else if (status == 2) {
//            statusName = "已使用";
//        } else if (status == 3) {
//            statusName = "不存在";
//        } else {
//            statusName = "异常";
//        }
    }

    public void getStatusNameByStatus(){
        DetStatusEnum statusEnum = DetStatusEnum.getByStatus(status);
        statusName = statusEnum.getName();
    }

    public String getZBDetCodeStr() {
//        XLog.v("test",bytesToHexString(src));
        StringBuilder sb = new StringBuilder("");
        if (source == null || source.length <= 0) {
            return null;
        }
//        int i= src[6];
        int time;
        time = source[6];
        if (time > 100) {
            time = time % 100;
        }

        String str = String.format(Locale.CHINA, "%02d", time);
//        System.out.println("1:"+str);
        sb.append(str);

        time = source[7];
        if (time > 10) {
            time = time % 10;
        }

        str = String.format(Locale.CHINA, "%01d", time);
//        System.out.println("2:"+str);
        sb.append(str);
        time = source[8];
        if (time > 100) {
            time = time % 100;
        }
        str = String.format(Locale.CHINA, "%02d", time);
//        System.out.println("3:"+str);
        sb.append(str);
        time = source[9];
        if (time > 100) {
            time = time % 100;
        }
        str = String.format(Locale.CHINA, "%02d", time);
//        System.out.println("4:"+str);
        sb.append(str);
//        sb.append(' ');
//        sb.append(' ');
        str = String.format(Locale.CHINA, "%c", source[10]);
//        System.out.println("5:"+str);
        sb.append(str);
//        sb.append(' ');
//        int temp = src[12];
//        XLog.v("test",""+temp);
//        temp = temp*256+(int)src[11];
//        XLog.v("test",""+temp);
//        System.out.println(source[12] +" "+source[11]+" "+source[13]);
        int temp = (int) (((source[11] & 0xFF)
                | (source[12] & 0xFF) << 8));


        if (temp > 1000) {
            temp = temp % 1000;
        }

        str = String.format(Locale.CHINA, "%03d", temp);


        sb.append(str);

        temp = source[13];


        if (temp > 100) {
            temp = temp % 100;
        }

        str = String.format(Locale.CHINA, "%02d", temp);


        sb.append(str);


        zbDetCode = sb.toString();
        return zbDetCode;
    }

    private void getAcCodeFromCode() {
        byte[] d2 = new byte[7];
        byte[] uidBytes = SommerUtils.hexStringToBytes(uid);
//        chipID = SommerUtils.bytesToHexString(d2);


//        SimpleDateFormat sdf = new SimpleDateFormat("yy");
//        Date date = new Date();
//        if(source[6]==RanYiCode){
        acCode = new byte[4];
        d2[0] = uidBytes[0];
        d2[1] = uidBytes[1];
        d2[2] = uidBytes[2];
        d2[3] = uidBytes[6];
        d2[4] = uidBytes[5];
        d2[5] = uidBytes[4];
        d2[6] = uidBytes[3];
//            d2[0] = 0x61;
//            d2[1] = 0x00;
//            d2[2] = 0x00;
//            d2[3] = (byte) 0xe9;
//            d2[4] = 0x56;
//            d2[5] = (byte) 0xd8;
//            d2[6] = 0x0f;
        byte[] crcs = CRCUtil.CRC16(d2);
        byte[] crcs2 = CRCUtil.CRC16(extId);
//            System.out.println("crc:"+SommerUtils.bytesToHexString(crcs2));
        acCode[0] = crcs[0];
        acCode[1] = crcs[1];
        acCode[2] = crcs2[0];
        acCode[3] = crcs2[1];
//            System.out.println("AcCode:"+SommerUtils.bytesToHexString(acCode));

//        }

    }


    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Detonator(RptDetonatorEntity detonatorEntity) {
        detCode = detonatorEntity.getCode();
        uid = detonatorEntity.getUid();
        relay = detonatorEntity.getRelay();
        time = detonatorEntity.getValidTime();
        num = detonatorEntity.getNum();
        ids = SommerUtils.hexStringToBytes(detonatorEntity.getDetIDs());
        chipID = detonatorEntity.getChipID();
        source = SommerUtils.hexStringToBytes(detonatorEntity.getSource());
        acCode = SommerUtils.hexStringToBytes(detonatorEntity.getWorkCode());
        extId = SommerUtils.hexStringToBytes(detonatorEntity.getExtId());
        type = detonatorEntity.getType();
        status = detonatorEntity.getStatus();
        DetStatusEnum statusEnum = DetStatusEnum.getByStatus(status);
        statusName = statusEnum.getName();
//        if (status == 0) {
//            statusName = "正常";
//        } else if (status == 1) {
//            statusName = "黑名单";
//        } else if (status == 2) {
//            statusName = "已使用";
//        } else if (status == 3) {
//            statusName = "不存在";
//        } else {
//            statusName = "异常";
//        }

//        status = rptDetBean.get
    }

    public Detonator(DetonatorEntity detonatorEntity) {
        detCode = detonatorEntity.getCode();
        uid = detonatorEntity.getUid();
        if (!StringUtils.isEmpty(detonatorEntity.getRelay()))
            relay = Integer.parseInt(detonatorEntity.getRelay());
        time = detonatorEntity.getValidTime();
        ids = SommerUtils.hexStringToBytes(detonatorEntity.getUid());
        acCode = SommerUtils.hexStringToBytes(detonatorEntity.getWorkCode());
        status = detonatorEntity.getStatus();
        DetStatusEnum statusEnum = DetStatusEnum.getByStatus(status);
        statusName = statusEnum.getName();
//        if (status == 0) {
//            statusName = "正常";
//        } else if (status == 1) {
//            statusName = "黑名单";
//        } else if (status == 2) {
//            statusName = "已使用";
//        } else if (status == 3) {
//            statusName = "不存在";
//        } else {
//            statusName = "异常";
//        }

//        status = rptDetBean.get
    }

    public byte[] getExtId() {
        return extId;
    }

    public void setExtId(byte[] extId) {
        this.extId = extId;
    }

    public String  getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public byte[] getIds() {
        return ids;
    }

    public void setIds(byte[] ids) {
        this.ids = ids;
    }

    public String getChipID() {
        return chipID;
    }

    public void setChipID(String chipID) {
        this.chipID = chipID;
    }

    public byte[] getAcCode() {
        return acCode;
    }

    public void setAcCode(byte[] acCode) {
        this.acCode = acCode;
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }

    public String getDetCode() {
        return detCode;
    }

    public void setDetCode(String detCode) {
        this.detCode = detCode;
    }

    public int getRelay() {
        return relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    private String getChipId() {
        StringBuilder sb = new StringBuilder("");
        String str;

        str = String.format(Locale.CHINA, "%02d", source[8]);
        sb.append(str);
        str = String.format(Locale.CHINA, "%02d", source[9]);
        sb.append(str);
        str = String.format(Locale.CHINA, "%01d", source[7]);
        sb.append(str);
        Integer code1 = Integer.parseInt(sb.toString());
//        System.out.println(sb.toString()+"   ||  "+Integer.toHexString(code1));
        code1 = code1 << 17;
//        System.out.println(sb.toString()+"   ||  "+Integer.toHexString(code1));

        sb = new StringBuilder("");
        int temp = (int) (((source[12] & 0xFF) << 8)
                | (source[11] & 0xFF));
//       System.out.println(temp);
        str = String.format(Locale.CHINA, "%03d", temp);
        sb.append(str);
//       System.out.println(temp);
        str = String.format(Locale.CHINA, "%02d", source[13]);
        sb.append(str);
        Integer code2 = Integer.parseInt(sb.toString());
//        System.out.println(sb.toString()+"   ||  "+Integer.toHexString(code2));
        Integer ids = code1 | code2;
//        System.out.println("  ids:  "+Integer.toHexString(ids));
        chipID = Integer.toHexString(ids);
        return chipID;
    }

    private void getUidStr() {
//        XLog.v("test",bytesToHexString(src));
        StringBuilder sb = new StringBuilder("");
        if (source == null || source.length <= 0) {
            return;
        }
//        int i= src[6];
        String str = String.format(Locale.CHINA, "%02d", source[6]);
        sb.append(str);
        if (source[7] == 9) {

            sb.append("19");

        }else{
            int year = source[7]+20;
            sb.append(""+year);
        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yy");
//        Date date = new Date();
//        sb.append(sdf.format(date));
//        sb.append("00");
        sb.append("A8");
        str = String.format(Locale.CHINA, "%02x", source[5]);
        sb.append(str);
        str = String.format(Locale.CHINA, "%02x", source[4]);
        sb.append(str);
        str = String.format(Locale.CHINA, "%02x", source[3]);
        sb.append(str);
        str = String.format(Locale.CHINA, "%02x", source[2]);
        sb.append(str);
        uid = sb.toString().toUpperCase();
    }

    private String getDetCodeStr() {
//        XLog.v("test",bytesToHexString(src));
        StringBuilder sb = new StringBuilder("");
        if (extId == null || extId.length <= 0) {
            return null;
        }
//        int i= src[6];
        String str = String.format(Locale.CHINA, "%02d", extId[0]);
        sb.append(str);
//        i=src[7];
//        sb.append(' ');
        str = String.format(Locale.CHINA, "%01d", extId[1]);
        sb.append(str);
        str = String.format(Locale.CHINA, "%02d", extId[2]);
        sb.append(str);
        str = String.format(Locale.CHINA, "%02d", extId[3]);
        sb.append(str);
//        sb.append(' ');
//        sb.append(' ');
        str = String.format(Locale.CHINA, "%c", extId[4]);
        sb.append(str);
//        sb.append(' ');
//        int temp = src[12];
//        XLog.v("test",""+temp);
//        temp = temp*256+(int)src[11];
//        XLog.v("test",""+temp);

        int temp = (int) (((extId[6] & 0xFF) << 8)
                | (extId[5] & 0xFF));
//       System.out.println(temp);
        str = String.format(Locale.CHINA, "%03d", temp);
        sb.append(str);

//       System.out.println(temp);
        str = String.format(Locale.CHINA, "%02d", extId[7]);
        sb.append(str);
//        temp = (int) ((source[13] & 0xFF));
////        sb.append(' ');
//        str = String.format("%02d",temp);
////        XLog.v("test",""+src[13]);
//        sb.append(str);
//        XLog.v("test",sb.toString());
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Detonator{" +
                "num=" + num +
                ", ids=" + SommerUtils.bytesToHexString((ids)) +
                ", chipID='" + chipID + '\'' +
                ", source=" + SommerUtils.bytesToHexString(source) +
                ", detCode='" + detCode + '\'' +
                ", relay=" + relay +
                ", time=" + time +
                ", isValid=" + isValid +
                ", acCode='" + SommerUtils.bytesToHexString(acCode) +
                ", status=" + status +
                ", statusName=" + statusName +
                ", extId=" + SommerUtils.bytesToHexString(extId) +
                ", uid='" + uid + '\'' +
                '}';
    }

}
