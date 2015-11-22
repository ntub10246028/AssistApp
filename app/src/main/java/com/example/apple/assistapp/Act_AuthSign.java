package com.example.apple.assistapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.apple.assistapp.other.Net;

import java.util.HashMap;


public class Act_AuthSign extends Activity {
    // Obj
    private Context ctx = Act_AuthSign.this;
    private Resources res;
    // Http
    private HttpConnection conn;
    private static final int SUCCESS = 1;
    private static final int FAIL = 0;
    // StartActivity
    private static final int SignAuth = 111;
    // SharedPreferences
    private SharedPreferences settings;
    private static final String DATA = "data";
    private static final String phoneField = "phone";
    // UI
    private LinearLayout ll_inputphone;
    private Spinner sp_countryNum;
    private EditText et_phone;
    private Button bt_commit;
    // other
    private String[] countryNum = null;
    private String mImei;
    private String mPhone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authsign);
        InitResources();
        findView();
        readData();

        if (mPhone.length() > 0) {
            CheckTask();
        } else {
            ll_inputphone.setVisibility(View.VISIBLE);
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
//            conn = new HttpConnection();
//            String url = "urlll";
//            HashMap<String, String> map = new HashMap<String, String>();
//            map.put("PHONE", phone);
//            map.put("IMEI", imei);
//            conn.performPost(url,map);

            //result = SUCCESS;
            result = FAIL;
            return result;
        }

        protected void onPostExecute(Integer result) {

            switch (result) {
                case SUCCESS: // ok
                    ToMainActivity();
                    break;
                case FAIL: // err : database no you
                    SMS_dialog();
                    break;
                default:
                    Toast.makeText(ctx, phone + "\n" + imei, Toast.LENGTH_SHORT).show();
                    ToMainActivity();
            }
        }
    }

    private void ToMainActivity() {
        finish();
        Intent it = new Intent(ctx, Act_Main.class);
        startActivity(it);
        finish();
    }

    private void ToAuthSMSActivity() {
        Intent it = new Intent(ctx, Act_AuthSMS.class);
        startActivity(it);
    }

    private void SMS_dialog() {
        new AlertDialog.Builder(ctx).setTitle(mPhone).setMessage("我們會將簡訊驗證碼傳到上面的號碼").setPositiveButton("確認", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ToAuthSMSActivity();
            }
        }).setNegativeButton("取消", null).show();
    }

    private boolean formatIsVaild() {
        String countryN = countryNum[sp_countryNum.getSelectedItemPosition()];
        String phone = et_phone.getText().toString();
        if (countryN.length() * phone.length() == 0) {
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

    private void InitResources() {
        res = getResources();
        countryNum = res.getStringArray(R.array.country_number);
    }

    private void findView() {
        ll_inputphone = (LinearLayout) findViewById(R.id.ll_inputphone);
        sp_countryNum = (Spinner) findViewById(R.id.sp_countrynum);
        et_phone = (EditText) findViewById(R.id.et_phone);
        bt_commit = (Button) findViewById(R.id.bt_commit);
        // button
        bt_commit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (formatIsVaild()) {
                    saveData();
                    readData();
                    ll_inputphone.setVisibility(View.INVISIBLE);
                    Toast.makeText(ctx, countryNum[sp_countryNum.getSelectedItemPosition()] + et_phone.getText().toString(), Toast.LENGTH_SHORT).show();
                    CheckTask();
                } else {
                    Toast.makeText(ctx, res.getString(R.string.msg_err_format), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, countryNum);
        sp_countryNum.setAdapter(adapter);
    }

    private void saveData() {
        settings = getSharedPreferences(DATA, 0);
        settings.edit().putString(phoneField, countryNum[sp_countryNum.getSelectedItemPosition()] + et_phone.getText().toString()).commit();
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
