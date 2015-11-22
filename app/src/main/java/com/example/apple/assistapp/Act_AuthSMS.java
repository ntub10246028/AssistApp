package com.example.apple.assistapp;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class Act_AuthSMS extends Activity implements Br_SMS.BRInteraction {
    // Obj
    private Context ctx = Act_AuthSMS.this;

    // UI
    private TextView tv_content;
    // SMS
    private Br_SMS receiver_sms;
    private static final String MSG_RECEIVED =
            "android.provider.Telephony.SMS_RECEIVED";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authsms);
        findView();
        // 註冊Receiver
        receiver_sms = new Br_SMS();
        registerReceiver(receiver_sms, new IntentFilter(MSG_RECEIVED));
        receiver_sms.setBRInteractionListener(this);
    }


    private void findView() {
        tv_content = (TextView) findViewById(R.id.tv_content);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver_sms);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setText(String content) {
        if (content != null) {
            tv_content.setText(content);
        }
    }
}
