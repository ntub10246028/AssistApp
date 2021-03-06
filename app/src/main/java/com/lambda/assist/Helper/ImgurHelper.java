package com.lambda.assist.Helper;

/**
 * Created by asus on 2016/3/23.
 */
public class ImgurHelper {

    public static final String start = "[assist:image=";
    public static final String end = "]";
    public static final String imgurHttp = "http";

    public static String checkUrl(String content) {
        int sIndex = content.indexOf(start);
        int eIndex = content.indexOf(end, sIndex + start.length() + 1);
        if (sIndex == -1 || eIndex == -1)
            return null;

        return content.substring(sIndex + start.length(), eIndex);
    }
}
