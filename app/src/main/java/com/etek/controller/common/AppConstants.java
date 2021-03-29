package com.etek.controller.common;



public  class AppConstants {

    public static final   String BaiduAddress = "http://api.map.baidu.com/geocoder/v2/";

    public final static String DET_DETAIL="http://222.191.229.234:1018/api/DetMsg/GetDetMsgInfo";
    public final static String DET_APP= "http://222.191.229.234:1018/api/DET/GET?id=";

    public final static String ZHONGBAO_HTTP= "113.140.1.135";
    public final static int ZHONGBAO_PORT= 9903;

    public final static String ETEK_HTTP= "222.191.229.234";
    public final static int ETEK_PORT= 1089;

    //  中爆黔南
    public final static String QIONGNAN_HTTP= "113.140.1.135";
    public final static int QIONGNAN_PORT= 9903;

    //  中爆黔东南
    public final static String QIONGDONGNAN_HTTP= "113.140.1.137";
    public final static int QIONGDONGNAN_PORT= 8608;

    //  中爆广西
    public final static String GUANGXI_HTTP= "119.29.111.172";
    public final static int GUANGXI_PORT= 6088;

    //  中爆贵阳
    public final static String GUIYANG_HTTP= "119.29.111.172";
    public final static int GUIYANG_PORT= 6089;

    // 储存下载的Id
    public  static final String DOWNLOAD_APK_ID_PREFS = "download_apk_id_prefs";

    public final static String HostIp="www.laputatotoru.com";

    public final static String ProjectFileDownload = "/mbdzlgtxzx/servlet/DzlgLyffJsonServlert";

    public final static String ProjectReport = "/mbdzlgtxzx/servlet/DzlgSysbJsonServlert";

    public final static String OfflineDownload = "/mbdzlgtxzx/servlet/DzlgMmlxxzJsonServlert";

    public final static String OnlineDownload = "/mbdzlgtxzx/servlet/DzlgMmxzJsonServlert";

    public final static String ProjectReportTest = "/api/DET/Post";             //  【数据上报】（模拟），当丹灵和中爆同时关闭时
    public final static String CheckoutReport = "/api/DET/PostFromBody";        //  【离线授权（授权下载）】
    public final static String WhiteBlackList = "/api/DET";
    public final static String DETUnCheck = "/api/DETUnCheck/PostFromBody";     //  【离线/在线检查】
    public final static String DETBACKUP = "/api/DetBackUp/Post";               //  【数据上报（正式）】，丹灵和中爆其中之一打开或全打开时

    public final static String DanningServer = "http://qq.mbdzlg.com";
    public final static String DanningTestServer = "http://test.mbdzlg.com";

    public final static String ETEKTestServer = "http://222.191.229.234:1018";

    public final static String APPNAME = "detonator";

    /** Empty String for comparisons **/
    public   static final String empty = "";

    public static final String LONGMAO_UPLOAD = "http://129.211.7.213:9030/fileUpload";

    public static final String UPLOAD_LOG ="http://47.117.132.63:6066/logs/upload";

    //  项目中雷管最大数量
    public static final int MAX_DET_NUM = 500;

    // 数据恢复时需要输入的校验密码
    public static final String CLEAN_DATA_PASSWORD = "202102";

    public static final String ETEK_ONLINE_GET_PSWD ="https://47.117.132.63:6062/sms/pword/reset?phone=%s";

    public static final String ETEK_UPLOAD_HANDSET_INFO="https://47.117.132.63:6062/handsets/info";
}
