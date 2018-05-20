package net.jiuli.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by jiuli on 18-2-11.
 */

public class FileTools {


    public static String formatSize(Long size) {
        String result;
        if (size < 1000) {
            result = format("%sB", size);
        } else if (size < 1000 * 1000) {
            result = format("%dK", size / 1024);
        } else if (size < 1000 * 1000 * 1000) {
            result = format("%01.1fM", size / 1024f / 1024f);
        } else if (size < 1000 * 1000 * 1000 * 1000 * 1000) {
            result = format("%01.1fG", size / 1024f / 1024f / 1024f);
        } else {
            result = "很大";
        }
        return result;
    }

    private static String format(String format, Object... args) {
        return String.format(format, args);
    }


    public static String getParentFolderName(@NonNull String path) {
        return new WeakReference<>(new File(path)).get().getParentFile().getName();
    }

    public static String getName(@NonNull String path) {
        return new WeakReference<>(new File(path)).get().getName();
    }

    public static String getParent(@NonNull String path) {
        return new WeakReference<>(new File(path)).get().getParent();
    }

    public static Long getSize(@NonNull String path) {
        return new WeakReference<>(new File(path)).get().length();
    }

}
