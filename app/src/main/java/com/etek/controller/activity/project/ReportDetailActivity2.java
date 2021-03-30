package com.etek.controller.activity.project;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.etek.controller.R;
import com.etek.controller.activity.project.comment.AppSpSaveConstant;
import com.etek.controller.activity.project.dialog.ReprotDialog;
import com.etek.controller.activity.project.manager.SpManager;
import com.etek.controller.adapter.ReportDetailAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.ReportDto2;
import com.etek.controller.dto.ServerResult;
import com.etek.controller.entity.DetController;
import com.etek.controller.enums.ReportServerEnum;
import com.etek.controller.enums.ResultErrEnum;
import com.etek.controller.hardware.test.HttpCallback;
import com.etek.controller.hardware.util.DetLog;
import com.etek.controller.minaclient.DetMessage;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.controller.activity.BaseActivity;
import com.etek.controller.yunnan.bean.YunUploadBean;
import com.etek.controller.yunnan.bean.YunUploadResponse;
import com.etek.controller.yunnan.bean.YunUploadResult;
import com.etek.controller.yunnan.util.DataTransformUtil;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.NetUtil;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 上报详情页
 */
public class ReportDetailActivity2 extends BaseActivity {
    private long proId;
    private TextView snId;
    private TextView rptStatus;
    private TextView controllerId;
    private TextView controllerLocation;
    private TextView controllerTime;
    private RecyclerView detonatorList;
    private PendingProject projectInfoEntity;
    private List<ProjectDetonator> detonatorEntityList;
    private ReportDetailAdapter reportDetailAdapter;
    private int result = 0;
    private Boolean isServerDanningOn;
    private Boolean isServerZhongbaoOn;
    private Boolean isServerEtekOn;
    private TextView proHint;

    // 丹灵上保返回
    private String danlingLoadReturn = "";
    // 中爆上传返回
    private String zhongbaoLoadReturn = "";

    private final String TAG = "ReportDetailActivity2";
    private ReportDto2 reportDotInfo;
    private YunUploadBean yunUploadData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSupportActionBar(R.string.title_activity_rpt_detail);
        setContentView(R.layout.activity_detrpt_detail2);
        getProjectId();
        initView();

