package com.askew.net.schedulers;

import java.util.concurrent.Future;

/**
 * Created by lihoudong204 on 2018/11/15
 * 线程调度接口
 */
public interface IScheduler {
    Future schedule(Runnable task);

    void shutdown();
}
