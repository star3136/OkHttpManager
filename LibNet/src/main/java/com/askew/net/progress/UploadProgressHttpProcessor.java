package com.askew.net.progress;

import com.askew.net.IHttpProcessor;
import com.askew.net.callback.OkHttpProgressCallback;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/11/21
 * 设置上传进度的IHttpProcessor
 */
public class UploadProgressHttpProcessor implements IHttpProcessor {
    private String filePath;
    private OkHttpProgressCallback progressCallback;

    public UploadProgressHttpProcessor(String filePath, OkHttpProgressCallback progressCallback) {
        this.filePath = filePath;
        this.progressCallback = progressCallback;
    }

    @Override
    public Request preRequest(Request request) {
        return request.newBuilder().post(new ProgressRequestBody(request.body(), 0, progressCallback)).build();
    }

    @Override
    public Response postResponse(Response response) {
        return response;
    }
}
