package com.askew.net.callback;

import com.askew.net.lifecycle.BindObserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/11/19
 * 弱引用的OkHttpProgressCallback, 注意传入的OkHttpProgressCallback不要使用匿名内部类
 * 推荐使用{@link OkHttpProgressCallback}，配合生命周期方法{@link com.askew.net.method.IHttpMethod#bindUntil(BindObserver)}
 */
public class WeakOkHttpProgressCallback<T> extends OkHttpProgressCallback<T> {
    private WeakReference<OkHttpProgressCallback<T>> sourceWeak;

    public WeakOkHttpProgressCallback(OkHttpProgressCallback<T> okHttpCallback) {
        this.sourceWeak = new WeakReference<>(okHttpCallback);
    }

    @Override
    public boolean interceptResponse() {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            return okHttpCallback.interceptResponse();
        }
        return true;
    }

    @Override
    public void onPrepare(Request request) {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onPrepare(request);
        }
    }

    @Override
    public void onProgress(int progress, long current, long total) {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onProgress(progress, current, total);
        }
    }

    @Override
    public void onResponse(Response response) {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onResponse(response);
        }
    }

    @Override
    public void onSuccess(T result) {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onSuccess(result);
        }
    }

    @Override
    public void onFail(int code, String msg) {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onFail(code, msg);
        }
    }

    @Override
    public void onError(Exception e) {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onError(e);
        }
    }

    @Override
    public void onCancel() {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
        if (okHttpCallback != null) {
            okHttpCallback.onCancel();
        }
    }

    @Override
    public void onComplete() {
        OkHttpProgressCallback<T> okHttpCallback = sourceWeak.get();
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
