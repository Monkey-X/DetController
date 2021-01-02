package com.etek.controller.hardware.util; /***
 * 雷管的ID、管码、二维码相关转换和校验函数类
 * @author Xin Hongwei
 * @version V1.01
 */

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class DetIDConverter {

    private static final String TAG = "DetIDConverter";
    private static Map<Integer, String> m_mapDeskCode = new HashMap<Integer, String>();

    //  起爆器的厂商代码
    public static byte m_bMID = 99;

    static {
        m_mapDeskCode.put(99, "ZYX123456");
        m_mapDeskCode.put(61, "D23456789");
        m_mapDeskCode.put(38, "612345789");
        m_mapDeskCode.put(64, "ARPNTHKS1");
        m_mapDeskCode.put(28, "BCDEFGHIJ");
        m_mapDeskCode.put(60, "546789abd");
        m_mapDeskCode.put(9, "ADXYZ1234");
    }

    public DetIDConverter() {
    }

    /*** 获取机台号的索引
     * @return 返回机台号索引，如果没找到返回-1
     * */
    private static byte GetDeskIndex(byte facCode, byte dskCode) {
        Integer nval = (int) facCode;
        String str = m_mapDeskCode.get(nval);
        if (null == str) return -1;

        return (byte) str.indexOf(dskCode);
    }

    /***
     * ID号转为管码号
     * @param ID        4字节的模组ID
     * @param facCode    厂家代码
     * @param dskCode    机台号
     * @return 8字节的管码编号
     */
    public static byte[] Conv_ID2DC(byte[] ID, byte facCode, byte dskCode) {
        long hexID;
        long tmp;
        long ID3;
        byte[] szbuf = new byte[4];

        //	获取机台号索引
        byte dskIdx = GetDeskIndex(facCode, dskCode);

        ID3 = ID[3] - (long) ((dskIdx << 4) & 0xF0);

        szbuf[0] = (byte) ID3;
        szbuf[1] = ID[2];
        szbuf[2] = ID[1];
        szbuf[3] = ID[0];
        int nval = DataConverter.bytes2Int(szbuf);
        hexID = DataConverter.getIntValue(nval);

        //hexID=(ID3<<24)|(ID[2]<<16)|(ID[1]<<8)|ID[0];

        tmp = (hexID & 0xFFFE0000L) >> 17;
        byte[] DC = new byte[8];

        DC[0] = facCode;
        DC[1] = (byte) (tmp % 10);
        DC[2] = (byte) (tmp / 1000);
        DC[3] = (byte) ((tmp - tmp % 10) / 10 % 100);

        tmp = hexID & 0x0001FFFF;
        DC[4] = dskCode;
        DC[5] = (byte) (tmp / 100);
        DC[6] = (byte) ((tmp / 100) >> 8);
        DC[7] = (byte) (tmp % 100);

        return DC;
    }

    /***
     * 管码编号转为模组ID
     * @param DC    8字节的管码编号
     * @return
     */
    public static byte[] Conv_DC2ID(byte[] DC) {
        long hexID;
        byte dskIdx = GetDeskIndex(DC[0], DC[4]);

        //hexID =(DC[2]*1000+DC[3]*10+DC[1])<<17;
        //hexID|=(DC[5]|(DC[6]<<8))*100+DC[7];
        hexID = DataConverter.getByteValue(DC[2]) * 1000;
        hexID = hexID + DataConverter.getByteValue(DC[3]) * 10;
        hexID = hexID + DataConverter.getByteValue(DC[1]);
        hexID = (hexID << 17);
        hexID = hexID + (DataConverter.getByteValue(DC[5]) + DataConverter.getByteValue(DC[6]) * 0x100) * 100;
        hexID = hexID + DataConverter.getByteValue(DC[7]);

        byte[] ID = new byte[4];

        ID[0] = (byte) hexID;
        ID[1] = (byte) (hexID >> 8);
        ID[2] = (byte) (hexID >> 16);
        ID[3] = (byte) (hexID >> 24);

        //ID[3]+=(byte)((dskIdx<<4)&0xF0);
        ID[3] = (byte) (DataConverter.getByteValue(ID[3]) + DataConverter.getByteValue((byte) (dskIdx * 0x10)));
        return ID;
    }

    /***
     * 将8字节的管码编号转为格式化管码
     * @param DC    8字节的管码
     * @return 格式化管码
     */
    public static String GetDisplayDC(byte[] DC) {
        int i;
        int[] nval = new int[8];

        if (DC.length < 8) return "";

        for (i = 0; i < 8; i++) nval[i] = DataConverter.getByteValue(DC[i]);

        //sprintf((char*)str,”%02d%01d%02d%02d%c%03d%02d”,
        //DC[0], DC[1], DC[2], DC[3], DC[4], DC[5]|(DC[6]<<8), DC[7]);
        String str = String.format("%02d%01d%02d%02d%c%03d%02d",
                nval[0],
                nval[1],
                nval[2],
                nval[3],
                nval[4],
                (nval[6] * 0x100 + nval[5]) % 1000,
                nval[7]);
        return str;
    }

    /***
     * 通过显示的管码，计算实际存储的管码
     * @param strDC
     * @return
     */
    public static byte[] GetDCByString(String strDC) {
        byte[] nval = new byte[8];

        if(12==strDC.length()){
            return GetDCByOldQRString(strDC);
        }

        if (strDC.length() < 13)
            return null;

        //sprintf((char*)str,”%02d %01d %02d %02d %c%03d%02d”,
        //DC[0], DC[1], DC[2], DC[3], DC[4], DC[5]|(DC[6]<<8), DC[7]);
        nval[0] = Byte.parseByte(strDC.substring(0, 2));
        nval[1] = Byte.parseByte(strDC.substring(2, 3));
        nval[2] = Byte.parseByte(strDC.substring(3, 5));
        nval[3] = Byte.parseByte(strDC.substring(5, 7));
        nval[4] = (byte) strDC.charAt(7);
        int ret = Integer.parseInt(strDC.substring(8, 11));
        nval[5]=(byte)(ret&0xff);
        ret = ret - DataConverter.getByteValue(nval[5]);
        nval[6] = (byte)(ret/0x100);
        nval[7] = Byte.parseByte(strDC.substring(11, 13));
        return nval;
    }

    /***
     * 通过旧的二维码（12位）获取管码信息
     * @param strDC
     * @return
     */
    private static byte[] GetDCByOldQRString(String strDC) {
        byte[] nval = new byte[8];

        if (strDC.length() < 12)
            return null;

        //sprintf((char*)str,”%02d %01d %02d %02d %c%03d%02d”,
        //DC[0], DC[1], DC[2], DC[3], DC[4], DC[5]|(DC[6]<<8), DC[7]);
        nval[0]= 61;
        nval[1] = Byte.parseByte(strDC.substring(0,1));
        nval[2] = Byte.parseByte(strDC.substring(1,3));
        nval[3] = Byte.parseByte(strDC.substring(3,5));
        nval[4] = 0x44;	//D

        int ret = Integer.parseInt(strDC.substring(5, 8));
        nval[5]=(byte)(ret&0xff);
        ret = ret - DataConverter.getByteValue(nval[5]);
        nval[6] = (byte)(ret/0x100);

        nval[7] = Byte.parseByte(strDC.substring(8, 10));
        return nval;
    }

    /***
     * 检测雷管二维码的检验有效性
     * @param strQRCode    二维码字符串，长度必须大于等于17
     * @return
     *    true-校验合法，
     * 	false--校验不合法
     */
    public static boolean VerifyQRCheckValue(String strQRCode) {
        int i;
        int nxor, nlen;

        nlen = strQRCode.length();
        //  旧12位的二维码
        if(12==nlen){
            return VerifyOldQRCheckValue(strQRCode);
        }

        if (nlen < 17) {
            Log.d(TAG, "VerifyQRCheckValue: false");
            return false;
        }

        //	获取前15个字符的字节流
        byte[] charr = strQRCode.getBytes();
        if(99!=m_bMID){
            if(charr[0]!=m_bMID){
                Log.d(TAG, "VerifyQRCheckValue: 和起爆器厂商编码不一致！");
                return false;
            }
        }

        int[] nval = new int[15];
        for (i = 0; i < 15; i++) nval[i] = DataConverter.getByteValue((byte) charr[i]);

        //	计算校验值
        nxor = 0;
        for (i = 0; i < 15; i++) nxor = nxor + nval[i];
        nxor = nxor % 100;

        //	校验值转为字符串
        String strxor = String.format("%02d", nxor);

        //	比较最后两个是否相同
        String strxor0 = strQRCode.substring(nlen - 2);
        if (!strxor.equals(strxor0)) return false;
        Log.d(TAG, "VerifyQRCheckValue: true");
        return true;
    }

    /***
     * 校验旧的12位条码
     * @param strQRCode
     * @return
     */
    private static boolean VerifyOldQRCheckValue(String strQRCode){
        int[] nval = new int[8];
        nval[0]= 61;
        nval[1] = Integer.parseInt(strQRCode.substring(0,1));
        nval[2] = Integer.parseInt(strQRCode.substring(1,3));
        nval[3] = Integer.parseInt(strQRCode.substring(3,5));
        nval[4] = 0x44;//D

        int n = Integer.parseInt(strQRCode.substring(5,8));
        nval[5] = n&0xff;
        nval[6]=(n-nval[5])/0x100;
        nval[7] = Integer.parseInt(strQRCode.substring(8,10));

        //  校验
        n = 0;
        for(int i=0;i<8;i++) n = n + nval[i];
        n = n %100;

        //  校验值比较
        int nxor = Integer.parseInt(strQRCode.substring(10,12));

        if(n!=nxor) return false;

        return true;
    }

    public static void SetMID(byte bMID){
        m_bMID = bMID;
    }

    /***
     * 雷管管码、ID和二维码相关类测试
     */
    public void testProc() {
        byte[] ID = {(byte) 0xF7, (byte) 0xAC, (byte) 0x1E, (byte) 0x74};
        byte[] DC = Conv_ID2DC(ID, (byte) 28, (byte) 0x44);
        String str = DataConverter.bytes2HexString(DC);

        str = GetDisplayDC(DC);

        byte[] dc0 = GetDCByString(str);

        byte[] id2 = Conv_DC2ID(DC);
        str = DataConverter.bytes2HexString(id2);
        int ret = DataConverter.bytes2Int(id2);

        //	二维码校验比对
        str = "6191201D123451985";
        VerifyQRCheckValue(str);


        return;
    }

}
