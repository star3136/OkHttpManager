package com.askew.net.disposable;

import com.askew.net.ApiHider;
import com.askew.net.callback.OkHttpProgressCallback;
import com.askew.net.callback.WrapOkHttpProgressCallback;

/**
 * Created by lihoudong204 on 2018/11/16
 * 既作为{@link OkHttpProgressCallback}的装饰者，又作为IDisposable链上的一个节点
 */
public class OkHttpProgressCallbackDisposable<T> extends WrapOkHttpProgressCallback<T> implements IWrapDisposable {
    private IWrapDisposable delegate;
    private Object tag;

    public OkHttpProgressCallbackDisposable(OkHttpProgressCallback<T> source, Object tag) {
        super(source);
        delegate = new WrapDisposable(this);
        if (source instanceof IWrapDisposable) {
            ((IWrapDisposable) source).setChild(this);
            setParent((IWrapDisposable) source);
            ApiHider.getOkHttpManager().addRequest(tag, this, (IDisposable) source);
        } else {
            ApiHider.getOkHttpManager().addRequest(tag, null, this);
        }
        this.tag = tag;
    }

    @Override
    public void setChild(IWrapDisposable disposable) {
        delegate.setChild(disposable);
    }

    @Override
    public void setParent(IWrapDisposable disposable) {
        delegate.setParent(disposable);
    }

    @Override
    public IWrapDisposable getChild() {
        return delegate.getChild();
    }

    @Override
    public IWrapDisposable getParent() {
        return delegate.getParent();
    }

    @Override
    public boolean hasChild() {
        return delegate.hasChild();
    }

    @Override
    public void cancel(IWrapDisposable parent) {
        delegate.cancel(parent);
    }

    @Override
    public void cancel() {
        if (isCanceled()) {
            return;
        }
        delegate.cancel();
        ApiHider.getOkHttpManager().removeRequest(tag, this);
    }

    @Override
    public boolean isCanceled() {
        return delegate.isCanceled();
    }
}
