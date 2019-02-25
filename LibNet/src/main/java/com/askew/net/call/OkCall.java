package com.askew.net.call;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.askew.net.ApiHider;
import com.askew.net.IHttpProcessor;
import com.askew.net.OkException;
import com.askew.net.callback.OkHttpCallback;
import com.askew.net.disposable.FutureDisposable;
import com.askew.net.disposable.IDisposable;
import com.askew.net.disposable.OkDisposable;
import com.askew.net.disposable.IWrapDisposable;
import com.askew.net.disposable.OkWrapDisposable;
import com.askew.net.utils.GsonUtils;
import com.askew.net.utils.HttpResponseUtils;
import com.askew.net.utils.NetLog;
import com.askew.net.utils.ThreadUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by lihoudong204 on 2018/11/14
 * 真正发起请求的类
 */
public abstract class OkCall<T> {
    protected Request request;
    protected Call call;
    protected IWrapDisposable disposable;    //取消请求时的接口
    protected String fileDir;    //保存的文件目录
    protected String fileName;   //保存的文件名
    protected Object tag;         //本次请求的tag
    protected OkHttpCallback<T> okHttpCallback;

    public OkCall(Request request, String fileDir, String fileName, Object tag, OkHttpCallback<T> okHttpCallback) {
        this.request = request;
        this.fileDir = fileDir;
        this.fileName = fileName;
        this.tag = tag;
        this.okHttpCallback = okHttpCallback;
    }


    /**
     * 解析服务端响应
     *
     * @param type
     * @param response
     * @param file
     * @param <R>
     * @return
     * @throws IOException
     */
    protected static <R> R parseResponse(Type type, Response response, File file) throws IOException {
        BufferedSource bs = null;
        try {
            if (type.equals(Response.class)) {

                return (R) response;
            } else if (type.equals(new TypeToken<byte[]>() {
            }.getType())) {
                if (file != null) {
                    bs = Okio.buffer(Okio.source(file));
                    return (R) bs.readByteArray();
                } else {
                    return (R) response.body().bytes();
                }
            } else if (type.equals(String.class)) {
                if (file != null) {
                    bs = Okio.buffer(Okio.source(file));
                    return (R) bs.readString(Charset.forName("UTF-8"));
                } else {
                    return (R) response.body().string();
                }
            } else if (type.equals(InputStream.class)) {
                if (file != null) {
                    return (R) Okio.buffer(Okio.source(file)).inputStream();  //这里不设置bs，返回的流不能在这里关闭
                } else {
                    return (R) response.body().byteStream();
                }
            } else if (type.equals(File.class)) {
                return (R) file;
            } else if (type.equals(Response.class)) {
                return (R) response;
            } else if (type.equals(JSONObject.class)) {
                try {
                    return (R) new JSONObject(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            } else if (type.equals(JSONArray.class)) {
                try {
                    return (R) new JSONArray(response.body().string());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            String json = "";
            if (file != null) {
                bs = Okio.buffer(Okio.source(file));
                json = bs.readString(Charset.forName("UTF-8"));
            } else {
                json = response.body().string();
            }
            return GsonUtils.fromJson(json, type);
        } finally {
            if (bs != null) {
                bs.close();
            }
        }

    }

    /**
     * 发起请求
     *
     * @return
     */
    public IDisposable call() {
        if (request == null) {
            NetLog.v("请求被拦截，不发起请求");
            if (okHttpCallback != null) {
                okHttpCallback.onCancel();
                okHttpCallback.onComplete();
            }
            return null;
        }
        call = newCall();
        disposable = new OkDisposable(call, tag);
        if (okHttpCallback != null && okHttpCallback instanceof IWrapDisposable) {
            disposable.setParent((IWrapDisposable) okHttpCallback);
            ((IWrapDisposable) okHttpCallback).setChild(disposable);
            if (((IWrapDisposable) okHttpCallback).isCanceled()) {
                return disposable;
            }
        } else {
            ApiHider.getOkHttpManager().addRequest(tag, null, disposable);
        }
        if (!disposable.isCanceled()) {
            okHttpCallback.onPrepare(request);
            realCall();
        }

        return disposable;
    }

    void onResponse(Response response, Type type) throws Exception {
        NetLog.v("执行OkCall onResponse");
        File file = null;
        if (response == null) {
            NetLog.v("响应被拦截，不会响应回调");
            return;
        }
        /**
         * 用户拦截处理response
         */
        if (okHttpCallback != null && okHttpCallback.interceptResponse()) {
            okHttpCallback.onResponse(response);
            return;
        }

        //尝试保存文件
        if (!TextUtils.isEmpty(fileDir)) {
            file = new File(HttpResponseUtils.getFilePath(response, fileDir, fileName));
            HttpResponseUtils.trySaveFile(response, file);
        }

        if (okHttpCallback == null || type == null) {
            return;
        }
        if (type.equals(File.class) && TextUtils.isEmpty(fileDir)) {
            okHttpCallback.onError(new OkException("Parameter type is File, but didn't set file path"));
            return;
        }

        if (!response.isSuccessful()) {
            if (okHttpCallback != null) {
                okHttpCallback.onFail(response.code(), response.message());
            }
            return;
        }


        T result = parseResponse(type, response, file);
        okHttpCallback.onSuccess(result);
    }

    protected Call newCall() {
        return ApiHider.getSettings().getOkHttpClient().newCall(request);
    }

    /**
     * 子类实现这个方法，执行不同的请求逻辑
     */
    protected abstract void realCall();
}
