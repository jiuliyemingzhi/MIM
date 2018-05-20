package net.jiuli.common.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by jiuli on 17-8-26.
 */

public class StreamUtil {
    public static boolean copy(File inFile, OutputStream os) {
        if (inFile.exists()) {
            InputStream is = null;
            try {
                is = new FileInputStream(inFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return copy(is, os);
        } else {
            return false;
        }
    }


    public static boolean copy(File inFile, File outFile) {
        if (inFile.exists()) {
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(inFile));
                return copy(is, outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static void unZip(File zipFile, File desDir) throws IOException {
        String folderPath = desDir.getAbsolutePath();
        ZipFile zf = new ZipFile(zipFile);

        for (Enumeration<? extends ZipEntry> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry zipEntry = entries.nextElement();
            String name = zipEntry.getName();
            if (name.startsWith(".")) {
                continue;
            }
            InputStream inputStream = zf.getInputStream(zipEntry);
            String path = String.format("%s%s%s", folderPath, File.separator, name);
            File file = new File(path);
            copy(inputStream, file);
        }
    }


    public static boolean copy(InputStream is, OutputStream os) {
        try {
            byte[] bytes = new byte[10240];
            for (int readLen; (readLen = is.read(bytes)) > 0; ) {
                os.write(bytes, 0, readLen);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(is);
            close(os);
        }
    }

    public static boolean copy(InputStream is, File outFile) {
        if (!outFile.exists()) {
            File fileParentDir = outFile.getParentFile();
            if (!fileParentDir.exists()) {
                if (!fileParentDir.mkdir()) {
                    return false;
                }
            }
            try {
                if (!outFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
            return copy(is, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        } else {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static boolean delete(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        } else {
            File file = new File(path);
            return file.exists() && file.delete();
        }

    }

}
