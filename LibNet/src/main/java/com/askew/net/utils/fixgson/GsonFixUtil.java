package com.askew.net.utils.fixgson;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lihoudong204 on 2019/1/10
 * 修复gson反序列化空字符串的问题
 */
public class GsonFixUtil {
    public static void fix(Gson gson) {
        try {
            List<TypeAdapterFactory> typeAdapterFactories = (List<TypeAdapterFactory>) ReflectUtils.getFieldValue(gson, "factories");
            TypeAdapterFactory relfectFactory = null;
            for (TypeAdapterFactory typeAdapterFactory : typeAdapterFactories) {
                if (typeAdapterFactory instanceof ReflectiveTypeAdapterFactory) {
                    relfectFactory = typeAdapterFactory;
                    break;
                }
            }
            if (relfectFactory != null) {
                List<TypeAdapterFactory> newFactories = new ArrayList<>(typeAdapterFactories);
                newFactories.remove(relfectFactory);
                newFactories.add(new FixReflectiveTypeAdapterFactory((ConstructorConstructor)ReflectUtils.getFieldValue(relfectFactory, "constructorConstructor"),
                        (FieldNamingStrategy)ReflectUtils.getFieldValue(relfectFactory, "fieldNamingPolicy"),
                        (Excluder)ReflectUtils.getFieldValue(relfectFactory, "excluder"),
                        (JsonAdapterAnnotationTypeAdapterFactory)ReflectUtils.getFieldValue(relfectFactory, "jsonAdapterFactory")));
                ReflectUtils.setFieldValue(gson,"factories", Collections.unmodifiableList(newFactories));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
