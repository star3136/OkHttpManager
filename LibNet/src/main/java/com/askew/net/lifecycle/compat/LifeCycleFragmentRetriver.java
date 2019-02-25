package com.askew.net.lifecycle.compat;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import com.askew.net.utils.NetLog;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by lihoudong204 on 2018/11/19
 * 管理LifeCycleFragment
 */
public class LifeCycleFragmentRetriver {
    private static final String FRAGMENT_TAG = LifeCycleFragmentRetriver.class.getName();

    private static Map<FragmentManager, LifeCycleFragment> supportFragments = new WeakHashMap<>();

    static LifeCycleFragment get(Activity activity) {
        return get(activity.getFragmentManager());
    }

    static LifeCycleFragment get(Fragment fragment) {
        return get(fragment.getChildFragmentManager());
    }

    private static LifeCycleFragment get(FragmentManager fm) {
        LifeCycleFragment lifeCycleFragment = (LifeCycleFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (lifeCycleFragment == null) { //FragmentManager.commitAllowingStateLoss()并不是及时生效
            lifeCycleFragment = supportFragments.get(fm);
        }
        if (lifeCycleFragment == null) {
            NetLog.v("未找到fragment, 新建一个");
            lifeCycleFragment = new LifeCycleFragment();
            fm.beginTransaction().add(lifeCycleFragment, FRAGMENT_TAG).commitAllowingStateLoss();
            supportFragments.put(fm, lifeCycleFragment);
        } else {
            NetLog.v("找到了fragment");
            /**
             * 有可能app被销毁，又被启动，这时候FragmentManager中有这个Fragment，但是fragments中没有，所以需要补上
             */
            supportFragments.put(fm, lifeCycleFragment);
        }
        return lifeCycleFragment;
    }

    static void remove(FragmentManager fm) {
        if (fm == null) {
            NetLog.v("删除LifeCycleFragment时，fm为空");
        }
        if (supportFragments.remove(fm) != null) {
            NetLog.v("删除Fragment成功");
        }
    }
}
