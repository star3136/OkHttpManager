package com.askew.net.interceptor;

import com.askew.net.utils.NetLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2019/1/17
 */
public class ProxyInterceptor implements Interceptor {
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
            NetLog.v("开始执行本地拦截器");
            Response response = interceptorManager.interceptor(chain, request);
            NetLog.v("本地拦截器执行完成");
            return response;
        }
        return chain.proceed(request);
    }
}
