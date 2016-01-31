package com.lambda.app.assistapp.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.lambda.app.assistapp.Other.ActivityCode;
import com.lambda.app.assistapp.Other.Net;
import com.lambda.app.assistapp.Other.TaskCode;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.ConnectionApp.SignatureApp;
import com.lambda.app.assistapp.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;


public class Act_AuthSign extends Activity {
    // Obj
    private Context ctxt = Act_AuthSign.this;
    private MyHttpClient client;
    private Resources res;
    // StartActivity
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
    private int mPassNo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authsign);
        InitialSomething();
        InitialUI();
        InitialAction();
        readData();
        CheckUserExsitTask();
    }

    private void CheckUserExsitTask() {
        if (!mPhone.isEmpty()) {
            if (Net.isNetWork(ctxt)) {
                new CheckUserExsitTask().execute();
            } else {
                Toast.makeText(ctxt, res.getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
            }
        } else {
            ll_inputphone.setVisibility(View.VISIBLE);
        }
    }

    class CheckUserExsitTask extends AsyncTask<String, String, Integer> {
        private String phone;
        private String imei;
        private String sResult = "";
        // UI
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Integer doInBackground(String... datas) {
            Integer result = TaskCode.NoResponse;
            phone = mPhone;
            imei = getImei();
            if (client == null) {
                client = MyHttpClient.getMyHttpClient();
                client.setContext(ctxt);
            }

            SignatureApp sa = new SignatureApp(ctxt, R.raw.sign);
            String session = null;

            while (!sa.isSuccess()) {
                session = sa.postSignature(imei, phone, client);
            }
            try {
                result = sa.getResultNo();
                mPassNo = sa.getPass();
                HttpGet hg = new HttpGet("https://app.lambda.tw/session");
                hg.setHeader("lack.session", session);
                Log.d(session, hg.getFirstHeader("lack.session").toString());
                HttpResponse response = client.execute(hg);
                HttpEntity entity = response.getEntity();
                Log.d("HttpEntity Result !!!", EntityUtils.toString(entity));
            } catch (Exception e) {
                e.printStackTrace();
                sResult = e.toString();
            }

            return result;
        }

        protected void onPostExecute(Integer result) {
            pDialog.dismiss();
            switch (result) {
                case TaskCode.Success: // ok
                    Intent it = new Intent(ctxt, Act_Main.class);
                    startActivity(it);
                    finish();
                    break;
                case TaskCode.Empty: // err : database no you
                    SMS_dialog();
                    Toast.makeText(ctxt, mPassNo + "", Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.NoResponse:
                    Toast.makeText(ctxt, "NoResponse", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void AuthUserPWDTask() {
        if (Net.isNetWork(ctxt)) {
            new AuthUserPWDTask().execute();
        } else {
            Toast.makeText(ctxt, res.getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class AuthUserPWDTask extends AsyncTask<String, String, Integer> {
        private String phone;
        private String imei;
        private int passNo;
        private String sResult = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            phone = mPhone;
            imei = getImei();
            passNo = mPassNo;
        }

        protected Integer doInBackground(String... datas) {
            Integer result = TaskCode.NoResponse;

            SignatureApp sa = new SignatureApp(ctxt, R.raw.sign);
            String session = null;

            while (!sa.isSuccess()) {
                session = sa.postauthorize(imei, phone, passNo, client);
            }
            try {
                result = sa.getResultNo();
                HttpGet hg = new HttpGet("https://app.lambda.tw/session");
                hg.setHeader("lack.session", session);
                Log.d(session, hg.getFirstHeader("lack.session").toString());
                HttpResponse response = client.execute(hg);
                HttpEntity entity = response.getEntity();
                Log.d("xxxxxxxxxxxxxx", EntityUtils.toString(entity));
            } catch (Exception e) {
                e.printStackTrace();
                sResult = e.toString();
            }
            return result;
        }

        protected void onPostExecute(Integer result) {
            switch (result) {
                case TaskCode.Success: // ok
                    Intent it = new Intent(ctxt, Act_Main.class);
                    startActivity(it);
                    finish();
                    break;
                case TaskCode.Empty: // err : database no you
                    SMS_dialog();
                    break;
                case TaskCode.NoResponse:
                    Toast.makeText(ctxt, res.getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        }
    }

    private void SMS_dialog() {
        new AlertDialog.Builder(ctxt).setTitle(mPhone).setMessage(res.getString(R.string.sms_text)).setPositiveButton("確認", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(ctxt, Act_AuthSMS.class);
                startActivityForResult(it, ActivityCode.AuthSMS);
            }
        }).setNegativeButton("取消", null).show();
    }

    private boolean formatIsVaild() {
        String countryN = countryNum[sp_countryNum.getSelectedItemPosition()];
        String phone = et_phone.getText().toString();
        if (countryN.isEmpty() || phone.isEmpty()) {
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

    private void InitialSomething() {
        res = getResources();
        countryNum = res.getStringArray(R.array.country_number);
        client = null;
    }

    private void InitialUI() {
        ll_inputphone = (LinearLayout) findViewById(R.id.ll_inputphone);
        sp_countryNum = (Spinner) findViewById(R.id.sp_countrynum);
        et_phone = (EditText) findViewById(R.id.et_phone);
        bt_commit = (Button) findViewById(R.id.bt_commit);
    }

    private void InitialAction() {
        // button
        bt_commit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (formatIsVaild()) {
                    saveData();
                    readData();
                    //ll_inputphone.setVisibility(View.INVISIBLE);
                    //Toast.makeText(ctxt, countryNum[sp_countryNum.getSelectedItemPosition()] + et_phone.getText().toString(), Toast.LENGTH_SHORT).show();
                    CheckUserExsitTask();
                } else {
                    Toast.makeText(ctxt, res.getString(R.string.msg_err_format), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctxt, android.R.layout.simple_spinner_dropdown_item, countryNum);
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
        Toast.makeText(ctxt, "clear", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // ok
            if (requestCode == ActivityCode.AuthSMS) {

                String pwd = data.getStringExtra("pwd");
                int ipwd = -1;
                try {
                    ipwd = Integer.valueOf(pwd);
                } catch (NumberFormatException ex) {

                }
                if (ipwd == -1 || ipwd != mPassNo) {
                    Toast.makeText(ctxt, "驗證碼錯誤", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctxt, mPassNo + "", Toast.LENGTH_SHORT).show();
                    AuthUserPWDTask();
                }
            }
        } else { // cancel
            if (requestCode == ActivityCode.AuthSMS) {

            }
        }
    }
}
