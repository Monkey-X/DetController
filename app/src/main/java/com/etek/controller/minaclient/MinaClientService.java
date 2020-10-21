package com.etek.controller.minaclient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import com.elvishew.xlog.XLog;


import com.etek.controller.common.Globals;
import com.etek.controller.dto.ZBRptDetonator;
import com.etek.controller.utils.SommerUtils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;


public class MinaClientService extends Service {

    private static final String TAG = "MinaClientService";


    Context context;
    //    int totolMessage = 0;
//    int currentMessage = 0;
    final static int DETNumTotal = 10000;
    private List<ZBRptDetonator> detonators;
    private static final String MYACTION = "com.etek.upload";
    //    private List<List<String>> detMegs;
    private List<String> detMegs;

    private Handler subHandler;
    Intent intent;
    int count = 0;
    boolean isTimeout = false;

    @Override
    public IBinder onBind(Intent intent) {

        return new Mybinder();
    }

    Timer timer = new Timer();
//    TimerTask task = new TimerTask() {
//
//        @Override
//        public void run() {
//            if(isTimeout){
//                XLog.d(TAG, "time out ="+(count++));
//                if(count>20){
//                    count = 0;
//                    isTimeout = false;
//                    intent = new Intent(MYACTION);
//                    intent.putExtra("RecMsg", 3);
//                    sendBroadcast(intent);
//                }
//            }
//            // 需要做的事:发送消息
//
//        }
//    };


    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
//        detonatorDAO = new DetonatorDAO(context);
//        detonators = detonatorDAO.getUnderUpDetonatorsLimit(950);

//		XLog.d(TAG, data);
        createMessageList();
        UploadThread();
//        timer = new Timer();
//        timer.schedule(task, DETNumTotal, DETNumTotal); // 1s后执行task,经过1s再次执行

    }


    public class Mybinder extends Binder {

        public MinaClientService getservice() {

            return MinaClientService.this;

        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        XLog.i( "DownLoadService.onStartCommand()...");
        XLog.e( "flags:  startId: " ,flags , startId);
//        mainMesage();
        return super.onStartCommand(intent, flags, startId);
    }

    public void createMessageList() {
        detMegs = new ArrayList<>();

        List<String> msgs = new ArrayList<String>();
        if (detonators == null)
            return;

        int total = detonators.size();
        XLog.v( "total=", total);


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
        message.setLng(Globals.longitude);
        message.setLat(Globals.latitude);
        message.setSn(Globals.deviceId);
        String timestamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
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
            message.setSn(Globals.deviceId);
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
                String value = detonators.get((j * 10) + k)+ "O";
                message.addDetonator(value);
            }

            msgs.add(new String(message.toByte()));
//XLog.v(TAG,JSON.toJSONString(msgs));


        }


        detMegs = msgs;
//        }
//        XLog.v(TAG,JSON.toJSONString(msgs));
//        FileUtils.saveDetFile("detMegs", JSON.toJSONString(detMegs));
//        System.out.println("message list create");
//		List<DetMessage> det
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    private void sendStart() {
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
            if (Globals.isTest) {
                cf = connector.connect(new InetSocketAddress("222.191.229.234",
                        1089));
            } else {
                cf = connector.connect(new InetSocketAddress("113.140.1.135",
                        9903));
            }

            cf.awaitUninterruptibly();
            for (int j = 0; j < detMegs.size(); j++) {
                XLog.v( detMegs.get(j));
//                XLog.v(TAG, "LEN = "+detMegs.get(j).length());
                byte[] bs = detMegs.get(j).getBytes();
//                XLog.v(TAG,Utils.bytesToHexArrString(detMegs.get(j).getBytes()));
                cf.getSession().write(bs);
            }

            cf.getSession().getCloseFuture().awaitUninterruptibly();

            connector.dispose();
        } catch (Exception e) {
            e.printStackTrace();

            isTimeout = false;
            intent = new Intent(MYACTION);
            intent.putExtra("RecMsg", 2);
            sendBroadcast(intent);
        }

    }

    public void showMesage() {
        isTimeout = true;
//        XLog.v(TAG,"totolMessage:"+totolMessage+"  currentMessage:"+currentMessage);
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("count", 1);
        msg.setData(data);

        subHandler.sendMessage(msg);


    }


    public class MinaHandler extends IoHandlerAdapter {


        public void messageReceived(IoSession session, Object message) {
            if (message == null) {
                XLog.e("MinaHandler",message.toString());
                return;
            }
            byte[] bt = (byte[]) message;
            String msg = new String(bt);
            XLog.v( msg);
            if (!"".equalsIgnoreCase(msg)) {
                String[] cmds = msg.split("\\$");
//                XLog.v(TAG, cmds[0]);
                String cmd = msg.substring(3, 4);
//                XLog.v(TAG, cmd);
                if ("O".equalsIgnoreCase(cmd)) {
                    count = 0;
                    isTimeout = false;
//                    XLog.d(TAG, "Ok ");
                    for (ZBRptDetonator det : detonators) {
                        det.setUpload(true);

                    }

//                    currentMessage++;
//
//                    if(currentMessage<totolMessage){
////                        mainMesage();
//                        showMesage();
//                    }else {
                    intent = new Intent(MYACTION);
                    intent.putExtra("RecMsg", 1);
                    sendBroadcast(intent);
//                    }
//                    mainMesage();
                } else if ("R".equalsIgnoreCase(cmd)) {

                    String data = msg.substring(1, msg.length() - 3);
                    int[] ds = SommerUtils.intStringToInts(data);
                    for (int d : ds) {
                        XLog.v( "d:%d", d);
                    }

                } else {
                    XLog.d( "cmd :", cmd);
                }


            }
//			XLog.d(TAG,"Receiver:" + msg);
//			byte[] lenByte = new byte[2];
//			System.arraycopy(bt,1,lenByte,0,2);
//			String lenStr = new String(lenByte);
//			XLog.v(TAG,lenStr);

//			intent = new Intent(MYACTION);
//			intent.putExtra("RecMsg",s);
//			sendBroadcast(intent);
        }

        public void messageSent(IoSession session, Object message) {

        }

        public void sessionClosed(IoSession session) {
            session.close(true);

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
//            System.out.println("sessionIdle");

        }

        public void sessionOpened(IoSession session) {

        }
    }


    void UploadThread() {
//        XLog.v(TAG, "UploadThread create");
        new Thread(new Runnable() {

            @Override
            public void run() {


                Looper.prepare();
                subHandler = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {

                        sendStart();

                    }

                };

                Looper.loop();

            }

            ;

        }) {

        }.start();
    }


}