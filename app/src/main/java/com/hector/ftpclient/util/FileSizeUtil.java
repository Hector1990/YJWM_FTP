package com.hector.ftpclient.util;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Hector on 15/12/21.
 */
public class FileSizeUtil {

    public static String customeFileSize(long size) {
        int level = 0;
        while (size > 1024) {
            level++;
            size /= 1024;
        }
        return size + parseLevel(level);
    }

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
//            file.createNewFile();
        }
        return size;
    }

    private static String parseLevel(int level) {
        switch (level) {
            case 0:
                return "Byte";
            case 1:
                return "KB";
            case 2:
                return "MB";
            case 3:
                return "GB";
            case 4:
                return "TB";
            default:
                return "";
        }
    }
}
