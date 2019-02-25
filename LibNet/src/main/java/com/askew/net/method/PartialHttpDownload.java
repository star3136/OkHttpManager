package com.askew.net.method;

import android.text.TextUtils;

import com.askew.net.ApiHider;
import com.askew.net.IHttpProcessor;
import com.askew.net.OkException;
import com.askew.net.callback.OkHttpCallback;
import com.askew.net.OkHttpManagerSettings;
import com.askew.net.callback.OkHttpProgressCallback;
import com.askew.net.call.DefaultSyncOkHttpCallback;
import com.askew.net.call.OkCall;
import com.askew.net.call.OkSyncCall;
import com.askew.net.disposable.IDisposable;
import com.askew.net.disposable.OkHttpCallbackDisposable;
import com.askew.net.disposable.OkHttpProgressCallbackDisposable;
import com.askew.net.utils.HttpResponseUtils;
import com.askew.net.utils.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/11/21
 * 支持断点续传的下载
 */
public class PartialHttpDownload extends HttpDownload {

    public PartialHttpDownload(OkHttpManagerSettings settings, String url, String fileDir, String fileName) {
        super(settings, url, fileDir, fileName);
    }

    @Override
    public <R> IDisposable call(OkHttpCallback<R> callback) {
        appendPartialHeader();
        PartialInterceptor partialInterceptor = new PartialInterceptor(fileDir, fileName);
        withHttpInterceptor(partialInterceptor);

        return super.call(getPartialCallback(callback, partialInterceptor));
    }

    @Override
    public <R> IDisposable callSync(OkHttpCallback<R> okHttpCallback) {
        appendPartialHeader();
        PartialInterceptor partialInterceptor = new PartialInterceptor(fileDir, fileName);
        withHttpInterceptor(partialInterceptor);

        return super.callSync(getPartialCallback(okHttpCallback, partialInterceptor));
    }

    @Override
    public <R> R callSync(Class<R> cls) throws OkException {
        appendPartialHeader();
        PartialInterceptor partialInterceptor = new PartialInterceptor(fileDir, fileName);
        withHttpInterceptor(partialInterceptor);
        Request request = newRequest();

        DefaultSyncOkHttpCallback<R> defaultCallback = new DefaultSyncPartialOkHttpCallback<R>(partialInterceptor);
        OkCall<R> okCall = new OkSyncCall<>(request, fileDir, fileName, tag, defaultCallback, cls);

        okCall.call();

        try {
            R response = defaultCallback.getResponse();
            if (response != null) {
                return response;
            }else {
                throw new OkException(defaultCallback.getErrorMsg());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new OkException(e.getMessage());
        }
    }

    private void appendPartialHeader() {
        /**
         * 必须手动指定了文件目录和文件名，才会添加断点续传需要的http头字段
         */
        if (TextUtils.isEmpty(fileDir) || TextUtils.isEmpty(fileName)) {
            return;
        }
        String filePath = fileDir + File.separator + fileName;
        long startRange = ApiHider.getCache().getStartRange(filePath);
        String ifRange = ApiHider.getCache().getIfRange(filePath);
        File file = new File(filePath);
        if (!file.exists()) { //文件被删除了，但是磁盘记录还存在
            if (startRange > 0) {
                startRange = 0;
            }
        }
        if (startRange != 0 && !TextUtils.isEmpty(ifRange)) {
            withHeader("Range", "bytes=" + startRange + "-");
            withHeader("If-Range", ifRange);
        }
    }

    private <T> OkHttpCallback<T> getPartialCallback(OkHttpCallback<T> callback, PartialInterceptor partialInterceptor) {
        if (callback instanceof OkHttpProgressCallback) {
            return new PartialOkHttpProgressCallback<T>((OkHttpProgressCallback<T>) callback, getTag(), partialInterceptor);
        }else {
            return new PartialOkHttpCallback<>(callback, getTag(), partialInterceptor);
        }
    }

    private static void saveStartRange(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file.exists()) {
            ApiHider.getCache().saveStartRange(filePath, file.length());
        }
    }

    /*==============================内部类===============================*/

    private static class PartialOkHttpCallback<T> extends OkHttpCallbackDisposable<T> {
        private PartialInterceptor partialInterceptor;

