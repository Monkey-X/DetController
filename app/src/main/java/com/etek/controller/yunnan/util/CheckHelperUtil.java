package com.etek.controller.yunnan.util;

import android.text.TextUtils;
import android.util.Log;

import com.etek.controller.dto.LocationBean;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.utils.DateStringUtils;
import com.etek.controller.utils.LocationUtil;
import com.etek.controller.yunnan.enetity.YunnanAuthBobmEntity;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Random;

/**
 * 项目进行规则检查时帮助类
 */
public class CheckHelperUtil {

    private static final String TAG="CheckHelperUtil";
    /**
     * 云南项目获取比对的离线准爆文件
     *
     * @param list
     * @param projectDetonatorList
     * @return
     */
    public static YunnanAuthBobmEntity getAuthDownloadFile(List<YunnanAuthBobmEntity> list, List<ProjectDetonator> projectDetonatorList) {
        for (YunnanAuthBobmEntity yunnanAuthBobmEntity : list) {
            String lgmStr = yunnanAuthBobmEntity.getLgmStr();
            List<String> detStrings = DataTransformUtil.strToList(lgmStr);
            if (detStrings != null && detStrings.size() != 0) {
                for (ProjectDetonator projectDetonator : projectDetonatorList) {
                    String code = projectDetonator.getCode();
                    if (detStrings.contains(code)) {
                        return yunnanAuthBobmEntity;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 云南项目检查起爆器是否存在
     *
     * @param strControllerId
     * @param authDownloadFile
     */
    public static boolean checkController(String strControllerId, YunnanAuthBobmEntity authDownloadFile) {
        String qbqStr = authDownloadFile.getQbqStr();
        List<String> controllerList = DataTransformUtil.strToList(qbqStr);
        if (controllerList != null && controllerList.size() != 0) {
            if (controllerList.contains("000000000") || controllerList.contains(strControllerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 云南项目检查时间的有效期
     *
     * @param authDownloadFile
     */
    public static boolean checkUsefulDate(YunnanAuthBobmEntity authDownloadFile) {
        String kssj = authDownloadFile.getKssj();
        String jssj = authDownloadFile.getJssj();
        Logger.d("kssj= " + kssj + " jssj =" + jssj);
        long ksTime = DateStringUtils.getDateLong(kssj);
        long jsTime = DateStringUtils.getDateLong(jssj);
        long currTime = System.currentTimeMillis();
        if (currTime >= ksTime && currTime <= jsTime) {
            return true;
        }
        return false;
    }

    /**
     * 检查定位信息
     *
     * @param authDownloadFile
     * @param cacheLongitude
     * @param cacheLatitude
     * @param pendingProject
     */
    public static boolean checkLocation(YunnanAuthBobmEntity authDownloadFile, Double cacheLongitude, Double cacheLatitude, PendingProject pendingProject) {
        String zbqyStr = authDownloadFile.getZbqyStr();
        if (TextUtils.isEmpty(zbqyStr)) {
            return true;
        }
        List<LocationBean> qyList = DataTransformUtil.getQyList(zbqyStr);
        if (qyList == null || qyList.size() == 0) {
            return true;
        }

        // 缺省认为准爆报警为10km
        if(0==authDownloadFile.getZbbj())
            authDownloadFile.setZbbj(10);

        Log.d(TAG,"准爆半径：" + authDownloadFile.getZbbj());
        for (LocationBean locationBean : qyList) {
            LocationUtil.LocationRange range = LocationUtil.getAround(locationBean.getLatitude(), locationBean.getLongitude(), authDownloadFile.getZbbj());

            Log.d(TAG,String.format("缓存[%.4f,%.4f], 范围[%.4f,%.4f]",
                    cacheLongitude, cacheLatitude,
                    locationBean.getLongitude(), locationBean.getLatitude()));
            if (cacheLatitude > range.getMinLat()
                    && cacheLatitude < range.getMaxLat()
                    && cacheLongitude > range.getMinLng()
                    && cacheLongitude < range.getMaxLng()) {
                //  离线：缓存准爆区域
                pendingProject.setLongitude(getEmuLongLatitude(locationBean.getLongitude()));
                pendingProject.setLatitude(getEmuLongLatitude(locationBean.getLatitude()));
                return true;
            }
        }
        return false;
    }

    //  修改经纬度后4,5位
    private static Double getEmuLongLatitude(Double dval) {
        int n0 = (int) (dval * 1000);
        n0 = n0 * 100;

        Random random = new Random();
        int ends = random.nextInt(99);
        n0 = n0 + ends;

        Double d = (n0 * 1.00) / (1000 * 100);
        return d;
    }

    // 检查项目中的雷管是否在下载的清单文件中
    public static String checkDetFile(List<String> dets, List<ProjectDetonator> projectDetonatorList) {

        for (ProjectDetonator projectDetonator : projectDetonatorList) {
            String detCode = projectDetonator.getCode();
            if (!dets.contains(detCode)) {
                return detCode;
            }
        }
        return "";
    }
}
