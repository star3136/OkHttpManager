package com.askew.net.progress;

import com.askew.net.callback.OkHttpProgressCallback;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2019/1/17
 */
public class UploadProgressInterceptor implements Interceptor {
    private String filePath;
    private OkHttpProgressCallback progressCallback;

    public UploadProgressInterceptor(String filePath, OkHttpProgressCallback progressCallback) {
        this.filePath = filePath;
        this.progressCallback = progressCallback;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder().post(new ProgressRequestBody(request.body(), 0, progressCallback)).build();
        return chain.proceed(request);
    }
}
