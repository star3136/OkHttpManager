package com.askew.net.schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by lihoudong204 on 2018/11/15
 * IO线程调度器
 */
public class IoScheduler implements IScheduler {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    @Override
    public Future schedule(Runnable task) {
        return executor.submit(task);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }
}
