package com.askew.net;

/**
 * Created by lihoudong204 on 2018/9/28
 */
public class OkException extends Exception {
    public OkException() {
    }

    public OkException(String message) {
        super(message);
    }

    public OkException(String message, Throwable cause) {
        super(message, cause);
    }

    public OkException(Throwable cause) {
        super(cause);
    }
}
