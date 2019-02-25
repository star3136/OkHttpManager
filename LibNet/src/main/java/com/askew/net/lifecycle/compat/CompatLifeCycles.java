package com.askew.net.lifecycle.compat;

import android.app.Activity;
import android.app.Fragment;

import com.askew.net.lifecycle.BindObserver;

/**
 * Created by lihoudong204 on 2018/11/19
 * 兼容类
 * 如果没有使用suppor包的FragmentActivity或者Fragment，那么使用此类
 */
public class CompatLifeCycles {
    public static BindObserver get(Activity activity, int event){
        LifeCycleFragment lifeCycleFragment = LifeCycleFragmentRetriver.get(activity);
        FragmentBindObserver bindObserver = FragmentBindObserver.get(event);
        lifeCycleFragment.addBindObserver(bindObserver);
        return bindObserver;
    }

    public static BindObserver get(Fragment fragment, int event){
        LifeCycleFragment lifeCycleFragment = LifeCycleFragmentRetriver.get(fragment);
        FragmentBindObserver bindObserver = FragmentBindObserver.get(event);
        lifeCycleFragment.addBindObserver(bindObserver);
        return bindObserver;
    }
}
