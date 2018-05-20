package net.jiuli.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jiuli on 17-8-25.
 */

public class HashUtil {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String convertToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(HEX_DIGITS[(b & 0xF0) >>> 4]);
            sb.append(HEX_DIGITS[b & 0x0F]);
        }
        return sb.toString();
    }

    public static String getMd5String(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(str.getBytes());
        return convertToHexString(md5.digest());
    }


    public static String getMD5String(File file) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        InputStream is = null;
        byte[] bytes = new byte[10240];
        try {
            is = new FileInputStream(file);
            for (int readLen; (readLen = is.read(bytes)) > 0; ) {
                md5.update(bytes, 0, readLen);
            }
            return convertToHexString(md5.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}