package com.askew.net.method;

import android.text.TextUtils;

import com.askew.net.OkHttpManagerSettings;
import com.askew.net.interceptor.InterceptorManager;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by lihoudong204 on 2018/9/27
 * Http的POST方法
 * 提供两种post方式
 * 1.以表单的格式提交,使用{@link HttpMethod#withParam(String, String)} 添加的参数
 * 2.直接提交,{@link com.askew.net.OkHttpManager#post(String, String)}
 */
public class HttpPost extends HttpMethod {
    protected OkHttpManagerSettings settings;
    protected String url;
    private Object body;

    public HttpPost(OkHttpManagerSettings settings, String url, String body) {
        this.settings = settings;
        this.url = url;
        if (body != null) {
            body(body);
        }
    }

    public HttpPost(OkHttpManagerSettings settings, String url, byte[] body) {
        this.settings = settings;
        this.url = url;
        if (body != null) {
            body(body);
        }
    }

    public HttpPost(OkHttpManagerSettings settings, String url) {
        this(settings, url, (byte[]) null);
    }


    public HttpPost body(String body) {
        this.body = body;
        withHeader("Content-Type", "application/json");
        return this;
    }

    public HttpPost body(byte[] body) {
        this.body = body;
        withHeader("Content-Type", "application/octet-stream");
        return this;
    }

    @Override
    Request newRequest() {
        Request.Builder builder = new Request.Builder();
        /**
         * 添加公共Http请求头部
         */
        addHeaders(builder, settings.getCommonHttpHeaders());
        /**
         * 添加本次请求的请求头部
         */
        addHeaders(builder, httpHeaders);

        RequestBody requestBody = null;


        if (body == null) {
            String contentType = httpHeaders.get("Content-Type");
            if (TextUtils.isEmpty(contentType) || contentType.equals("application/x-www-form-urlencoded")) {
                /**
                 * 从表单创建
                 */
                requestBody = buildFormBody();
            } else {
                requestBody = RequestBody.create(MediaType.parse(contentType), "");
            }
        } else {
            requestBody = buildContentBody();
        }

        builder.post(requestBody).url(url).tag(new InterceptorManager(httpInterceptors, networkInterceptors));
        return builder.build();
    }

    private RequestBody buildContentBody() {
        RequestBody requestBody = null;
        if (body instanceof byte[]) {
            requestBody = RequestBody.create(MediaType.parse(httpHeaders.get("Content-Type")), (byte[]) body);
        } else if (body instanceof String) {
            requestBody = RequestBody.create(MediaType.parse(httpHeaders.get("Content-Type")), (String) body);
        }

        return requestBody;
    }

    private RequestBody buildFormBody() {
        RequestBody requestBody = null;
        FormBody.Builder postBodyBuilder = new FormBody.Builder();

        /**
         * 添加公共请求参数
         */
        addParams(postBodyBuilder, settings.getCommonHttpParams());

        /**
         * 添加本次请求参数
         */
        addParams(postBodyBuilder, httpParams);

        requestBody = postBodyBuilder.build();
        return requestBody;
    }

    private void addParams(FormBody.Builder builder, Map<String, String> httpParams) {
        if (httpParams == null || httpParams.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : httpParams.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
    }
}
