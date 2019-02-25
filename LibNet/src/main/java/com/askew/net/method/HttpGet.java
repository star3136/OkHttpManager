package com.askew.net.method;

import com.askew.net.OkHttpManagerSettings;
import com.askew.net.interceptor.InterceptorManager;

import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by lihoudong204 on 2018/9/26
 * Http的GET方法
 *
 */
public class HttpGet extends HttpMethod {
    private String url;
    protected OkHttpManagerSettings settings;

    public HttpGet(OkHttpManagerSettings settings, String url) {
        this.settings = settings;
        this.url = url;
    }

    Request newRequest() {
        Request.Builder builder = new Request.Builder().get();
        /**
         * 添加公共Http请求头部
         */
        addHeaders(builder, settings.getCommonHttpHeaders());
        /**
         * 添加本次请求的请求头部
         */
        addHeaders(builder, httpHeaders);

        HttpUrl httpUrl = HttpUrl.parse(url);

        HttpUrl.Builder newUrlBuilder = httpUrl.newBuilder();
        /**
         * 添加公共参数
         */
        addParams(newUrlBuilder, settings.getCommonHttpParams());
        /**
         * 添加本次请求的参数
         */
        addParams(newUrlBuilder, httpParams);

        return builder.url(newUrlBuilder.build()).tag(new InterceptorManager(httpInterceptors, networkInterceptors)).build();
    }

    private void addParams(HttpUrl.Builder builder, Map<String, String> httpParams) {
        if (httpParams == null || httpParams.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : httpParams.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
    }
}
