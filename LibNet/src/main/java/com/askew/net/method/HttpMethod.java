package com.askew.net.method;

import android.text.TextUtils;

import com.askew.net.IHttpProcessor;
import com.askew.net.OkException;
import com.askew.net.callback.OkHttpCallback;
import com.askew.net.call.DefaultSyncOkHttpCallback;
import com.askew.net.call.OkAsyncCall;
import com.askew.net.call.OkCall;
import com.askew.net.call.OkSyncCall;
import com.askew.net.disposable.IDisposable;
import com.askew.net.lifecycle.BindObserver;
import com.askew.net.schedulers.IScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by lihoudong204 on 2018/9/26
 * Http方法的基类，主要显实现了GET、POST方法
 */
public abstract class HttpMethod implements IHttpMethod{
    protected Map<String, String> httpHeaders = new HashMap<>();
    protected Map<String, String> httpParams = new HashMap<>();
    protected Call call;
    protected String fileDir;
    protected String fileName;
    protected Object tag;
    protected List<Interceptor> httpInterceptors;
    protected List<Interceptor> networkInterceptors;

    public HttpMethod withHeader(String name, String value) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
            return this;
        }
        if (httpHeaders == null) {
            httpHeaders = new HashMap<>();
        }
        httpHeaders.put(name, value);
        return this;
    }

    public HttpMethod withParam(String name, String value) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
            return this;
        }
        if (httpParams == null) {
            httpParams = new HashMap<>();
        }
        httpParams.put(name, value);
        return this;
    }

    @Override
    public IHttpMethod withHttpInterceptor(Interceptor interceptor) {
        if (httpInterceptors == null) {
            httpInterceptors = new ArrayList<>();
        }
        this.httpInterceptors.add(interceptor);
        return this;
    }

    @Override
    public IHttpMethod withNetworkHttpInterceptor(Interceptor interceptor) {
        if (networkInterceptors == null) {
            networkInterceptors = new ArrayList<>();
        }
        networkInterceptors.add(interceptor);
        return this;
    }

    public HttpMethod tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public IHttpMethod bindUntil(BindObserver bindObserver) {
        return new BindObserverHttpMethod(this, new WrapHttpMethod.RequestChainParam(true), bindObserver);
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public IHttpMethod saveToFile(String fileDir, String fileName) {
        this.fileDir = fileDir;
        this.fileName = fileName;
        return this;
    }

    @Override
    public IHttpMethod requestOn(IScheduler scheduler) {
        return new HttpMethodRequestOn(this, new WrapHttpMethod.RequestChainParam(false), scheduler);
    }

    @Override
    public IHttpMethod responseOn(IScheduler scheduler) {
        return new HttpMethodResponseOn(this, new WrapHttpMethod.RequestChainParam(true), scheduler);
    }

    public <R> IDisposable call(final OkHttpCallback<R> callback) {
        Request request = newRequest();

        OkCall okCall = new OkAsyncCall<R>(request, fileDir, fileName, tag, callback);
        return okCall.call();
    }


    public <R> R callSync(Class<R> cls) throws OkException {
        Request request = newRequest();

        DefaultSyncOkHttpCallback<R> defaultCallback = new DefaultSyncOkHttpCallback<>();
        OkCall<R> okCall = new OkSyncCall<>(request, fileDir, fileName, tag, defaultCallback, cls);
        okCall.call();

        try {
            R response = defaultCallback.getResponse();
            if (response != null) {
                return response;
            }else {
                throw new OkException(defaultCallback.getErrorMsg());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new OkException(e.getMessage());
        }
    }

    public <R> IDisposable callSync(OkHttpCallback<R> callback) {
        Request request = newRequest();

        OkCall<R> okCall = new OkSyncCall<>(request, fileDir, fileName, tag, callback, null);
        return okCall.call();
    }

    abstract Request newRequest();

    protected void addHeaders(Request.Builder builder, Map<String, String> httpHeaders) {
        if (httpHeaders == null || httpHeaders.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
    }
}
