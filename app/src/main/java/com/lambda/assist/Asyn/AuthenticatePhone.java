package com.lambda.assist.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.ConnectionApp.SignatureApp;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * Created by asus on 2016/3/1.
 */
public class AuthenticatePhone extends AsyncTask<String, String, Integer> {
    public interface OnAuthenticatePhoneListener {
        void finish(Integer result);
    }

    private final MyHttpClient client;
    private final OnAuthenticatePhoneListener mListener;
    private String imei;
    private String phone;
    public AuthenticatePhone(MyHttpClient client, OnAuthenticatePhoneListener mListener) {
        this.client = client;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = TaskCode.NoResponse;
        imei = datas[0];
        phone = datas[1];
        SignatureApp sa = new SignatureApp(client.getContext(), R.raw.sign);

        String session = sa.postSignature(imei, phone, client);
        try {
            result = sa.getResultNo();
            HttpGet hg = new HttpGet("https://app.lambda.tw/session");
            hg.setHeader("lack.session", session);
            Log.d(session, hg.getFirstHeader("lack.session").toString());
            HttpResponse response = client.execute(hg);
            HttpEntity entity = response.getEntity();
            Log.d("HttpEntity Result !!!", EntityUtils.toString(entity));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer);

    }
}
