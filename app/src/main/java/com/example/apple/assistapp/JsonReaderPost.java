package com.example.apple.assistapp;
/**
 * Created by super on 2015/9/18.
 */
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JsonReaderPost {
    private Context mContext;


    public JsonReaderPost(Context mContext) {
        this.mContext = mContext;
    }
    public void Reader(List<NameValuePair> params) throws IOException, JSONException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        String ints = "";
        //List<NameValuePair> params = new ArrayList<NameValuePair>();
        //params.add(new BasicNameValuePair("query","SELECT+AlertId+FROM+Orion.Alerts"));

        //HttpClient client = new DefaultHttpClient();
        HttpClient client =new MyHttpClient(mContext);

        HttpPost httpPost = new
                HttpPost("https://192.168.11.112/edit");
        //httpPost.addHeader("content-type", "application/json");
        //httpPost.addHeader("Authorization", "Basic YWRtaW46");
        httpPost.setEntity(new UrlEncodedFormEntity(params));


        HttpResponse response;
        String result = null;

        response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        //Log.d("response", EntityUtils.toString(entity));
        result=EntityUtils.toString(entity);


        // Converting the String result into JSONObject jsonObj and then into
        // JSONArray to get data
        JSONObject jsonObj = new JSONObject(result);
        String json = jsonObj.getString("data");
        Log.d("json",json);

    }


}