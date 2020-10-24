<<<<<<< HEAD
package com.etek.controller;

import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.ProInfoDto;
import com.etek.controller.dto.ReportDto;
import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.utils.CRCUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.LocationUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.Base64Utils;
import com.etek.sommerlibrary.utils.DES3Utils;
import com.etek.sommerlibrary.utils.DateUtil;

import static com.etek.controller.utils.SommerUtils.bytesToInt;
import static com.etek.controller.utils.SommerUtils.getFloat;
import static java.lang.String.format;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    @Ignore
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    @Ignore
    public void test_crc() {
        byte[] cmd = new byte[3];
        cmd[0] = 0x30;
        cmd[1] = 0x00;
        cmd[2] = CRCUtil.calcCrc8(cmd, 0, 2);
//        XLog.i(TAG,"cmd "+SommerUtils.bytesToHexArrString(cmd));
        cmd = new byte[11];
        cmd[0] = 0x30;
        cmd[1] = 0x61;
        cmd[2] = 0x65;
        cmd[3] = 0x75;
        cmd[4] = 0x23;
        cmd[5] = 0x68;
        cmd[6] = 0x10;
        cmd[7] = 0x19;
        cmd[8] = 0x00;
        cmd[9] = 0x60;
        cmd[10] = (byte) 0x82;
//        XLog.i(TAG,"cmd "+SommerUtils.bytesToHexArrString(cmd));
        System.out.println(Arrays.toString(cmd));
        assertEquals(cmd[10], CRCUtil.calcCrc8(cmd, 0, 10));
    }


    @Test

    public void test_time() {
        byte[] t1 = {(byte) 0xa3, (byte) 0xe6, 0x02, 0x00, 0x27, 0x31, 0x02, 0x00, (byte) 0x86, (byte) 0xcf, (byte) 0xf0, 0x42, 0x6e, 0x0b, (byte) 0xfc, 0x41};

        int date = bytesToInt(t1, 0);
        System.out.println(date);
        String dateStr = String.format("%06d", date);
        System.out.println(dateStr);
        int time = bytesToInt(t1, 4);
        System.out.println(time);
        String timeStr = String.format("%06d-%06d", date, time);
        System.out.println(timeStr);

        Date d1 = DateUtil.parseDate("yyMMdd-HHmmss", timeStr);
        System.out.println(d1);


//        long datetime = bytes2Long(t1);
//        System.out.println(datetime);
//        long millions=new Long(date).longValue()*100;
//        millions = millions*60*60*24;
//        Calendar c=Calendar.getInstance();
//        c.set(Calendar.MINUTE,date);
//        System.out.println(""+c.getTime());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateString = sdf.format(c.getTime());
//        System.out.println(dateString);
        float lng = getFloat(t1, 8);
        System.out.println(lng);
        float lat = getFloat(t1, 12);
        System.out.println(lat);

        byte[] t2 = {(byte) 0x46, (byte) 0x61, 0x61, 0x00, 0x02, 0x55, 0x02, 0x00, (byte) 0x86, (byte) 0xcf, (byte) 0xf0, 0x42, 0x6e, 0x0b, (byte) 0xfc, 0x41};
        StringBuilder sb = new StringBuilder();
        String tmp;

        tmp = format("%c", t2[0]);
        sb.append(tmp);


        tmp = format("%02x", t2[1]);
        sb.append(tmp);
        tmp = format("%02x", t2[2]);
        sb.append(tmp);
        tmp = format("%02x", t2[3]);
        sb.append(tmp);
        tmp = format("%02x", t2[4]);
        sb.append(tmp);
        tmp = format("%02x", t2[5]);
        sb.append(tmp);

        System.out.println(sb.toString());


    }


    @Test
    public void testEncode() {
        String paramStr = "{\"sbbh\":\"61000255\",\"jd\":\"118.182993\",\"wd\":\"36.490714\",\"bpsj\":\"2018-08-01 16:23:01\",\"bprysfz\":\"123456789123456789\",\"uid\":\"61000001028324,61000000916433,61000000942128\",\"xmbh\":\"370100X15040023\",\"htid\":\"370101318060002\"}";
        System.out.println(paramStr);
        byte[] paramArr = DES3Utils.encryptMode(paramStr.getBytes(), DES3Utils.PASSWORD_CRYPT_KEY);
        if (paramArr == null) {
            System.out.println("paramarr is null");
            return;
        } else {
            System.out.println(new String(paramArr));
            byte[] decode1 = Base64Utils.getEncodeBytes(paramArr);
            System.out.println(new String(decode1));
        }

    }


    @Test
    public void detDecode() {
        String detListStr = " [{\"acCode\":\"AvBs0A==\",\"chipID\":\"00000000000000\",\"code\":\"AQDGVtgPPQgCAkTeAA6CAAAA\",\"detCode\":\"6180202D22214\",\"extId\":\"PQgCAkTeAA4=\",\"ids\":\"xlbYDw==\",\"num\":1,\"relay\":130,\"status\":0,\"time\":1552625815590,\"uid\":\"6100000fd856c6\",\"valid\":true},{\"acCode\":\"QF6TKQ==\",\"chipID\":\"00000000000000\",\"code\":\"AgDIVtgPPQgCAkTeABCqAAAA\",\"detCode\":\"6180202D22216\",\"extId\":\"PQgCAkTeABA=\",\"ids\":\"yFbYDw==\",\"num\":2,\"relay\":170,\"status\":0,\"time\":1552625816024,\"uid\":\"6100000fd856c8\",\"valid\":true},{\"acCode\":\"+0IaOA==\",\"chipID\":\"00000000000000\",\"code\":\"AwDJVtgPPQgCAkTeABG+AAAA\",\"detCode\":\"6180202D22217\",\"extId\":\"PQgCAkTeABE=\",\"ids\":\"yVbYDw==\",\"num\":3,\"relay\":190,\"status\":0,\"time\":1552625816540,\"uid\":\"6100000fd856c9\",\"valid\":true},{\"acCode\":\"NmeBCg==\",\"chipID\":\"00000000000000\",\"code\":\"BADKVtgPPQgCAkTeABLSAAAA\",\"detCode\":\"6180202D22218\",\"extId\":\"PQgCAkTeABI=\",\"ids\":\"ylbYDw==\",\"num\":4,\"relay\":210,\"status\":0,\"time\":1552625816986,\"uid\":\"6100000fd856ca\",\"valid\":true},{\"acCode\":\"jXsIGw==\",\"chipID\":\"00000000000000\",\"code\":\"BQDLVtgPPQgCAkTeABPmAAAA\",\"detCode\":\"6180202D22219\",\"extId\":\"PQgCAkTeABM=\",\"ids\":\"y1bYDw==\",\"num\":5,\"relay\":230,\"status\":0,\"time\":1552625817493,\"uid\":\"6100000fd856cb\",\"valid\":true},{\"acCode\":\"yzRYlA==\",\"chipID\":\"00000000000000\",\"code\":\"BgDgVtgPPQgCAkTeACj6AAAA\",\"detCode\":\"6180202D22240\",\"extId\":\"PQgCAkTeACg=\",\"ids\":\"4FbYDw==\",\"num\":6,\"relay\":250,\"status\":0,\"time\":1552625817779,\"uid\":\"6100000fd856e0\",\"valid\":true},{\"acCode\":\"cCjRhQ==\",\"chipID\":\"00000000000000\",\"code\":\"BwDhVtgPPQgCAkTeACkOAQAA\",\"detCode\":\"6180202D22241\",\"extId\":\"PQgCAkTeACk=\",\"ids\":\"4VbYDw==\",\"num\":7,\"relay\":270,\"status\":0,\"time\":1552625818087,\"uid\":\"6100000fd856e1\",\"valid\":true},{\"acCode\":\"vQ1Ktw==\",\"chipID\":\"00000000000000\",\"code\":\"CADiVtgPPQgCAkTeACoiAQAA\",\"detCode\":\"6180202D22242\",\"extId\":\"PQgCAkTeACo=\",\"ids\":\"4lbYDw==\",\"num\":8,\"relay\":290,\"status\":0,\"time\":1552625818415,\"uid\":\"6100000fd856e2\",\"valid\":true},{\"acCode\":\"BhHDpg==\",\"chipID\":\"00000000000000\",\"code\":\"CQDjVtgPPQgCAkTeACs2AQAA\",\"detCode\":\"6180202D22243\",\"extId\":\"PQgCAkTeACs=\",\"ids\":\"41bYDw==\",\"num\":9,\"relay\":310,\"status\":0,\"time\":1552625818736,\"uid\":\"6100000fd856e3\",\"valid\":true},{\"acCode\":\"J0Z80g==\",\"chipID\":\"00000000000000\",\"code\":\"CgDkVtgPPQgCAkTeACxKAQAA\",\"detCode\":\"6180202D22244\",\"extId\":\"PQgCAkTeACw=\",\"ids\":\"5FbYDw==\",\"num\":10,\"relay\":330,\"status\":0,\"time\":1552625819058,\"uid\":\"6100000fd856e4\",\"valid\":true},{\"acCode\":\"nFr1ww==\",\"chipID\":\"00000000000000\",\"code\":\"CwDlVtgPPQgCAkTeAC1eAQAA\",\"detCode\":\"6180202D22245\",\"extId\":\"PQgCAkTeAC0=\",\"ids\":\"5VbYDw==\",\"num\":11,\"relay\":350,\"status\":0,\"time\":1552625819395,\"uid\":\"6100000fd856e5\",\"valid\":true},{\"acCode\":\"UX9u8Q==\",\"chipID\":\"00000000000000\",\"code\":\"DADmVtgPPQgCAkTeAC5yAQAA\",\"detCode\":\"6180202D22246\",\"extId\":\"PQgCAkTeAC4=\",\"ids\":\"5lbYDw==\",\"num\":12,\"relay\":370,\"status\":0,\"time\":1552625819815,\"uid\":\"6100000fd856e6\",\"valid\":true},{\"acCode\":\"6mPn4A==\",\"chipID\":\"00000000000000\",\"code\":\"DQDnVtgPPQgCAkTeAC+GAQAA\",\"detCode\":\"6180202D22247\",\"extId\":\"PQgCAkTeAC8=\",\"ids\":\"51bYDw==\",\"num\":13,\"relay\":390,\"status\":0,\"time\":1552625820102,\"uid\":\"6100000fd856e7\",\"valid\":true},{\"acCode\":\"E9GRCA==\",\"chipID\":\"00000000000000\",\"code\":\"DgDoVtgPPQgCAkTeADCaAQAA\",\"detCode\":\"6180202D22248\",\"extId\":\"PQgCAkTeADA=\",\"ids\":\"6FbYDw==\",\"num\":14,\"relay\":410,\"status\":0,\"time\":1552625820408,\"uid\":\"6100000fd856e8\",\"valid\":true},{\"acCode\":\"qM0YGQ==\",\"chipID\":\"00000000000000\",\"code\":\"DwDpVtgPPQgCAkTeADGuAQAA\",\"detCode\":\"6180202D22249\",\"extId\":\"PQgCAkTeADE=\",\"ids\":\"6VbYDw==\",\"num\":15,\"relay\":430,\"status\":0,\"time\":1552625820827,\"uid\":\"6100000fd856e9\",\"valid\":true}]";
        String proInfoStr = "{\"lgs\":{\"lg\":[{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E9\",\"fbh\":\"6180202D22249\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"A8CD1819\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E8\",\"fbh\":\"6180202D22248\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"13D19108\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E7\",\"fbh\":\"6180202D22247\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"EA63E7E0\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E6\",\"fbh\":\"6180202D22246\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"517F6EF1\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E5\",\"fbh\":\"6180202D22245\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"9C5AF5C3\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E4\",\"fbh\":\"6180202D22244\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"27467CD2\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E3\",\"fbh\":\"6180202D22243\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"0611C3A6\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E2\",\"fbh\":\"6180202D22242\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"BD0D4AB7\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E1\",\"fbh\":\"6180202D22241\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"7028D185\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E0\",\"fbh\":\"6180202D22240\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"CB345894\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856CB\",\"fbh\":\"6180202D22219\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"8D7B081B\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856CA\",\"fbh\":\"6180202D22218\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"3667810A\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856C9\",\"fbh\":\"6180202D22217\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"FB421A38\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C8\",\"fbh\":\"6180202D22216\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C7\",\"fbh\":\"6180202D22215\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C6\",\"fbh\":\"6180202D22214\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C5\",\"fbh\":\"6180202D22213\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C4\",\"fbh\":\"6180202D22212\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C3\",\"fbh\":\"6180202D22211\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C2\",\"fbh\":\"6180202D22210\",\"yxq\":\"\",\"gzm\":\"\"}]},\"jbqys\":{\"jbqy\":[]},\"sbbhs\":[{\"sbbh\":\"61000255\"},{\"sbbh\":\"F9900909097\"},{\"sbbh\":\"F5300000223\"},{\"sbbh\":\"D0000005\"},{\"sbbh\":\"F530000104\"},{\"sbbh\":\"F6000000123\"},{\"sbbh\":\"F0000098\"},{\"sbbh\":\"F38000001234\"},{\"sbbh\":\"f3333445678\"},{\"sbbh\":\"f7788991011\"},{\"sbbh\":\"F9900000296\"},{\"sbbh\":\"F3800000215\"},{\"sbbh\":\"61000041\"},{\"sbbh\":\"F48000000523\"},{\"sbbh\":\"F5300001041\"},{\"sbbh\":\"f8886789053\"},{\"sbbh\":\"F6161000255\"},{\"sbbh\":\"SA0001\"},{\"sbbh\":\"SY02\"},{\"sbbh\":\"44171101\"},{\"sbbh\":\"123456\"},{\"sbbh\":\"111111111111111111111111\"},{\"sbbh\":\"15965935456i87\"},{\"sbbh\":\"SY03\"},{\"sbbh\":\"20190207\"},{\"sbbh\":\"SY001\"},{\"sbbh\":\"F11\"}],\"zbqys\":{\"zbqy\":[{\"zbqybj\":\"5000\",\"zbqymc\":\"河北测试\",\"zbqywd\":\"38.346694\",\"zbqssj\":null,\"zbqyjd\":\"114.728516\",\"zbjzsj\":null}]},\"sqrq\":\"2019-03-15 09:16:49\",\"cwxx\":\"0\"}";
        System.out.println(proInfoStr);
        ProInfoDto proInfoDto = JSON.parseObject(proInfoStr, ProInfoDto.class);

        List<Detonator> detList = JSON.parseArray(detListStr, Detonator.class);
//        System.out.println(detInfoDto);
//        byte[] data = {   0x53,0x0e,0x00, (byte) 0xe8,0x56, (byte) 0xd8,0x0f,0x3d,0x08,0x02,0x02,0x44, (byte) 0xde,0x00,0x30, (byte) 0x9a,
//                0x01,0x00,0x00,0x21};
//        BLEDevResp bleDevResp = new BLEDevResp(data);
//        Detonator detonator = new Detonator(bleDevResp.getData());
//        System.out.println(detonator);
        int unUsedDet = proInfoDto.getLgs().getLg().size();
        int unRegDet = 0;
        boolean isUnreg = false;
        List<Lg> lgs = proInfoDto.getLgs().getLg();
        for (Detonator det : detList) {

            isUnreg = false;
            for (Lg lg : lgs) {

                if (det.getDetCode().equalsIgnoreCase(lg.getFbh())
                        && det.getUid().equalsIgnoreCase(lg.getUid())) {
                    unUsedDet--;
                    if(unUsedDet<0){
                        unUsedDet = 0;
                    }
                    isUnreg = true;

                    if (SommerUtils.bytesToHexString(det.getAcCode()).equalsIgnoreCase(lg.getGzm())) {
                        System.out.println("三码合一");
                        System.out.println(lg);

                    }else{

                        System.out.println("双码合一");
                        System.out.println(lg);
                    }
                }
//                System.out.println(lg);
            }
            if(!isUnreg){
                unRegDet++;
            }
        }
        System.out.println("unRegDet:"+unRegDet+"unUsedDet:"+unUsedDet);

    }

    @Test
  public   void permission(){
        double latitude =25.59091466778714;
        double longitude= 118.05328889800047;
//          longitude:

        PermissibleZoneEntity permissibleZoneEntity = new PermissibleZoneEntity(5000,118.079058,25.559628);
        LocationUtil.LocationRange range = LocationUtil.getAround(permissibleZoneEntity.getLatitude(), permissibleZoneEntity.getLongitude(), permissibleZoneEntity.getRadius());
        if (latitude > range.getMinLat()
                && latitude< range.getMaxLat()
                && longitude> range.getMinLng()
                && longitude< range.getMaxLng()) {
            System.out.println("in ");
        }else {
            System.out.println("out");
        }
    }




    @Test
    public   void getDetonator(){
        byte[] data ={0x01,0x00, (byte) 0xd7, (byte) 0x85,0x24,0x18,0x3d,0x00,0x03,0x09,0x44,0x56,0x01,0x3f,0x01,0x00,
                0x00,0x00};
        Detonator detonator = new Detonator(data);
        System.out.println(detonator);
        DetUtil.getExtid(detonator.getDetCode());
    }
    String u1 = "{\"bprysfz\":\"533421198207090717\",\"bpsj\":\"2020-07-16 14:48:32\",\"dwdm\":\"5301034300004\",\"htid\":\"533421319120010\",\"jd\":\"99.895200\",\"sbbh\":\"F61A8200045\",\"uid\":\"6120A82F80B229,6120A82F80B23E,6120A82F80B23F,6120A82F80B239,6120A82F80B235,6120A82F80B231,6120A82F80B236,6120A82F80B23D,6120A82F80B23B,6120A82F80B227,6120A82F80B22A,6120A82F80B228,6120A82F80B22E,6120A82F80B232,6120A82F80B224,6120A82F80B237,6120A82F80B238,6120A82F80B23A,6120A82F80B22F,6120A82F80B22B,6120A82F80B225,6120A82F80B226,6120A82F80B230,6120A82F80B22C,6120A82F80B22D,6120A82F80B23C,6120A82F80B233,6120A82F80B234\",\"wd\":\"28.119450\",\"xmbh\":\"\"}";


    @Test
    public void getCrpCode() throws UnsupportedEncodingException {
//        ReportDto reportDto = new ReportDto();
//        Result result = reportDto.getRptEncode(u1);
//        if (!result.isSuccess()) {
//            System.out.println("数据编码出错：" + result.getMessage());
//
//        }
//      String  urlString = URLEncoder.encode(result.getCode(), "GBK");
//        System.out.println(urlString);
    }

    @Test
    public void decode()  {
       Detonator detonator = new Detonator();
        int fb = detonator.getDetonatorByFbh("6191128D46031");
        if(fb>0){
            System.out.println(detonator);
        }else {
            System.out.println("错误！");
        }

    }
