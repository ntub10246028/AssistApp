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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JsonReaderPost {
    private Context mContext;
    private String cookie;

    public JsonReaderPost(Context mContext) {
        this.mContext = mContext;
    }

    public JSONObject Reader(List<NameValuePair> params,String dir,MyHttpClient client) throws IOException, JSONException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        //String ints = "";
        //List<NameValuePair> params = new ArrayList<NameValuePair>();
        //params.add(new BasicNameValuePair("query","SELECT+AlertId+FROM+Orion.Alerts"));

        //HttpClient client = new DefaultHttpClient();
        //DefaultHttpClient client =new MyHttpClient(mContext);

        HttpPost httpPost = new
                HttpPost("https://app.lambda.tw/"+dir);
        //httpPost.addHeader("content-type", "application/json");
        //httpPost.addHeader("Authorization", "Basic YWRtaW46");
        httpPost.setEntity(new UrlEncodedFormEntity(params));


        HttpResponse response;
        String result = null;

        response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        //Log.d("response", EntityUtils.toString(entity));
        result=EntityUtils.toString(entity);
        Log.d("DEBUG",result);
        response.getEntity().consumeContent();


        //String Strresult = EntityUtils.toString(entity, HTTP.UTF_8);
        List<Cookie> cookies = client.getCookieStore().getCookies();
        String get_result;
        String Strcookie=null;
        if (!cookies.isEmpty())
        {
            get_result="get cookie ok...";
            for (int i = 0; i < cookies.size(); i++)
            {
                Cookie cookie = cookies.get(i);
                Log.d(cookie.getName(),cookie.getValue());
                this.setCookie(cookie.getValue());
                Strcookie = cookie.getName() + "=" + cookie.getValue() + ";domain=" +
                        cookie.getDomain();
            }
            get_result=Strcookie;
        }
        else
        {
            get_result="get cookie error";
        }
        Log.d("cookie",get_result);

        // Converting the String result into JSONObject jsonObj and then into
        // JSONArray to get data

        //JSONObject jsonObj = new JSONObject(result);
        //return jsonObj;
        return new JSONObject(result);

    }


    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}