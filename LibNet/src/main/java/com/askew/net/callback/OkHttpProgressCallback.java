package com.askew.net.callback;

/**
 * Created by lihoudong204 on 2018/9/27
 * 带进度到Callback
 */
public abstract class OkHttpProgressCallback<T> extends OkHttpCallback<T> {
    /**
     * 当前的进度
     * @param progress
     * @param current
     * @param total
     */
    public abstract void onProgress(final int progress, long current, long total);
}
