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
//            XLog.d("old:"+new String(encode));
//            urlString = URLEncoder.encode(new String(encode), "GBK");
//            XLog.d("new:"+urlString);
            return Result.successOf(new String(encode).trim());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
//        return Result.successOf(new String(encode));
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
}
