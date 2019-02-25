package com.askew.net.method;

import com.askew.net.callback.OkHttpCallback;
import com.askew.net.disposable.IDisposable;
import com.askew.net.lifecycle.BindObserver;
import com.askew.net.schedulers.IScheduler;

import okhttp3.Interceptor;

/**
 * Created by lihoudong204 on 2018/11/15
 */
public interface IHttpMethod {
    IHttpMethod withHeader(String name, String value);

    IHttpMethod withParam(String name, String value);

    IHttpMethod withHttpInterceptor(Interceptor interceptor);

    IHttpMethod withNetworkHttpInterceptor(Interceptor interceptor);

    IHttpMethod tag(Object tag);

    Object getTag();

    IHttpMethod saveToFile(String fileDir, String fileName);

    <R> IDisposable call(final OkHttpCallback<R> callback);

    IHttpMethod requestOn(IScheduler scheduler);

    IHttpMethod responseOn(IScheduler scheduler);

    IHttpMethod bindUntil(BindObserver bindObserver);
}
