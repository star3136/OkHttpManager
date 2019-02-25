package com.askew.net.method;

import android.text.TextUtils;

import com.askew.net.callback.OkHttpCallback;
import com.askew.net.OkHttpManagerSettings;
import com.askew.net.callback.OkHttpProgressCallback;
import com.askew.net.disposable.IDisposable;
import com.askew.net.progress.DownloadProgressHttpProcessor;
import com.askew.net.progress.DownloadProgressInterceptor;

/**
 * Created by lihoudong204 on 2018/9/26
 * 带进度的下载
 */
public class HttpDownload extends HttpGet {

    public HttpDownload(OkHttpManagerSettings settings, String url, String fileDir, String fileName) {
        super(settings, url);
        if (TextUtils.isEmpty(fileDir)) {
            throw new NullPointerException("fileDir is null");
        }
        saveToFile(fileDir, fileName);
    }

    @Override
    public <R> IDisposable call(OkHttpCallback<R> callback) {
        if (callback instanceof OkHttpProgressCallback) {
            withHttpInterceptor(new DownloadProgressInterceptor(fileDir, fileName, (OkHttpProgressCallback) callback));
        }
        return super.call(callback);
    }

    @Override
    public <R> IDisposable callSync(OkHttpCallback<R> callback) {
        if (callback instanceof OkHttpProgressCallback) {
            withHttpInterceptor(new DownloadProgressInterceptor(fileDir, fileName, (OkHttpProgressCallback) callback));
        }
        return super.callSync(callback);
    }
}
