package com.askew.net.lifecycle;

import android.app.Activity;
import android.app.Fragment;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.askew.net.lifecycle.archlifecycle.OnDestroyLifeCycle;
import com.askew.net.lifecycle.archlifecycle.OnPauseLifeCycle;
import com.askew.net.lifecycle.archlifecycle.OnStopLifeCycle;
import com.askew.net.lifecycle.compat.CompatLifeCycles;
import com.askew.net.lifecycle.compat.FragmentBindObserver;
import com.askew.net.lifecycle.compat.LifeCycleFragment;
import com.askew.net.lifecycle.compat.LifeCycleFragmentRetriver;

/**
 * Created by lihoudong204 on 2018/11/19
 * 对外提供的生命周期工具类
 */
public class OkLifeCycles {
    public static BindObserver OnPause(Context context) {
        if (context instanceof FragmentActivity) {
            return OnPause((LifecycleOwner)context);
        }else if(context instanceof Activity){
            return OnPause((Activity) context);
        }

        throw new IllegalArgumentException("Invalid context");
    }

    public static BindObserver OnStop(Context context) {
        if (context instanceof FragmentActivity) {
            return OnStop((LifecycleOwner)context);
        }else if(context instanceof Activity){
            return OnStop((Activity) context);
        }

        throw new IllegalArgumentException("Invalid context");
    }

    public static BindObserver OnDestroy(Context context) {
        if (context instanceof FragmentActivity) {
            return OnDestroy((LifecycleOwner)context);
        }else if(context instanceof Activity){
            return OnDestroy((Activity) context);
        }

        throw new IllegalArgumentException("Invalid context");
    }

    public static BindObserver OnPause(Activity activity) {
        return CompatLifeCycles.get(activity, FragmentBindObserver.ON_PAUSE);
    }

    public static BindObserver OnStop(Activity activity) {
        return CompatLifeCycles.get(activity, FragmentBindObserver.ON_STOP);
    }

    public static BindObserver OnDestroy(Activity activity) {
        return CompatLifeCycles.get(activity, FragmentBindObserver.ON_DESTROY);
    }

    public static BindObserver OnPause(FragmentActivity activity) {
        return OnPause((LifecycleOwner) activity);
    }

    public static BindObserver OnStop(FragmentActivity activity) {
        return OnStop((LifecycleOwner) activity);
    }

    public static BindObserver OnDestroy(FragmentActivity activity) {
        return OnDestroy((LifecycleOwner) activity);
    }


    public static BindObserver OnPause(android.support.v4.app.Fragment fragment) {
        return OnPause((LifecycleOwner) fragment);

    }

    public static BindObserver OnStop(android.support.v4.app.Fragment fragment) {
        return OnStop((LifecycleOwner) fragment);
    }

    public static BindObserver OnDestroy(android.support.v4.app.Fragment fragment) {
        return OnDestroy((LifecycleOwner) fragment);
    }

    public static BindObserver OnPause(Fragment fragment) {
        return CompatLifeCycles.get(fragment, FragmentBindObserver.ON_PAUSE);
    }

    public static BindObserver OnStop(Fragment fragment) {
        return CompatLifeCycles.get(fragment, FragmentBindObserver.ON_STOP);
    }

    public static BindObserver OnDestroy(Fragment fragment) {
        return CompatLifeCycles.get(fragment, FragmentBindObserver.ON_DESTROY);
    }


    private static BindObserver OnPause(LifecycleOwner lifecycleOwner) {
        return new OnPauseLifeCycle(lifecycleOwner);
    }

    private static BindObserver OnStop(LifecycleOwner lifecycleOwner) {
        return new OnStopLifeCycle(lifecycleOwner);
    }

    private static BindObserver OnDestroy(LifecycleOwner lifecycleOwner) {
        return new OnDestroyLifeCycle(lifecycleOwner);
    }
}
