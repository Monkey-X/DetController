package com.etek.controller.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


import com.etek.controller.common.AppConstants;

import java.io.File;

import com.elvishew.xlog.XLog;

/**
 * 更新APP
 */
public class DownLoadAppService extends Service {

    private DownloadManager manager;
    private DownloadCompleteReceiver receiver;
    private String url;
    private String DOWNLOADPATH = "/detonator/apk/";


//    private void initDownManager() {
//        XLog.d("url:", url);
//        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        receiver = new DownloadCompleteReceiver();
//        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url));
//        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
//                | DownloadManager.Request.NETWORK_WIFI);
//        down.setAllowedOverRoaming(false);
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
//        down.setMimeType(mimeString);
//        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//        down.setVisibleInDownloadsUi(true);
//        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "detonator.apk");
//        down.setTitle("detonator");
//        manager.enqueue(down);
//        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//    }

    private void initDownManager() {
        receiver = new DownloadCompleteReceiver();
        if (url == null || url.isEmpty()) {
            XLog.e("请填写\"App下载地址\"");
            return;
        }
        XLog.d("url:", url);

        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        request.setTitle("DET APP更新下载");
//        request.setDescription(description);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,  "detonator.apk");

        manager = (DownloadManager)  getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    /**
     * 下载Apk, 并设置Apk地址,
     * 默认位置: /storage/sdcard0/Download
     *
     * @param context  上下文
     * @param appUrl   更新信息
     * @param infoName 通知名称
     * @param storeApk 存储的Apk
     */
    @SuppressWarnings("unused")
    public static void downloadApk(
            Context context, String appUrl,
            String infoName, String storeApk
    ) {

//        String description = updateInfo.data.descriponFailuretion;
//        String appUrl = updateInfo.data.appURL;

        if (appUrl == null || appUrl.isEmpty()) {
            XLog.e("请填写\"App下载地址\"");
            return;
        }

        appUrl = appUrl.trim(); // 去掉首尾空格

        if (!appUrl.startsWith("http")) {
            appUrl = "http://" + appUrl; // 添加Http信息
        }

        XLog.d("appUrl: " + appUrl);

        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(appUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        request.setTitle(infoName);
//        request.setDescription(description);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, storeApk);

        Context appContext = context.getApplicationContext();
        DownloadManager manager = (DownloadManager)
                appContext.getSystemService(Context.DOWNLOAD_SERVICE);

        // 存储下载Key
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
        sp.edit().putLong(AppConstants.DOWNLOAD_APK_ID_PREFS, manager.enqueue(request)).apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        url = intent.getStringExtra("downloadurl");
        String path = Environment.DIRECTORY_DOWNLOADS + "detonator.apk";
        XLog.d("path:", path);
        File file = new File(path);
        if (file.exists()) {
            deleteFileWithPath(path);
        }
        try {
            initDownManager();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent0 = new Intent(Intent.ACTION_VIEW, uri);
                intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent0);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    class DownloadCompleteReceiver extends BroadcastReceiver {
        private static final int INSTALL_APK_REQUESTCODE = 1;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (manager.getUriForDownloadedFile(downId) != null) {
                    XLog.d("downId:" + downId);

                    installAPK(context, getRealFilePath(context, manager.getUriForDownloadedFile(downId)));

                } else {
                    XLog.e("下载失败");

                }
                DownLoadAppService.this.stopSelf();
            }
        }

        private void installAPK(Context context, String path) {
            File file = new File(path);
            if (file.exists()) {
                openFile(file, context);
            } else {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public void openFile(File file, Context mContext) {
        try {
            Intent addIntent = new Intent();
            addIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            addIntent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uriForFile = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                addIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//            addIntent.setDataAndType(uriForFile, mContext.getContentResolver().getType(uriForFile));
                addIntent.setDataAndType(uriForFile, "application/vnd.android.package-archive");

            } else {
                addIntent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
            }

            mContext.startActivity(addIntent);
        } catch (Exception exp) {
            exp.printStackTrace();
            XLog.e(exp.getMessage().toString());
            Toast.makeText(mContext, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            f.delete();
            return true;
        }
        return false;
    }

}
