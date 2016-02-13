package com.lambda.app.assistapp.ConnectionApp;
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
    private String cookie;

    public JsonReaderPost() {
    }

    public JSONObject Reader(List<NameValuePair> params, String dir, MyHttpClient client) throws IOException, JSONException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {

        HttpPost httpPost = new HttpPost("https://app.lambda.tw/" + dir);
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response;
        String result = null;
        response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        result = EntityUtils.toString(entity);
        Log.d("JsonReaderPost", result);
        List<Cookie> cookies = client.getCookieStore().getCookies();
        String get_result;
        String Strcookie = null;
        if (!cookies.isEmpty()) {
            get_result = "get cookie ok...";
            for (int i = 0; i < cookies.size(); i++) {
                Cookie cookie = cookies.get(i);
                Log.d(cookie.getName(), cookie.getValue());
                this.setCookie(cookie.getValue());
                Strcookie = cookie.getName() + "=" + cookie.getValue() + ";domain=" +
                        cookie.getDomain();
            }
            get_result = Strcookie;
        } else {
            get_result = "get cookie error";
        }
        Log.d("cookie", get_result);
        return new JSONObject(result);

    }


    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}