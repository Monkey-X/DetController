package com.etek.controller.utils;

import android.app.Activity;
import android.content.Context;


import com.etek.controller.hardware.test.HttpCallback;
import com.loopj.android.http.AsyncHttpClient;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;



/**
 * Created by Sommer on 2015/10/28.
 */
public class AsyncHttpCilentUtil {
    private static AsyncHttpClient client;

    public static AsyncHttpClient getInstance(Context paramContext) {
        if (client == null) {
            client = new AsyncHttpClient();
            client.setLoggingEnabled(true);
            client.setTimeout(3000);
//            PersistentCookieStore myCookieStore = new PersistentCookieStore(paramContext);
//            client.setCookieStore(myCookieStore);
        }
        return client;
    }


    public static void getOkHttpClient(String url, Callback httpCallBack) {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .addNetworkInterceptor(logInterceptor)
                .build();

        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        //        formBody.add("strRecv",token);//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .get()//传递请求体
                .build();
        client.newCall(request).enqueue(httpCallBack);
//        new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                XLog.e(LOG_TAG,"onFailure:"+call);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                XLog.d(LOG_TAG,"onSuccess:"+response.toString());
//            }});//回调方法的使用与get异步请求相同，此时略。
    }
    /**
     * Post请求 异步
     * 使用 Callback 回调可返回子线程中获得的网络数据
     *
     * @param url
     * @param params 参数
     */
    public static void httpPost(final String url, final Map<String, String> params, final Callback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if(params!=null){
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    String value = params.get(key);
                    formBodyBuilder.add(key, value);
                }
            }

            FormBody formBody = formBodyBuilder.build();
            Request request = new Request
                    .Builder()
                    .post(formBody)
                    .url(url)
                    .build();
            //Response response = null;
            okHttpClient.newCall(request).enqueue(callback);
        }).start();
    }

    public static void httpPostNew(Activity activity,final String url, final Map<String, String> params, final HttpCallback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if(params!=null){
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    String value = params.get(key);
                    formBodyBuilder.add(key, value);
                }
            }

            FormBody formBody = formBodyBuilder.build();
            Request request = new Request
                    .Builder()
                    .post(formBody)
                    .url(url)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback!=null) {
                                callback.onFaile(e);
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback!=null) {
                                callback.onSuccess(response);
                            }
                        }
                    });
                }
            });
        }).start();
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void httpPostJson(final String url, final String jsonStr, final Callback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request
                    .Builder()
                    .post(body)
                    .url(url)
                    .build();
            //Response response = null;
            okHttpClient.newCall(request).enqueue(callback);
        }).start();
    }


    /**
     * Get请求
     */
    public static void httpGet(final String url, final Callback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();

            Request request = new Request
                    .Builder()
                    .get()
                    .url(url)
                    .build();
            okHttpClient.newCall(request).enqueue(callback);
        }).start();
    }


    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }


    /**
     * 不认证的https访问
     * @param activity
     * @param url
     * @param params
     * @param callback
     */
    public static void httpsPost(Activity activity,final String url, final Map<String, String> params, final HttpCallback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .sslSocketFactory(createSSLSocketFactory())
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if(params!=null){
                Set<String> keySet = params.keySet();
                for (String key : keySet) {
                    String value = params.get(key);
                    formBodyBuilder.add(key, value);
                }
            }

            FormBody formBody = formBodyBuilder.build();
            Request request = new Request
                    .Builder()
                    .post(formBody)
                    .url(url)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback!=null) {
                                callback.onFaile(e);
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback!=null) {
                                callback.onSuccess(response);
                            }
                        }
                    });
                }
            });
        }).start();
    }

    public static void httpsPostJson(final String url, final String jsonStr, final Callback callback) {
        new Thread(() -> {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .sslSocketFactory(createSSLSocketFactory())
                    .addNetworkInterceptor(logInterceptor)
                    .build();
            RequestBody body = RequestBody.create(JSON, jsonStr);
            Request request = new Request
                    .Builder()
                    .post(body)
                    .url(url)
                    .build();
            //Response response = null;
            okHttpClient.newCall(request).enqueue(callback);
        }).start();
    }

}
