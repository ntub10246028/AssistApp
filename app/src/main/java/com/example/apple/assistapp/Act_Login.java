package com.example.apple.assistapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.user.assist.R;


public class Act_Login extends Activity {
    // Obj
    private Context ctx = Act_Login.this;
    // StartActivity
    private static final int SignAuth = 111;
    // SharedPreferences
    private SharedPreferences settings;
    private static final String DATA = "data";
    private static final String phoneField = "phone";
    // other
    private String imei;
    private String phone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        readData();
        if (phone.length() > 0) {

        } else {
            Intent it = new Intent(ctx, Act_SignAuth.class);
            startActivityForResult(it, SignAuth);
            Toast.makeText(ctx, "Please input your phone number to sign up ", Toast.LENGTH_SHORT).show();
        }
    }

    private void findView() {
    }

    private void saveData() {
        settings = getSharedPreferences(DATA, 0);
        settings.edit().putString(phoneField, "").commit();
    }

    private void readData() {
        settings = getSharedPreferences(DATA, 0);
        phone = settings.getString(phoneField, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SignAuth:

                    break;
            }
        } else {

        }
    }
}
