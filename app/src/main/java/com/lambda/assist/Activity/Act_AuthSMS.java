package com.lambda.assist.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lambda.assist.Asyn.AuthenticatePass;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Other.Hardware;
import com.lambda.assist.Other.MyDialog;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;
import com.lambda.assist.Receiver.Br_SMS;


public class Act_AuthSMS extends Activity implements Br_SMS.BRInteraction {
    // Obj
    private Context ctxt = Act_AuthSMS.this;

    // UI
    private EditText et_pwd;
    private Button bt_ok;
    // SMS
    private Br_SMS receiver_sms;
    private static final String MSG_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    //
    private String phoneFromAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authsms);
        InitialSomething();
        InitialUI();
        InitialAction();
        // 註冊Receiver
        receiver_sms = new Br_SMS();
        registerReceiver(receiver_sms, new IntentFilter(MSG_RECEIVED));
        receiver_sms.setBRInteractionListener(this);
    }

    private void AuthenticatePassTask(final String pass) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            AuthenticatePass task = new AuthenticatePass(MyHttpClient.getMyHttpClient(), new AuthenticatePass.OnAuthenticatePassListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case TaskCode.Success:
                            Intent it = new Intent();
                            it.putExtra("success", true);
                            it.putExtra("phone", phoneFromAuth);
                            setResult(RESULT_OK, it);
                            finishActivity();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute(getImei(), phoneFromAuth, pass);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private String getImei() {
        TelephonyManager tM = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return tM.getDeviceId();
    }

    private void InitialSomething() {
        phoneFromAuth = getIntent().getStringExtra("phone");
    }

    private void InitialUI() {
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        bt_ok = (Button) findViewById(R.id.bt_sms_ok);

    }

    private void InitialAction() {
        bt_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Hardware.closeKeyBoard(ctxt, view);

                String pass = et_pwd.getText().toString();
                if (!pass.isEmpty()) {
                    AuthenticatePassTask(pass);
                }


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

    private void finishActivity() {
        this.finish();
    }

    public void setText(String content) {
        if (content != null) {
            unregisterReceiver(receiver_sms);
            et_pwd.setText(content);
            Intent it = new Intent();
            it.putExtra("pwd", content);
            setResult(RESULT_OK, it);
            finish();
        }
    }
}