        public PartialOkHttpCallback(OkHttpCallback<T> source, Object tag, PartialInterceptor partialInterceptor) {
            super(source, tag);
            this.partialInterceptor = partialInterceptor;
        }

        @Override
        public void onSuccess(final T result) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(partialInterceptor.getFilePath())) {
                        ApiHider.getCache().remove(partialInterceptor.getFilePath());
                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    source.onSuccess(result);
                }
            });
        }

        @Override
        public void onFail(final int code, final String msg) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(partialInterceptor.getFilePath())) {
                        ApiHider.getCache().remove(partialInterceptor.getFilePath());
                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    source.onFail(code, msg);
                }
            });
        }

        @Override
        public void onError(final Exception e) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    saveStartRange(partialInterceptor.getFilePath());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    source.onError(e);
                }
            });
        }

        @Override
        public void onCancel() {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    saveStartRange(partialInterceptor.getFilePath());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    source.onCancel();
                }
            });
        }
    }

    private static class PartialOkHttpProgressCallback<T> extends OkHttpProgressCallbackDisposable<T> {
        private PartialInterceptor partialInterceptor;

        public PartialOkHttpProgressCallback(OkHttpProgressCallback<T> source, Object tag, PartialInterceptor partialInterceptor) {
            super(source, tag);
            this.partialInterceptor = partialInterceptor;
        }

        @Override
        public void onSuccess(final T result) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(partialInterceptor.getFilePath())) {
                        ApiHider.getCache().remove(partialInterceptor.getFilePath());
                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    source.onSuccess(result);
                }
            });
        }

        @Override
        public void onFail(final int code, final String msg) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(partialInterceptor.getFilePath())) {
                        ApiHider.getCache().remove(partialInterceptor.getFilePath());
                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    source.onFail(code, msg);
                }
            });
        }

        @Override
        public void onError(final Exception e) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    saveStartRange(partialInterceptor.getFilePath());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    source.onError(e);
                }
            });
        }

        @Override
        public void onCancel() {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    saveStartRange(partialInterceptor.getFilePath());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    source.onCancel();
                }
            });
        }
    }

    /**
     * 使用同步断点续传下载的回调
     * @param <R>
     */

    private static class DefaultSyncPartialOkHttpCallback<R> extends DefaultSyncOkHttpCallback<R>{
        private PartialInterceptor partialInterceptor;
        public DefaultSyncPartialOkHttpCallback(PartialInterceptor partialInterceptor) {
            this.partialInterceptor = partialInterceptor;
        }

        @Override
        public void onSuccess(final R result) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    ApiHider.getCache().remove(partialInterceptor.getFilePath());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    DefaultSyncPartialOkHttpCallback.super.onSuccess(result);
                }
            });
        }

        @Override
        public void onFail(final int code, final String msg) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    ApiHider.getCache().remove(partialInterceptor.getFilePath());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    DefaultSyncPartialOkHttpCallback.super.onFail(code, msg);
                }
            });
        }


        @Override
        public void onError(final Exception e) {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    saveStartRange(partialInterceptor.getFilePath());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    DefaultSyncPartialOkHttpCallback.super.onError(e);
                }
            });
        }

        @Override
        public void onCancel() {
            ThreadUtils.submit(new Runnable() {
                @Override
                public void run() {
                    saveStartRange(partialInterceptor.getFilePath());
                }
            }, new Runnable() {
                @Override
                public void run() {
                    DefaultSyncPartialOkHttpCallback.super.onCancel();
                }
            });
        }
    }

    private static class PartialInterceptor implements Interceptor {
        private String fileDir;
        private String fileName;
        private String filePath;

        public PartialInterceptor(String fileDir, String fileName) {
            this.fileDir = fileDir;
            this.fileName = fileName;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if (response.isSuccessful()) {
                filePath = HttpResponseUtils.getFilePath(response, fileDir, fileName);
            }
            if (response.code() == 200) {
                //断点续传
                ApiHider.getCache().saveContentLength(filePath, response.body().contentLength());
                ApiHider.getCache().saveIfRange(filePath, response.header("Last-Modified"));
            }

            return response;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}
