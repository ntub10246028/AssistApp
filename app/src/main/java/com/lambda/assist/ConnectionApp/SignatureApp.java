package com.lambda.assist.ConnectionApp;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by super on 2015/9/22.
 * this Class for Signature this app for server check.
 */
public class SignatureApp {
    private BigInteger N;
    private BigInteger d;
    private boolean success;
    private Context ctx;
    private int resultNo;
    private int pass;


    private static String url_sign = "sign";
    private static String url_auth = "auth";

    public SignatureApp(Context ctx, int resId) {
        this.setCtx(ctx);

        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader bufferedreader = new BufferedReader(inputreader);
        String line;
        try {
            boolean lineOne = true;
            while ((line = bufferedreader.readLine()) != null) {
                if (lineOne) {
                    this.setN(new BigInteger(line));
                    lineOne = !lineOne;
                } else {
                    this.setD(new BigInteger(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String postSignature(String imei, String phone, MyHttpClient client) {

        BigInteger m1 = new BigInteger(imei).modPow(this.d, this.N);
        BigInteger m2 = new BigInteger(phone).modPow(this.d, this.N);

        JsonReaderPost jp = new JsonReaderPost();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("imei", m1.toString()));
        params.add(new BasicNameValuePair("phone", m2.toString()));
        try {
            JSONObject jobj = jp.Reader(params, this.url_sign, client);
            if (jobj != null) {
                setResultNo(jobj.getInt("result"));
                if (getResultNo() == -8)
                    setPass(jobj.getInt("pass"));
                this.setSuccess(true);
            } else {
                setResultNo(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jp.getCookie();
    }

    public String postauthorize(String imei, String phone, int pass, MyHttpClient client) {
        Random rand = new Random();
        String strPass = Integer.toString(pass);
        BigInteger m1 = new BigInteger(imei).modPow(this.d, this.N);
        BigInteger m2 = new BigInteger(phone).modPow(this.d, this.N);
        BigInteger m3 = new BigInteger(strPass).modPow(this.d, this.N);

        JsonReaderPost jp = new JsonReaderPost();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("imei", m1.toString()));
        params.add(new BasicNameValuePair("phone", m2.toString()));
        params.add(new BasicNameValuePair("password", m3.toString()));
        try {
            JSONObject jobj = jp.Reader(params, this.url_auth, client);
            if (jobj != null) {
                setResultNo(jobj.getInt("result"));
                this.setSuccess(true);
            } else {
                setResultNo(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jp.getCookie();
    }


    public BigInteger getN() {
        return N;
    }

    public void setN(BigInteger n) {
        N = n;
    }

    public BigInteger getD() {
        return d;
    }

    public void setD(BigInteger d) {
        this.d = d;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public int getResultNo() {
        return resultNo;
    }

    public void setResultNo(int resultNo) {
        this.resultNo = resultNo;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }
}
