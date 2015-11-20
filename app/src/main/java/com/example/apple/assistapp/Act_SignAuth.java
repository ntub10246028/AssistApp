package com.example.apple.assistapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Act_SignAuth extends Activity {
    // Obj
    private Context ctx = Act_SignAuth.this;

    // UI
    private TextView tv_content;
    // SMS
    private static final String MSG_RECEIVED =
            "android.provider.Telephony.SMS_RECEIVED";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signauth);
        findView();
        // 註冊Receiver
        registerReceiver(mBroadcastReceiver, new IntentFilter(MSG_RECEIVED));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {
            if (intent.getAction().equals(MSG_RECEIVED)) {
                Bundle msg = intent.getExtras();
                Object[] messages = (Object[]) msg.get("pdus");
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) messages[0]);

                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append("From:" + sms.getDisplayOriginatingAddress() + "\n");
                strBuilder.append("text:" + sms.getMessageBody());
                tv_content.setText(strBuilder);
            }
        }
    };


    private void findView() {

        tv_content = (TextView) findViewById(R.id.tv_content);


    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
