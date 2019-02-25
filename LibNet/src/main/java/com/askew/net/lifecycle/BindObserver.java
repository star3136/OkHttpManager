package com.askew.net.lifecycle;

import com.askew.net.disposable.IDisposable;

/**
 * Created by lihoudong204 on 2018/11/19
 */
public abstract class BindObserver {
    private IDisposable disposable;
    public void bind(IDisposable disposable){
        this.disposable = disposable;
    }
    public void onEvent(){
        if (disposable != null) {
            disposable.cancel();
        }
    }
}
