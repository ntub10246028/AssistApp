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

import com.example.user.assist.R;

public class Act_SignAuth extends Activity {
    // Obj
    private Context ctx = Act_SignAuth.this;

    // UI
    private Button bt_commit;
    private EditText et_countrynum, et_phone;
    private TextView tv_content;
    // SMS
    private static final String MSG_RECEIVED =
            "android.provider.Telephony.SMS_RECEIVED";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signauth);
        findView();
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

        public void onReceive(Context ctxt, Intent intent) {
            if(intent.getAction().equals(MSG_RECEIVED)){
                Bundle msg = intent.getExtras();
                Object[] messages = (Object[]) msg.get("pdus");
                SmsMessage sms = SmsMessage.createFromPdu((byte[])messages[0]);

                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append("From:"+sms.getDisplayOriginatingAddress()+"\n");
                strBuilder.append("text:" + sms.getMessageBody());
                tv_content.setText(strBuilder);
            }
        }
    };

    private boolean formatIsVaild() {
        String countryNum = et_countrynum.getText().toString();
        String phone = et_phone.getText().toString();
        if (countryNum.length() * phone.length() == 0) {
            return false; // 有個位置是空的 X
        }
        if (phone.length() == 10) {
            if (!phone.substring(0, 2).equals("09"))
                return false; // 如果是 10 位數開頭不是 09 X
        } else if (phone.length() == 9) {
            if (!phone.substring(0, 1).equals("9"))
                return false;// 如果是 9 位數開頭不是 9 X
        } else {
            return false;// 長度不是 9,10 位數 X
        }
        return true;
    }

    private String getImei() {
        TelephonyManager tM = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return tM.getDeviceId();
    }


    private void findView() {
        et_countrynum = (EditText) findViewById(R.id.et_countrynum);
        et_phone = (EditText) findViewById(R.id.et_phone);
        bt_commit = (Button) findViewById(R.id.bt_test);
        tv_content = (TextView) findViewById(R.id.tv_content);

        bt_commit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (formatIsVaild()) {
                    registerReceiver(mBroadcastReceiver, new IntentFilter(MSG_RECEIVED));
                } else {
                    Toast.makeText(ctx, "格式錯誤", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
