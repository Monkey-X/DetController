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
import com.elvishew.xlog.XLog;
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
import com.etek.controller.enums.ReportServerEnum;
import com.etek.controller.enums.ResultErrEnum;
import com.etek.controller.hardware.util.DetLog;
import com.etek.controller.minaclient.DetMessage;
import com.etek.controller.minaclient.MessageCodecFactory;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.NetUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;
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
        XLog.d(TAG,"proIds: " + proId);
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
            if (!StringUtils.isEmpty(projectInfoEntity.getCompanyCode())) {
                proHint.setText("项目编号：");
                snId.setText(projectInfoEntity.getProCode());
            }
            if (!StringUtils.isEmpty(projectInfoEntity.getContractCode())) {
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
            reportDotInfo = getReportDot(userinfo, projectInfoEntity);

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
            if (StringUtils.isEmpty(reportDto.getDwdm())) {
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
            sendReport();
        });
        builder.setNegativeButton("取消", null);
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(false); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
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
        Log.d(TAG, "zhognbao: reportServerEnum " + Globals.zhongbaoAddress + reportServerEnum);

        new Thread(() -> {
            List<String> msgs = createMessageList(detonatorEntities);
            sendRptToZhongBao(msgs, firstLoad);
        }).start();
    }

    //  上传到力芯后台
    private void sendRptToEtekServer(boolean bBackup,ReportDto2 reportDto) {

        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        DetLog.writeLog(TAG,String.format("上报力芯：%s",rptJson));
        Result result = RptUtil.getRptEncode(rptJson);
        XLog.d(result);
        String url = "";
        if(bBackup) {
            url = AppConstants.ETEKTestServer + AppConstants.DETBACKUP;
        }else{
            url = AppConstants.ETEKTestServer + AppConstants.ProjectReportTest;
        }

        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");
        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                DetLog.writeLog(TAG,"上报力芯失败：:"+ e.getMessage());
//                showLongToast("上报ETEK失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                if (StringUtils.isBlank(respStr)) {
                    DetLog.writeLog(TAG,"respStr is null ");
                    return;
                }

                ServerResult serverResult = null;
                try {
                    serverResult = JSON.parseObject(respStr, ServerResult.class);
                    if (!serverResult.getSuccess().contains("0")) {
                        Integer code = Integer.parseInt(serverResult.getSuccess());
                        ResultErrEnum errEnum = ResultErrEnum.getBycode(code);
                        DetLog.writeLog(TAG, "力芯错误代码：" + errEnum.getMessage());
//                        showLongToast("上报ETEK失败");
                    } else {
                        DetLog.writeLog(TAG,"上报ETEK成功");
//                        showLongToast("上报ETEK成功");
                    }
                } catch (Exception e) {
                    DetLog.writeLog(TAG, "力芯返回解析错误：" + e.getMessage());
//                    showLongToast("上报ETEK失败");
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
                if (StringUtils.isEmpty(respStr)) {
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
            showReproteDialog();
        }
    }


    private void showReproteDialog() {
        ReprotDialog reprotDialog = new ReprotDialog();
        reprotDialog.setReturnString(danlingLoadReturn, zhongbaoLoadReturn);
        reprotDialog.show(getSupportFragmentManager(), "reprot");
    }

    //  上传到中爆后台
    private void sendRptToZhongBao(List<String> detMsgs, boolean firstLoad) {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast("encode", new ProtocolCodecFilter(new MessageCodecFactory()));
            connector.getSessionConfig().setReadBufferSize(2048);
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10000);
            connector.setConnectTimeoutMillis(1000 * 60 * 3);
            connector.setHandler(new ReportDetailActivity2.MinaHandler(firstLoad));

            ConnectFuture cf;
            ReportServerEnum reportServerEnum = ReportServerEnum.getByName(Globals.zhongbaoAddress);
            DetLog.writeLog(TAG,String.format("中爆地址：%s:%d",reportServerEnum.getAddress(), reportServerEnum.getPort()));
            cf = connector.connect(new InetSocketAddress(reportServerEnum.getAddress(), reportServerEnum.getPort()));
            cf.awaitUninterruptibly();
            for (int j = 0; j < detMsgs.size(); j++) {
                byte[] bs = detMsgs.get(j).getBytes();
                XLog.w("detMsgs:" + new String(bs));
                cf.getSession().write(bs);
                Thread.sleep(100);
                DetLog.writeLog(TAG,String.format("中爆 包[%d]: (%s)发送成功！",j+1,detMsgs.get(j)));
            }

            cf.getSession().getCloseFuture().awaitUninterruptibly();

            connector.dispose();

        } catch (Exception e) {
            DetLog.writeLog(TAG, String.format("中爆 发送失败:%s", e.getMessage()));
            showSendRptMessage("上报中爆失败", "2");
            zhongbaoLoadReturn = e.getMessage();
            uploadToZhongBaoFail(firstLoad);
        } finally {
            DetLog.writeLog(TAG,String.format("中爆 发送结束!"));
        }
    }


    private void showSendRptMessage(String strmsg, String strStatus) {
        projectInfoEntity.setReportStatus(strStatus);
        DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
//        showLongToast(strmsg);
    }

