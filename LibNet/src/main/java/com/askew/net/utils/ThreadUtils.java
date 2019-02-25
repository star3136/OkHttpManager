package com.askew.net.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lihoudong204 on 2018/11/21
 */
public class ThreadUtils {
    private static ExecutorService executor;
    private static Handler handler;

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private static Handler getHandler() {
        if (handler == null) {
            synchronized (ThreadUtils.class) {
                if (handler == null) {
                    handler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return handler;
    }

    private static ExecutorService getExecutor() {
        if (executor == null) {
            synchronized (ThreadUtils.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(0, 1, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
                }
            }
        }
        return executor;
    }

    public static void submit(final Runnable otherThread, final Runnable currentThread) {
        if (isMainThread()) {
            getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    otherThread.run();
                    getHandler().post(currentThread);
                }
            });
        }else {
            otherThread.run();
            currentThread.run();
        }
    }
}
