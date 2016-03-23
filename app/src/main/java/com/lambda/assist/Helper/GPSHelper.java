package com.lambda.assist.Helper;

import java.math.BigDecimal;

/**
 * Created by asus on 2016/3/6.
 */
public class GPSHelper {
    private static final double EARTH_RADIUS = 6378137.0;

    public static String gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;

        if (s < 0)
            return "";
        if (s < 1000)
            return s + "公尺";
        s = s / 1000;
        String str_distance = Double.toString(s);
        double distance = new BigDecimal(str_distance)
                .setScale(1, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        return distance + "公里";
    }
}
