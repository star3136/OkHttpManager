package com.askew.net.call;

import com.askew.net.IHttpProcessor;
import com.askew.net.callback.OkHttpCallback;

import java.util.List;

import okhttp3.Request;

/**
 * Created by lihoudong204 on 2018/11/14
 * 异步请求的OkCall
 */
public class OkAsyncCall<T> extends OkCall<T> {
    public OkAsyncCall(Request request, String fileDir, String fileName, Object tag, OkHttpCallback<T> okHttpCallback) {
        super(request, fileDir, fileName, tag, okHttpCallback);
    }

    /**
     * 发起异步请求
     */
    @Override
    protected void realCall() {
        /**
         * 加入OkHttp的请求队列
         */
        call.enqueue(new InnerOkHttpCallback<T>(this, disposable, okHttpCallback));
    }
}
