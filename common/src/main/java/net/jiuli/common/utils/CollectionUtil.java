package net.jiuli.common.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jiuli on 17-8-25.
 */

public class CollectionUtil {
    public static <T> T[] toArray(List<T> items, Class<T> tClass) {
        if (items == null || items.size() == 0) {
            return null;
        }
        int size = items.size();
        try {
            T[] array = (T[]) Array.newInstance(tClass, size);
            return items.toArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T[] toArray(Set<T> items, Class<T> tClass) {
        if (items == null || items.size() == 0) {
            return null;
        }
        int size = items.size();
        try {
            T[] array = (T[]) Array.newInstance(tClass, size);
            return items.toArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    public static <T> T[] toArray(HashSet<T> items, Class<T> tClass) {
        if (items == null || items.size() == 0) {
            return null;
        }
        int size = items.size();
        try {
            T[] array = (T[]) Array.newInstance(tClass, size);
            return items.toArray(array);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static <T> ArrayList<T> toArray(T[] items) {
        if (items == null || items.length == 0) {
            return null;
        }
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;

    }
}
