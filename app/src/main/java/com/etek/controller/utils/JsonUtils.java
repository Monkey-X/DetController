package com.etek.controller.utils;


import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.DetonatorEntity;
import com.etek.controller.persistence.entity.ReportEntity;
import com.etek.controller.persistence.gen.DetonatorEntityDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 模拟请求的json串
 */
public class JsonUtils {
    //发送检测报告ProjectInfo,   响应{"success":0,"cwxx":"成功"}
    public static String OnLineAuthorizeActivity2ProjectInfo = "{id=1, proCode='370100X15040023', proName='燃一测试项目', companyCode='3701004200003', companyName='济南黄河爆破工程有限责任公司', contractCode='370101318060002', contractName='燃一测试合同', fileSn='45', createTime=Mon Nov 02 17:57:48 GMT+08:00 2020, applyDate=Mon Nov 02 17:57:48 GMT+08:00 2020, status=1, isSelect=false, detonatorList=null, forbiddenZoneList=null, controllerList=null, permissibleZoneList=null}";

    /**
     * 模拟雷管数据
     * CheckoutDetailActivity2【91】
     */
    public static void monitDetonatorEntity(long proId) {
        List<DetonatorEntity> list = DBManager.getInstance().getDetonatorEntityDao().queryBuilder().where(DetonatorEntityDao.Properties.ProjectInfoId.eq(proId)).list();
        if (list != null && list.size() == 0){
            for (int i = 0; i < 8; i++) {
                DetonatorEntity detonatorEntity = new DetonatorEntity();
                detonatorEntity.setCode("6170725D02064");
                detonatorEntity.setUid("61000001028324");
                detonatorEntity.setStatus(i%2);
                detonatorEntity.setProjectInfoId(proId);
                detonatorEntity.setRelay("100" + i);
                detonatorEntity.setHolePosition("孔" + i);
                DBManager.getInstance().getDetonatorEntityDao().insert(detonatorEntity);
            }
        }
    }

    /**
     * 模拟上报页面数据
     * ReportActivity2【56】
     */
    public static void monitReportEntity(){
        //模拟数据，显示页面
        List<ReportEntity> reportEntities = DBManager.getInstance().getReportEntityDao().loadAll();
        if (reportEntities != null && reportEntities.size() == 0){
            for (int i = 0; i < 10; i++) {
                ReportEntity reportEntity = new ReportEntity();
                reportEntity.setCompanyCode("111111111111");
                reportEntity.setContractId("1");
                reportEntity.setControllerId("6100025" + i);
                reportEntity.setLatitude(121.511034);
                reportEntity.setLongitude(31.239241);
                reportEntity.setStatus(i%2);
                reportEntity.setBlastTime(new Date());
                DBManager.getInstance().getReportEntityDao().insert(reportEntity);
            }
        }
    }

    /**
     * 模拟上报雷管数据
     * CheckoutDetailActivity2【91】
     */
    public static List<Detonator> monitDetonator(){
        List<Detonator> list = new ArrayList();
        list.clear();
        for (int i = 0; i < 10; i++) {
            Detonator detonator = new Detonator();
            detonator.setChipID("123456");
            detonator.setDetCode("9876543");
            detonator.setUid("44444444");
            detonator.setZbDetCode("5555555555");
            detonator.setStatusName(i%2 + "");
            detonator.setNum(i);
            list.add(detonator);
        }
        return list;
    }
}