=======
package com.etek.controller;

import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.etek.controller.dto.Lg;
import com.etek.controller.dto.ProInfoDto;
import com.etek.controller.dto.ReportDto;
import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.entity.PermissibleZoneEntity;
import com.etek.controller.utils.CRCUtil;
import com.etek.controller.utils.DetUtil;
import com.etek.controller.utils.LocationUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.Base64Utils;
import com.etek.sommerlibrary.utils.DES3Utils;
import com.etek.sommerlibrary.utils.DateUtil;

import static com.etek.controller.utils.SommerUtils.bytesToInt;
import static com.etek.controller.utils.SommerUtils.getFloat;
import static java.lang.String.format;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    @Ignore
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    @Ignore
    public void test_crc() {
        byte[] cmd = new byte[3];
        cmd[0] = 0x30;
        cmd[1] = 0x00;
        cmd[2] = CRCUtil.calcCrc8(cmd, 0, 2);
//        XLog.i(TAG,"cmd "+SommerUtils.bytesToHexArrString(cmd));
        cmd = new byte[11];
        cmd[0] = 0x30;
        cmd[1] = 0x61;
        cmd[2] = 0x65;
        cmd[3] = 0x75;
        cmd[4] = 0x23;
        cmd[5] = 0x68;
        cmd[6] = 0x10;
        cmd[7] = 0x19;
        cmd[8] = 0x00;
        cmd[9] = 0x60;
        cmd[10] = (byte) 0x82;
//        XLog.i(TAG,"cmd "+SommerUtils.bytesToHexArrString(cmd));
        System.out.println(Arrays.toString(cmd));
        assertEquals(cmd[10], CRCUtil.calcCrc8(cmd, 0, 10));
    }


    @Test

    public void test_time() {
        byte[] t1 = {(byte) 0xa3, (byte) 0xe6, 0x02, 0x00, 0x27, 0x31, 0x02, 0x00, (byte) 0x86, (byte) 0xcf, (byte) 0xf0, 0x42, 0x6e, 0x0b, (byte) 0xfc, 0x41};

        int date = bytesToInt(t1, 0);
        System.out.println(date);
        String dateStr = String.format("%06d", date);
        System.out.println(dateStr);
        int time = bytesToInt(t1, 4);
        System.out.println(time);
        String timeStr = String.format("%06d-%06d", date, time);
        System.out.println(timeStr);

        Date d1 = DateUtil.parseDate("yyMMdd-HHmmss", timeStr);
        System.out.println(d1);


//        long datetime = bytes2Long(t1);
//        System.out.println(datetime);
//        long millions=new Long(date).longValue()*100;
//        millions = millions*60*60*24;
//        Calendar c=Calendar.getInstance();
//        c.set(Calendar.MINUTE,date);
//        System.out.println(""+c.getTime());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateString = sdf.format(c.getTime());
//        System.out.println(dateString);
        float lng = getFloat(t1, 8);
        System.out.println(lng);
        float lat = getFloat(t1, 12);
        System.out.println(lat);

        byte[] t2 = {(byte) 0x46, (byte) 0x61, 0x61, 0x00, 0x02, 0x55, 0x02, 0x00, (byte) 0x86, (byte) 0xcf, (byte) 0xf0, 0x42, 0x6e, 0x0b, (byte) 0xfc, 0x41};
        StringBuilder sb = new StringBuilder();
        String tmp;

        tmp = format("%c", t2[0]);
        sb.append(tmp);


        tmp = format("%02x", t2[1]);
        sb.append(tmp);
        tmp = format("%02x", t2[2]);
        sb.append(tmp);
        tmp = format("%02x", t2[3]);
        sb.append(tmp);
        tmp = format("%02x", t2[4]);
        sb.append(tmp);
        tmp = format("%02x", t2[5]);
        sb.append(tmp);

        System.out.println(sb.toString());


    }


    @Test
    public void testEncode() {
        String paramStr = "{\"sbbh\":\"61000255\",\"jd\":\"118.182993\",\"wd\":\"36.490714\",\"bpsj\":\"2018-08-01 16:23:01\",\"bprysfz\":\"123456789123456789\",\"uid\":\"61000001028324,61000000916433,61000000942128\",\"xmbh\":\"370100X15040023\",\"htid\":\"370101318060002\"}";
        System.out.println(paramStr);
        byte[] paramArr = DES3Utils.encryptMode(paramStr.getBytes(), DES3Utils.PASSWORD_CRYPT_KEY);
        if (paramArr == null) {
            System.out.println("paramarr is null");
            return;
        } else {
            System.out.println(new String(paramArr));
            byte[] decode1 = Base64Utils.getEncodeBytes(paramArr);
            System.out.println(new String(decode1));
        }

    }


    @Test
    public void detDecode() {
        String detListStr = " [{\"acCode\":\"AvBs0A==\",\"chipID\":\"00000000000000\",\"code\":\"AQDGVtgPPQgCAkTeAA6CAAAA\",\"detCode\":\"6180202D22214\",\"extId\":\"PQgCAkTeAA4=\",\"ids\":\"xlbYDw==\",\"num\":1,\"relay\":130,\"status\":0,\"time\":1552625815590,\"uid\":\"6100000fd856c6\",\"valid\":true},{\"acCode\":\"QF6TKQ==\",\"chipID\":\"00000000000000\",\"code\":\"AgDIVtgPPQgCAkTeABCqAAAA\",\"detCode\":\"6180202D22216\",\"extId\":\"PQgCAkTeABA=\",\"ids\":\"yFbYDw==\",\"num\":2,\"relay\":170,\"status\":0,\"time\":1552625816024,\"uid\":\"6100000fd856c8\",\"valid\":true},{\"acCode\":\"+0IaOA==\",\"chipID\":\"00000000000000\",\"code\":\"AwDJVtgPPQgCAkTeABG+AAAA\",\"detCode\":\"6180202D22217\",\"extId\":\"PQgCAkTeABE=\",\"ids\":\"yVbYDw==\",\"num\":3,\"relay\":190,\"status\":0,\"time\":1552625816540,\"uid\":\"6100000fd856c9\",\"valid\":true},{\"acCode\":\"NmeBCg==\",\"chipID\":\"00000000000000\",\"code\":\"BADKVtgPPQgCAkTeABLSAAAA\",\"detCode\":\"6180202D22218\",\"extId\":\"PQgCAkTeABI=\",\"ids\":\"ylbYDw==\",\"num\":4,\"relay\":210,\"status\":0,\"time\":1552625816986,\"uid\":\"6100000fd856ca\",\"valid\":true},{\"acCode\":\"jXsIGw==\",\"chipID\":\"00000000000000\",\"code\":\"BQDLVtgPPQgCAkTeABPmAAAA\",\"detCode\":\"6180202D22219\",\"extId\":\"PQgCAkTeABM=\",\"ids\":\"y1bYDw==\",\"num\":5,\"relay\":230,\"status\":0,\"time\":1552625817493,\"uid\":\"6100000fd856cb\",\"valid\":true},{\"acCode\":\"yzRYlA==\",\"chipID\":\"00000000000000\",\"code\":\"BgDgVtgPPQgCAkTeACj6AAAA\",\"detCode\":\"6180202D22240\",\"extId\":\"PQgCAkTeACg=\",\"ids\":\"4FbYDw==\",\"num\":6,\"relay\":250,\"status\":0,\"time\":1552625817779,\"uid\":\"6100000fd856e0\",\"valid\":true},{\"acCode\":\"cCjRhQ==\",\"chipID\":\"00000000000000\",\"code\":\"BwDhVtgPPQgCAkTeACkOAQAA\",\"detCode\":\"6180202D22241\",\"extId\":\"PQgCAkTeACk=\",\"ids\":\"4VbYDw==\",\"num\":7,\"relay\":270,\"status\":0,\"time\":1552625818087,\"uid\":\"6100000fd856e1\",\"valid\":true},{\"acCode\":\"vQ1Ktw==\",\"chipID\":\"00000000000000\",\"code\":\"CADiVtgPPQgCAkTeACoiAQAA\",\"detCode\":\"6180202D22242\",\"extId\":\"PQgCAkTeACo=\",\"ids\":\"4lbYDw==\",\"num\":8,\"relay\":290,\"status\":0,\"time\":1552625818415,\"uid\":\"6100000fd856e2\",\"valid\":true},{\"acCode\":\"BhHDpg==\",\"chipID\":\"00000000000000\",\"code\":\"CQDjVtgPPQgCAkTeACs2AQAA\",\"detCode\":\"6180202D22243\",\"extId\":\"PQgCAkTeACs=\",\"ids\":\"41bYDw==\",\"num\":9,\"relay\":310,\"status\":0,\"time\":1552625818736,\"uid\":\"6100000fd856e3\",\"valid\":true},{\"acCode\":\"J0Z80g==\",\"chipID\":\"00000000000000\",\"code\":\"CgDkVtgPPQgCAkTeACxKAQAA\",\"detCode\":\"6180202D22244\",\"extId\":\"PQgCAkTeACw=\",\"ids\":\"5FbYDw==\",\"num\":10,\"relay\":330,\"status\":0,\"time\":1552625819058,\"uid\":\"6100000fd856e4\",\"valid\":true},{\"acCode\":\"nFr1ww==\",\"chipID\":\"00000000000000\",\"code\":\"CwDlVtgPPQgCAkTeAC1eAQAA\",\"detCode\":\"6180202D22245\",\"extId\":\"PQgCAkTeAC0=\",\"ids\":\"5VbYDw==\",\"num\":11,\"relay\":350,\"status\":0,\"time\":1552625819395,\"uid\":\"6100000fd856e5\",\"valid\":true},{\"acCode\":\"UX9u8Q==\",\"chipID\":\"00000000000000\",\"code\":\"DADmVtgPPQgCAkTeAC5yAQAA\",\"detCode\":\"6180202D22246\",\"extId\":\"PQgCAkTeAC4=\",\"ids\":\"5lbYDw==\",\"num\":12,\"relay\":370,\"status\":0,\"time\":1552625819815,\"uid\":\"6100000fd856e6\",\"valid\":true},{\"acCode\":\"6mPn4A==\",\"chipID\":\"00000000000000\",\"code\":\"DQDnVtgPPQgCAkTeAC+GAQAA\",\"detCode\":\"6180202D22247\",\"extId\":\"PQgCAkTeAC8=\",\"ids\":\"51bYDw==\",\"num\":13,\"relay\":390,\"status\":0,\"time\":1552625820102,\"uid\":\"6100000fd856e7\",\"valid\":true},{\"acCode\":\"E9GRCA==\",\"chipID\":\"00000000000000\",\"code\":\"DgDoVtgPPQgCAkTeADCaAQAA\",\"detCode\":\"6180202D22248\",\"extId\":\"PQgCAkTeADA=\",\"ids\":\"6FbYDw==\",\"num\":14,\"relay\":410,\"status\":0,\"time\":1552625820408,\"uid\":\"6100000fd856e8\",\"valid\":true},{\"acCode\":\"qM0YGQ==\",\"chipID\":\"00000000000000\",\"code\":\"DwDpVtgPPQgCAkTeADGuAQAA\",\"detCode\":\"6180202D22249\",\"extId\":\"PQgCAkTeADE=\",\"ids\":\"6VbYDw==\",\"num\":15,\"relay\":430,\"status\":0,\"time\":1552625820827,\"uid\":\"6100000fd856e9\",\"valid\":true}]";
        String proInfoStr = "{\"lgs\":{\"lg\":[{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E9\",\"fbh\":\"6180202D22249\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"A8CD1819\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E8\",\"fbh\":\"6180202D22248\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"13D19108\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E7\",\"fbh\":\"6180202D22247\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"EA63E7E0\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E6\",\"fbh\":\"6180202D22246\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"517F6EF1\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E5\",\"fbh\":\"6180202D22245\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"9C5AF5C3\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E4\",\"fbh\":\"6180202D22244\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"27467CD2\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E3\",\"fbh\":\"6180202D22243\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"0611C3A6\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E2\",\"fbh\":\"6180202D22242\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"BD0D4AB7\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E1\",\"fbh\":\"6180202D22241\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"7028D185\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856E0\",\"fbh\":\"6180202D22240\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"CB345894\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856CB\",\"fbh\":\"6180202D22219\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"8D7B081B\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856CA\",\"fbh\":\"6180202D22218\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"3667810A\"},{\"gzmcwxx\":\"0\",\"uid\":\"6100000FD856C9\",\"fbh\":\"6180202D22217\",\"yxq\":\"2019-03-18 09:16:49\",\"gzm\":\"FB421A38\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C8\",\"fbh\":\"6180202D22216\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C7\",\"fbh\":\"6180202D22215\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C6\",\"fbh\":\"6180202D22214\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C5\",\"fbh\":\"6180202D22213\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C4\",\"fbh\":\"6180202D22212\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C3\",\"fbh\":\"6180202D22211\",\"yxq\":\"\",\"gzm\":\"\"},{\"gzmcwxx\":\"2\",\"uid\":\"6100000FD856C2\",\"fbh\":\"6180202D22210\",\"yxq\":\"\",\"gzm\":\"\"}]},\"jbqys\":{\"jbqy\":[]},\"sbbhs\":[{\"sbbh\":\"61000255\"},{\"sbbh\":\"F9900909097\"},{\"sbbh\":\"F5300000223\"},{\"sbbh\":\"D0000005\"},{\"sbbh\":\"F530000104\"},{\"sbbh\":\"F6000000123\"},{\"sbbh\":\"F0000098\"},{\"sbbh\":\"F38000001234\"},{\"sbbh\":\"f3333445678\"},{\"sbbh\":\"f7788991011\"},{\"sbbh\":\"F9900000296\"},{\"sbbh\":\"F3800000215\"},{\"sbbh\":\"61000041\"},{\"sbbh\":\"F48000000523\"},{\"sbbh\":\"F5300001041\"},{\"sbbh\":\"f8886789053\"},{\"sbbh\":\"F6161000255\"},{\"sbbh\":\"SA0001\"},{\"sbbh\":\"SY02\"},{\"sbbh\":\"44171101\"},{\"sbbh\":\"123456\"},{\"sbbh\":\"111111111111111111111111\"},{\"sbbh\":\"15965935456i87\"},{\"sbbh\":\"SY03\"},{\"sbbh\":\"20190207\"},{\"sbbh\":\"SY001\"},{\"sbbh\":\"F11\"}],\"zbqys\":{\"zbqy\":[{\"zbqybj\":\"5000\",\"zbqymc\":\"河北测试\",\"zbqywd\":\"38.346694\",\"zbqssj\":null,\"zbqyjd\":\"114.728516\",\"zbjzsj\":null}]},\"sqrq\":\"2019-03-15 09:16:49\",\"cwxx\":\"0\"}";
        System.out.println(proInfoStr);
        ProInfoDto proInfoDto = JSON.parseObject(proInfoStr, ProInfoDto.class);

        List<Detonator> detList = JSON.parseArray(detListStr, Detonator.class);
//        System.out.println(detInfoDto);
//        byte[] data = {   0x53,0x0e,0x00, (byte) 0xe8,0x56, (byte) 0xd8,0x0f,0x3d,0x08,0x02,0x02,0x44, (byte) 0xde,0x00,0x30, (byte) 0x9a,
//                0x01,0x00,0x00,0x21};
//        BLEDevResp bleDevResp = new BLEDevResp(data);
//        Detonator detonator = new Detonator(bleDevResp.getData());
//        System.out.println(detonator);
        int unUsedDet = proInfoDto.getLgs().getLg().size();
        int unRegDet = 0;
        boolean isUnreg = false;
        List<Lg> lgs = proInfoDto.getLgs().getLg();
        for (Detonator det : detList) {

            isUnreg = false;
            for (Lg lg : lgs) {

                if (det.getDetCode().equalsIgnoreCase(lg.getFbh())
                        && det.getUid().equalsIgnoreCase(lg.getUid())) {
                    unUsedDet--;
                    if(unUsedDet<0){
                        unUsedDet = 0;
                    }
                    isUnreg = true;

                    if (SommerUtils.bytesToHexString(det.getAcCode()).equalsIgnoreCase(lg.getGzm())) {
                        System.out.println("三码合一");
                        System.out.println(lg);

                    }else{

                        System.out.println("双码合一");
                        System.out.println(lg);
                    }
                }
//                System.out.println(lg);
            }
            if(!isUnreg){
                unRegDet++;
            }
        }
        System.out.println("unRegDet:"+unRegDet+"unUsedDet:"+unUsedDet);

    }

    @Test
  public   void permission(){
        double latitude =25.59091466778714;
        double longitude= 118.05328889800047;
//          longitude:

        PermissibleZoneEntity permissibleZoneEntity = new PermissibleZoneEntity(5000,118.079058,25.559628);
        LocationUtil.LocationRange range = LocationUtil.getAround(permissibleZoneEntity.getLatitude(), permissibleZoneEntity.getLongitude(), permissibleZoneEntity.getRadius());
        if (latitude > range.getMinLat()
                && latitude< range.getMaxLat()
                && longitude> range.getMinLng()
                && longitude< range.getMaxLng()) {
            System.out.println("in ");
        }else {
            System.out.println("out");
        }
    }




    @Test
    public   void getDetonator(){
        byte[] data ={0x01,0x00, (byte) 0xd7, (byte) 0x85,0x24,0x18,0x3d,0x00,0x03,0x09,0x44,0x56,0x01,0x3f,0x01,0x00,
                0x00,0x00};
        Detonator detonator = new Detonator(data);
        System.out.println(detonator);
        DetUtil.getExtid(detonator.getDetCode());
    }
    String u1 = "{\"bprysfz\":\"533421198207090717\",\"bpsj\":\"2020-07-16 14:48:32\",\"dwdm\":\"5301034300004\",\"htid\":\"533421319120010\",\"jd\":\"99.895200\",\"sbbh\":\"F61A8200045\",\"uid\":\"6120A82F80B229,6120A82F80B23E,6120A82F80B23F,6120A82F80B239,6120A82F80B235,6120A82F80B231,6120A82F80B236,6120A82F80B23D,6120A82F80B23B,6120A82F80B227,6120A82F80B22A,6120A82F80B228,6120A82F80B22E,6120A82F80B232,6120A82F80B224,6120A82F80B237,6120A82F80B238,6120A82F80B23A,6120A82F80B22F,6120A82F80B22B,6120A82F80B225,6120A82F80B226,6120A82F80B230,6120A82F80B22C,6120A82F80B22D,6120A82F80B23C,6120A82F80B233,6120A82F80B234\",\"wd\":\"28.119450\",\"xmbh\":\"\"}";


    @Test
    public void getCrpCode() throws UnsupportedEncodingException {
//        ReportDto reportDto = new ReportDto();
//        Result result = reportDto.getRptEncode(u1);
//        if (!result.isSuccess()) {
//            System.out.println("数据编码出错：" + result.getMessage());
//
//        }
//      String  urlString = URLEncoder.encode(result.getCode(), "GBK");
//        System.out.println(urlString);
    }

    @Test
    public void decode()  {
       Detonator detonator = new Detonator();
        int fb = detonator.getDetonatorByFbh("6191128D46031");
        if(fb>0){
            System.out.println(detonator);
        }else {
            System.out.println("错误！");
        }

    }
>>>>>>> 806c842... 雷管组网
}