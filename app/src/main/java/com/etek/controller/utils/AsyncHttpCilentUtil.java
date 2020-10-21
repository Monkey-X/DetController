package com.etek.controller.utils;

import android.content.Context;



import com.loopj.android.http.AsyncHttpClient;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.RequestBody;
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


}
