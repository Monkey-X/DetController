package com.etek.sommerlibrary.utils;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DES3Utils {
    // 定义加密算法
    private static final String Algorithm = "DESede";
    //    private static final String Algorithm = "DESede";
    // 加密密钥
    public static final String PASSWORD_CRYPT_KEY = "jadl12345678912345678912";
    public static final String CRYPT_KEY_FRONT = "jadl12345678901234";

    // 加密 src为源数据的字节数组
    public static byte[] encryptMode(byte[] src, String passwordCryptKey) {

        try {// 生成密钥
            SecretKey deskey = new SecretKeySpec(
                    build3Deskey(PASSWORD_CRYPT_KEY + passwordCryptKey), Algorithm);
            // 实例化cipher
            Cipher cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            return cipher.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解密函数
    public static byte[] decryptMode(byte[] src, String passwordCryptKey) {
        SecretKey deskey;
        try {
            deskey = new SecretKeySpec(( passwordCryptKey).getBytes(),
                    Algorithm);
            Cipher cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return cipher.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {

            e1.printStackTrace();

        } catch (javax.crypto.NoSuchPaddingException e2) {

            e2.printStackTrace();

        } catch (java.lang.Exception e3) {

            e3.printStackTrace();

        }

        return null;
    }

    // 根据字符串生成密钥24位的字节数组
    public static byte[] build3Deskey(String keyStr) throws Exception {
        byte[] key = new byte[24];
        byte[] temp = keyStr.getBytes("UTF-8");
        if (key.length > temp.length) {
            System.arraycopy(temp, 0, key, 0, temp.length);

        } else {
            System.arraycopy(temp, 0, key, 0, key.length);

        }
        return key;
    }
}