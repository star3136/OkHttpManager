package com.askew.net.progress;

import com.askew.net.callback.OkHttpProgressCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by lihoudong204 on 2018/9/27
 * 监听上传进度
 */
public class ProgressRequestBody extends RequestBody {
    private long start;
    private RequestBody originRequestBody;
    private OkHttpProgressCallback progressCallback;

    public ProgressRequestBody(RequestBody originRequestBody, long start, OkHttpProgressCallback progressCallback) {
        this.start = start;
        this.originRequestBody = originRequestBody;
        this.progressCallback = progressCallback;
    }
    @Override
    public MediaType contentType() {
        return originRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return originRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        if (progressCallback == null) {
            originRequestBody.writeTo(bufferedSink);
            return;
        }
        BufferedSink sink = Okio.buffer(write(bufferedSink));
        originRequestBody.writeTo(sink);
        sink.flush();
    }

    private Sink write(Sink sink) {
        return new ForwardingSink(sink) {
            private long totalWritedBytes = start;
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                totalWritedBytes += byteCount;
                if (progressCallback != null) {
                    progressCallback.onProgress((int) ((float)totalWritedBytes / contentLength() * 100), totalWritedBytes, contentLength());
                }
            }
        };
    }
}
