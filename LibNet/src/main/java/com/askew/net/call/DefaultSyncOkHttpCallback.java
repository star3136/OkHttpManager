package com.askew.net.call;

import com.askew.net.OkException;
import com.askew.net.callback.OkHttpCallback;

import java.util.concurrent.CountDownLatch;

/**
 * Created by lihoudong204 on 2018/11/14
 * 默认的同步请求的Callback，详情{@link com.askew.net.method.HttpMethod#callSync(Class)}
 */
public class DefaultSyncOkHttpCallback<R> extends OkHttpCallback<R> {
    private Holder<R> holder;  //暂存请求成功的结果或者失败的msg
    private CountDownLatch latch;  //等待响应完成

    public DefaultSyncOkHttpCallback() {
        holder = new Holder<>();
        latch = new CountDownLatch(1);
    }

    @Override
    public void onSuccess(R result) {
        holder.obj = result;
        latch.countDown();
    }

    @Override
    public void onFail(int code, String msg) {
        holder.e = new OkException(msg);
        latch.countDown();
    }

    @Override
    public void onError(Exception e) {
        holder.e = e;
        latch.countDown();
    }

    @Override
    public void onCancel() {
        holder.e = new OkException("request canceled");
        latch.countDown();
    }

    public R getResponse() throws InterruptedException {
        latch.await();
        return holder.obj;
    }

    public Exception getErrorMsg() {
        return holder.e;
    }


    private static class Holder<T> {
        T obj;
        Exception e;
    }
}
