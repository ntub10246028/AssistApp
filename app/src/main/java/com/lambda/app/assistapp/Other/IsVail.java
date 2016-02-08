package com.lambda.app.assistapp.Other;

import android.content.Context;
import android.widget.Toast;

import com.lambda.app.assistapp.R;


/**
 * Created by asus on 2016/1/26.
 */
public class IsVail {
    public static boolean isVail_New_Mission(Context ctxt, String title, String content) {
        if (title.isEmpty()) {
            Toast.makeText(ctxt, ctxt.getResources().getString(R.string.msg_err_new_mission_title), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (content.isEmpty()) {
            Toast.makeText(ctxt, ctxt.getResources().getString(R.string.msg_err_new_mission_content), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static String isVail_TimePick(Context ctxt, int h, int m, int s) {
        StringBuilder sb = new StringBuilder();
        if (h == 24) {
            return "24:00:00";
        }
        sb.append((h < 10 ? "0" : ""));
        sb.append(h);
        sb.append(":");
        sb.append((m < 10 ? "0" : ""));
        sb.append(m);
        sb.append(":");
        sb.append((s < 10 ? "0" : ""));
        sb.append(s);
        return sb.toString();
    }


    public static boolean isBetween(int value, int min, int max) {
        return min <= value && value <= max;
    }
}
