package com.askew.net.progress;

import com.askew.net.ApiHider;
import com.askew.net.callback.OkHttpProgressCallback;
import com.askew.net.utils.HttpResponseUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by lihoudong204 on 2019/1/17
 */
public class DownloadProgressInterceptor implements Interceptor {
    private String fileDir;
    private String fileName;
    private OkHttpProgressCallback progressCallback;

    public DownloadProgressInterceptor(String fileDir, String fileName, OkHttpProgressCallback progressCallback) {
        this.fileDir = fileDir;
        this.fileName = fileName;
        this.progressCallback = progressCallback;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
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
