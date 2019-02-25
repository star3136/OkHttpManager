package com.askew.net.disposable;

/**
 * Created by lihoudong204 on 2018/12/14
 */
public class WrapDisposable implements IWrapDisposable {
    protected BlockHolder<IWrapDisposable> child = new BlockHolder<>();
    protected IWrapDisposable parent;
    private IWrapDisposable outDisposable;
    protected boolean canceled;

    public WrapDisposable(IWrapDisposable outDisposable) {
        this.outDisposable = outDisposable;
    }

    public WrapDisposable() {
    }

    @Override
    public void setChild(IWrapDisposable disposable) {
        if (child.hasObj()) {
            if (child.get().isCanceled()) {
                disposable.cancel();
                return;
            }
            disposable.setChild(child.get());
            child.get().setParent(disposable);
        }
        child.set(disposable);
    }

    @Override
    public void setParent(IWrapDisposable disposable) {
        this.parent = disposable;
    }

    @Override
    public IWrapDisposable getChild() {
        return child.get();
    }

    @Override
    public IWrapDisposable getParent() {
        return parent;
    }

    @Override
    public boolean hasChild() {
        return child.hasObj();
    }

    @Override
    public void cancel(IWrapDisposable parent) {
        if (this.parent == null || parent == this.parent) {
            this.parent = null;
            if (outDisposable != null) {
                outDisposable.cancel();
            } else {
                cancel();
            }
        }
    }

    @Override
    public void cancel() {
        if (isCanceled()) {
            return;
        }
        if (parent != null) {
            parent.cancel();
            return;
        }
        IWrapDisposable childDisposable = child.get();
        if (childDisposable != null) {
            childDisposable.cancel(outDisposable == null ? this : outDisposable);
        }
        canceled = true;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }
}
