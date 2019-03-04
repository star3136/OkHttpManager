package com.askew.net;

import android.content.Context;
import android.text.TextUtils;

import com.askew.net.interceptor.ProxyInterceptor;
import com.askew.net.interceptor.ProxyNetworkInterceptor;
import com.askew.net.utils.CollectionsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by lihoudong204 on 2018/9/26
 * 配置项
 */
public class OkHttpManagerSettings {
    private Context context;
    private OkHttpClient okHttpClient;

    private Map<String, String> commonHttpHeaders;
    private Map<String, String> commonHttpParams;

    private OkHttpManagerSettings(Builder builder) {
        context = builder.context.getApplicationContext();
        okHttpClient = builder.okHttpClient;
        commonHttpHeaders = builder.httpHeaders;
        commonHttpParams = builder.httpParams;


        OkHttpClient.Builder okBuilder = null;
        if (okHttpClient == null) {
            okBuilder = new OkHttpClient.Builder();
        } else {
            okBuilder = builder.okHttpClient.newBuilder();
        }
        if (!CollectionsUtils.isEmpty(builder.commonInterceptors)) {
            for (Interceptor interceptor : builder.commonInterceptors) {
                okBuilder.addInterceptor(interceptor);
            }
        }

        if (!CollectionsUtils.isEmpty(builder.commonNetworkInterceptors)) {
            for (Interceptor interceptor : builder.commonNetworkInterceptors) {
                okBuilder.addNetworkInterceptor(interceptor);
            }
        }

        okHttpClient = okBuilder
                .build();
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public Map<String, String> getCommonHttpHeaders() {
        return commonHttpHeaders;
    }

    public Map<String, String> getCommonHttpParams() {
        return commonHttpParams;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public Context getContext() {
        return context;
    }

    public void init() {
        OkHttpManager.getInstance().init(this);
    }

    public static class Builder {
        private Context context;
        private OkHttpClient okHttpClient;
        private Map<String, String> httpHeaders = new HashMap<>();
        private Map<String, String> httpParams = new HashMap<>();
        private List<Interceptor> commonInterceptors;
        private List<Interceptor> commonNetworkInterceptors;

        Builder(OkHttpManagerSettings settings) {
            context = settings.context;
            okHttpClient = settings.okHttpClient;
            httpHeaders = settings.commonHttpHeaders;
            httpParams = settings.commonHttpParams;
        }

        Builder() {
            withCommonInterceptor(new ProxyInterceptor());
            withCommonNetworkInterceptor(new ProxyNetworkInterceptor());
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder okHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Builder withCommonHttpHeader(String name, String value) {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
                return this;
            }
            this.httpHeaders.put(name, value);
            return this;
        }

        public Builder withCommonHttpParam(String name, String value) {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
                return this;
            }
            this.httpParams.put(name, value);
            return this;
        }

        public Builder withCommonInterceptor(Interceptor interceptor) {
            if (commonInterceptors == null) {
                commonInterceptors = new ArrayList<>();
            }
            commonInterceptors.add(interceptor);
            return this;
        }

        public Builder withCommonNetworkInterceptor(Interceptor interceptor) {
            if (commonNetworkInterceptors == null) {
                commonNetworkInterceptors = new ArrayList<>();
            }
            commonNetworkInterceptors.add(interceptor);
            return this;
        }

        public OkHttpManagerSettings build() {
            return new OkHttpManagerSettings(this);
        }
    }
}
