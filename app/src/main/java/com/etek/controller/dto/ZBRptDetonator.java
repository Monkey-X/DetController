package com.etek.controller.dto;



import java.util.Date;


public class ZBRptDetonator {
    private static final String TAG ="ZBRptDetonator" ;

    long id;

    int chipID;

    byte[] chipCode;

    String detonatorCode;

    int relayTime;


    private Date time;


    private boolean upload ;



    byte[] code;

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public String getDetonatorCode() {
        return detonatorCode;
    }

    public void setDetonatorCode(String detonatorCode) {
        this.detonatorCode = detonatorCode;
    }

    public byte[] getCode() {
        return code;
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getChipID() {
        return chipID;
    }

    public void setChipID(int chipID) {
        this.chipID = chipID;
    }

    public byte[] getChipCode() {
        return chipCode;
    }

    public void setChipCode(byte[] chipCode) {
        this.chipCode = chipCode;
    }

    public int getRelayTime() {
        return relayTime;
    }

    public void setRelayTime(int relayTime) {
        this.relayTime = relayTime;
    }


//    public long getDetonatorCode() {
//        return detonatorCode;
//    }

//    public void setDetonatorCode(long detonatorCode) {
//        this.detonatorCode = detonatorCode;
//    }

    public ZBRptDetonator() {
    }

    public ZBRptDetonator(byte[] codes) {
//        String bt = "0100603502003d06061741e70363f4010000";
//         data = Utils.hexStringToBytes(bt);
        chipID = (codes[1] * 256 + codes[0])&0xffff;

        byte[] d2 = new byte[4];
        d2[0] = codes[5];
        d2[1] = codes[4];
        d2[2] = codes[3];
        d2[3] = codes[2];
        chipCode = d2;

        detonatorCode = getDetonatorCodeStr(codes);

//        XLog.v(TAG,"det:"+detonatorCode);
        relayTime = (int) ((codes[14] & 0xFF)
                | ((codes[15] & 0xFF) << 8) | ((codes[16] & 0xFF) << 16) | ((codes[17] & 0xFF) << 24));
        time = new Date();

        code = codes;
        upload = false;

    }

//    public void setDetonatorFromArray(byte[] data){
//
////        chipCode = data[1]<<8+data[0];
//        chipID = data[5]<<24+data[4]<<16+ data[3]<<8+data[2];
////        detonatorCode = data[5]*16777216+data[6]*65536+ data[7]*256+data[8];
////        detonatorCode =data[13]<<56+data[12]<<48+ data[11]<<40+data[10]<<32+data[9]<<24+data[8]<<16+ data[7]<<8+data[6];
//        relayTime =  data[17]<<24+data[16]<<16+ data[15]<<8+data[14];
//        time = new Date();
//    }

    @Override
    public String toString() {
        return "Detonator{" +
                "id=" + id +
                ", chipID='" + chipID + '\'' +
                ", chipCode='" + chipCode + '\'' +
                ", detonatorCode='" + detonatorCode + '\'' +
                ", relayTime='" + relayTime + '\'' +

                ", time=" + time +

                '}';
    }

    public String getDetonatorCodeStr(byte[] code) {
//        XLog.v("test",bytesToHexString(src));
        StringBuilder sb = new StringBuilder("");
        if (code == null || code.length <= 0) {
            return null;
        }
//        int i= src[6];
        int time;
        time =code[6];
        if(time>100){
            time= time%100;
        }
        String str = String.format("%02d",time);

//        System.out.println("1:"+str);
        sb.append(str);



        time =code[7];
        if(time>10){
            time= time%10;
        }

        str = String.format("%01d", time);
//        System.out.println("2:"+str);
        sb.append(str);
        time =code[8];
        if(time>100){
            time= time%100;
        }
        str = String.format("%02d", time);
//        System.out.println("3:"+str);
        sb.append(str);
        time =code[9];
        if(time>100){
            time= time%100;
        }
        str = String.format("%02d", time);
//        System.out.println("4:"+str);
        sb.append(str);
//        sb.append(' ');
//        sb.append(' ');
        str = String.format("%c", code[10]);
//        System.out.println("5:"+str);
        sb.append(str);
//        sb.append(' ');
//        int temp = src[12];
//        XLog.v("test",""+temp);
//        temp = temp*256+(int)src[11];
//        XLog.v("test",""+temp);
//        System.out.println(source[12] +" "+source[11]+" "+source[13]);
        int temp = (int) (((code[11] & 0xFF)
                | (code[12] & 0xFF) << 8));


       if(temp>1000){
           temp = temp%1000;
       }
//        System.out.println(temp);
        str = String.format("%03d", temp);

//        System.out.println("6:"+str);

        sb.append(str);

        temp = code[13] ;


        if(temp>100){
            temp = temp%100;
        }
//        System.out.println(temp);
        str = String.format("%02d", temp);

//        System.out.println("6:"+str);

        sb.append(str);


//        temp = (int) ((source[13] & 0xFF));
////        sb.append(' ');
//        str = String.format("%02d",temp);
////        XLog.v("test",""+src[13]);
//        sb.append(str);
//        XLog.v("test",sb.toString());
        return sb.toString();
    }
}
