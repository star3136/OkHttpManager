package com.askew.net.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by lihoudong204 on 2018/11/15
 * 指定线程数量的调度器
 */
public class CustomScheduler implements IScheduler {
    private ExecutorService executor;

    public CustomScheduler(int maxCount) {
        executor = Executors.newFixedThreadPool(maxCount);
    }

    @Override
    public Future schedule(Runnable task) {
        Future future = executor.submit(task);
        return future;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }
}
