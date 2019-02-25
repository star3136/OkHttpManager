package com.askew.net.method;

import com.askew.net.lifecycle.BindObserver;
import com.askew.net.schedulers.IScheduler;

/**
 * Created by lihoudong204 on 2018/11/15
 * 切换线程的HttpMethod基类
 */
public abstract class AbstractSchedulerHttpMethod extends WrapHttpMethod {
    protected IScheduler scheduler;

    public AbstractSchedulerHttpMethod(IHttpMethod delegate, RequestChainParam requestChainParam, IScheduler scheduler) {
        super(delegate, requestChainParam);
        this.scheduler = scheduler;
    }

    @Override
    public IHttpMethod bindUntil(BindObserver bindObserver) {
        return new BindObserverHttpMethod(this, requestChainParam, bindObserver);
    }
}
