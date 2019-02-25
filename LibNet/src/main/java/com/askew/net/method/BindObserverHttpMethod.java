package com.askew.net.method;

import com.askew.net.callback.OkHttpCallback;
import com.askew.net.disposable.IDisposable;
import com.askew.net.lifecycle.BindObserver;
import com.askew.net.schedulers.IScheduler;

/**
 * Created by lihoudong204 on 2018/11/19
 * 绑定生命周期的HttpMethod
 */
public class BindObserverHttpMethod extends WrapHttpMethod {
    private BindObserver bindObserver;

    public BindObserverHttpMethod(IHttpMethod delegate, RequestChainParam requestChainParam, BindObserver bindObserver) {
        super(delegate, requestChainParam);
        this.bindObserver = bindObserver;
    }


    @Override
    public IHttpMethod requestOn(IScheduler scheduler) {
        return new HttpMethodRequestOn(this, requestChainParam, scheduler);
    }

    @Override
    public IHttpMethod responseOn(IScheduler scheduler) {
        return new HttpMethodResponseOn(this, requestChainParam, scheduler);
    }

    @Override
    public <R> IDisposable call(OkHttpCallback<R> callback) {
        OkHttpCallback<R> callbackDisposable = getOkHttpCallbackDisposable(callback);
        bindObserver.bind((IDisposable) callbackDisposable);

        if (requestChainParam.isUseDefaultThreadPool() || !(delegate instanceof HttpMethod)) {
            delegate.call(callbackDisposable);
        } else {
            ((HttpMethod) delegate).callSync(callbackDisposable);
        }
        return (IDisposable) callbackDisposable;
    }

    @Override
    public IHttpMethod bindUntil(BindObserver bindObserver) {
        return new BindObserverHttpMethod(this, requestChainParam, bindObserver);
    }
}
