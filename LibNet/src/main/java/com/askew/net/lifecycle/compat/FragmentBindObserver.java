package com.askew.net.lifecycle.compat;

import com.askew.net.lifecycle.BindObserver;

/**
 * Created by lihoudong204 on 2018/11/19
 * Fragment使用的BindObserver
 */
public class FragmentBindObserver extends BindObserver {
    public static final int ON_PAUSE = 1;
    public static final int ON_STOP = 2;
    public static final int ON_DESTROY = 3;

    private int event;

    public static FragmentBindObserver get(int event) {
        return new FragmentBindObserver(event);
    }
    private FragmentBindObserver(int event){
        this.event = event;
    }

    public int getEvent() {
        return event;
    }
}
