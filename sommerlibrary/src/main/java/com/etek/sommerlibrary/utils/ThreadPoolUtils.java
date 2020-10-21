package com.etek.sommerlibrary.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 */
public class ThreadPoolUtils {

    private static ThreadPoolExecutor threadPoolExecutor;

    public static ThreadPoolExecutor getThreadPool() {
        if (threadPoolExecutor == null) {
            int i = Runtime.getRuntime().availableProcessors();
            threadPoolExecutor = new ThreadPoolExecutor(i, i * 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
        return threadPoolExecutor;
    }
}
