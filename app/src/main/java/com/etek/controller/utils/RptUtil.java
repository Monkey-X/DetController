package com.etek.controller.utils;

import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.Base64Utils;
import com.etek.sommerlibrary.utils.DES3Utils;

public class RptUtil {

    public static Result getRptEncode(String rptJson){
        byte[] encode ;
        String urlString;
        try {
            byte[]  paramArr = DES3Utils.encryptMode(rptJson.getBytes(),DES3Utils.PASSWORD_CRYPT_KEY);
            encode = Base64Utils.getEncodeBytes(paramArr);
            return Result.successOf(new String(encode).trim());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    public static Result getRptDecode(String rspStr){

        try {
            byte[] decode1 = Base64Utils.getDecodeBytes(rspStr);
            byte[] decode2 = DES3Utils.decryptMode(decode1, DES3Utils.PASSWORD_CRYPT_KEY );
            return Result.successOf(new String(decode2).trim());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    public static Result getRptDecode(String rspStr,String xlh){

        try {
            byte[] decode1 = Base64Utils.getDecodeBytes(rspStr);
            byte[] decode2 = DES3Utils.decryptMode(decode1, DES3Utils.CRYPT_KEY_FRONT+xlh );
            return Result.successOf(new String(decode2).trim());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
