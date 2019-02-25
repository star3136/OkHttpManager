package com.askew.net.disposable;

/**
 * Created by lihoudong204 on 2018/11/16
 * 双向链表结构的IDisposable
 * 任何一个链表中的节点发起取消请求，那么整条链表最终都会取消,并且从{@link com.askew.net.RequestManager}中移除链表头节点
 * 解决的问题：监听生命周期方法取消请求时，不能保证取到IDisposable链的头节点，那么此时需要递归到头节点，再取消请求
 */
public interface IWrapDisposable extends IDisposable {
    /**
     * 设置子节点
     * @param disposable
     */
    void setChild(IWrapDisposable disposable);

    /**
     * 设置父节点
     * @param disposable
     */
    void setParent(IWrapDisposable disposable);

    IWrapDisposable getChild();

    IWrapDisposable getParent();

    boolean hasChild();

    /**
     * 父节点调用此方法来取消请求
     * @param parent 父节点
     */
    void cancel(IWrapDisposable parent);
}
