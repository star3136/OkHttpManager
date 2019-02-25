package com.askew.net.method;

import com.askew.net.callback.OkHttpCallback;
import com.askew.net.callback.OkHttpProgressCallback;
import com.askew.net.disposable.OkHttpCallbackDisposable;
import com.askew.net.disposable.OkHttpProgressCallbackDisposable;
import com.askew.net.disposable.IDisposable;
import com.askew.net.schedulers.IScheduler;

import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/11/15
 * 切换回调线程的HttpMethod
 */
public class HttpMethodResponseOn extends AbstractSchedulerHttpMethod {
    public HttpMethodResponseOn(IHttpMethod delegate, RequestChainParam requestChainParam, IScheduler scheduler) {
        super(delegate, requestChainParam, scheduler);
    }

    @Override
    public IHttpMethod requestOn(IScheduler scheduler) {
        return new HttpMethodRequestOn(this, requestChainParam, scheduler);
    }

    @Override
    public <R> IDisposable call(OkHttpCallback<R> callback) {
        final OkHttpCallback<R> callbackDisposable = getOkHttpCallbackDisposable(callback);

        if (requestChainParam.isUseDefaultThreadPool() || !(delegate instanceof HttpMethod)) {
            delegate.call(callbackDisposable);
        } else {
            ((HttpMethod) delegate).callSync(callbackDisposable);
        }

        return (IDisposable) callbackDisposable;
    }

    @Override
    protected <T> OkHttpCallback<T> getOkHttpCallbackDisposable(OkHttpCallback<T> callback) {
        if (callback instanceof OkHttpProgressCallback) {
            return new ProgressCallbackOnScheduler<>(scheduler, (OkHttpProgressCallback<T>) callback, getTag());
        }else {
            return new CallbackOnScheduler<>(scheduler, callback, getTag());
        }
    }

    private static class CallbackOnScheduler<T> extends OkHttpCallbackDisposable<T> {
        private IScheduler scheduler;

        public CallbackOnScheduler(IScheduler scheduler, OkHttpCallback<T> source, Object tag) {
            super(source, tag);
            this.scheduler = scheduler;
        }

        @Override
        public void onPrepare(final Request request) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onPrepare(request);
                }
            });
        }

        @Override
        public void onResponse(final Response response) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onResponse(response);
                }
            });
        }

        @Override
        public void onSuccess(final T result) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onSuccess(result);
                }
            });
        }

        @Override
        public void onFail(final int code, final String msg) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onFail(code, msg);
                }
            });
        }

        @Override
        public void onError(final Exception e) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onError(e);
                }
            });
        }

        @Override
        public void onCancel() {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onCancel();
                }
            });
        }

        @Override
        public void onComplete() {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onComplete();
                }
            });
        }

        @Override
        public Type getParameterizedType() {
            return source.getParameterizedType();
        }
    }

    private static class ProgressCallbackOnScheduler<T> extends OkHttpProgressCallbackDisposable<T> {
        private IScheduler scheduler;

        public ProgressCallbackOnScheduler(IScheduler scheduler, OkHttpProgressCallback<T> source, Object tag) {
            super(source, tag);
            this.scheduler = scheduler;
        }

        @Override
        public void onProgress(final int progress, final long current, final long total) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onProgress(progress, current, total);
                }
            });
        }

        @Override
        public void onCancel() {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onCancel();
                }
            });
        }

        @Override
        public void onResponse(final Response response) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onResponse(response);
                }
            });
        }

        @Override
        public void onSuccess(final T result) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onSuccess(result);
                }
            });
        }

        @Override
        public void onFail(final int code, final String msg) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onFail(code, msg);
                }
            });
        }

        @Override
        public void onComplete() {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onComplete();
                }
            });
        }

        @Override
        public void onError(final Exception e) {
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    source.onError(e);
                }
            });
        }
    }
}
