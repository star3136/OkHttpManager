package com.askew.net.progress;

import com.askew.net.ApiHider;
import com.askew.net.IHttpProcessor;
import com.askew.net.callback.OkHttpProgressCallback;
import com.askew.net.utils.HttpResponseUtils;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by lihoudong204 on 2018/11/21
 * 设置下载进度的IHttpProcessor
 */
public class DownloadProgressHttpProcessor implements IHttpProcessor {
    private String fileDir;
    private String fileName;
    private OkHttpProgressCallback progressCallback;

    public DownloadProgressHttpProcessor(String fileDir, String fileName, OkHttpProgressCallback progressCallback) {
        this.fileDir = fileDir;
        this.fileName = fileName;
        this.progressCallback = progressCallback;
    }

    @Override
    public Request preRequest(Request request) {
        return request;
    }

    @Override
    public Response afterResponse(Response response) {
        long[] ranges = null;
        if (response.code() == 206) {
            String contentRange = response.header("Content-Range");
            ranges = HttpResponseUtils.parseContentRange(contentRange);
        }
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (ranges != null) {
                responseBody = new ProgressResponseBody(responseBody, ranges[0], ApiHider.getCache().getContentLength(HttpResponseUtils.getFilePath(response, fileDir, fileName)), progressCallback);
            } else {
                responseBody = new ProgressResponseBody(responseBody, 0, 0, progressCallback);
            }
            return response.newBuilder().body(responseBody).build();
        }
        return response;
    }
}
