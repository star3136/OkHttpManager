package com.askew.net.callback;

import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/12/14
 */
public class WrapOkHttpCallback<T> extends OkHttpCallback<T> {
    protected OkHttpCallback<T> source;

    public WrapOkHttpCallback(OkHttpCallback<T> source) {
        this.source = source;
    }

    @Override
    public void onPrepare(Request request) {
        source.onPrepare(request);
    }

    @Override
    public boolean interceptResponse() {
        return source.interceptResponse();
    }

    @Override
    public void onResponse(Response response) {
        source.onResponse(response);
    }

    @Override
    public void onSuccess(T result) {
        source.onSuccess(result);
    }

    @Override
    public void onFail(int code, String msg) {
        source.onFail(code, msg);
    }

    @Override
    public void onError(Exception e) {
        source.onError(e);
    }

    @Override
    public void onCancel() {
        source.onCancel();
    }

    @Override
    public void onComplete() {
        source.onComplete();
    }

    @Override
    public Type getParameterizedType() {
        return source.getParameterizedType();
    }
}
