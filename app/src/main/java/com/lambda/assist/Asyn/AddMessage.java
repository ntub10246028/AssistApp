package com.lambda.assist.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/3/9.
 */
public class AddMessage extends AsyncTask<String, Integer, Integer> {
    public interface OnAddMessageListener {
        void finish(Integer result);
    }

    private final OnAddMessageListener mListener;
    private String missionid, message;

    public AddMessage(OnAddMessageListener mListener) {
        this.mListener = mListener;
    }

    protected Integer doInBackground(String... datas) {
        Integer result = TaskCode.NoResponse;
        missionid = datas[0];
        message = datas[1];
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("missionID", missionid));
        params.add(new BasicNameValuePair("message", message));
        Log.d("AddMessage:Post", missionid + " " + message);
        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_sendmessage, MyHttpClient.getMyHttpClient());
            if (jobj == null)
                return result;
            result = jobj.getInt("result");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("AddMessage", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer);
    }

}
