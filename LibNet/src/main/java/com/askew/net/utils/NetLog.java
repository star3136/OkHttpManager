package com.askew.net.utils;

import android.util.Log;

import com.askew.net.BuildConfig;

/**
 * Created by lihoudong204 on 2018/11/19
 */
public class NetLog {
    private static boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "LibNet";

    public static void v(String msg){
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }
}
