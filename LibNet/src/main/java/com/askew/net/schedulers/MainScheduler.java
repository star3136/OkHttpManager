package com.askew.net.schedulers;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by lihoudong204 on 2018/11/15
 * UI线程调度器
 */
public class MainScheduler implements IScheduler {
    private static volatile Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public Future schedule(final Runnable task) {
        if (handler == null) {
            return null;
        }
        FutureTask futureTask = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {
                task.run();
                return null;
            }
        });
        handler.post(futureTask);
        return futureTask;
    }

    @Override
    public void shutdown() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
