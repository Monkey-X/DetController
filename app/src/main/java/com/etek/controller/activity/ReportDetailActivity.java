package com.etek.controller.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.elvishew.xlog.XLog;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import com.etek.controller.R;
import com.etek.controller.common.AppConstants;
import com.etek.controller.common.Globals;

import com.etek.controller.dto.ReportDto;
import com.etek.controller.dto.ServerResult;
import com.etek.controller.entity.DetController;
import com.etek.controller.entity.Detonator;


import com.etek.controller.enums.ReportServerEnum;
import com.etek.controller.enums.ResultErrEnum;
import com.etek.controller.minaclient.DetMessage;
import com.etek.controller.minaclient.MessageCodecFactory;


import com.etek.controller.persistence.DBManager;
import com.etek.controller.persistence.entity.ReportEntity;

import com.etek.controller.utils.AsyncHttpCilentUtil;
import com.etek.controller.utils.ListUtil;
import com.etek.controller.utils.RptUtil;
import com.etek.controller.utils.SommerUtils;
import com.etek.sommerlibrary.activity.BaseActivity;

import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;

import com.etek.sommerlibrary.utils.NetUtil;
import com.etek.sommerlibrary.widget.TableView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.LinkedHashMap;
import java.util.List;



public class ReportDetailActivity extends BaseActivity {

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
    public static final int MAX_GROUP = 50;
    /**
     * 这个是有用的注释
     */
    DetController detController;

    @BindView(R.id.rpt_status)
    TextView tvStatus;

    @BindView(R.id.ctrl_location)
    TextView tvLocation;

    @BindView(R.id.ctrl_id)
    TextView tvDevice;

    @BindView(R.id.ctrl_time)
    TextView tvTime;

    @BindView(R.id.det_table)
    TableView table;

    @BindView(R.id.sn_id)
    TextView tvSNid;

//    @BindView(R.id.img_loading)
//    ImageView img_loading;

    int result = 0;
    int step = 0;
    int allSize = 0;

