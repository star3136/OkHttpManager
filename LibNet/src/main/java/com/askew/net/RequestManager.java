package com.askew.net;

import com.askew.net.disposable.IDisposable;
import com.askew.net.disposable.IWrapDisposable;
import com.askew.net.utils.NetLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lihoudong204 on 2018/11/16
 * 管理正在发起到请求
 */
public class RequestManager {
    private final Map<Object, Map<IDisposable, IDisposable>> runningRequests = new HashMap<>();

    public void addRequest(Object tag, IDisposable oldOne, IDisposable newOne) {
        /**
         * 防止在添加请求时，正在取消所有的请求，造成请求丢失
         */
        synchronized (runningRequests) {
            Map<IDisposable, IDisposable> disposables = runningRequests.get(tag);
            if (disposables == null) {
                disposables = new HashMap<>();
                runningRequests.put(tag, disposables);
            }

            if (oldOne != null) {
                disposables.remove(oldOne);
            }
            disposables.put(newOne, newOne);
        }
    }

    public void cancelAll() {
        Collection<Map<IDisposable, IDisposable>> currentRequests = null;

        /**
         * 用锁加拷贝保持线程安全
         * 防止在取消的同时，有新的请求，造成请求丢失
         */
        synchronized (runningRequests) {
            currentRequests = new ArrayList<>(runningRequests.values());
            runningRequests.clear();
        }
        for (Map<IDisposable, IDisposable> disposables : currentRequests) {
            for (IDisposable disposable : disposables.keySet()) {
                disposable.cancel();
            }
        }
    }

    public void cancel(Object tag) {
        Map<IDisposable, IDisposable> disposables;
        synchronized (runningRequests) {
            disposables = runningRequests.remove(tag);
        }
        if (disposables == null || disposables.isEmpty()) {
            return;
        }
        if (disposables != null) {
            for (IDisposable disposable : disposables.keySet()) {
                disposable.cancel();
            }
            disposables.clear();
        }
    }

    /**
     * 取消请求,内部使用，外部用户不需要调用这个方法，如果持有了{@link IDisposable},则直接调用{@link IDisposable#cancel()}即可
     *
     * @param tag
     * @param disposable
     */
    public void cancel(Object tag, IDisposable disposable) {
        IDisposable dis = removeRequest(tag, disposable);
        if (dis != null) {
            dis.cancel();
        }
    }


    public IDisposable removeRequest(Object tag, IDisposable disposable) {
        if (disposable == null) {
            return null;
        }
        synchronized (runningRequests) {
            Map<IDisposable, IDisposable> disposables = runningRequests.get(tag);
            if (disposables == null) {
                return null;
            }
            IDisposable dis = disposables.remove(disposable);
            if (disposables.isEmpty()) {
                runningRequests.remove(tag);
            }
            if (dis != null) {
                NetLog.v("removeRequest 找到diposable-------------");
                return disposable;
            }
        }

        NetLog.v("removeRequest 未找到diposable================");
        return null;
    }

    public IDisposable findAndRemoveRequest(Object tag, IDisposable disposable) {
        if (disposable == null) {
            return null;
        }
        IDisposable result = null;
        synchronized (runningRequests) {
            Map<IDisposable, IDisposable> disposables = runningRequests.get(tag);
            if (disposables == null) {
                return null;
            }
            if (disposables.remove(disposable) == disposable) {
                result = disposable;
            }
            if (disposable instanceof IWrapDisposable) {
                IWrapDisposable parent = ((IWrapDisposable) disposable);
                while (result == null && parent.getParent() != null) {
                    parent = parent.getParent();
                    if (disposables.remove(parent) == parent) {
                        NetLog.v("findAndRemoveRequest 在parent中找到disposable");
                        result = parent;
                    }
                }

                IWrapDisposable child = ((IWrapDisposable) disposable);
                while (result == null && child.hasChild()) {
                    child = child.getChild();
                    if (disposables.remove(child) == child) {
                        NetLog.v("findAndRemoveRequest 在child中到disposable");
                        result = child;
                    }
                }
            }
            if (result == null) {
                NetLog.v("findAndRemoveRequest 未找到disposable");
            }
            if (result != null && disposables.isEmpty()) {
                runningRequests.remove(tag);
            }
        }

        return result;
    }
}
