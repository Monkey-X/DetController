package com.etek.controller.activity.project.manager;

import com.tencent.mmkv.MMKV;

/**
 * 存储临时文件的工具类
 */
public class SpManager {

    private MMKV mmkv;

    private SpManager() {
        mmkv = MMKV.defaultMMKV();
    }

    public static SpManager getIntance() {
        return SingletonHoler.sIntance;
    }

    private static class SingletonHoler {
        private static final SpManager sIntance = new SpManager();
    }

    public void saveSpBoolean(String key, boolean value) {
        mmkv.encode(key, value);
    }

    public void saveSpString(String key, String value) {
        mmkv.encode(key, value);
    }

    public void saveSpInt(String key, int value) {
        mmkv.encode(key, value);
    }


    public boolean getSpBoolean(String key){
       return mmkv.decodeBool(key);
    }

    public String getSpString(String key){
       return mmkv.decodeString(key,"");
    }

    public int getSpInt(String key){
        return mmkv.decodeInt(key);
    }
}