    List<ReportDto> reportDtos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSupportActionBar(R.string.title_activity_rpt_detail);
        setContentView(R.layout.activity_detrpt_detail);
        ButterKnife.bind(this);
        initView();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rpt_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            setResult(result, intent);
            finish();
        } else if (item.getItemId() == R.id.action_send) {
            showSendDialog();
        }

        return true;
    }


    void initView() {

        detController = (DetController) getIntent().getSerializableExtra("DetController");
        if (detController == null) {
            showToast("起爆器序列号为空！");
            delayAction(null, 1000);
        }
        XLog.d(detController.toString());
        if (!StringUtils.isEmpty(detController.getProjectId()))
            tvSNid.setText(detController.getProjectId());
        if (!StringUtils.isEmpty(detController.getContractId()))
            tvSNid.setText(detController.getContractId());
//        tvStatus.setText("序号："+detController.getStatus());

        if (detController.getStatus() == 0) {

            tvStatus.setText(R.string.un_report);
            tvStatus.setTextColor(getMyColor(R.color.red));

        } else if (detController.getStatus() == 1) {
            tvStatus.setText(R.string.reported);
            tvStatus.setTextColor(getMyColor(R.color.green));

        } else if (detController.getStatus() == 2) {
            tvStatus.setText(R.string.report_error);
            tvStatus.setTextColor(getMyColor(R.color.orange));

        }

        tvDevice.setText(detController.getSn());

        DecimalFormat df = new DecimalFormat("0.000000");
        String loc = df.format(detController.getLongitude()) + " , " + df.format(detController.getLatitude());
        tvLocation.setText(loc);


        String timeStr = DateUtil.getDateStr(detController.getBlastTime());
        tvTime.setText(timeStr);
        initTableView(detController.getDetList());
//        Button button = findViewById(R.id.report);
//        button.setOnClickListener(v -> sendReport());
//        reportDao = new ReportDao(mContext);
//        XLog.i(detController.toString());
        reportDtos = getReportDto(detController);

    }

    private void sendReport() {
        if (NetUtil.getNetType(mContext) < 0) {
            showStatusDialog("请去设置网络！");
            return;
        }

        if (Globals.isTest) {
            sendReport2ETEKTest();
        } else {
            if (Globals.isServerDanningOn) {
//            showToast("丹灵！");
                XLog.d("丹灵！");
                sendDanLingReport();
            }

            if (Globals.isServerZhongbaoOn) {

                XLog.d("中爆！");
                UPZBThread(detController.getDetList());

            }
//            sendCmdMessage(MSG_RPT_ETEK_TEST);

        }

    }

    void sendCmdMessage(int msg) {
        Message message = new Message();
        message.what = msg;
        if (handler != null) {
            handler.sendMessage(message);
        }

    }

    String json = "{\"bprysfz\":\"522524198308821253\",\"bpsj\":\"2020-07-09 18:27:10\",\"dwdm\":\"5201234300173\",\"htid\":\"520123319070002\",\"jd\":\"106.556999\",\"sbbh\":\"F60A8200003\",\"uid\":\"6020A8373C00C8,6020A8373C00E3,6020A8373C00E2,6020A8373C00D7,6020A8373C00DE,6020A8373C00CB,6020A8373C00CA,6020A8373C00F5,6020A8373C00DC,6020A8373C00D1,6020A8373C00DB,6020A8373C00CF,6020A8373C00F3,6020A8373C00D9,6020A8373C00F4,6020A8373C00DA,6020A8373C00D0,6020A8373C00CE,6020A8373C00F6,6020A8373C00DF,6020A8373C00CC,6020A8373C00E0,6020A8373C00F2,6020A8373C00DD,6020A8373C00D5,6020A8373C00D6,6020A8373C00CD,6020A8373C00F7,6020A8373C00F1,6020A8373C00F0,6020A8373C00C9,6020A8373C00D8,6020A8373C00E1,6020A8373C00D4,6020A8373C00D2,6020A8373C00E5,6020A8373C00D3,6020A8373C00E4\",\"wd\":\"26.779318\",\"xmbh\":\"\"}";

    Handler handler = new Handler(msg -> {

        if (msg.what == MSG_RPT_OK) {
            result = Activity.RESULT_OK;
            tvStatus.setText(R.string.reported);
            tvStatus.setTextColor(getMyColor(R.color.green));
//            result = ActivityResult.ok();
        } else if (msg.what == MSG_RPT_ZHONGBAO_ERR) {
            result = Activity.RESULT_CANCELED;
            tvStatus.setText("中爆服务器错误");
            tvStatus.setTextColor(getMyColor(R.color.red_normal));
        } else if (msg.what == MSG_RPT_DANLING_OK) {
            XLog.i("step:" + step);
            if(step == allSize){

                sendReportToETEKBck();
            }else {
                setProgressBar(step);
                sendRptToDanling(reportDtos.get(step));
            }


        } else if (msg.what == MSG_RPT_DANLING_ERR) {
//            result = Activity.RESULT_CANCELED;
////            result = ActivityResult.errorOfMsg("丹灵服务器错误");
//            tvStatus.setText("丹灵服务器错误");
//            tvStatus.setTextColor(getMyColor(R.color.red_normal));
//            showToast("丹灵服务器报错！");
            dismissProgressBar();
            detController.setStatus(2);
            ReportEntity reportEntity = detController.getReportEntity();
            reportEntity.setId(detController.getId());
            DBManager.getInstance().getReportEntityDao().insertOrReplace(reportEntity);




        } else if (msg.what == MSG_RPT_ETEK_TEST_ERR) {
            result = Activity.RESULT_CANCELED;
            dismissProgressBar();
//            result = ActivityResult.errorOfMsg("丹灵服务器错误");
            tvStatus.setText("模拟服务器错误");
            tvStatus.setTextColor(getMyColor(R.color.red_normal));

            detController.setStatus(2);
            ReportEntity reportEntity = detController.getReportEntity();
            reportEntity.setId(detController.getId());
            DBManager.getInstance().getReportEntityDao().insertOrReplace(reportEntity);

        } else if (msg.what == MSG_RPT_ETEK_BCK) {
            sendReportToETEKBck();
        } else if (msg.what == MSG_RPT_ETEK_TEST_OK) {
            XLog.i("step:" + step);

            if(step == allSize){
                dismissProgressBar();
                showToast("上传ETEK服务器成功!");
                result = Activity.RESULT_OK;
                tvStatus.setText(R.string.reported);
                tvStatus.setTextColor(getMyColor(R.color.green));
                detController.setStatus(1);
                ReportEntity reportEntity = detController.getReportEntity();
                reportEntity.setId(detController.getId());
                DBManager.getInstance().getReportEntityDao().insertOrReplace(reportEntity);

            }else {
                setProgressBar(step);
                sendRptToEtekServer(reportDtos.get(step));
            }

        } else if (msg.what == MSG_RPT_ETEK_BCK_OK) {
            XLog.i("step:" + step);

            if(step == allSize*2){
                dismissProgressBar();

                result = Activity.RESULT_OK;
                tvStatus.setText(R.string.reported);
                tvStatus.setTextColor(getMyColor(R.color.green));
                detController.setStatus(1);
                ReportEntity reportEntity = detController.getReportEntity();
                reportEntity.setId(detController.getId());
                DBManager.getInstance().getReportEntityDao().insertOrReplace(reportEntity);
            }else {
                setProgressBar(step);
                sendRptToEtekServerBck(reportDtos.get(step-allSize));
            }

        } else if (msg.what == MSG_RPT_ETEK_BCK_ERR) {
//            result = Activity.RESULT_CANCELED;
            dismissProgressBar();
//            result = ActivityResult.errorOfMsg("丹灵服务器错误");
//            tvStatus.setText("模拟服务器错误");
//            tvStatus.setTextColor(getMyColor(R.color.red_normal));

//            detController.setStatus(2);
//            ReportEntity reportEntity = detController.getReportEntity();
//            reportEntity.setId(detController.getId());
//            DBManager.getInstance().getReportEntityDao().insertOrReplace(reportEntity);

        }
        return false;
    });



    private void initTableView(List<Detonator> detonatorList) {

        if (detonatorList == null){
            return;
        }

        int width2 = getWindowWidth();

        table.setHeaderNames("序号", "雷管编码", "状态");
        width2 = width2 - 30;
        table.setColumnWidth(0, width2 / 4);
        table.setColumnWidth(1, width2 / 2);
        table.setColumnWidth(2, width2 / 4);
//        detRegTable.setUnitTextColor(R.color.mediumseagreen);

        //其他可选设置项
        table.setUnitSelectable(false);//单元格处理事件的时候是否可以选中


//                                List<String[]> datas = new ArrayList<>();
        int size = detonatorList.size();
        String[][] detData = new String[size][3];
        int[][] detColor = new int[size][3];
        int i;
        for (i = 0; i < size; i++) {
            detData[i][0] = "" + (i + 1);
            detColor[i][0] = R.color.black;
            detData[i][1] = detonatorList.get(i).getDetCode();
            detColor[i][1] = R.color.black;
            if (detonatorList.get(i).getStatus() == 0) {
                detData[i][2] = "正常";
                detColor[i][2] = R.color.mediumseagreen;
            } else if (detonatorList.get(i).getStatus() == 1) {
                detData[i][2] = "未注册";
                detColor[i][2] = R.color.red_normal;
            } else if (detonatorList.get(i).getStatus() == 2) {
                detData[i][2] = "已使用";
                detColor[i][2] = R.color.blue;
            } else if (detonatorList.get(i).getStatus() == 3) {
                detData[i][2] = "不存在";
                detColor[i][2] = R.color.gray;
            }


        }

        table.setmUnitTextColors(detColor);
        table.setTableData(detData);
        table.notifyAttributesChanged();
    }


    private List<String> createMessageList(List<Detonator> detonators) {


        List<String> msgs = new ArrayList<String>();
        if (detonators == null)
            return null;

        int total = detonators.size();
//        XLog.v("total=" + total);


        int packs = 0;
        boolean isOdd;
        if (total % 10 == 0) {
            packs = total / 10;
            isOdd = false;
        } else {
            packs = total / 10 + 1;
            isOdd = true;
        }

//        XLog.d("sn:" + detController.getShortSn());
        DetMessage message = new DetMessage();
        message.setLng(detController.getLongitude());
        message.setLat(detController.getLatitude());
        message.setSn(detController.getShortSn());
//        XLog.d("sn:" + detController.getShortSn());
        String timestamp;
        if (detController.getBlastTime() != null) {
            timestamp = new SimpleDateFormat("yyMMddHHmmss").format(detController.getBlastTime());
        } else {
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
            message.setSn(detController.getShortSn());
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
//                XLog.d("zhongbao:"+detonators.get((j * 10) + k).getZBDetCodeStr());
                String value = detonators.get((j * 10) + k).getZBDetCodeStr() + "O";
                message.addDetonator(value);
            }

            msgs.add(new String(message.toByte()));


        }


        return msgs;

    }

    private void sendRptToZhongBao(List<String> detMsgs) {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast(
                    "encode",
                    new ProtocolCodecFilter(new MessageCodecFactory()));

            connector.getSessionConfig().setReadBufferSize(2048);
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10000);
            connector.setConnectTimeoutMillis(1000 * 60 * 3);

            connector.setHandler(new MinaHandler());
