package com.example.apple.assistapp;

import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
    private static String url="sign";

    public SignatureApp(Context ctx, int resId)
    {
        this.setCtx(ctx);

        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader bufferedreader = new BufferedReader(inputreader);
        String line;
        try
        {
            boolean lineOne = false;
            while (( line = bufferedreader.readLine()) != null)
            {
                if(lineOne){
                    this.setN(new BigInteger(line));
                    lineOne=!lineOne;
                }else{
                    this.setD(new BigInteger(line));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void postSignature(){
        Random rand= new Random();
        String randomNum = String.valueOf(rand.nextInt((10 - 0) + 1) + 0);
        Log.d("postNum",randomNum);

        BigInteger m2 = new BigInteger(randomNum).modPow(this.d, this.N);
        Log.d("postNum",m2.toString());

        JsonReaderPost jp= new JsonReaderPost(this.ctx);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sign",randomNum));
        try {
            jp.Reader(params,this.url).getJSONObject("sign");
        }catch (Exception e){
            e.printStackTrace();
        }
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
}
