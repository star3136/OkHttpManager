package com.askew.net.cache;

import android.content.Context;
import android.text.TextUtils;

import com.jakewharton.disklrucache.DiskLruCache;
import com.askew.net.utils.GsonUtils;
import com.askew.net.utils.MD5Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by lihoudong204 on 2018/11/20
 * 磁盘缓存
 * 存储断点续传相关的数据
 */
public class DiskCache {
    private DiskLruCache diskLruCache;
    private static final int VERSION = 1;
    private static final int MAX_SIZE = 20 * 1024 * 1024;  //容量 20MB

    public DiskCache(Context context) {
        try {
            diskLruCache = DiskLruCache.open(getCacheFilePath(context), VERSION, 1, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存续传的起始range
     * @param fileName
     * @param startRange
     */
    public synchronized void saveStartRange(String fileName, long startRange) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        PartialEntity entity = get(fileName);
        entity.setStartRange(startRange);
        put(fileName, entity);
    }

    /**
     * 保存文件的总长度
     * @param fileName
     * @param contentLength
     */
    public synchronized void saveContentLength(String fileName, long contentLength) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        PartialEntity entity = get(fileName);
        entity.setContentLength(contentLength);
        put(fileName, entity);
    }

    /**
     * 保存用于校验服务端文件是否更改的字段，服务端返回200时，以Http头部Last-Modified的值保存
     * @param fileName
     * @param ifRange
     */
    public synchronized void saveIfRange(String fileName, String ifRange) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(ifRange)) {
            return;
        }
        PartialEntity entity = get(fileName);
        entity.setIfRange(ifRange);
        put(fileName, entity);
    }

    /**
     * 获取续传的起始range
     * @param fileName
     * @return
     */
    public synchronized long getStartRange(String fileName){
        if (TextUtils.isEmpty(fileName)) {
            return 0;
        }
        PartialEntity entity = get(fileName);
        return entity.getStartRange();
    }

    /**
     * 获取文件的总长度
     * @param fileName
     * @return
     */
    public synchronized long getContentLength(String fileName){
        if (TextUtils.isEmpty(fileName)) {
            return 0;
        }
        PartialEntity entity = get(fileName);
        return entity.getContentLength();
    }

    /**
     * 获取校验字段，请求时作为Http头部的If-Range字段的值
     * @param fileName
     * @return
     */
    public synchronized String getIfRange(String fileName){
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        PartialEntity entity = get(fileName);
        return entity.getIfRange();
    }

    /**
     * 删除，文件下载成功后，需要删除
     * @param fileName
     * @return
     */
    public synchronized boolean remove(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        try {
            return diskLruCache.remove(MD5Utils.md5(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void put(String fileName, PartialEntity entity) {
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(MD5Utils.md5(fileName));
            editor.set(0, GsonUtils.toJson(entity));
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PartialEntity get(String fileName) {

        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(MD5Utils.md5(fileName));
            if (snapshot == null) {
                return new PartialEntity();
            }
            String partialJson = snapshot.getString(0);
            if (TextUtils.isEmpty(partialJson)) {
                return new PartialEntity();
            }
            return GsonUtils.fromJson(partialJson, PartialEntity.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File getCacheFilePath(Context context) {
        String dirPath = context.getFilesDir().getAbsolutePath() + File.separator + "okhttpmanager";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
