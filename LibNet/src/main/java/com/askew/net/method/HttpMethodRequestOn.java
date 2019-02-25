package com.askew.net.method;

import com.askew.net.callback.OkHttpCallback;
import com.askew.net.disposable.FutureDisposable;
import com.askew.net.disposable.IDisposable;
import com.askew.net.disposable.IWrapDisposable;
import com.askew.net.schedulers.IScheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * Created by lihoudong204 on 2018/11/15
 * 切换工作线程的HttpMethod
 */
public class HttpMethodRequestOn extends AbstractSchedulerHttpMethod {

    public HttpMethodRequestOn(IHttpMethod delegate, RequestChainParam requestChainParam, IScheduler scheduler) {
        super(delegate, requestChainParam, scheduler);
        requestChainParam.useDefaultThreadPool = false;
    }

    @Override
    public IHttpMethod responseOn(IScheduler scheduler) {
        return new HttpMethodResponseOn(this, requestChainParam, scheduler);
    }

    @Override
    public <R> IDisposable call(final OkHttpCallback<R> callback) {
        final OkHttpCallback<R> okHttpCallbackDisposable = getOkHttpCallbackDisposable(callback);
        //保证okHttpCallbackDisposable的线程安全
        final CountDownLatch latch = new CountDownLatch(1);
        Future future = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (delegate instanceof HttpMethod) {
                    ((HttpMethod) delegate).callSync(okHttpCallbackDisposable);
                } else {
                    delegate.call(okHttpCallbackDisposable);
                }
            }
        });

        ((IWrapDisposable) okHttpCallbackDisposable).setChild(new FutureDisposable(future, okHttpCallbackDisposable, getTag()));
        latch.countDown();
        return (IDisposable) okHttpCallbackDisposable;
    }


}
