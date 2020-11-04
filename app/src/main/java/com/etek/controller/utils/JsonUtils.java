package com.etek.controller.utils;


import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ProjectInfoEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 模拟请求的json串
 */
public class JsonUtils {

    /**
     * 手动添加雷管 校验
     */
    //模拟手动添加雷管验证时的json
    public static String  VerifyResult = "{\"dwdm\":\"3701004200003\",\"htid\":\"370101318060002\",\"jd\":\"118.182993\",\"sbbh\":\"61000255\",\"uid\":\"61000001028324\",\"wd\":\"36.490714\",\"xmbh\":\"370100X15040023\"}" ;
    //模拟服务器返回并解密后的数据
    public static String VerifyResultResponse = "{\"lgs\":{\"lg\":[{\"gzmcwxx\":\"0\",\"uid\":\"61000001028324\",\"fbh\":\"6170725D02064\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"9C02343B\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000000916433\",\"fbh\":\"6170828D00650\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"9C02343B\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000000942128\",\"fbh\":\"6170725D02021\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"9C02343B\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000000651187\",\"fbh\":\"6170725D02173\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"9C02343B\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000000917033\",\"fbh\":\"6170828D00632\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"1555DFB4\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000000909938\",\"fbh\":\"6170828D00653\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"6719F3F3\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000000491245\",\"fbh\":\"6170725D01651\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"C46554DE\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000000920802\",\"fbh\":\"6170828D01135\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"2E693B79\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000001138506\",\"fbh\":\"6170725D02007\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"CC6C92CA\"},{\"gzmcwxx\":\"0\",\"uid\":\"61000000913776\",\"fbh\":\"6170828D00649\",\"yxq\":\"2018-08-25 16:53:49\",\"gzm\":\"86A7D7B5\"}]},\"jbqys\":{\"jbqy\":[]},\"sbbhs\":[{\"sbbh\":\"20170714\"},{\"sbbh\":\"61000255\"},{\"sbbh\":\"F5300000223\"},{\"sbbh\":\"D0000005\"},{\"sbbh\":\"F530000104\"},{\"sbbh\":\"F6000000123\"},{\"sbbh\":\"F0000098\"},{\"sbbh\":\"F38000001234\"},{\"sbbh\":\"f3333445678\"},{\"sbbh\":\"f7788991011\"},{\"sbbh\":\"F5300001041\"}],\"zbqys\":{\"zbqy\":[{\"zbqybj\":\"5000\",\"zbqymc\":\"铜仁站\",\"zbqywd\":\"27.795399\",\"zbqssj\":null,\"zbqyjd\":\"109.223797\",\"zbjzsj\":null}]},\"sqrq\":\"2018-08-22 16:53:49\",\"cwxx\":\"0\"}";


    /**
     * 在线授权  校验并发送检测报告
     */
    //模拟在线授权json,   响应[系统出现异常，请与管理员联系...]
    public static String OnLineAuthorizeActivity2Json = "{\"dwdm\":\"3701004200003\",\"htid\":\"370101318060002\",\"jd\":\"121.510362\",\"sbbh\":61000255,\"uid\":\"61000000920802,61000001138506,61000000913776,61000000916433,61000000917033\",\"wd\":\"31.239046\",\"xmbh\":370100X15040023\"}";
    //发送检测报告ProjectInfo,   响应{"success":0,"cwxx":"成功"}
    public static String OnLineAuthorizeActivity2ProjectInfo = "{id=1, proCode='370100X15040023', proName='燃一测试项目', companyCode='3701004200003', companyName='济南黄河爆破工程有限责任公司', contractCode='370101318060002', contractName='燃一测试合同', fileSn='45', createTime=Mon Nov 02 17:57:48 GMT+08:00 2020, applyDate=Mon Nov 02 17:57:48 GMT+08:00 2020, status=1, isSelect=false, detonatorList=null, forbiddenZoneList=null, controllerList=null, permissibleZoneList=null}";

    //模拟在线授权页面展示的数据
    public static void projectInfo(){
        // 模拟项目信息数据
        List<ProjectInfoEntity> projectInfoEntities = DBManager.getInstance().getProjectInfoEntityDao().loadAll();
        if (projectInfoEntities != null && projectInfoEntities.size() == 0) {
            for (int i = 0; i < 5; i++) {
                ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity((long) i, "370100X1504002" + i, "燃一测试项目" + i, "370100420000" + i, "济南黄河爆破工程有限责任公司" + i, "37010131806000" + i, "燃一测试合同" + i, "45" + i, new Date(), new Date(), i % 2, true);
                DBManager.getInstance().getProjectInfoEntityDao().insert(projectInfoEntity);
            }
        }
    }

    private static List<Detonator> data = new ArrayList<>();
    public static DetController detController(){
        //模拟雷管数据
        data.clear();
        DetController detController = new DetController();
        for (int i = 0; i < 4; i++) {
            Detonator detonator = new Detonator();
            detonator.setZbDetCode("2222222222");
            detonator.setChipID("3333333333");
            detonator.setTime(new Date());
            detonator.setUid("4444444444");
            detonator.setValid(true);
            detonator.setStatus(i%2);
            detonator.setType(2);
            detonator.setDetCode("55555555" + i);
            detController.addDetonator(detonator);
            data.add(detonator);
        }
        detController.setDetList(data);
        return detController;
    }
}
