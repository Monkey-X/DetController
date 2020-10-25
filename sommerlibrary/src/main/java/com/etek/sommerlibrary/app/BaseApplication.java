package com.etek.sommerlibrary.app;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;


import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;

import com.elvishew.xlog.printer.AndroidPrinter;

import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;

import com.elvishew.xlog.printer.file.backup.BackupStrategy;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.etek.sommerlibrary.BuildConfig;
import com.etek.sommerlibrary.R;
import com.etek.sommerlibrary.exception.MyCrashHandler;
import com.etek.sommerlibrary.utils.ContextUtils;

import java.io.File;




public class BaseApplication extends Application {


    public static Context appContext;
    public static Printer globalFilePrinter;

    private static final long MAX_TIME = 1000 * 60 * 60 * 24 * 2; // two days

    @Override
    public void onCreate() {

        super.onCreate();
        // 程序创建的时候执行

        appContext = this;
        ContextUtils.init(this);

        //        if (BuildConfig.DEBUG) {
        //Timber日志打印
//        Timber.plant(new Timber.DebugTree());
//        }
        if (!BuildConfig.DEBUG) {
            MyCrashHandler handler = new MyCrashHandler();
            Thread.setDefaultUncaughtExceptionHandler(handler);
        }

        initXlog();
    }


    /**
     * Initialize XLog.
     */
    private void initXlog() {

        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                        : LogLevel.VERBOSE)
                .tag(getString(R.string.global_tag))                   // Specify TAG, default: "X-LOG"
                // .t()                                                // Enable thread info, disabled by default
                // .st(2)                                              // Enable stack trace info with depth 2, disabled by default
                // .b()                                                // Enable border, disabled by default
                // .jsonFormatter(new MyJsonFormatter())               // Default: DefaultJsonFormatter
                // .xmlFormatter(new MyXmlFormatter())                 // Default: DefaultXmlFormatter
                // .throwableFormatter(new MyThrowableFormatter())     // Default: DefaultThrowableFormatter
                // .threadFormatter(new MyThreadFormatter())           // Default: DefaultThreadFormatter
                // .stackTraceFormatter(new MyStackTraceFormatter())   // Default: DefaultStackTraceFormatter
                // .borderFormatter(new MyBoardFormatter())            // Default: DefaultBorderFormatter
                // .addObjectFormatter(AnyClass.class,                 // Add formatter for specific class of object
                //     new AnyClassObjectFormatter())                  // Use Object.toString() by default
//                .addInterceptor(new BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
//                        "blacklist1", "blacklist2", "blacklist3"))
                // .addInterceptor(new WhitelistTagsFilterInterceptor( // Add whitelist tags filter
                //     "whitelist1", "whitelist2", "whitelist3"))
                // .addInterceptor(new MyInterceptor())                // Add a log interceptor
                .build();

        Printer androidPrinter = new AndroidPrinter();             // Printer that print the log using android.util.Log
        Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
                .Builder(new File(Environment.getExternalStorageDirectory(), "detonation").getPath())       // Specify the path to save log file
                .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                .backupStrategy(new FileSizeBackupStrategy(1024 * 1024 * 10) )
                // .backupStrategy(new MyBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
                // .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // Default: NeverCleanStrategy()
                .flattener(new ClassicFlattener())                     // Default: DefaultFlattener
                .build();

        XLog.init(                                                 // Initialize XLog
                config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                androidPrinter,                                        // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
                filePrinter);

        // For future usage: partial usage in MainActivity.
        globalFilePrinter = filePrinter;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context appContext) {
        BaseApplication.appContext = appContext;
    }

    @Override
    public void onTerminate() {


        super.onTerminate();

    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {

        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }


}
