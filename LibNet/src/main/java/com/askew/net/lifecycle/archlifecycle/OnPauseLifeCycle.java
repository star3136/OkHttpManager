package com.askew.net.lifecycle.archlifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import com.askew.net.utils.NetLog;

/**
 * Created by lihoudong204 on 2018/11/16
 * Activity/Fragment onPause时
 */
public class OnPauseLifeCycle extends LifeCycle {
    public OnPauseLifeCycle(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        NetLog.v("OnPauseLifeCycle...");
        onEvent();
    }
}
