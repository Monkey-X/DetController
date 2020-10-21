package com.etek.controller.dto;

import com.etek.controller.utils.SommerUtils;

public class ReceiverData {
    byte cmd;

    byte[] data;

    int sum;

    boolean isValid;

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


    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public ReceiverData(byte[] arrs) {
        if(!isRecValid(arrs)){
            return;
        }
        int len = arrs.length;
        cmd = arrs[0];
        data = new byte[len-2];
        System.arraycopy(arrs,1,data,0,len-2);
        sum = (int)((arrs[len-2]<<8)+arrs[len-1]);
        int sum = arrs[len-1]& 0xff;
        sum += (int)(arrs[len-2]<<8);
    }

    public  boolean isRecValid(byte[] recs){
        if(recs==null||recs.length==0){
            this.isValid = false;
            return false;
        }

        int recCrc = SommerUtils.ChkSum16Generate(recs,recs.length-2);
        int sum = recs[recs.length-1]& 0xff;
         sum += (int)(recs[recs.length-2]<<8);
        if(recCrc==sum){
            this.isValid = true;
            return true;
        }else {
            this.isValid = false;
            return  false;
        }
    }


}