//            ConnectFuture cf = connector.connect(new InetSocketAddress("192.168.1.73",
//                    6088));
            ConnectFuture cf;
            ReportServerEnum reportServerEnum = ReportServerEnum.getByName(Globals.zhongbaoAddress);
                        cf = connector.connect(new InetSocketAddress(reportServerEnum.getAddress(),
                    reportServerEnum.getPort()));
//            cf = connector.connect(new InetSocketAddress(AppConstants.ETEK_HTTP,
//                    AppConstants.ETEK_PORT));
//            String address = "14.23.69.2";
//            int port = 1088;
//            cf = connector.connect(new InetSocketAddress(address,
//                    port));


            cf.awaitUninterruptibly();
            for (int j = 0; j < detMsgs.size(); j++) {

                byte[] bs = detMsgs.get(j).getBytes();
                XLog.w("detMsgs:"+new String(bs));
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



    public class MinaHandler extends IoHandlerAdapter {


        public void messageReceived(IoSession session, Object message) {
            if (message == null) {
                XLog.e("MinaHandler error");
                return;
            }
            String msg = new String((byte[]) message).trim();
            XLog.d(msg);
            if (!StringUtils.isBlank(msg)) {
                String[] cmds = msg.split("\\$");
                XLog.d(cmds[0]);

                if (cmds[0].contains("O")) {
                    XLog.v("ETEK TEST OK!");
//                    if (Globals.isSimUPload) {
//                        showToast("上传中爆测试服务器成功!");
//                    } else {
//                        showToast("上传中爆正式服务器成功!");
//                    }
                    showStatusDialog("上传中爆服务器成功!");

//                    detController.setStatus(1);
//                    ReportEntity reportEntity = new ReportEntity(detController);
//                    DBManager.getInstance().getReportEntityDao().insertOrReplace(reportEntity);
                    detController.setStatus(1);
                    ReportEntity reportEntity = detController.getReportEntity();
                    reportEntity.setId(detController.getId());
                    DBManager.getInstance().getReportEntityDao().insertOrReplace(reportEntity);
//                        reportDao.updateControllerById(detController);
                    sendCmdMessage(MSG_RPT_OK);


                } else {

                    showStatusDialog("上传服务器错误!");
                    detController.setStatus(2);

//                        reportDao.updateController(detController);
                    ReportEntity reportEntity = detController.getReportEntity();
                    reportEntity.setId(detController.getId());
                    DBManager.getInstance().getReportEntityDao().insertOrReplace(reportEntity);
                    sendCmdMessage(MSG_RPT_ZHONGBAO_ERR);
                    XLog.v("cmd :" + cmds[0]);


                }


            }

        }

        public void messageSent(IoSession session, Object message) {

        }

        public void sessionClosed(IoSession session) {
//            session.close();
            session.closeNow();
            XLog.w("sessionClosed");
        }

        public void sessionCreated(IoSession session) {

            IoSessionConfig cfg1 = session.getConfig();
            if (cfg1 instanceof SocketSessionConfig) {
                SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
                // ((SocketSessionConfig) cfg).setReceiveBufferSize(4096);
                cfg.setReceiveBufferSize(2 * 1024 * 1024);
                cfg.setReadBufferSize(2 * 1024 * 1024);
                cfg.setKeepAlive(true);
                // if (session.== TransportType.SOCKET) {
                // ((SocketSessionConfig) cfg).setKeepAlive(true);
                cfg.setSoLinger(0);
                cfg.setTcpNoDelay(true);
                cfg.setWriteTimeout(1000);
            }

        }

        public void sessionIdle(IoSession session, IdleStatus idle)
                throws Exception {
            XLog.w("sessionIdle");

        }

        public void sessionOpened(IoSession session) {
            XLog.w("sessionOpened");
        }
    }


    void UPZBThread(List<Detonator> detonators) {
//            showToast("是否模拟：" +Globals.isSimUPload);
        Globals.zhongbaoAddress = getStringInfo("zhongbaoAddress");
        ReportServerEnum reportServerEnum = ReportServerEnum.getByName(Globals.zhongbaoAddress);

        XLog.d("zhognbao: reportServerEnum "+ Globals.zhongbaoAddress+ reportServerEnum);
        new Thread(() -> {
            List<String> msgs = createMessageList(detonators);
            sendRptToZhongBao(msgs);
        }).start();
    }


    void showSendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("是否上传此数据？");
        //设置对话框标题
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setPositiveButton("确认", (dialog, which) -> {
            dialog.dismiss();
            sendReport();

        });
        builder.setNegativeButton("取消", null);
        // 4.设置常用api，并show弹出
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(false); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }


    private void sendDanLingReport() {
        allSize = reportDtos.size();
        showProgressBar("上传数据！",allSize*2);
        step = 0;
        sendRptToDanling(reportDtos.get(step));


    }

    private void sendRptToDanling(ReportDto reportDto) {
        String rptJson =  JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        XLog.v(rptJson);
        // jiangsheng
        Result result = RptUtil.getRptEncode(rptJson);
        if (!result.isSuccess()) {
            showToast("数据编码出错：" + result.getMessage());
            return;
        }
        XLog.d("param:" + result.getData());
        String  url = AppConstants.DanningServer + AppConstants.ProjectReport;
//        XLog.v("url:", url);
        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params, "UTF-8");

