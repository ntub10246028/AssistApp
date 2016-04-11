package com.lambda.assist.Other;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/4/11.
 */
public class ListUtil {
    public static <T> void append(List<T> a, List<T> b) {
        for (T t : b) {
            a.add(t);
        }
    }
}
