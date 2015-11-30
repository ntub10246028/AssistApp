package com.example.apple.assistapp;

import android.content.Context;
import android.util.Log;

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
    private String errorNo;
    private static String url = "sign";

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

    public String postSignature(String imei, MyHttpClient client) {
        Random rand = new Random();

        BigInteger m2 = new BigInteger(imei).modPow(this.d, this.N);

        JsonReaderPost jp = new JsonReaderPost(this.ctx);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("imei", m2.toString()));
        params.add(new BasicNameValuePair("phone", m2.toString()));
        try {
            //String re = jsonData(jp.Reader(params, this.url, client).toString(), "status");
            String re = jsonData(jp.Reader(params, this.url, client).toString(), "error");
            //Log.d("!!!!!!!!!!!!!!1",re);
            //if(re.equals("\"T\""))
            if (re.length() < 2) {
                errorNo = re;
                this.setSuccess(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jp.getCookie();
    }

    private String jsonData(String origin, String key) {
        String subString;
//        try {
//            subString = origin.substring(origin.indexOf(key), origin.indexOf(","));
//        } catch (Exception e) {
//
//        } finally {
//            subString = origin.substring(origin.indexOf(key), origin.indexOf("}"));
//        }
        subString = origin.substring(origin.indexOf(key), origin.indexOf("}"));


        return subString.substring(subString.indexOf(":") + 1);
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(String errorNo) {
        this.errorNo = errorNo;
    }
}
