package com.askew.net.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lihoudong204 on 2019/1/17
 */
public class CollectionsUtils {
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    public static <T extends Collection> T unmodifiable(T t){
        if (t == null) {
            return null;
        }
        if (t instanceof List) {
            return (T) Collections.unmodifiableList((List<?>) t);
        } else if (t instanceof Map) {
            return (T) Collections.unmodifiableMap((Map<?, ?>) t);
        } else if (t instanceof Set) {
            return (T) Collections.unmodifiableSet((Set<?>) t);
        } else {
            return (T) Collections.unmodifiableCollection(t);
        }
    }
}
