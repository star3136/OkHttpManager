package com.askew.net.lifecycle.archlifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import com.askew.net.utils.NetLog;

/**
 * Created by lihoudong204 on 2018/11/19
 * Activity/Fragment onDestroy时
 */
public class OnDestroyLifeCycle extends LifeCycle {
    public OnDestroyLifeCycle(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        NetLog.v("OnDestroyLifeCycle...");
        onEvent();
    }
}
