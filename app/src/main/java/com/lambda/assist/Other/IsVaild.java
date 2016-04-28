package com.lambda.assist.Other;

import android.content.Context;
import android.widget.Toast;

import com.lambda.assist.R;


/**
 * Created by asus on 2016/1/26.
 */
public class IsVaild {
    public static boolean isVail_New_Mission(Context ctxt, String title, String content, String lon, String lat, String onlinetime, String runtime) {
        if (title.isEmpty()) {
            Toast.makeText(ctxt, ctxt.getResources().getString(R.string.msg_err_new_mission_title), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (content.isEmpty()) {
            Toast.makeText(ctxt, ctxt.getResources().getString(R.string.msg_err_new_mission_content), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(lon.equals("0.0") || lat.equals("0.0")){
            Toast.makeText(ctxt, ctxt.getResources().getString(R.string.msg_err_new_mission_gps), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static String isVail_TimePick(Context ctxt, int h, int m, int s) {
        if (h == 0 && m == 0 && s == 0) {
            Toast.makeText(ctxt, "至少1秒", Toast.LENGTH_SHORT).show();
            return "00:00:01";
        }
        if (h == 24) {
            Toast.makeText(ctxt, "最多1天", Toast.LENGTH_SHORT).show();
            return "24:00:00";
        }

        StringBuilder sb = new StringBuilder();
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

    public static boolean phoneformat(String countryNum, String phone) {
        if (countryNum.isEmpty() || phone.isEmpty()) {
            return false; // 有個位置是空的 X
        }
        if (phone.length() == 10) {
            if (!phone.substring(0, 2).equals("09")) {
                return false; // 如果是 10 位數開頭不是 09 X
            }
        } else {
            return false;// 長度不是 9,10 位數 X
        }
        return true;
    }

    public static boolean isVaild_Message(String message) {
        if (message.isEmpty()) {
            return false;
        }
        return true;
    }


    public static boolean isBetween(int value, int min, int max) {
        return min <= value && value <= max;
    }
}
