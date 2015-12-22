package com.hector.ftpclient.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hector on 15/12/21.
 */
public class DateUtil {

    public static String parseDate(String time) {
        Date date = new Date(time.substring(0, time.length() - 1));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String parseDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}
