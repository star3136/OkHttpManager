package com.askew.net.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.askew.net.utils.fixgson.GsonFixUtil;

import java.lang.reflect.Type;

/**
 * Created by lihoudong204 on 2018/10/30
 */
public class GsonUtils {
    private static Gson gson = new Gson();
    static {
        GsonFixUtil.fix(gson);
    }

    public static String toJson(Object cls) {
        if (cls == null) {
            return "";
        }
        return gson.toJson(cls);
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, cls);
    }

    public static <T> T fromJson(String json, Type type) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, type);
    }
}
