package com.askew.net.method;

import android.text.TextUtils;

import com.askew.net.callback.OkHttpCallback;
import com.askew.net.OkHttpManagerSettings;
import com.askew.net.callback.OkHttpProgressCallback;
import com.askew.net.disposable.IDisposable;
import com.askew.net.interceptor.InterceptorManager;
import com.askew.net.progress.UploadProgressHttpProcessor;
import com.askew.net.progress.UploadProgressInterceptor;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by lihoudong204 on 2018/9/27
 * 带进度的上传
 */
public class HttpUpload extends HttpMethod {
    private String filePath;
    private String name;
    private byte[] bytes;
    private OkHttpManagerSettings settings;
    private String url;

    public HttpUpload(OkHttpManagerSettings settings, String url, String filePath) {
        this.settings = settings;
        this.url = url;
        this.filePath = filePath;
        int nameIndex = filePath.lastIndexOf(File.separator);
        if (nameIndex == -1) {
            name = filePath;
        } else {
            name = filePath.substring(nameIndex + 1);
        }
    }

    public HttpUpload(OkHttpManagerSettings settings, String url, String name, String filePath) {
        this.settings = settings;
        this.url = url;
        this.name = name;
        this.filePath = filePath;
    }

    public HttpUpload(OkHttpManagerSettings settings, String url, String name, byte[] bytes) {
        this.settings = settings;
        this.url = url;
        this.name = name;
        this.bytes = bytes;
    }

    @Override
    public <T> IDisposable call(OkHttpCallback<T> callback) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        if (callback instanceof OkHttpProgressCallback) {
            withHttpInterceptor(new UploadProgressInterceptor(filePath, (OkHttpProgressCallback) callback));
        }
        return super.call(callback);
    }

    @Override
    public <R> IDisposable callSync(OkHttpCallback<R> callback) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        if (callback instanceof OkHttpProgressCallback) {
            withHttpInterceptor(new UploadProgressInterceptor(filePath, (OkHttpProgressCallback) callback));

        }
        return super.callSync(callback);
    }

    @Override
    Request newRequest() {
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = null;
        if (!TextUtils.isEmpty(filePath)) {
            File targetFile = new File(filePath);
            requestBody = RequestBody.create(MediaType.parse
                    ("application/octet-stream"), targetFile);
        } else if (bytes != null) {
            requestBody = RequestBody.create(MediaType.parse
                    ("application/octet-stream"), bytes);
        } else {
            return null;
        }

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .addFormDataPart("file", name, requestBody);

        addHeaders(builder, settings.getCommonHttpHeaders());

        addHeaders(builder, httpHeaders);

        addParams(bodyBuilder, settings.getCommonHttpParams());

        addParams(bodyBuilder, httpParams);
        builder.post(bodyBuilder.build()).url(url).tag(new InterceptorManager(httpInterceptors, networkInterceptors));

        return builder.build();
    }

    private void addParams(MultipartBody.Builder builder, Map<String, String> httpParams) {
        if (httpParams == null || httpParams.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : httpParams.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
    }

}
