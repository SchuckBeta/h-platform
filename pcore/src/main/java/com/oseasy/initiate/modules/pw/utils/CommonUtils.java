package com.hch.platform.pcore.modules.pw.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {

    /**
     * 从当前天开始，往前或往后推多少天，取那天0时0分0秒
     *
     * @param delay
     * @return
     */
    public static Date getDayFromBegin(int delay) {
        Calendar c = Calendar.getInstance();
        return getDayFromBegin(c.getTime(), delay);
    }

    /**
     * 从当前天开始，往前或往后推多少天，取那天23时59分59秒
     *
     * @param delay
     * @return
     */
    public static Date getDayToEnd(int delay) {
        Calendar c = Calendar.getInstance();
        return getDayToEnd(c.getTime(), delay);
    }

    /**
     * 指定日期，往前或往后推多少天，取那天0时0分0秒
     *
     * @param date
     * @param delay
     * @return
     */
    public static Date getDayFromBegin(Date date, int delay) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DATE, delay);
        return c.getTime();
    }

    /**
     * 指定日期，往前或往后推多少天，取那天23时59分59秒
     *
     * @param date
     * @param delay
     * @return
     */
    public static Date getDayToEnd(Date date, int delay) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.add(Calendar.DATE, delay);
        return c.getTime();
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        sdf.applyPattern(pattern);
        return sdf.format(date);
    }

    public static String formatDate(Date date) {
        return formatDate(date, null);
    }


}
