package com.etek.controller.entity;

import com.etek.controller.common.Globals;


import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Firmware implements Serializable {


    int len;
    byte[] source;
    int pNum;
    String name;
    String path;
    Map<Integer,byte[]> pkts = new HashMap();



    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }

    public int getpNum() {
        return pNum;
    }

    public void setpNum(int pNum) {
        this.pNum = pNum;
    }

    public Map<Integer, byte[]> getPkts() {
        return pkts;
    }

    public void setPkts(Map<Integer, byte[]> pkts) {
        this.pkts = pkts;
    }



    public Firmware(byte[] source) {
        boolean isOdd = false;
        int pageSize = 0;
        if(Globals.type==1) {
             pageSize = 128;
        }else {
            pageSize = 16;
        }
        this.source = source;
        this.len = source.length;
        if(source.length%pageSize!=0){
            this.pNum = source.length/pageSize+1;
            isOdd = true;
        }else{
            this.pNum = source.length/pageSize;
        }

        for(int i=0;i<pNum;i++){

            int len = pageSize;
            if(isOdd&&i==(pNum-1)){
                len =  source.length%pageSize;
            }
            byte[] pkt = new byte[pageSize];
            Arrays.fill(pkt,(byte)0xff);
            System.arraycopy(source, i*pageSize, pkt, 0, len);// 复制已读取数据
            pkts.put(i,pkt);

        }

    }

    @Override
    public String toString() {
        return "Firmware{" +
                "len=" + len +
//                ", source=" + Arrays.toString(source) +
                ", pNum=" + pNum +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
//                ", pkts=" + pkts +
                '}';
    }
}
