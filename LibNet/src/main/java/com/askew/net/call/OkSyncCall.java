package com.askew.net.call;

import com.askew.net.ApiHider;
import com.askew.net.IHttpProcessor;
import com.askew.net.callback.OkHttpCallback;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lihoudong204 on 2018/11/14
 * 同步请求的OkCall
 */
public class OkSyncCall<T> extends OkCall<T> {
    private Type cls;

    public OkSyncCall(Request request, String fileDir, String fileName, Object tag, OkHttpCallback<T> okHttpCallback, Type cls) {
        super(request, fileDir, fileName, tag, okHttpCallback);
        this.cls = cls;
    }

    /**
     * 发起同步请求
     */
    @Override
    protected void realCall() {
        try {
            Response response = call.execute();
            onResponse(response, getType(okHttpCallback, cls));
        } catch (Exception e) {
            e.printStackTrace();
            if (disposable.isCanceled()) {
                okHttpCallback.onCancel();
            } else {
                okHttpCallback.onError(e);
            }
        }finally {
            ApiHider.getOkHttpManager().findAndRemoveRequest(tag, disposable);
            okHttpCallback.onComplete();
        }
    }

    /**
     * 获取类型，发射创建对应的实体类时使用
     * @param callback
     * @param cls
     * @return
     */
    private Type getType(OkHttpCallback<T> callback, Type cls) {
        if (cls != null) {
            return cls;
        } else {
            return callback.getParameterizedType();
        }
    }
}
