package com.askew.net.method;

import com.askew.net.IHttpProcessor;
import com.askew.net.callback.OkHttpCallback;
import com.askew.net.callback.OkHttpProgressCallback;
import com.askew.net.disposable.OkHttpCallbackDisposable;
import com.askew.net.disposable.OkHttpProgressCallbackDisposable;
import com.askew.net.disposable.IDisposable;
import com.askew.net.lifecycle.BindObserver;
import com.askew.net.schedulers.IScheduler;

import okhttp3.Interceptor;

/**
 * Created by lihoudong204 on 2018/11/19
 */
public class WrapHttpMethod implements IHttpMethod {
    protected IHttpMethod delegate;
    protected RequestChainParam requestChainParam;

    public WrapHttpMethod(IHttpMethod delegate, RequestChainParam requestChainParam) {
        this.delegate = delegate;
        this.requestChainParam = requestChainParam;
    }

    @Override
    public IHttpMethod withHeader(String name, String value) {
        delegate.withHeader(name, value);
        return this;
    }

    @Override
    public IHttpMethod withParam(String name, String value) {
        delegate.withParam(name, value);
        return this;
    }

    @Override
    public IHttpMethod withHttpInterceptor(Interceptor interceptor) {
        delegate.withHttpInterceptor(interceptor);
        return this;
    }

    @Override
    public IHttpMethod withNetworkHttpInterceptor(Interceptor interceptor) {
        delegate.withNetworkHttpInterceptor(interceptor);
        return this;
    }

    @Override
    public IHttpMethod tag(Object tag) {
        delegate.tag(tag);
        return this;
    }

    @Override
    public Object getTag() {
        return delegate.getTag();
    }

    @Override
    public IHttpMethod saveToFile(String fileDir, String fileName) {
        delegate.saveToFile(fileDir, fileName);
        return this;
    }

    @Override
    public <R> IDisposable call(OkHttpCallback<R> callback) {
        return delegate.call(callback);
    }

    @Override
    public IHttpMethod requestOn(IScheduler scheduler) {
        delegate.requestOn(scheduler);
        return this;
    }

    @Override
    public IHttpMethod responseOn(IScheduler scheduler) {
        delegate.responseOn(scheduler);
        return this;
    }

    @Override
    public IHttpMethod bindUntil(BindObserver bindObserver) {
        delegate.bindUntil(bindObserver);
        return this;
    }


    static class RequestChainParam {
        boolean useDefaultThreadPool;

        public RequestChainParam(boolean useDefaultThreadPool) {
            this.useDefaultThreadPool = useDefaultThreadPool;
        }

        public boolean isUseDefaultThreadPool() {
            return useDefaultThreadPool;
        }
    }

    protected <T> OkHttpCallback<T> getOkHttpCallbackDisposable(OkHttpCallback<T> callback) {
        if (callback instanceof OkHttpProgressCallback) {
            return new OkHttpProgressCallbackDisposable<>((OkHttpProgressCallback<T>) callback, getTag());
        }else {
            return new OkHttpCallbackDisposable<>(callback, getTag());
        }
    }
}
