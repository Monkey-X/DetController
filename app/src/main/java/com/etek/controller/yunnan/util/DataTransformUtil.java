package com.etek.controller.yunnan.util;

import android.text.TextUtils;

import com.etek.controller.dto.LocationBean;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.utils.DateStringUtils;
import com.etek.controller.yunnan.bean.OfflineAuthBombBean;
import com.etek.controller.yunnan.bean.YunDetInfoBean;
import com.etek.controller.yunnan.bean.YunUploadBean;
import com.etek.controller.yunnan.enetity.YunnanAuthBobmEntity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 云南数据和 数据库存储数据的转换
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
            if (i != zbqy.size() - 1) {
                stringBuilder1.append("/");
            }
        }
        return stringBuilder1.toString();
    }

   // 云南数据获取准爆区域
    public static List<LocationBean> getQyList(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }

        String[] split = data.split("/");
        List<LocationBean> locations = new ArrayList<>();
        if (split != null && split.length > 0) {
            for (int i = 0; i < split.length; i++) {
                String locationStr = split[i];
                Logger.d(locationStr);
                String[] split1 = locationStr.split("&");
                double longitude = Double.parseDouble(split1[0]);
                double latitude = Double.parseDouble(split1[1]);
                LocationBean locationBean = new LocationBean(longitude, latitude);
                locations.add(locationBean);
            }
        }
        return locations;
    }

    // 云南数据的转换
    public static List<String> strToList(String dataStr) {
        if (TextUtils.isEmpty(dataStr)) {
            return null;
        }

        String[] split = dataStr.split("/");

        List<String> strings = Arrays.asList(split);
        return strings;
    }

    // 获取上报给云南的数据
    public static YunUploadBean getYunUploadData(PendingProject projectInfoEntity, List<ProjectDetonator> detonatorEntityList){
        YunUploadBean yunUploadBean = new YunUploadBean();
        yunUploadBean.setId(projectInfoEntity.getFileId());
        String dateString = DateStringUtils.getDateString(projectInfoEntity.getLocationTime());
        yunUploadBean.setDwsj(dateString);
        yunUploadBean.setQbq(projectInfoEntity.getControllerId());
        List<Double> locations = new ArrayList<>();
        locations.add(projectInfoEntity.getLongitude());
        locations.add(projectInfoEntity.getLatitude());
        yunUploadBean.setZbqy(locations);
        yunUploadBean.setQssj(projectInfoEntity.getCreateTime());

        List<YunDetInfoBean> yunDetInfoBeans = new ArrayList<>();
        for (ProjectDetonator projectDetonator : detonatorEntityList) {
            YunDetInfoBean yunDetInfoBean = new YunDetInfoBean();
            yunDetInfoBean.setLgm(projectDetonator.getCode());
            yunDetInfoBean.setUID(projectDetonator.getUid());
            yunDetInfoBean.setZt("1");
            yunDetInfoBeans.add(yunDetInfoBean);
        }

        yunUploadBean.setLgm(yunDetInfoBeans);

        return yunUploadBean;
    }
}
