package com.askew.net;

import com.askew.net.cache.DiskCache;

/**
 * Created by lihoudong204 on 2018/11/14
 */
public class ApiHider {
    public static OkHttpManagerSettings getSettings() {
        return OkHttpManager.getInstance().settings;
    }

    public static OkHttpManager getOkHttpManager() {
        return OkHttpManager.getInstance();
    }

    public static DiskCache getCache() {
        return OkHttpManager.getInstance().diskCache;
    }
}
