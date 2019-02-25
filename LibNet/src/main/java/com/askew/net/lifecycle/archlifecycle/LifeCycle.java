package com.askew.net.lifecycle.archlifecycle;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;

import com.askew.net.lifecycle.BindObserver;

/**
 * Created by lihoudong204 on 2018/11/16
 * 使用support包的LifeCycle来实现生命周期绑定
 */
public abstract class LifeCycle extends BindObserver implements LifecycleObserver {
    protected LifecycleOwner lifecycleOwner;
    public LifeCycle(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        /**
         * 添加绑定
         */
        lifecycleOwner.getLifecycle().addObserver(this);
    }
}