        initReportSwitch();
    }

    private void initReportSwitch() {
        isServerDanningOn = SpManager.getIntance().getSpBoolean(AppSpSaveConstant.SEVER_DANNING_ON);
        isServerZhongbaoOn = SpManager.getIntance().getSpBoolean(AppSpSaveConstant.SEVER_ZHONGBAO_ON);
        isServerEtekOn = true;
    }

    /**
     * 获取项目数据
     */
    private void getProjectId() {
        proId = getIntent().getLongExtra(AppIntentString.PROJECT_ID, -1);
        Logger.d(TAG,"proIds: " + proId);
        projectInfoEntity = DBManager.getInstance().getPendingProjectDao().queryBuilder().where(PendingProjectDao.Properties.Id.eq(proId)).unique();
        detonatorEntityList = DBManager.getInstance().getProjectDetonatorDao().queryBuilder().where(ProjectDetonatorDao.Properties.ProjectInfoId.eq(proId)).list();
    }

    /**
     * 初始化view
     */
    private void initView() {
        snId = findViewById(R.id.sn_id);
        proHint = findViewById(R.id.proHint);
        rptStatus = findViewById(R.id.rpt_status);
        controllerId = findViewById(R.id.ctrl_id);
        controllerLocation = findViewById(R.id.ctrl_location);
        controllerTime = findViewById(R.id.ctrl_time);
        detonatorList = findViewById(R.id.report_detonator_list);
        detonatorList.setLayoutManager(new LinearLayoutManager(this));
        reportDetailAdapter = new ReportDetailAdapter(R.layout.detonator_list_item, detonatorEntityList);
        detonatorList.setAdapter(reportDetailAdapter);

        if (projectInfoEntity != null) {
            //序号
            if (!TextUtils.isEmpty(projectInfoEntity.getCompanyCode())) {
                proHint.setText("项目编号：");
                snId.setText(projectInfoEntity.getProCode());
            }
            if (!TextUtils.isEmpty(projectInfoEntity.getContractCode())) {
                proHint.setText("合同备案序号：");
                snId.setText(projectInfoEntity.getContractCode());
            }
            //项目上报的状态
            showPreportStatus();
            //起爆器编号
            controllerId.setText(projectInfoEntity.getControllerId());
            //地标
            DecimalFormat df = new DecimalFormat("0.0000");
            String loc = df.format(projectInfoEntity.getLongitude()) + "  ,  " + df.format(projectInfoEntity.getLatitude());
            controllerLocation.setText(loc);
            //起爆器时间
            controllerTime.setText(projectInfoEntity.getDate());
            String userinfo = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
            // 上报的信息
//            reportDotInfo = getReportDot(userinfo, projectInfoEntity);

            yunUploadData = DataTransformUtil.getYunUploadData(projectInfoEntity, detonatorEntityList);

            DetLog.writeLog(TAG,projectInfoEntity.toString());

        }
    }

    /**
     * 展示文件上报的状态
     */
    private void showPreportStatus() {
        String reportStatus = projectInfoEntity.getReportStatus();
        if (TextUtils.isEmpty(reportStatus)) {
            reportStatus = "0";
        }
        if ("0".equals(projectInfoEntity.getReportStatus())) {
            rptStatus.setText(R.string.un_report);
            rptStatus.setTextColor(getMyColor(R.color.red));
        } else if ("1".equals(projectInfoEntity.getReportStatus())) {
            rptStatus.setText(R.string.reported);
            rptStatus.setTextColor(getMyColor(R.color.green));
        } else if ("2".equals(projectInfoEntity.getReportStatus())) {
            rptStatus.setText(R.string.report_error);
            rptStatus.setTextColor(getMyColor(R.color.orange));
        }
    }

    /**
     * 获取上报的信息
     *
     * @param userInfo
     * @param projectInfoEntity
     * @return
     */
    private ReportDto2 getReportDot(String userInfo, PendingProject projectInfoEntity) {
        ReportDto2 reportDto = new ReportDto2();
        if (detonatorEntityList != null && !detonatorEntityList.isEmpty()) {
            reportDto.setDetControllerWithoutDet2(userInfo, projectInfoEntity);
            reportDto.setDets2(detonatorEntityList);
            if (TextUtils.isEmpty(reportDto.getDwdm())) {
                reportDto.setDwdm(Globals.user.getCompanyCode());
            }
        }
        return reportDto;
    }

    /**
     * 右上角菜单按钮
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (detonatorEntityList != null && detonatorEntityList.size() > 0) {
            getMenuInflater().inflate(R.menu.menu_rpt_detail, menu);
        }
        return true;
    }

    /**
     * 菜单按钮选中事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_send) {
            showSendDialog();
        }
        return true;
    }

    /**
     * 上传对话框
     */
    private void showSendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("是否上传此数据？");
        //设置对话框标题
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确认", (dialog, which) -> {
            dialog.dismiss();
            sendreportToYun();
        });
        builder.setNegativeButton("取消", null);
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(false); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    // 上报云南的数据
    private void sendreportToYun() {
        if (NetUtil.getNetType(mContext) < 0) {
            showStatusDialog("请去设置网络！");
            return;
        }
        showProDialog("正在上传数据...");
        Gson gson = new Gson();
        String uploadString = gson.toJson(yunUploadData);
        String companyCode = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_COMPANY_CODE);
        String authCode = projectInfoEntity.getAuthCode();
        String url = String.format(AppConstants.YunNanFileUpload,companyCode,authCode);
        DetLog.writeLog(TAG,"上报地址："+url);
        DetLog.writeLog(TAG,"上报数据："+uploadString);

        AsyncHttpCilentUtil.httpPostJson(this, url, uploadString, new HttpCallback() {
            @Override
            public void onFaile(IOException e) {
                missProDialog();
                Log.d(TAG,"上报失败！"+ e.getMessage());
                showYunReport("上报失败！","2");
            }

            @Override
            public void onSuccess(Response response) {
                missProDialog();
                try {
                    String string = response.body().string();
                    Log.d(TAG,"上报返回数据："+string);
                    if (TextUtils.isEmpty(string)) {
                        Log.d(TAG,"上报返回数据为空");
                        showYunReport("上报失败！","2");
                        return;
                    }

                    YunUploadResult yunUploadResponse = new Gson().fromJson(string, YunUploadResult.class);
                    if (yunUploadResponse!=null && yunUploadResponse.getResult().isOk()) {
                        showYunReport("上报成功！","1");
                        return;
                    }
                } catch (IOException e) {
                    Log.d(TAG,"sendreportToYun faile"+e.getMessage());
                }
                showYunReport("上报失败！","2");
            }
        });
    }

    private void showYunReport(String msg,String strStatus){
        showSendRptMessage(null, strStatus);
        showStatusDialog(msg);
        showPreportStatus();
    }

    /**
     * 上报
     */
    private void sendReport() {
        if (NetUtil.getNetType(mContext) < 0) {
            showStatusDialog("请去设置网络！");
            return;
        }

        if (isServerDanningOn) {
            Log.d(TAG, "丹灵！");
            showProDialog("正在上传数据...");
            sendDanLingReport(true);
        }else if (isServerZhongbaoOn){
            Log.d(TAG, "中爆！");
            showProDialog("正在上传数据...");
            UPZBThread(detonatorEntityList, true);
        }

        if(isServerDanningOn||isServerZhongbaoOn){
            //  丹灵or中爆开关全部或之一On，传输到DetBackUp/Post
            Log.d(TAG, "力芯DetBackUp！");
            sendRptToEtekServer(true,reportDotInfo);
        }else{
            //  丹灵or中爆开关全部Off，传输到DET/Post
            Log.d(TAG, "力芯Post！");
            sendRptToEtekServer(false,reportDotInfo);

            Log.d(TAG,"showReproteDialog1");
            showReproteDialog();
        }

    }

    /**
     * 上传丹灵
     */
    private void sendDanLingReport(boolean firstLoad) {
        sendRptToDanling(reportDotInfo, firstLoad);
    }

    /**
     * 上传中爆
     */
    private void UPZBThread(List<ProjectDetonator> detonatorEntities, boolean firstLoad) {
        //  中爆设置了缺省地址：中爆黔南
        Globals.zhongbaoAddress = SpManager.getIntance().getSpString(AppSpSaveConstant.ZHONGBAO_ADDRESS);
        if (!TextUtils.isEmpty(Globals.zhongbaoAddress)) {
            Globals.zhongbaoAddress = "中爆黔南";
            SpManager.getIntance().saveSpString(AppSpSaveConstant.ZHONGBAO_ADDRESS, Globals.zhongbaoAddress);
        }

        ReportServerEnum reportServerEnum = ReportServerEnum.getByName(Globals.zhongbaoAddress);
        DetLog.writeLog(TAG, "zhognbao: reportServerEnum " + Globals.zhongbaoAddress + reportServerEnum);

        new Thread(() -> {
            List<String> msgs = createMessageList(detonatorEntities);
            //sendRptToZhongBao(msgs, firstLoad);
            sendRptToZhongBaoSocket(msgs, firstLoad);
        }).start();
    }

    //  上传到力芯后台
    private void sendRptToEtekServer(boolean bBackup,ReportDto2 reportDto) {
        if(bBackup){
            sendRptToZTEKBackUpPOST();
        }else{
            sendRptToZTEKDetPOST(reportDto);
        }
        return;
    }

    /**
     * 上报到力芯的/Det/BackUp/Post接口，参数不加密，使用DetController对象上报
     */
    private void sendRptToZTEKBackUpPOST(){

        String userinfo = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        DetController dc = new DetController(userinfo,projectInfoEntity);

        String rptJson = JSON.toJSONString(dc, SerializerFeature.WriteMapNullValue);
        DetLog.writeLog(TAG,String.format("上报力芯：%s",rptJson));

        // 力芯BackUp/Post接口： 不加密传输
        String url = AppConstants.ETEKTestServer + AppConstants.DETBACKUP;
        AsyncHttpCilentUtil.httpPostJson(url, rptJson, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DetLog.writeLog(TAG,"上报力芯失败：:"+ e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                DetLog.writeLog(TAG,"力芯返回："+respStr);

                if (TextUtils.isEmpty(respStr)) {
                    DetLog.writeLog(TAG,"respStr is null ");
                    return;
                }

                try {
                    DetLog.writeLog(TAG,"上报ETEK成功");
                } catch (Exception e) {
                    DetLog.writeLog(TAG, "力芯返回解析错误：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 上报到力芯DET/POST，使用原有接口、参数加密，使用ReportDto2对象上报
     * @param reportDto
     */
    private void sendRptToZTEKDetPOST(ReportDto2 reportDto){
        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        DetLog.writeLog(TAG,String.format("上报力芯：%s",rptJson));
        Result result = RptUtil.getRptEncode(rptJson);
        Logger.d(result);
        String url =  AppConstants.ETEKTestServer + AppConstants.ProjectReportTest;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");
        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DetLog.writeLog(TAG,"上报力芯失败：:"+ e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                DetLog.writeLog(TAG,"力芯返回："+respStr);

                if (TextUtils.isEmpty(respStr)) {
                    DetLog.writeLog(TAG,"respStr is null ");
                    return;
                }

                try {
                    //  返回： {"success":0,"cwxx":"成功"}
                    ServerResult serverResult = JSON.parseObject(respStr, ServerResult.class);
                    if (!serverResult.getSuccess().contains("0")) {
                        Integer code = Integer.parseInt(serverResult.getSuccess());
                        ResultErrEnum errEnum = ResultErrEnum.getBycode(code);
                        DetLog.writeLog(TAG, "力芯错误代码：" + errEnum.getMessage());
                    } else {
                        DetLog.writeLog(TAG,"上报ETEK成功");
                    }

                } catch (Exception e) {
                    DetLog.writeLog(TAG, "力芯返回解析错误：" + e.getMessage());
                }
            }
        });
    }

    //  上传到丹灵后台
    private void sendRptToDanling(ReportDto2 reportDto, boolean firstLoad) {

        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        DetLog.writeLog(TAG, "丹灵上报数据：" + rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            missProDialog();
//            showToast("数据编码出错：" + result.getMessage());
            return;
        }
        String url = AppConstants.DanningServer + AppConstants.ProjectReport;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());
        AsyncHttpCilentUtil.httpPost(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showSendRptMessage("上报丹灵失败", "2");
                danlingLoadReturn = "请求失败";
                uploadToDanlingFail(firstLoad);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                DetLog.writeLog(TAG,"丹灵返回 respStr:  " + respStr);
                if (TextUtils.isEmpty(respStr)) {
                    danlingLoadReturn = "返回信息为空";
                    showSendRptMessage("上报丹灵失败", "2");
                    uploadToDanlingFail(firstLoad);
                    return;
                }
                ServerResult serverResult = null;
                try {
                    serverResult = JSON.parseObject(respStr, ServerResult.class);
                    if (serverResult.getSuccess().contains("fail")) {
                        DetLog.writeLog(TAG, "丹灵返回错误代码：" + serverResult.getCwxxms());
                        danlingLoadReturn = "返回错误，错误码：" + serverResult.getCwxxms();
                        showSendRptMessage("上报丹灵失败", "2");
                        uploadToDanlingFail(firstLoad);
                    } else {
                        DetLog.writeLog(TAG, "丹灵上报成功");
                        danlingLoadReturn = "上报丹灵成功";
                        showSendRptMessage("上报丹灵成功", "1");
                        uploadToDanlingSuccess(firstLoad);
                    }
                } catch (Exception e) {
                    DetLog.writeLog(TAG, "丹灵返回解析错误：" + e.getMessage());
                    danlingLoadReturn = "丹灵返回解析错误";
                    showSendRptMessage("上报丹灵失败", "2");
                    uploadToDanlingFail(firstLoad);
                }
            }
        });
    }

    /**
     * 上传数据到丹灵成功了
     *
     * @param firstLoad
     */
    private void uploadToDanlingSuccess(boolean firstLoad) {
        if (firstLoad && isServerZhongbaoOn) {
            UPZBThread(detonatorEntityList, false);
        } else {
            missProDialog();

            Log.d(TAG,"showReproteDialog2");
            showReproteDialog();
        }
    }

    /**
     * 上传到丹灵失败了
     */
    private void uploadToDanlingFail(boolean firstLoad) {
        if (firstLoad && isServerZhongbaoOn) {
            UPZBThread(detonatorEntityList, false);
        } else {
            // 展示上报的对话框
            missProDialog();

            Log.d(TAG,"showReproteDialog3");
            showReproteDialog();
        }
    }

    private void showReproteDialog() {
        ReprotDialog reprotDialog = new ReprotDialog();
        reprotDialog.setReturnString(danlingLoadReturn, zhongbaoLoadReturn);
        reprotDialog.show(getSupportFragmentManager(), "reprot");
    }

    //  上传到中爆后台
    private void sendRptToZhongBaoSocket(List<String> detMsgs, boolean firstLoad) {
        Socket socket =new Socket();
        OutputStream os = null;
        InputStream in =null;

        final int MAX_TIME_OUT = 10000;

        //  连接
        try{
            ReportServerEnum reportServerEnum = ReportServerEnum.getByName(Globals.zhongbaoAddress);
            DetLog.writeLog(TAG,String.format("中爆地址：%s:%d",reportServerEnum.getAddress(), reportServerEnum.getPort()));

            SocketAddress socketAddress = new InetSocketAddress(reportServerEnum.getAddress(),reportServerEnum.getPort());
            //SocketAddress socketAddress = new InetSocketAddress("192.168.1.2",18800);
            socket.connect(socketAddress,10000);
            Log.d(TAG,"连接成功！");

            os = socket.getOutputStream();
            in = socket.getInputStream();
        }catch (Exception e) {
            DetLog.writeLog(TAG, String.format("中爆 连接失败:%s", e.getMessage()));
            uploadToZhongBaoFail(firstLoad);
            zhongbaoLoadReturn ="连接失败";
            return;
        }

        try{
            //  先循环发送
            for (int j = 0; j < detMsgs.size(); j++) {
                byte[] bs = detMsgs.get(j).getBytes();
                Logger.w("detMsgs:" + new String(bs));
                os.write(bs);
                DetLog.writeLog(TAG,String.format("中爆 包[%d]: (%s)发送成功！",j+1,detMsgs.get(j)));
                Thread.sleep(100);
            }

            //  接收服务器的应答
            Date dt0= new Date();
            Long tm0 = dt0.getTime();
            DetLog.writeLog(TAG,String.format("当前时间:%d",tm0));

            int n=0;
            while(true){
                n = in.available();
                if(n>0)
                    break;

                Date dt1= new Date();
                Long tm1 = dt1.getTime();
                if((tm1-tm0)>MAX_TIME_OUT){
                    DetLog.writeLog(TAG,String.format("超时，但无数据:%d——>%d",tm0,tm1));
                    break;
                }
                Thread.sleep(100);
            }

            //  没有数据
            if(n<=0) {
                DetLog.writeLog(TAG, "上报中爆无应答！");
                showSendRptMessage("上报中爆发送接收失败", "2");
                uploadToZhongBaoFail(firstLoad);
                zhongbaoLoadReturn = "服务器无应答";

                in.close();
                os.close();
                socket.close();
                return;
            }

            //  接收数据
            byte[] data = new byte[1024];
            in.read(data,0,1024);
            String msg = new String((byte[]) data).trim();
            DetLog.writeLog(TAG,"接收到"+msg);

            // 数据为空
            if(TextUtils.isEmpty(msg)){
                DetLog.writeLog(TAG,"上报中爆无应答！");
                showSendRptMessage("上报中爆发送接收失败", "2");
                uploadToZhongBaoFail(firstLoad);
                zhongbaoLoadReturn ="服务器无应答";

                in.close();
                os.close();
                socket.close();
                return;
            }

            // 数据Split
            String[] cmds = msg.split("\\$");
            if (cmds[0].contains("O")){
                DetLog.writeLog(TAG,"上报中爆成功！");
                zhongbaoLoadReturn ="上报成功";
                uploadToZhongBaoSuccess(firstLoad);

                missProDialog();
                showReproteDialog();

                in.close();
                os.close();
                socket.close();
                return;
            }

            DetLog.writeLog(TAG,"上报中爆应答信息不包含0！");
            showSendRptMessage("服务器应答错误", "2");
            uploadToZhongBaoFail(firstLoad);
            zhongbaoLoadReturn ="服务器无应答";

            in.close();
            os.close();
            socket.close();
        }catch (Exception e){
            zhongbaoLoadReturn ="发送接收异常";
            DetLog.writeLog(TAG, String.format("中爆 发送接收失败:%s", e.getMessage()));
            uploadToZhongBaoFail(firstLoad);
        }

        return;
    }

    private void showSendRptMessage(String strmsg, String strStatus) {
        projectInfoEntity.setReportStatus(strStatus);
        DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
    }

    private List<String> createMessageList(List<ProjectDetonator> detonators) {
        List<String> msgs = new ArrayList<String>();
        if (detonators == null)
            return null;

        int total = detonators.size();
        int packs = 0;
        boolean isOdd;
        if (total % 10 == 0) {
            packs = total / 10;
            isOdd = false;
        } else {
            packs = total / 10 + 1;
            isOdd = true;
        }

        DetMessage message = new DetMessage();
         message.setLng(projectInfoEntity.getLongitude());
         message.setLat(projectInfoEntity.getLatitude());
        //  起爆器编号只使用后8位
        message.setSn(projectInfoEntity.getShortSn());

        //String timestamp = projectInfoEntity.getDate();
        String timestamp;
        Log.d(TAG,String.format("PROJECT TIME:%s",projectInfoEntity.getDate()));
        try{
            Date dt0 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(projectInfoEntity.getDate());
            timestamp = new SimpleDateFormat("yyMMddHHmmss").format(dt0);
        }catch (ParseException e){
            Log.d(TAG,String.format("DateParse(%s):%s",projectInfoEntity.getDate(),e.getMessage()));
            timestamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        }

        message.setQbDate(timestamp);
        message.setPackCount(packs + 1);
        message.setType(0);
        message.setSendCount(1);
        message.setTotalDet(total);
        msgs.add(new String(message.toByte()));

        int len = 0;
        for (int j = 0; j < packs; j++) {
            message = new DetMessage();
            message.setPackCount(packs + 1);
            message.setSn(projectInfoEntity.getShortSn());
            message.setType(1);
            message.setSendCount(j + 2);

            if (isOdd) {
                if (j != (packs - 1)) {
                    len = 10;
                } else {
                    len = total % 10;
                }
            } else {
                len = 10;
            }
            for (int k = 0; k < len; k++) {
                //                debug("zhongbao:"+detonators.get((j * 10) + k).getZBDetCodeStr());
                String value = detonators.get((j * 10) + k).getZBDetCodeStr() + "O";
                message.addDetonator(value);
            }

            msgs.add(new String(message.toByte()));
        }

        return msgs;
    }

    /**
     * 上传中爆服务器失败
     *
     * @param firstLoad
     */
    private void uploadToZhongBaoFail(boolean firstLoad) {
        showSendRptMessage("上报中爆失败", "2");
        if (firstLoad && isServerDanningOn) {
            sendDanLingReport(false);
        } else {
            uploadFinish();
        }
    }

    /**
     * 上传中爆服务器成功
     *
     * @param firstLoad
     */
    private void uploadToZhongBaoSuccess(boolean firstLoad) {
        showSendRptMessage("上报中爆成功", "1");
        if (firstLoad && isServerDanningOn) {
            sendDanLingReport(false);
        } else {
            uploadFinish();
        }
    }

    /**
     * 数据上报丹灵和中爆后的操作
     */
    private void uploadFinish(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                missProDialog();

                Log.d(TAG,"showReproteDialog7");
                showReproteDialog();
                showPreportStatus();
            }
        });
    }
}
