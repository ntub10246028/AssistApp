package com.lambda.assist.Other;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hao_jun on 2016/3/8.
 */
public class MyTime {
    public static String convertTime_History(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = dateFormat.parse(time);
            Date now = new Date();
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTime(d);
            c2.setTime(now);

            if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
                int hour = c1.get(Calendar.HOUR_OF_DAY);
                int min = c1.get(Calendar.MINUTE);
                if (between(hour, 0, 5)) {
                    return "凌晨" + (hour) + ":" + (min < 10 ? "0" + min : min);
                } else if (between(hour, 6, 11)) {
                    return "早上" + (hour) + ":" + (min < 10 ? "0" + min : min);
                } else if (between(hour, 12, 17)) {
                    return "下午" + (hour - 12) + ":" + (min < 10 ? "0" + min : min);
                } else if (between(hour, 18, 23)) {
                    return "晚上" + (hour - 12) + ":" + (min < 10 ? "0" + min : min);
                } else {
                    return (hour - 12) + ":" + (min < 10 ? "0" + min : min);
                }
            } else if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) - 1) {
                return "昨天";
            } else {
                int month = c1.get(Calendar.MONTH) + 1;
                int day = c1.get(Calendar.DAY_OF_MONTH);
                return month + "/" + day;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static boolean between(int num, int from, int to) {
        return from <= num && num <= to;
    }
}
