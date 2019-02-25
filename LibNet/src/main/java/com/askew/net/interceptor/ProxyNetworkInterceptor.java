package com.askew.net.interceptor;

import com.askew.net.utils.NetLog;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2019/1/17
 */
public class ProxyNetworkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request tmp = request;
        InterceptorManager interceptorManager = null;
        while (tmp.tag() != null) {
            if (tmp.tag() instanceof Request && tmp.tag() != tmp) {
                tmp = (Request) tmp.tag();
            } else if (tmp.tag() instanceof InterceptorManager) {
                interceptorManager = (InterceptorManager) tmp.tag();
                break;
            } else {
                break;
            }
        }
        if (interceptorManager != null) {
            NetLog.v("开始执行网络拦截器");
            Response response = interceptorManager.networkInterceptor(chain, request);
            NetLog.v("网络拦截器执行完成");
            return response;
        }
        return chain.proceed(request);
    }
}
