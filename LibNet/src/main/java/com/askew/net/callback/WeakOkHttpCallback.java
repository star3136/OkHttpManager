package com.askew.net.callback;

import com.askew.net.lifecycle.BindObserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/11/19
 * 弱引用的OkHttpCallback, 注意传入的OkHttpProgressCallback不要使用匿名内部类
 * 推荐使用{@link OkHttpCallback}，配合生命周期方法{@link com.askew.net.method.IHttpMethod#bindUntil(BindObserver)}
 */
public class WeakOkHttpCallback<T> extends OkHttpCallback<T> {
    private WeakReference<OkHttpCallback<T>> sourceWeak;

    public WeakOkHttpCallback(OkHttpCallback<T> okHttpCallback) {
        this.sourceWeak = new WeakReference<>(okHttpCallback);
    }

    @Override
    public void onPrepare(Request request) {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onPrepare(request);
        }
    }

    @Override
    public boolean interceptResponse() {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            return okHttpCallback.interceptResponse();
        }
        return true;
    }

    @Override
    public void onResponse(Response response) {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onResponse(response);
        }
    }

    @Override
    public void onSuccess(T result) {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onSuccess(result);
        }
    }

    @Override
    public void onFail(int code, String msg) {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onFail(code, msg);
        }
    }

    @Override
    public void onError(Exception e) {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onError(e);
        }
    }

    @Override
    public void onCancel() {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onCancel();
        }
    }

    @Override
    public void onComplete() {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onComplete();
        }
    }

    @Override
    public Type getParameterizedType() {
        OkHttpCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            return okHttpCallback.getParameterizedType();
        }
        return null;
    }
}
