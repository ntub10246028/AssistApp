package com.lambda.app.assistapp.Other;

import android.content.Context;
import android.widget.Toast;

import com.example.apple.assistapp.R;

/**
 * Created by asus on 2016/1/26.
 */
public class IsVail {
    public static boolean isVail_New_Mission(Context ctxt , String title , String content){
        if(title.isEmpty()){
            Toast.makeText(ctxt, ctxt.getResources().getString(R.string.msg_err_new_mission_title), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(content.isEmpty()){
            Toast.makeText(ctxt, ctxt.getResources().getString(R.string.msg_err_new_mission_content), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
