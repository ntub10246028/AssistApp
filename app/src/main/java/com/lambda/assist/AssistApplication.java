package com.lambda.assist;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by super on 6/4/16.
 */
public class AssistApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void restartApplication(){
        Context context =this.getApplicationContext();
        Intent mStartActivity = new Intent(context, com.lambda.assist.Activity.Act_AuthSign.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
}
