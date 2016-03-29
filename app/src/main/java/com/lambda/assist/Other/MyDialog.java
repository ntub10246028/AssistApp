package com.lambda.assist.Other;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by asus on 2016/3/2.
 */
public class MyDialog {
    public static ProgressDialog getProgressDialog(Context ctxt, String message) {
        ProgressDialog pDialog = new ProgressDialog(ctxt);
        pDialog.setMessage(message);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }
}
