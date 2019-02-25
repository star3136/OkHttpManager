package com.askew.net.disposable;

import com.askew.net.ApiHider;

/**
 * Created by lihoudong204 on 2018/12/18
 */
public class OkWrapDisposable extends WrapDisposable {
    private Object tag;

    public OkWrapDisposable(Object tag) {
        this.tag = tag;
    }

    @Override
    public void cancel() {
        if (super.isCanceled()) {
            return;
        }
        super.cancel();
        ApiHider.getOkHttpManager().removeRequest(tag, this);
    }
}
