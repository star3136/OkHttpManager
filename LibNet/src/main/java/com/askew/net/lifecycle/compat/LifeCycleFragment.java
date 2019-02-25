package com.askew.net.lifecycle.compat;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.askew.net.utils.NetLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihoudong204 on 2018/11/19
 * 监听生命周期的Fragment
 */
public class LifeCycleFragment extends Fragment {
    private List<FragmentBindObserver> bindObservers = new ArrayList<>();

    public void addBindObserver(FragmentBindObserver bindObserver) {
        bindObservers.add(bindObserver);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        NetLog.v("LifeCycleFragment-->>onPause");
        onEvent(FragmentBindObserver.ON_PAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
        NetLog.v("LifeCycleFragment-->>onStop");
        onEvent(FragmentBindObserver.ON_STOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NetLog.v("LifeCycleFragment-->>onDestroy");
        onEvent(FragmentBindObserver.ON_DESTROY);
        LifeCycleFragmentRetriver.remove(getFragmentManager());
    }

    private void onEvent(int event) {
        List<FragmentBindObserver> fragmentBindObservers = new ArrayList<>();
        for (FragmentBindObserver bindObserver : bindObservers) {
            if (bindObserver.getEvent() == event) {
                bindObserver.onEvent();
                fragmentBindObservers.add(bindObserver);
            }
        }

        bindObservers.removeAll(fragmentBindObservers);
    }

}
