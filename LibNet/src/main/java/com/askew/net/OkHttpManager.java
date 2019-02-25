package com.askew.net;

import com.askew.net.cache.DiskCache;
import com.askew.net.disposable.IDisposable;
import com.askew.net.method.HttpDownload;
import com.askew.net.method.HttpGet;
import com.askew.net.method.HttpMethod;
import com.askew.net.method.HttpPost;
import com.askew.net.method.HttpUpload;
import com.askew.net.method.PartialHttpDownload;

/**
 * Created by lihoudong204 on 2018/9/26
 * OkHttp封装管理类, 入口
 */
public class OkHttpManager {
    private static OkHttpManager instance;
    OkHttpManagerSettings settings;
    private RequestManager requestManager;
    DiskCache diskCache;

    private OkHttpManager(){
        requestManager = new RequestManager();
    }

    static OkHttpManager getInstance() {
        if (instance == null) {
            synchronized (OkHttpManager.class) {
                if (instance == null) {
                    instance = new OkHttpManager();
                }
            }
        }

        return instance;
    }

    void init(OkHttpManagerSettings settings) {
        this.settings = settings;
        diskCache = new DiskCache(settings.getContext());
    }

    public static OkHttpManagerSettings.Builder newSettingsBuilder() {
        if (instance == null || instance.settings == null) {
            return new OkHttpManagerSettings.Builder();
        }else {
            return instance.settings.newBuilder();
        }
    }

    /**
     * get方法
     * @param url
     * @return
     */
    public  static HttpMethod get(String url){
        getInstance().checkInit();
        return new HttpGet(getInstance().settings, url);
    }

    /**
     * post方法
     * @param url
     * @return
     */
    public static HttpMethod post(String url) {
        getInstance().checkInit();
        return new HttpPost(getInstance().settings, url);
    }

    /**
     * post方法
     * @param url
     * @param body json
     * @return
     */
    public static HttpMethod post(String url, String body){
        getInstance().checkInit();
        return new HttpPost(getInstance().settings, url, body);
    }

    /**
     * post方法
     * @param url
     * @param body byte数组
     * @return
     */
    public static HttpMethod post(String url, byte[] body){
        getInstance().checkInit();
        return new HttpPost(getInstance().settings, url, body);
    }

    /**
     * 下载文件
     * @param url
     * @param fileDir
     * @return
     */
    public static HttpMethod download(String url, String fileDir) {
        return download(url, fileDir, null);
    }

    /**
     * 下载文件
     * @param url
     * @param fileDir
     * @param fileName
     * @return
     */
    public static HttpMethod download(String url, String fileDir, String fileName) {
        return download(url, fileDir, fileName, false);
    }

    /**
     * 断点续传下载文件
     * @param url
     * @param fileDir
     * @param fileName
     * @param partial true 启用断点续传 false 不启用断点续传
     * @return
     */
    public static HttpMethod download(String url, String fileDir, String fileName, boolean partial) {
        getInstance().checkInit();
        if (partial) {
            return new PartialHttpDownload(getInstance().settings, url, fileDir, fileName);
        }else {
            return new HttpDownload(getInstance().settings, url, fileDir, fileName);
        }
    }

    /**
     * 上传文件
     * @param url
     * @param filepath
     * @return
     */
    public static HttpMethod upload(String url, String filepath) {
        getInstance().checkInit();
        return new HttpUpload(getInstance().settings, url, filepath);
    }

    /**
     * 上传文件
     * @param url
     * @param name
     * @param filepath
     * @return
     */
    public static HttpMethod upload(String url, String name, String filepath) {
        getInstance().checkInit();
        return new HttpUpload(getInstance().settings, url, name, filepath);
    }

    /**
     * 上传字节码
     * @param url
     * @param name
     * @param bytes
     * @return
     */
    public static HttpMethod upload(String url, String name, byte[] bytes) {
        getInstance().checkInit();
        return new HttpUpload(getInstance().settings, url, name, bytes);
    }

    /**
     * 检查settings是否设置
     */
    private void checkInit() {
        if (getInstance().settings == null) {
            throw new IllegalStateException("OkHttpManager didn't set");
//            synchronized (OkHttpManager.class) {
//                if (getInstance().settings == null) {
//                    newSettingsBuilder()
//                            .build()
//                            .init();
//                }
//            }
        }
    }

    /**
     * 添加正在运行的请求和它对应的key，方便取消请求
     * @param tag
     * @param oldOne
     * @param newOne
     */
    public void addRequest(Object tag, IDisposable oldOne, IDisposable newOne) {
        requestManager.addRequest(tag, oldOne, newOne);
    }

    /**
     * 移除请求
     * @param tag
     * @param disposable
     */
    public void removeRequest(Object tag, IDisposable disposable) {
        requestManager.removeRequest(tag, disposable);
    }


    public void findAndRemoveRequest(Object tag, IDisposable disposable) {
        requestManager.findAndRemoveRequest(tag, disposable);
    }
    /**
     * 取消对应tag的请求
     * @param tag
     */
    public static void cancel(Object tag) {
        getInstance().requestManager.cancel(tag);
    }


    /**
     * 取消所有的请求
     */
    public static void cancelAll() {
        getInstance().requestManager.cancelAll();
    }

    /**
     * 取消请求,内部使用，外部用户不需要调用这个方法，如果持有了{@link IDisposable},则直接调用{@link IDisposable#cancel()}即可
     * @param tag
     * @param disposable
     */
    public void cancel(Object tag, IDisposable disposable) {
        requestManager.cancel(tag, disposable);
    }
}
