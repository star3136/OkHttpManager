package com.askew.net.progress;


import com.askew.net.callback.OkHttpProgressCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by lihoudong204 on 2018/9/27
 * 监听下载进度
 */
public class ProgressResponseBody extends ResponseBody {
    private long start;
    private ResponseBody originResponseBody;
    private BufferedSource source;
    private OkHttpProgressCallback progressCallback;
    private long contentLength;

    public ProgressResponseBody(ResponseBody originResponseBody, long start, long contentLength, OkHttpProgressCallback progressCallback) {
        this.originResponseBody = originResponseBody;
        this.start = start;
        this.contentLength = contentLength;
        this.progressCallback = progressCallback;

    }

    @Override
    public MediaType contentType() {
        return originResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return originResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (progressCallback == null) {
            return originResponseBody.source();
        }

        if (source == null) {
            source = Okio.buffer(source(originResponseBody.source()));
        }
        return source;
    }

    private long getContentLength() {
        if (contentLength == 0) {
            return contentLength();
        }
        return contentLength;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = start;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += (bytesRead == -1 ? 0 : bytesRead);
                if (progressCallback != null) {
                    progressCallback.onProgress((int) ((float)totalBytesRead / getContentLength() * 100), totalBytesRead, getContentLength());
                }
                return bytesRead;
            }
        };
    }
}
