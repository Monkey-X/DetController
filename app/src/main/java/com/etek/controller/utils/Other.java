<<<<<<< HEAD
package com.etek.controller.utils;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.etek.controller.activity.ReportDetailActivity;
import com.etek.controller.common.AppConstants;
import com.etek.controller.dto.ReportDto;
import com.etek.controller.minaclient.MessageCodecFactory;
import com.etek.controller.persistence.DBManager;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Other {
//    void testRename(){
//        File file = mContext.getDatabasePath(DBManager.DB_NAME);
//
////                    String currentApkPath = mContext.getPackageResourcePath();
////                    File apkFile = new File(currentApkPath);
//        Map<String, String> appInfo = AppUtils.getAppInfo(mContext);
//        XLog.v(JSON.toJSONString(appInfo));
//        File file2 = new File(FileUtils.ExternalStorageDirectory,appInfo.get("immi")+"_"+ DateUtil.getDateDoc(new Date())+"_"+"det.db");
//        XLog.v(file2);
//        FileUtils.copyFile(file,file2,false);
//        XLog.v(file2);
//    }

    void testCrl() {
        String u1 = "{\"bprysfz\":\"533421198207090717\",\"bpsj\":\"2020-07-16 14:48:32\",\"dwdm\":\"5301034300004\",\"htid\":\"533421319120010\",\"jd\":\"99.895200\",\"sbbh\":\"F61A8200045\",\"uid\":\"6120A82F80B229,6120A82F80B23E,6120A82F80B23F,6120A82F80B239,6120A82F80B235,6120A82F80B231,6120A82F80B236,6120A82F80B23D,6120A82F80B23B,6120A82F80B227,6120A82F80B22A,6120A82F80B228,6120A82F80B22E,6120A82F80B232,6120A82F80B224,6120A82F80B237,6120A82F80B238,6120A82F80B23A,6120A82F80B22F,6120A82F80B22B,6120A82F80B225,6120A82F80B226,6120A82F80B230,6120A82F80B22C,6120A82F80B22D,6120A82F80B23C,6120A82F80B233,6120A82F80B234\",\"wd\":\"28.119450\",\"xmbh\":\"\"}";
        XLog.json(u1);

        ReportDto reportDto = new ReportDto();
        Result result = RptUtil.getRptEncode(u1);

        String urlString = null;
        try {
            urlString = URLEncoder.encode((String)result.getData(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("URL:"+urlString);

    }

    private void sendMsgToEtek(List<String> detMsgs) {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast(
                    "encode",
                    new ProtocolCodecFilter(new MessageCodecFactory()));

            connector.getSessionConfig().setReadBufferSize(2048);
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10000);
            connector.setConnectTimeoutMillis(1000 * 60 * 3);

//            connector.setHandler(new ReportDetailActivity.MinaHandler());
//            ConnectFuture cf = connector.connect(new InetSocketAddress("192.168.1.73",
//                    6088));
            ConnectFuture cf;

            cf = connector.connect(new InetSocketAddress(AppConstants.ETEK_HTTP,
                    AppConstants.ETEK_PORT));

            cf.awaitUninterruptibly();
            for (int j = 0; j < detMsgs.size(); j++) {

                byte[] bs = detMsgs.get(j).getBytes();

                cf.getSession().write(bs);
            }

            cf.getSession().getCloseFuture().awaitUninterruptibly();

            connector.dispose();
        } catch (Exception e) {
            XLog.e(e.getMessage());

        } finally {
            XLog.d("detMsgs finished");
        }


    }
}
=======
package com.etek.controller.utils;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.XLog;
import com.etek.controller.activity.ReportDetailActivity;
import com.etek.controller.common.AppConstants;
import com.etek.controller.dto.ReportDto;
import com.etek.controller.minaclient.MessageCodecFactory;
import com.etek.controller.persistence.DBManager;
import com.etek.sommerlibrary.dto.Result;
import com.etek.sommerlibrary.utils.DateUtil;
import com.etek.sommerlibrary.utils.FileUtils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Other {
//    void testRename(){
//        File file = mContext.getDatabasePath(DBManager.DB_NAME);
//
////                    String currentApkPath = mContext.getPackageResourcePath();
////                    File apkFile = new File(currentApkPath);
//        Map<String, String> appInfo = AppUtils.getAppInfo(mContext);
//        XLog.v(JSON.toJSONString(appInfo));
//        File file2 = new File(FileUtils.ExternalStorageDirectory,appInfo.get("immi")+"_"+ DateUtil.getDateDoc(new Date())+"_"+"det.db");
//        XLog.v(file2);
//        FileUtils.copyFile(file,file2,false);
//        XLog.v(file2);
//    }

    void testCrl() {
        String u1 = "{\"bprysfz\":\"533421198207090717\",\"bpsj\":\"2020-07-16 14:48:32\",\"dwdm\":\"5301034300004\",\"htid\":\"533421319120010\",\"jd\":\"99.895200\",\"sbbh\":\"F61A8200045\",\"uid\":\"6120A82F80B229,6120A82F80B23E,6120A82F80B23F,6120A82F80B239,6120A82F80B235,6120A82F80B231,6120A82F80B236,6120A82F80B23D,6120A82F80B23B,6120A82F80B227,6120A82F80B22A,6120A82F80B228,6120A82F80B22E,6120A82F80B232,6120A82F80B224,6120A82F80B237,6120A82F80B238,6120A82F80B23A,6120A82F80B22F,6120A82F80B22B,6120A82F80B225,6120A82F80B226,6120A82F80B230,6120A82F80B22C,6120A82F80B22D,6120A82F80B23C,6120A82F80B233,6120A82F80B234\",\"wd\":\"28.119450\",\"xmbh\":\"\"}";
        XLog.json(u1);

        ReportDto reportDto = new ReportDto();
        Result result = RptUtil.getRptEncode(u1);

        String urlString = null;
        try {
            urlString = URLEncoder.encode((String)result.getData(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("URL:"+urlString);

    }

    private void sendMsgToEtek(List<String> detMsgs) {
        try {
            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast(
                    "encode",
                    new ProtocolCodecFilter(new MessageCodecFactory()));

            connector.getSessionConfig().setReadBufferSize(2048);
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10000);
            connector.setConnectTimeoutMillis(1000 * 60 * 3);

//            connector.setHandler(new ReportDetailActivity.MinaHandler());
//            ConnectFuture cf = connector.connect(new InetSocketAddress("192.168.1.73",
//                    6088));
            ConnectFuture cf;

            cf = connector.connect(new InetSocketAddress(AppConstants.ETEK_HTTP,
                    AppConstants.ETEK_PORT));

            cf.awaitUninterruptibly();
            for (int j = 0; j < detMsgs.size(); j++) {

                byte[] bs = detMsgs.get(j).getBytes();

                cf.getSession().write(bs);
            }

            cf.getSession().getCloseFuture().awaitUninterruptibly();

            connector.dispose();
        } catch (Exception e) {
            XLog.e(e.getMessage());

        } finally {
            XLog.d("detMsgs finished");
        }


    }
}
>>>>>>> 806c842... 雷管组网
