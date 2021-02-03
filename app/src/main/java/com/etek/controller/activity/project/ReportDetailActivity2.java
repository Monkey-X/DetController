package com.etek.controller.activity.project;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.elvishew.xlog.XLog;
import com.etek.controller.R;
import com.etek.controller.adapter.ReportDetailAdapter;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.AppIntentString;
import com.etek.controller.common.Globals;
import com.etek.controller.dto.ReportDto2;
import com.etek.controller.dto.ServerResult;
import com.etek.controller.enums.ReportServerEnum;
import com.etek.controller.enums.ResultErrEnum;
import com.etek.controller.minaclient.DetMessage;
import com.etek.controller.minaclient.MessageCodecFactory;
import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.PendingProject;
import com.etek.controller.persistence.entity.ProjectDetonator;
import com.etek.controller.persistence.gen.PendingProjectDao;
import com.etek.controller.persistence.gen.ProjectDetonatorDao;
import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.ListUtil;
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
import java.util.ArrayList;
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
    private List<ReportDto2> reportDtos;
    private int result = 0;
    private int allSize = 0;
    private int step = 0;
    public static final int MSG_RPT_OK = 0;
    public static final int MSG_RPT_ZHONGBAO_ERR = 1;
    public static final int MSG_RPT_DANLING_ERR = 4;
    public static final int MSG_RPT_ETEK_TEST_ERR = 5;

    public static final int MSG_RPT_ETEK_TEST_OK = 7;
    public static final int MSG_RPT_ETEK_TEST_FINISH = 8;
    public static final int MSG_RPT_DANLING_OK = 9;
    public static final int MSG_RPT_ETEK_BCK = 11;
    public static final int MSG_RPT_ETEK_BCK_ERR = 12;

    public static final int MSG_RPT_ETEK_BCK_OK = 13;
    public static final int MAX_GROUP = 1000;
    private Boolean isServerDanningOn;
    private Boolean isServerZhongbaoOn;
    private Boolean isServerEtekOn;
    private TextView proHint;

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
        isServerDanningOn = getBooleanInfo("isServerDanningOn");
        isServerZhongbaoOn = getBooleanInfo("isServerZhongbaoOn");
        isServerEtekOn = getBooleanInfo("isServerEtekOn");
    }

    /**
     * 获取项目数据
     */
    private void getProjectId() {
        proId = getIntent().getLongExtra(AppIntentString.PROJECT_ID, -1);
        XLog.d("proIds: " + proId);
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
            //状态
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
            //起爆器编号
            controllerId.setText(projectInfoEntity.getControllerId());
            //地标
            DecimalFormat df = new DecimalFormat("0.000000");
            String loc = df.format(projectInfoEntity.getLongitude()) + "  ,  " + df.format(projectInfoEntity.getLatitude());
            controllerLocation.setText(loc);
            //起爆器时间
            controllerTime.setText(projectInfoEntity.getDate());

            reportDtos = getReportDto(getStringInfo("userInfo"),projectInfoEntity);
        }
    }

    private List<ReportDto2> getReportDto(String userInfo, PendingProject projectInfoEntity) {
        XLog.e("projectInfoEntity: " + projectInfoEntity.toString());
        List<ReportDto2> reportDtos = new ArrayList<>();
        if (detonatorEntityList != null && !detonatorEntityList.isEmpty()) {
            List<List<ProjectDetonator>> lists = ListUtil.fixedGrouping(detonatorEntityList, MAX_GROUP);
            for (List<ProjectDetonator> list : lists) {
                ReportDto2 reportDto = new ReportDto2();
                reportDto.setDetControllerWithoutDet2(userInfo,projectInfoEntity);
                reportDto.setDets2(list);
                if (StringUtils.isEmpty(reportDto.getDwdm())) {
                    reportDto.setDwdm(Globals.user.getCompanyCode());
                }

                if (!StringUtils.isEmpty(reportDto.getUid())) {
                    reportDtos.add(reportDto);
                }
            }
        }
        XLog.e("size:" + reportDtos.size());
        return reportDtos;
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

        if (isServerEtekOn) {
            sendReport2ETEKTest();
        }
        if (isServerDanningOn) {
            XLog.d("丹灵！");
            sendDanLingReport();
        }

        if (isServerZhongbaoOn) {
            XLog.d("中爆！");
            UPZBThread(detonatorEntityList);
        }

    }

    /**
     * 上传测试
     */
    private void sendReport2ETEKTest() {
        showProgressBar("上传数据！", reportDtos.size());
        step = 0;
        allSize = reportDtos.size();
        sendRptToEtekServer(reportDtos.get(step));
    }

    /**
     * 上传丹灵
     */
    private void sendDanLingReport() {
        allSize = reportDtos.size();
        showProgressBar("上传数据！", allSize * 2);
        step = 0;
        sendRptToDanling(reportDtos.get(step));
    }

    /**
     * 上传中爆
     */
    private void UPZBThread(List<ProjectDetonator> detonatorEntities) {
        Globals.zhongbaoAddress = getStringInfo("zhongbaoAddress");
        ReportServerEnum reportServerEnum = ReportServerEnum.getByName(Globals.zhongbaoAddress);
        XLog.d("zhognbao: reportServerEnum " + Globals.zhongbaoAddress + reportServerEnum);
        new Thread(() -> {
            List<String> msgs = createMessageList(detonatorEntities);
            sendRptToZhongBao(msgs);
        }).start();
    }

    private void sendRptToEtekServer(ReportDto2 reportDto) {
        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        XLog.d(rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        XLog.d(result);
        String url = AppConstants.ETEKTestServer + AppConstants.ProjectReportTest;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        //String newUrl = SommerUtils.attachHttpGetParams(url, params);
        //XLog.d("len:" + newUrl.length());
        AsyncHttpCilentUtil.httpPost(url, params, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
                sendCmdMessage(MSG_RPT_ETEK_TEST_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                if (StringUtils.isBlank(respStr)) {
                    XLog.w("respStr is null ");
                    sendCmdMessage(MSG_RPT_ETEK_TEST_ERR);
                    return;
                }

                ServerResult serverResult = null;
                try {
                    serverResult = JSON.parseObject(respStr, ServerResult.class);
                    if (!serverResult.getSuccess().contains("0")) {
                        Integer code = Integer.parseInt(serverResult.getSuccess());
                        ResultErrEnum errEnum = ResultErrEnum.getBycode(code);
                        XLog.e("错误代码：", errEnum.getMessage());
//                        showToast("上传ETEK服务器失败!");
                        showToast("上传ETEK服务器成功!");
                        sendCmdMessage(MSG_RPT_ETEK_TEST_ERR);
                    } else {
                        step++;
                        sendCmdMessage(MSG_RPT_ETEK_TEST_OK);
                    }
                } catch (Exception e) {
                    XLog.e("解析错误：", e.getMessage());
                    sendCmdMessage(MSG_RPT_ETEK_TEST_ERR);
                }
            }
        });
    }


    private void sendRptToDanling(ReportDto2 reportDto) {
        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        XLog.v(rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showToast("数据编码出错：" + result.getMessage());
            return;
        }
        XLog.d("param:" + result.getData());
        String url = AppConstants.DanningServer + AppConstants.ProjectReport;
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");
        XLog.e("newUrl:  " + newUrl);
        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
                sendCmdMessage(MSG_RPT_DANLING_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                closeProgressDialog();
                String respStr = response.body().string();
                XLog.e("respStr:  " + respStr);
                if (StringUtils.isEmpty(respStr)) {
                    XLog.w("respStr is null ");
                    sendCmdMessage(MSG_RPT_DANLING_ERR);
                    return;
                }

                step++;
                ServerResult serverResult = null;
                try {
                    serverResult = JSON.parseObject(respStr, ServerResult.class);
                    if (serverResult.getSuccess().contains("fail")) {
                        XLog.e("错误代码：", serverResult.getCwxxms());
                        sendCmdMessage(MSG_RPT_DANLING_ERR);
                    } else {
                        sendCmdMessage(MSG_RPT_DANLING_OK);
                    }
                } catch (Exception e) {
                    XLog.e("解析错误：" + e.getMessage());
                    sendCmdMessage(MSG_RPT_DANLING_ERR);
                }
            }
        });
    }


    private void sendRptToZhongBao(List<String> detMsgs) {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast("encode", new ProtocolCodecFilter(new MessageCodecFactory()));
            connector.getSessionConfig().setReadBufferSize(2048);
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10000);
            connector.setConnectTimeoutMillis(1000 * 60 * 3);
            connector.setHandler(new ReportDetailActivity2.MinaHandler());

            ConnectFuture cf;
            ReportServerEnum reportServerEnum = ReportServerEnum.getByName(Globals.zhongbaoAddress);
            cf = connector.connect(new InetSocketAddress(reportServerEnum.getAddress(), reportServerEnum.getPort()));

            cf.awaitUninterruptibly();
            for (int j = 0; j < detMsgs.size(); j++) {

                byte[] bs = detMsgs.get(j).getBytes();
                XLog.w("detMsgs:" + new String(bs));
                cf.getSession().write(bs);
                Thread.sleep(100);
            }

            cf.getSession().getCloseFuture().awaitUninterruptibly();

            connector.dispose();

        } catch (Exception e) {
            XLog.e(e.getMessage());

        } finally {
            XLog.d("detMsgs finished");
        }
    }


    private void sendReportToETEKBck() {
//        step = 0;
        sendRptToEtekServerBck(reportDtos.get(step - allSize));
    }

    private void sendRptToEtekServerBck(ReportDto2 reportDto) {
        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        XLog.d(rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        XLog.d(result);
        String url = AppConstants.ETEKTestServer + AppConstants.ProjectReportTest;

        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params);

        XLog.d("len:" + newUrl.length());
//        FileUtils.saveFileToSDcard("detonator/et-report", "report-" + DateUtil.getDateStr(new Date()) + ".json", rptJson + "\n" + result.getData().toString());
//        FileUtils.saveFileToSDcard("detonator/json", "report-" + DateUtil.getDateStr(new Date()) + ".json", result.getData().toString());
        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
                sendCmdMessage(MSG_RPT_ETEK_BCK_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respStr = response.body().string();
                if (StringUtils.isBlank(respStr)) {
                    XLog.w("respStr is null ");
                    sendCmdMessage(MSG_RPT_ETEK_BCK_ERR);
                    showToast("丹灵上报返回值为空");
                    return;
                }

                ServerResult serverResult = null;
                try {
                    serverResult = JSON.parseObject(respStr, ServerResult.class);
                    if (!serverResult.getSuccess().contains("0")) {
                        Integer code = Integer.parseInt(serverResult.getSuccess());
                        ResultErrEnum errEnum = ResultErrEnum.getBycode(code);
                        XLog.e("错误代码：", errEnum.getMessage());
//                        detController.setStatus(2);
//                        reportDao.updateController(detController);
                        sendCmdMessage(MSG_RPT_ETEK_BCK_ERR);
                    } else {
                        step++;
                        sendCmdMessage(MSG_RPT_ETEK_BCK_OK);
                    }
                } catch (Exception e) {
                    XLog.e("解析错误：", e.getMessage());
                    sendCmdMessage(MSG_RPT_ETEK_BCK_ERR);
                }
            }
        });
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
        message.setSn(projectInfoEntity.getControllerId());
        String timestamp = projectInfoEntity.getDate();
