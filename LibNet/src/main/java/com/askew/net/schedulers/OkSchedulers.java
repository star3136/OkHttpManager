package com.askew.net.schedulers;

/**
 * Created by lihoudong204 on 2018/11/15
 * 对外提供的线程调度工具类
 */
public class OkSchedulers {
    private static final IScheduler IO;
    private static final IScheduler MAIN;
    private static final IScheduler UPLOAD;

    static class IOHolder {
        static final IScheduler DEFAULT = new IoScheduler();
    }

    static class MainHolder {
        static final IScheduler DEFAULT = new MainScheduler();
    }

    static class UploadHolder {
        static final IScheduler DEFAULT = new CustomScheduler(3);
    }

    static {
        IO = IOHolder.DEFAULT;
        MAIN = MainHolder.DEFAULT;
        UPLOAD = UploadHolder.DEFAULT;
    }
    public static IScheduler io() {
        return IO;
    }

    public static IScheduler main() {
        return MAIN;
    }

    public static IScheduler upload() {
        return UPLOAD;
    }
}
