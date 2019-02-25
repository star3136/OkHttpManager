package com.askew.net.disposable;

/**
 * Created by lihoudong204 on 2018/11/13
 * 取消请求的接口
 */
public interface IDisposable {
    /**
     * 取消
     */
    void cancel();

    /**
     * 请求是否已经被取消
     */
    boolean isCanceled();
}