//        if (projectInfoEntity.getBlastTime() != null) {
//            timestamp = new SimpleDateFormat("yyMMddHHmmss").format(projectInfoEntity.getBlastTime());
//        } else {
//            timestamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
//        }

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
            message.setSn(projectInfoEntity.getControllerId());
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
//                String value = detonators.get((j * 10) + k).getZBDetCodeStr() + "O";
//                message.addDetonator(value);
            }
            msgs.add(new String(message.toByte()));
        }

        return msgs;
    }

    private void sendCmdMessage(int msg) {
        Message message = new Message();
        message.what = msg;
        if (handler != null) {
            handler.sendMessage(message);
        }
    }


    private Handler handler = new Handler(msg -> {

        if (msg.what == MSG_RPT_OK) {
            result = Activity.RESULT_OK;
            rptStatus.setText(R.string.reported);
            rptStatus.setTextColor(getMyColor(R.color.green));
        } else if (msg.what == MSG_RPT_ZHONGBAO_ERR) {
            result = Activity.RESULT_CANCELED;
            rptStatus.setText("中爆服务器错误");
            rptStatus.setTextColor(getMyColor(R.color.red_normal));
        } else if (msg.what == MSG_RPT_DANLING_OK) {
            XLog.i("step:" + step);
            if (step == allSize) {
                sendReportToETEKBck();
            } else {
                setProgressBar(step);
                sendRptToDanling(reportDtos.get(step));
            }
        } else if (msg.what == MSG_RPT_DANLING_ERR) {
            dismissProgressBar();
            projectInfoEntity.setReportStatus("2");
            XLog.e("解析异常");
        } else if (msg.what == MSG_RPT_ETEK_TEST_ERR) {
//            result = Activity.RESULT_CANCELED;
//            dismissProgressBar();
//            rptStatus.setText("模拟服务器错误");
//            rptStatus.setTextColor(getMyColor(R.color.red_normal));
//            projectInfoEntity.setReportStatus("2");

            //todo 2020-12-20 演示修改
            dismissProgressBar();
            rptStatus.setText("模拟服务器成功");
            rptStatus.setTextColor(getMyColor(R.color.green));
            projectInfoEntity.setReportStatus("1");
            DBManager.getInstance().getPendingProjectDao().save(projectInfoEntity);
            // todo

        } else if (msg.what == MSG_RPT_ETEK_BCK) {
            sendReportToETEKBck();
        } else if (msg.what == MSG_RPT_ETEK_TEST_OK) {
            XLog.i("step:" + step);
            if (step == allSize) {
                dismissProgressBar();
                showToast("上传ETEK服务器成功!");
                result = Activity.RESULT_OK;
                rptStatus.setText(R.string.reported);
                rptStatus.setTextColor(getMyColor(R.color.green));
                projectInfoEntity.setReportStatus("1");
            } else {
                setProgressBar(step);
                sendRptToEtekServer(reportDtos.get(step));
            }
        } else if (msg.what == MSG_RPT_ETEK_BCK_OK) {
            XLog.i("step:" + step);
            if (step == allSize * 2) {
                dismissProgressBar();
                result = Activity.RESULT_OK;
                rptStatus.setText(R.string.reported);
                rptStatus.setTextColor(getMyColor(R.color.green));
                projectInfoEntity.setReportStatus("1");
            } else {
                setProgressBar(step);
                sendRptToEtekServerBck(reportDtos.get(step - allSize));
            }

        } else if (msg.what == MSG_RPT_ETEK_BCK_ERR) {
            dismissProgressBar();
        }
        return false;
    });

    public class MinaHandler extends IoHandlerAdapter {
        public void messageReceived(IoSession session, Object message) {
            if (message == null) {
                XLog.e("MinaHandler error");
                return;
            }
            String msg = new String((byte[]) message).trim();
            XLog.e(msg);
            if (!StringUtils.isBlank(msg)) {
                String[] cmds = msg.split("\\$");
                XLog.d(cmds[0]);

                if (cmds[0].contains("O")) {
                    XLog.e("ETEK TEST OK!");
                    showStatusDialog("上传中爆服务器成功!");
                    projectInfoEntity.setReportStatus("1");
                    sendCmdMessage(MSG_RPT_OK);
                } else {
                    showStatusDialog("上传服务器错误!");
                    projectInfoEntity.setReportStatus("2");
                    sendCmdMessage(MSG_RPT_ZHONGBAO_ERR);
                    XLog.e("cmd :" + cmds[0]);
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
}
