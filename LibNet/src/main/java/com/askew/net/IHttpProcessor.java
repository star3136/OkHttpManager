package com.askew.net;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by lihoudong204 on 2018/9/30
 * 提供在请求发起之前和取得响应之后拦截处理的能力
 */
public interface IHttpProcessor {
    Request preRequest(Request request);

    Response afterResponse(Response response);
}
