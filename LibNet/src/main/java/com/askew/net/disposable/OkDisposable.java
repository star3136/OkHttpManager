package com.askew.net.disposable;

import com.askew.net.ApiHider;
import com.askew.net.OkHttpManager;

import okhttp3.Call;

/**
 * Created by lihoudong204 on 2018/11/13
 * 正在进行Http请求时的Disposable
 * 一般来说这个是IDisposable链的尾节点（和{@link FutureDisposable}互斥，尾节点只可能是{@link OkDisposable}或者{@link FutureDisposable}）
 */
public class OkDisposable extends WrapDisposable {
    private Call call;   //发起请求的OkHttp Call
    private Object tag;

    public OkDisposable(Call call, Object tag) {
        this.tag = tag;
        this.call = call;
    }


    @Override
    public void cancel() {
        if (isCanceled()) {
            return;
        }
        if (parent != null) {
            parent.cancel(this);
            return;
        }
        if (call != null) {
            call.cancel();
        }

        canceled = true;
        ApiHider.getOkHttpManager().removeRequest(tag, this);
    }
}
