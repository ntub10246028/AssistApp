package com.example.apple.assistapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.apple.assistapp.other.Net;


public class Act_Login extends Activity {
    // Obj
    private Context ctx = Act_Login.this;
    private Resources res;
    // StartActivity
    private static final int SignAuth = 111;
    // SharedPreferences
    private SharedPreferences settings;
    private static final String DATA = "data";
    private static final String phoneField = "phone";
    // UI
    private LinearLayout ll_inputphone;
    private Button bt_commit;
    private EditText et_countrynum, et_phone;
    // other
    private String mImei;
    private String mPhone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        res = getResources();
        findView();
        readData();

        if (mPhone.length() > 0) {
            CheckTask();
        } else {
            ll_inputphone.setVisibility(View.VISIBLE);
            Toast.makeText(ctx, "Please input your phone number to sign up ", Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckTask() {
        if (Net.isNetWork(ctx)) {
            new CheckTask().execute();
        } else {
            Toast.makeText(ctx, res.getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class CheckTask extends AsyncTask<String, String, Integer> {
        private String phone;
        private String imei;

        protected Integer doInBackground(String... datas) {
            Integer result = -1;
            phone = mPhone;
            imei = getImei();

            return result;
        }

        protected void onPostExecute(Integer result) {
            if (result > 0) {

            } else {
                Toast.makeText(ctx, phone + "\n" + imei, Toast.LENGTH_SHORT).show();
                //Toast.makeText(ctx, Integer.toString(result), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean formatIsVaild() {
        String countryNum = et_countrynum.getText().toString();
        String phone = et_phone.getText().toString();
        if (countryNum.length() * phone.length() == 0) {
            return false; // 有個位置是空的 X
        }
        if (phone.length() == 10) {
            if (!phone.substring(0, 2).equals("09")) {
                return false; // 如果是 10 位數開頭不是 09 X
            } else {
                et_phone.setText(phone.substring(1));
            }
        } else if (phone.length() == 9) {
            if (!phone.substring(0, 1).equals("9")) {
                return false;// 如果是 9 位數開頭不是 9 X
            } else {
                et_phone.setText(phone);
            }
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
        ll_inputphone = (LinearLayout) findViewById(R.id.ll_inputphone);
        et_countrynum = (EditText) findViewById(R.id.et_countrynum);
        et_phone = (EditText) findViewById(R.id.et_phone);
        bt_commit = (Button) findViewById(R.id.bt_commit);
        bt_commit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (formatIsVaild()) {
                    saveData();
                    readData();
                    ll_inputphone.setVisibility(View.INVISIBLE);
                    CheckTask();
                } else {
                    Toast.makeText(ctx, res.getString(R.string.msg_err_format), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveData() {
        settings = getSharedPreferences(DATA, 0);
        settings.edit().putString(phoneField, et_countrynum.getText().toString() + et_phone.getText().toString()).commit();
    }

    private void readData() {
        settings = getSharedPreferences(DATA, 0);
        mPhone = settings.getString(phoneField, "");
    }

    private void clearData() {
        settings = getSharedPreferences(DATA, 0);
        settings.edit().clear().commit();
        Toast.makeText(ctx, "clear", Toast.LENGTH_SHORT).show();
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
