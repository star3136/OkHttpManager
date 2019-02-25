package com.askew.net.disposable;

import com.askew.net.ApiHider;
import com.askew.net.callback.OkHttpCallback;
import com.askew.net.utils.NetLog;

import java.util.concurrent.Future;

/**
 * Created by lihoudong204 on 2018/11/15
 * future的Disposable
 * 一般来说这个是IDisposable链的尾节点（和{@link FutureDisposable}互斥，尾节点只可能是{@link OkDisposable}或者{@link FutureDisposable}）
 * 主要用于过渡，假设请求还阻塞在线程池中，并未真正开始，此时取消请求，那么最终使用此类来取消线程池的调度来达到取消请求的目的
 */
public class FutureDisposable extends WrapDisposable {
    volatile Future future;
    private OkHttpCallback okHttpCallback;
    private Object tag;

    public FutureDisposable(Future future, OkHttpCallback okHttpCallback, Object tag) {
        this.future = future;
        this.okHttpCallback = okHttpCallback;
        this.tag = tag;
        if (okHttpCallback instanceof IWrapDisposable) {
            setParent((IWrapDisposable) okHttpCallback);
        }
    }

    @Override
    public void cancel() {
        NetLog.v("调用FutureDisposable");
        if (canceled) {
            return;
        }
        if (parent != null) {
            parent.cancel();
            return;
        }

        future.cancel(true);
        canceled = true;
        if (okHttpCallback != null) {
            okHttpCallback.onCancel();
            okHttpCallback.onComplete();
        }
        ApiHider.getOkHttpManager().removeRequest(tag, this);
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

}
