package com.lambda.assist.Activity;

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

import com.lambda.assist.Asyn.AuthenticatePass;
import com.lambda.assist.Asyn.AuthenticatePhone;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.ConnectionApp.SignatureApp;
import com.lambda.assist.Other.ActivityCode;
import com.lambda.assist.Other.Hardware;
import com.lambda.assist.Other.IsVaild;
import com.lambda.assist.Other.MyDialog;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;


public class Act_AuthSign extends Activity {
    // Obj
    private Context ctxt = Act_AuthSign.this;
    private MyHttpClient client;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authsign);
        InitialSomething();
        InitialUI();
        InitialAction();
        readData();
    }

    private void AuthenticatePhoneTask(final String phone) {
        if (!phone.isEmpty()) {
            if (Net.isNetWork(ctxt)) {
                final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
                MyHttpClient.getMyHttpClient().setContext(ctxt);
                AuthenticatePhone task = new AuthenticatePhone(MyHttpClient.getMyHttpClient(), new AuthenticatePhone.OnAuthenticatePhoneListener() {
                    public void finish(Integer result) {
                        pd.dismiss();
                        switch (result) {
                            case TaskCode.Success: // ok
                                saveData(phone);
                                Intent it = new Intent(ctxt, Act_Main.class);
                                startActivity(it);
                                finishActivity();
                                break;
                            case TaskCode.ThisUserNoExist: // err : database no you
                                Intent SMSit = new Intent(ctxt, Act_AuthSMS.class);
                                SMSit.putExtra("phone", phone);
                                startActivityForResult(SMSit, ActivityCode.Sms);
                                break;
                            case TaskCode.NoResponse:
                                Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                task.execute(getImei(), phone);
            } else {
                Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
            }
        } else {
            ll_inputphone.setVisibility(View.VISIBLE);
        }
    }

    private String getImei() {
        TelephonyManager tM = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return tM.getDeviceId();
    }

    private void InitialSomething() {
        countryNum = getResources().getStringArray(R.array.country_number);
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
                Hardware.closeKeyBoard(ctxt, v);
                String countryN = countryNum[sp_countryNum.getSelectedItemPosition()];
                String phone = et_phone.getText().toString();
                if (IsVaild.phoneformat(countryN, phone)) {
                    String countryphone = countryN + phone.substring(1);
                    AuthenticatePhoneTask(countryphone);
                } else {
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_format), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctxt, android.R.layout.simple_spinner_dropdown_item, countryNum);
        sp_countryNum.setAdapter(adapter);
    }

    private void saveData(String phone) {
        settings = getSharedPreferences(DATA, 0);
        settings.edit().putString(phoneField, phone).commit();
    }

    private void readData() {
        settings = getSharedPreferences(DATA, 0);
        String savedphone = settings.getString(phoneField, "");
        AuthenticatePhoneTask(savedphone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ActivityCode.Sms:
                    boolean isSuccess = data.getBooleanExtra("success", false);
                    String phone = data.getStringExtra("phone");
                    if (isSuccess) {
                        AuthenticatePhoneTask(phone);
                    }
                    break;
            }
        }
    }

    private void finishActivity() {
        this.finish();
    }
}
