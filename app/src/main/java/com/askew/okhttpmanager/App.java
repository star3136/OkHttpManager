package com.askew.okhttpmanager;

import android.app.Application;

import com.askew.net.OkHttpManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpManager.newSettingsBuilder()
                .context(this)
                .build()
                .init();
    }
}
