package com.askew.net.call;

import com.askew.net.ApiHider;
import com.askew.net.callback.OkHttpCallback;
import com.askew.net.disposable.IDisposable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/11/14
 * 内部使用的OkHttp的异步请求Callback
 */
public class InnerOkHttpCallback<T> implements Callback {
    private OkCall okCall;
    private IDisposable disposable;
    private OkHttpCallback<T> okHttpCallback;

    public InnerOkHttpCallback(OkCall okCall, IDisposable disposable, OkHttpCallback<T> okHttpCallback) {
        this.okCall = okCall;
        this.disposable = disposable;
        this.okHttpCallback = okHttpCallback;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        try {
            if (disposable.isCanceled()) {
                okHttpCallback.onCancel();
            } else {
                if (okHttpCallback != null) {
                    okHttpCallback.onError(e);
                }
            }
        } catch (Exception ex) {
            e.printStackTrace();
        }finally {
            ApiHider.getOkHttpManager().findAndRemoveRequest(okCall.tag, disposable);
            okHttpCallback.onComplete();
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            okCall.onResponse(response, okHttpCallback.getParameterizedType());
        } catch (Exception e) {
            if (!disposable.isCanceled()) {
                okHttpCallback.onError(e);
            } else {
                okHttpCallback.onCancel();
            }
        } finally {
            ApiHider.getOkHttpManager().findAndRemoveRequest(okCall.tag, disposable);
            okHttpCallback.onComplete();
        }
    }
}
