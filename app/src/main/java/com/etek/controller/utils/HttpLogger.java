<<<<<<< HEAD
package com.etek.controller.utils;

import com.elvishew.xlog.XLog;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger {

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    @Override
    public void log(String message) {
        try {
//            message = new String(message.getBytes("GB2312"),UTF8);
            message = new String(message.getBytes(),UTF8);
        } catch (Exception e) {
//            e.printStackTrace();
            XLog.tag("HttpLogger").e(e);
        }
        XLog.tag("HttpLogger").d( message);


    }
}
=======
package com.etek.controller.utils;

import com.elvishew.xlog.XLog;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger {

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    @Override
    public void log(String message) {
        try {
//            message = new String(message.getBytes("GB2312"),UTF8);
            message = new String(message.getBytes(),UTF8);
        } catch (Exception e) {
//            e.printStackTrace();
            XLog.tag("HttpLogger").e(e);
        }
        XLog.tag("HttpLogger").d( message);


    }
}
>>>>>>> 806c842... 雷管组网
