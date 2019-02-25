package com.askew.net.callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/9/26
 */
public abstract class OkHttpCallback<T> {
    /**
     * 准备发起请求
     * @param request
     */
    public void onPrepare(Request request) {
    }

    /**
     * 是否拦截Response自己处理
     * @return
     * true 用户需要实现{@link OkHttpCallback#onResponse(Response)}，处理response，onSuccess、onFail不会被调用
     * false 内部处理，{@link OkHttpCallback#onResponse(Response)}不会被调用
     */
    public boolean interceptResponse(){
        return false;
    }
    /**
     * 以最原始的Response返回
     * @param response
     */
    public void onResponse(Response response){
    }

    /**
     * http请求成功，code 2xx
     * @param result
     */
    public abstract void onSuccess(T result);

    /**
     * http请求失败，code非2xx
     * @param code
     * @param msg
     */
    public abstract void onFail(int code, String msg);

    /**
     * 请求产生异常
     * @param e
     */
    public abstract void onError(Exception e);

    /**
     * 请求被取消
     */
    public void onCancel() {
    }

    /**
     * 请求结束
     */
    public void onComplete() {
    }

    public Type getParameterizedType() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
