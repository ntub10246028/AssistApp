package com.example.apple.assistapp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.apple.assistapp.Receiver.Br_SMS;
import com.example.apple.assistapp.R;

public class Act_AuthSMS extends Activity implements Br_SMS.BRInteraction {
    // Obj
    private Context ctx = Act_AuthSMS.this;

    // UI
    private EditText et_pwd;
    private Button bt_ok;
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
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        bt_ok = (Button) findViewById(R.id.bt_sms_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent it = new Intent();
                it.putExtra("pwd",et_pwd.getText().toString());
                setResult(RESULT_OK, it);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setText(String content) {
        if (content != null) {
            unregisterReceiver(receiver_sms);
            et_pwd.setText(content);
            Intent it = new Intent();
            it.putExtra("pwd",content);
            setResult(RESULT_OK, it);
            finish();
        }
    }
}
