package com.etek.controller.yunnan.util;

import com.etek.controller.yunnan.bean.OfflineAuthBombBean;
import com.etek.controller.yunnan.enetity.YunnanAuthBobmEntity;

import java.util.List;

/**
 * 数据库存储数据的转换
 */
public class DataTransformUtil {


    public static YunnanAuthBobmEntity tranToEntity(OfflineAuthBombBean offlineAuthBombBean){
        YunnanAuthBobmEntity yunnanAuthBobmEntity = new YunnanAuthBobmEntity();
        yunnanAuthBobmEntity.setFileId(offlineAuthBombBean.getId());
        yunnanAuthBobmEntity.setMc(offlineAuthBombBean.getMc());
        yunnanAuthBobmEntity.setBpcs(offlineAuthBombBean.getBpcs());
        yunnanAuthBobmEntity.setJssj(offlineAuthBombBean.getJssj());
        yunnanAuthBobmEntity.setKssj(offlineAuthBombBean.getKssj());
        yunnanAuthBobmEntity.setZbbj(offlineAuthBombBean.getZbbj());
        String lgmStr = listToStr(offlineAuthBombBean.getLgm());
        yunnanAuthBobmEntity.setLgmStr(lgmStr);

        String qbqStr = listToStr(offlineAuthBombBean.getQbq());
        yunnanAuthBobmEntity.setQbqStr(qbqStr);

        List<List<Double>> zbqy = offlineAuthBombBean.getZbqy();
        yunnanAuthBobmEntity.setZbqyStr(qbqyStr(zbqy));
        yunnanAuthBobmEntity.setDate(System.currentTimeMillis());
        yunnanAuthBobmEntity.setLgmCount(offlineAuthBombBean.getLgmCount());
        yunnanAuthBobmEntity.setQbqCount(offlineAuthBombBean.getQbqCount());
        yunnanAuthBobmEntity.setZbqyCount(offlineAuthBombBean.getZbqyCount());
        return yunnanAuthBobmEntity;
    }

    public static String listToStr(List<String> listData){
        if (listData ==null || listData.size() == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < listData.size(); i++) {
            stringBuilder.append(listData.get(i));
            if (i != listData.size()-1) {
                stringBuilder.append("/");
            }
        }
        return stringBuilder.toString();
    }

    public static String qbqyStr(List<List<Double>> zbqy){
        if (zbqy == null || zbqy.size() ==0) {
            return "";
        }

        StringBuilder stringBuilder1 = new StringBuilder();
        for (int i = 0; i < zbqy.size(); i++) {
            List<Double> doubles = zbqy.get(i);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i1 = 0; i1 < doubles.size(); i1++) {
                stringBuilder.append(doubles.get(i1));
                if (i1 != doubles.size()-1) {
                    stringBuilder.append("&");
                }
            }
            String s = stringBuilder.toString();

            stringBuilder1.append(s);
            if (i != zbqy.size()-1) {
                stringBuilder1.append("/");
            }
        }
        return stringBuilder1.toString();
    }
}
