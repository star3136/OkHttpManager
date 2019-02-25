package com.askew.net.interceptor;

import com.askew.net.utils.CollectionsUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2019/1/17
 */
public class InterceptorManager {
    private List<Interceptor> interceptors;
    private List<Interceptor> networkInterceptors;

    public InterceptorManager(List<Interceptor> interceptors, List<Interceptor> networkInterceptors) {
        this.interceptors = CollectionsUtils.unmodifiable(interceptors);
        this.networkInterceptors = CollectionsUtils.unmodifiable(networkInterceptors);
    }

    public Response interceptor(Interceptor.Chain chain, Request request) throws IOException {
        try {
            if (interceptors == null || interceptors.isEmpty()) {
                return chain.proceed(request);
            }
            return new ProxyChain(chain, interceptors, 0, request).proceed(request);
        }catch (Exception e){
            throw new IOException(e);
        }

    }

    public Response networkInterceptor(Interceptor.Chain chain, Request request) throws IOException {
        try {
            if (networkInterceptors == null || networkInterceptors.isEmpty()) {
                return chain.proceed(request);
            }
            return new ProxyChain(chain, networkInterceptors, 0, request).proceed(request);
        }catch (Exception e){
            throw new IOException(e);
        }
    }
}
