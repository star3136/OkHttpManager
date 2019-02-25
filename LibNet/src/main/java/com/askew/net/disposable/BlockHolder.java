package com.askew.net.disposable;

import java.util.concurrent.CountDownLatch;

/**
 * Created by lihoudong204 on 2018/12/14
 */
public class BlockHolder<T> {
    private T obj;
    private CountDownLatch latch = new CountDownLatch(1);

    public void set(T obj){
        this.obj = obj;
        latch.countDown();
    }

    public boolean hasObj() {
        return obj != null;
    }

    public T get(){
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
