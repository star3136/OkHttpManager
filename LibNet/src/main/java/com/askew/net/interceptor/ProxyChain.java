package com.askew.net.interceptor;

import com.askew.net.utils.NetLog;

import java.io.IOException;
import java.util.List;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2019/1/17
 */
public class ProxyChain implements Interceptor.Chain {
    private Interceptor.Chain source;
    private List<Interceptor> interceptors;
    private int index;
    private Request request;

    public ProxyChain(Interceptor.Chain source, List<Interceptor> interceptors, int index, Request request) {
        this.source = source;
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response proceed(Request request) throws IOException {
        if (index > interceptors.size()) {
            throw new IndexOutOfBoundsException("Proxy interceptors size out index");
        }
        if (request == null) {
            throw new NullPointerException("request == null");
        }
        if (source == null) {
            throw new NullPointerException("chain == null");
        }
        if (index == interceptors.size()) {
            return source.proceed(request);
        }

        return interceptors.get(index).intercept(new ProxyChain(source, interceptors, index + 1, request));
    }

    @Override
    public Connection connection() {
        return source.connection();
    }
}