//        FileUtils.saveFileToSDcard("detonator/report", "report-" + DateUtil.getDateStr(new Date()) + ".json", rptJson + "\n" + result.getData().toString());

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
//                        Integer code = Integer.parseInt(serverResult.getCwxx());
//                        ResultErrEnum errEnum = ResultErrEnum.getBycode(code);
                        XLog.e("错误代码：", serverResult.getCwxxms());
//                        showToast("错误代码：" + serverResult.getCwxxms());
//                        detController.setStatus(2);
//                        reportDao.updateController(detController);
                        sendCmdMessage(MSG_RPT_DANLING_ERR);
                    } else {
                        sendCmdMessage(MSG_RPT_DANLING_OK);
//                        result = ActivityResult.successOf("上传丹灵服务器成功!");
                    }

                } catch (Exception e) {
                    XLog.e("解析错误：" + e.getMessage());
//                    showLongToast("解析错误：" + e.getMessage());
                    sendCmdMessage(MSG_RPT_DANLING_ERR);

                }


            }
        });
    }

    private void sendReport2ETEKTest() {

        showProgressBar("上传数据！", reportDtos.size());
        step = 0;
        allSize = reportDtos.size();
        sendRptToEtekServer(reportDtos.get(step));


    }

    private void sendRptToEtekServer(ReportDto reportDto) {
        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        XLog.d(rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        XLog.d(result);
        String url = AppConstants.ETEKTestServer + AppConstants.ProjectReportTest;

        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params);

        XLog.d("len:"+newUrl.length());
//            FileUtils.saveFileToSDcard("detonator/et-report", "report-" + DateUtil.getDateStr(new Date()) + ".json", rptJson + "\n" + result.getData().toString());
//        FileUtils.saveFileToSDcard("detonator/json", "report-" + DateUtil.getDateStr(new Date()) + ".json", result.getData().toString());
        AsyncHttpCilentUtil.httpPost(newUrl, null, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                XLog.e("IOException:", e.getMessage());
                sendCmdMessage(MSG_RPT_ETEK_TEST_ERR);
//                    stopLoad();
//                showToast("丹灵服务器报错");
//                closeDialog();
//                sendCmdMessage(MSG_RPT_DANLING_ERR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                String respStr = response.body().string();
                if (StringUtils.isBlank(respStr)) {
                    XLog.w("respStr is null ");
                    sendCmdMessage(MSG_RPT_ETEK_TEST_ERR);
//                    showToast("丹灵上报返回值为空");
//                    sendCmdMessage(MSG_RPT_DANLING_ERR);
                    return;
                }


                ServerResult serverResult = null;
                try {
                    serverResult = JSON.parseObject(respStr, ServerResult.class);
                    if (!serverResult.getSuccess().contains("0")) {
                        Integer code = Integer.parseInt(serverResult.getSuccess());
                        ResultErrEnum errEnum = ResultErrEnum.getBycode(code);
                        XLog.e("错误代码：", errEnum.getMessage());
                        showToast("上传ETEK服务器失败!");
//                        detController.setStatus(2);
//                        reportDao.updateController(detController);
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
    private void sendRptToEtekServerBck(ReportDto reportDto) {
        String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
        XLog.d(rptJson);
        Result result = RptUtil.getRptEncode(rptJson);
        XLog.d(result);
        String url = AppConstants.ETEKTestServer + AppConstants.ProjectReportTest;

        LinkedHashMap params = new LinkedHashMap();
        params.put("param", result.getData());    //
        String newUrl = SommerUtils.attachHttpGetParams(url, params);

        XLog.d("len:"+newUrl.length());
//            FileUtils.saveFileToSDcard("detonator/et-report", "report-" + DateUtil.getDateStr(new Date()) + ".json", rptJson + "\n" + result.getData().toString());
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
//                    showToast("丹灵上报返回值为空");
//                    sendCmdMessage(MSG_RPT_DANLING_ERR);
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

    private void sendReportToETEKBck() {
//        step = 0;
        sendRptToEtekServerBck(reportDtos.get(step-allSize));


    }


    List<ReportDto> getReportDto(DetController detController) {
        List<ReportDto> reportDtos = new ArrayList<>();
        List<Detonator> detList = detController.getDetList();
        if (detList != null && !detList.isEmpty()) {
            List<List<Detonator>> lists = ListUtil.fixedGrouping(detList, MAX_GROUP);
            for (List<Detonator> list : lists) {
                ReportDto reportDto = new ReportDto();
                reportDto.setDetControllerWithoutDet(detController);
                reportDto.setDets(list);
                if (StringUtils.isEmpty(reportDto.getDwdm())) {
                    reportDto.setDwdm(Globals.user.getCompanyCode());
                }
//                String rptJson = JSON.toJSONString(reportDto, SerializerFeature.WriteMapNullValue);
////        XLog.d(rptJson);
//                Result result = RptUtil.getRptEncode(rptJson);
                if(!StringUtils.isEmpty(reportDto.getUid())){
                    reportDtos.add(reportDto);
                }

            }

        }
        XLog.w("size:" + reportDtos.size());
        return reportDtos;
    }
}
