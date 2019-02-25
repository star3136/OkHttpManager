package com.askew.net.utils.fixgson;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by lihoudong204 on 2019/1/10
 */
public class ReflectUtils {
    public static Field getField(Object obj, String fieldName) {
        return getField(obj.getClass(), fieldName);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        while (clazz != null) {

            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (field != null) {
                break;
            }
            clazz = clazz.getSuperclass();
        }
        return field;
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            return getField(obj, fieldName).get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        try {
            getField(obj, fieldName).set(obj, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName) {
        Method method = null;
        while (clazz != null) {

            try {
                method = clazz.getDeclaredMethod(methodName);
                method.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (method != null) {
                break;
            }
            clazz = clazz.getSuperclass();
        }
        return method;
    }

    public static Constructor getConstructor(Class clazz, Class<?>... parameterTypes) {
        Constructor constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return constructor;
    }
}
