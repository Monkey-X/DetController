<<<<<<< HEAD
package com.etek.controller.utils;


import android.util.Log;

import com.etek.controller.coreprogress.ProgressHelper;
import com.etek.controller.coreprogress.ProgressUIListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadHelper {

    public static final String TAG = "UploadHelper";

    public static void upload(String url, File file, ProgressUIListener progressUIListener, Callback callback) {
//		uploadInfo.setText("start upload");
//        String currentApkPath = context.getPackageResourcePath();
//        File apkFile = new File(currentApkPath);
//        String url = "http://222.191.229.234:8078/fileUpload";
//		File file = mContext.getDatabasePath("log.db");
//		LogD(file.toString());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.MINUTES)
                .readTimeout(100, TimeUnit.MINUTES)
                .writeTimeout(100, TimeUnit.MINUTES)
                .build();

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//		Call<ResponseBody> call = service.upload(body);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.addPart(body);
//		bodyBuilder.addFormDataPart("fileName", apkFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), apkFile));
        MultipartBody build = bodyBuilder.build();


        if(progressUIListener==null){
            RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

                //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                @Override
                public void onUIProgressStart(long totalBytes) {
                    super.onUIProgressStart(totalBytes);
                    Log.e("TAG", "onUIProgressStart:" + totalBytes);
//                Toast.makeText(getApplicationContext(), "开始上传：" + totalBytes, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    Log.d("TAG", "=============start===============");
                    Log.d("TAG", "numBytes:" + numBytes);
                    Log.d("TAG", "totalBytes:" + totalBytes);
                    Log.d("TAG", "percent:" + percent);
                    Log.d("TAG", "speed:" + speed);
                    Log.d("TAG", "============= end ===============");
//				uploadProgress.setProgress((int) (100 * percent));
//				uploadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + "  MB/秒");

                }

                //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                @Override
                public void onUIProgressFinish() {
                    super.onUIProgressFinish();
                    Log.e("TAG", "onUIProgressFinish:");
//                Toast.makeText(getApplicationContext(), "结束上传", Toast.LENGTH_SHORT).show();
                }
            });
            builder.post(requestBody);
        }else{
            RequestBody requestBody = ProgressHelper.withProgress(build,progressUIListener);
            builder.post(requestBody);
        }



        Call call = okHttpClient.newCall(builder.build());
        if (callback == null) {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TAG", "=============onFailure===============");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("TAG", "=============onResponse===============");
                    Log.e("TAG", "request headers:" + response.request().headers());
                    Log.e("TAG", "response headers:" + response.headers());
                }
            });
        } else {
            call.enqueue(callback);
        }

    }


=======
package com.etek.controller.utils;


import android.util.Log;

import com.etek.controller.coreprogress.ProgressHelper;
import com.etek.controller.coreprogress.ProgressUIListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadHelper {

    public static final String TAG = "UploadHelper";

    public static void upload(String url, File file, ProgressUIListener progressUIListener, Callback callback) {
//		uploadInfo.setText("start upload");
//        String currentApkPath = context.getPackageResourcePath();
//        File apkFile = new File(currentApkPath);
//        String url = "http://222.191.229.234:8078/fileUpload";
//		File file = mContext.getDatabasePath("log.db");
//		LogD(file.toString());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.MINUTES)
                .readTimeout(100, TimeUnit.MINUTES)
                .writeTimeout(100, TimeUnit.MINUTES)
                .build();

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//		Call<ResponseBody> call = service.upload(body);

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.addPart(body);
//		bodyBuilder.addFormDataPart("fileName", apkFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), apkFile));
        MultipartBody build = bodyBuilder.build();


        if(progressUIListener==null){
            RequestBody requestBody = ProgressHelper.withProgress(build, new ProgressUIListener() {

                //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                @Override
                public void onUIProgressStart(long totalBytes) {
                    super.onUIProgressStart(totalBytes);
                    Log.e("TAG", "onUIProgressStart:" + totalBytes);
//                Toast.makeText(getApplicationContext(), "开始上传：" + totalBytes, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    Log.d("TAG", "=============start===============");
                    Log.d("TAG", "numBytes:" + numBytes);
                    Log.d("TAG", "totalBytes:" + totalBytes);
                    Log.d("TAG", "percent:" + percent);
                    Log.d("TAG", "speed:" + speed);
                    Log.d("TAG", "============= end ===============");
//				uploadProgress.setProgress((int) (100 * percent));
//				uploadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + "  MB/秒");

                }

                //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                @Override
                public void onUIProgressFinish() {
                    super.onUIProgressFinish();
                    Log.e("TAG", "onUIProgressFinish:");
//                Toast.makeText(getApplicationContext(), "结束上传", Toast.LENGTH_SHORT).show();
                }
            });
            builder.post(requestBody);
        }else{
            RequestBody requestBody = ProgressHelper.withProgress(build,progressUIListener);
            builder.post(requestBody);
        }



        Call call = okHttpClient.newCall(builder.build());
        if (callback == null) {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TAG", "=============onFailure===============");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("TAG", "=============onResponse===============");
                    Log.e("TAG", "request headers:" + response.request().headers());
                    Log.e("TAG", "response headers:" + response.headers());
                }
            });
        } else {
            call.enqueue(callback);
        }

    }


>>>>>>> 806c842... 雷管组网
}