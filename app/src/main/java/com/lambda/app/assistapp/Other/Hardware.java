package com.lambda.app.assistapp.Other;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

/**
 * Created by v on 2016/2/6.
 */
public class Hardware {
    public static void closeKeyBoard(Context ctxt, View v) {
        InputMethodManager imm = (InputMethodManager) ctxt
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public static boolean isIntentAvailable(Context ctxt,Intent intent) {
        PackageManager manager = ctxt.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