//    private void sendReportToETEKBck() {
//        sendRptToEtekServerBck(reportDtos.get(step - allSize));
//    }
//
//    private void sendRptToEtekServerBck(ReportDto2 reportDto) {
//        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
//        XLog.d(rptJson);
//        Result result = RptUtil.getRptEncode(rptJson);
//        XLog.d(result);
//        String url = AppConstants.ETEKTestServer + AppConstants.DETBACKUP;
//
//        LinkedHashMap params = new LinkedHashMap();
//        params.put("param", result.getData());    //
//        String newUrl = SommerUtils.attachHttpGetParams(url, params);
//
//        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                XLog.e("IOException:", e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String respStr = response.body().string();
//                if (StringUtils.isBlank(respStr)) {
//                    return;
//                }
//
//                ServerResult serverResult = null;
//                try {
//                    serverResult = JSON.parseObject(respStr, ServerResult.class);
//                    if (!serverResult.getSuccess().contains("0")) {
//                        Integer code = Integer.parseInt(serverResult.getSuccess());
//                        ResultErrEnum errEnum = ResultErrEnum.getBycode(code);
//                        XLog.e("错误代码：", errEnum.getMessage());
//                    } else {
////                        sendCmdMessage(MSG_RPT_ETEK_BCK_OK);
//                    }
//                } catch (Exception e) {
//                    XLog.e("解析错误：", e.getMessage());
//                }
//            }
//        });
//    }

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

//    private void sendCmdMessage(int msg) {
//        Message message = new Message();
//        message.what = msg;
//        if (handler != null) {
//            handler.sendMessage(message);
//        }
//    }

//
//    private Handler handler = new Handler(msg -> {
//
//        if (msg.what == MSG_RPT_OK) {
//            result = Activity.RESULT_OK;
//            rptStatus.setText(R.string.reported);
//            rptStatus.setTextColor(getMyColor(R.color.green));
//        } else if (msg.what == MSG_RPT_ZHONGBAO_ERR) {
//            result = Activity.RESULT_CANCELED;
//            rptStatus.setText("中爆服务器错误");
//            rptStatus.setTextColor(getMyColor(R.color.red_normal));
//        }else if (msg.what == MSG_RPT_DANLING_ERR) {
//            dismissProgressBar();
//            projectInfoEntity.setReportStatus("2");
//            DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
//            XLog.e("解析异常");
//        } else if (msg.what == MSG_RPT_ETEK_TEST_ERR) {
//        } else if (msg.what == MSG_RPT_ETEK_BCK) {
////            sendReportToETEKBck();
//        } else if (msg.what == MSG_RPT_ETEK_TEST_OK) {
//            XLog.i("step:" + step);
//            if (step == allSize) {
//                dismissProgressBar();
//                showToast("上传ETEK服务器成功!");
//                result = Activity.RESULT_OK;
//                rptStatus.setText(R.string.reported);
//                rptStatus.setTextColor(getMyColor(R.color.green));
//                projectInfoEntity.setReportStatus("1");
//                DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
//            } else {
//                setProgressBar(step);
//                sendRptToEtekServer(reportDtos.get(step));
//            }
//        } else if (msg.what == MSG_RPT_ETEK_BCK_OK) {
//            XLog.i("step:" + step);
//            if (step == allSize * 2) {
//                dismissProgressBar();
//                result = Activity.RESULT_OK;
//                rptStatus.setText(R.string.reported);
//                rptStatus.setTextColor(getMyColor(R.color.green));
//                projectInfoEntity.setReportStatus("1");
//                DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
//            } else {
//                setProgressBar(step);
////                sendRptToEtekServerBck(reportDtos.get(step - allSize));
//            }
//
//        } else if (msg.what == MSG_RPT_ETEK_BCK_ERR) {
//            dismissProgressBar();
//        }
//        return false;
//    });

    public class MinaHandler extends IoHandlerAdapter {

        private boolean firstLoad;

        public MinaHandler(boolean firstLoad) {
            this.firstLoad = firstLoad;
        }

        public void messageReceived(IoSession session, Object message) {
            if (message == null) {
                showSendRptMessage("上传中爆服务器错误!", "2");
                zhongbaoLoadReturn = "上传中爆服务器错误";
                uploadToZhongBaoFail(firstLoad);
                return;
            }
            String msg = new String((byte[]) message).trim();
            XLog.e(msg);
            if (!StringUtils.isBlank(msg)) {
                String[] cmds = msg.split("\\$");
                XLog.d(cmds[0]);
                if (cmds[0].contains("O")) {
                    showSendRptMessage("上传中爆服务器成功!", "1");
                    zhongbaoLoadReturn = "上传中爆服务器成功";
                    uploadToZhongBaoSuccess(firstLoad);
                } else {
                    zhongbaoLoadReturn = "上传中爆服务器错误";
                    showSendRptMessage("上传中爆服务器错误!", "2");
                    uploadToZhongBaoFail(firstLoad);
                }
            }
        }

        public void messageSent(IoSession session, Object message) {

        }

        public void sessionClosed(IoSession session) {
            session.closeNow();
            XLog.e("sessionClosed");
        }

        public void sessionCreated(IoSession session) {
            IoSessionConfig cfg1 = session.getConfig();
            if (cfg1 instanceof SocketSessionConfig) {
                SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
                cfg.setReceiveBufferSize(2 * 1024 * 1024);
                cfg.setReadBufferSize(2 * 1024 * 1024);
                cfg.setKeepAlive(true);
                cfg.setSoLinger(0);
                cfg.setTcpNoDelay(true);
                cfg.setWriteTimeout(1000);
            }
        }

        public void sessionIdle(IoSession session, IdleStatus idle) throws Exception {
            XLog.e("sessionIdle");
        }

        public void sessionOpened(IoSession session) {
            XLog.w("sessionOpened");
        }
    }

    /**
     * 上传中爆服务器失败
     *
     * @param firstLoad
     */
    private void uploadToZhongBaoFail(boolean firstLoad) {
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
        missProDialog();
        showReproteDialog();
        showPreportStatus();
    }
}
