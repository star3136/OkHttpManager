package com.askew.net.lifecycle.archlifecycle;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import com.askew.net.utils.NetLog;

/**
 * Created by lihoudong204 on 2018/11/19
 * Activity/Fragment onStop时
 */
public class OnStopLifeCycle extends LifeCycle {
    public OnStopLifeCycle(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        NetLog.v( "OnStopLifeCycle...");
        onEvent();
    }
}
