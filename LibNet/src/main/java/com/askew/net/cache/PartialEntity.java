package com.askew.net.cache;

/**
 * Created by lihoudong204 on 2018/11/21
 * 断点续传的进度实体类
 */
public class PartialEntity {
    private long startRange;     //下载的起始range，也就是已下载的长度
    private long contentLength;  //文件的总长度
    private String ifRange;     //校验字段  以Last-Modified存入，以If-Range输出

    public long getStartRange() {
        return startRange;
    }

    public void setStartRange(long startRange) {
        this.startRange = startRange;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getIfRange() {
        return ifRange;
    }

    public void setIfRange(String ifRange) {
        this.ifRange = ifRange;
    }
}
