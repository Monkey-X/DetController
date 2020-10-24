<<<<<<< HEAD
package com.etek.sommerlibrary.utils;

import android.util.Base64;

public class Base64Utils {

    public static String  getEncode(String ori){
        return    Base64.encodeToString(ori.getBytes(), Base64.DEFAULT);
    }
    public static byte[] getEncodeBytes(byte[] arrBase64){
        return  Base64.encode(arrBase64, Base64.DEFAULT);
    }
    public static String getDecode(String strBase64){
        return  new String(Base64.decode(strBase64.getBytes(), Base64.DEFAULT));
    }

    public static byte[] getDecodeBytes(String strBase64){
        return  Base64.decode(strBase64.getBytes(), Base64.DEFAULT);
    }
}
=======
package com.etek.sommerlibrary.utils;

import android.util.Base64;

public class Base64Utils {

    public static String  getEncode(String ori){
        return    Base64.encodeToString(ori.getBytes(), Base64.DEFAULT);
    }
    public static byte[] getEncodeBytes(byte[] arrBase64){
        return  Base64.encode(arrBase64, Base64.DEFAULT);
    }
    public static String getDecode(String strBase64){
        return  new String(Base64.decode(strBase64.getBytes(), Base64.DEFAULT));
    }

    public static byte[] getDecodeBytes(String strBase64){
        return  Base64.decode(strBase64.getBytes(), Base64.DEFAULT);
    }
}
>>>>>>> 806c842... 雷管组网
