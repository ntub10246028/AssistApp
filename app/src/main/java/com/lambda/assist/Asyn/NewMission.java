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
 * Created by asus on 2016/3/2.
 */
public class NewMission extends AsyncTask<String, Integer, Integer> {
    public interface OnNewMissionListener {
        void finish(Integer result,Integer missionid);
    }

    private final OnNewMissionListener mListener;
    private String title, content, lon, lat, onlinetime, runtime;
    private int missionid;
    public NewMission(OnNewMissionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = TaskCode.NoResponse;
        title = datas[0];
        content = datas[1];
        lon = datas[2];
        lat = datas[3];
        onlinetime = datas[4];
        runtime = datas[5];
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("content", content));
        params.add(new BasicNameValuePair("locationID", "1"));
        params.add(new BasicNameValuePair("locationX", lon));
        params.add(new BasicNameValuePair("locationY", lat));
        params.add(new BasicNameValuePair("onlineLimitTime", onlinetime));
        params.add(new BasicNameValuePair("runLimitTime", runtime));
        params.add(new BasicNameValuePair("multi", "0"));
        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_New_Mission, MyHttpClient.getMyHttpClient());
            if (jobj == null)
                return result;
            Log.d("NewMission", jobj.toString());
            result = jobj.getInt("result");
            if (result == TaskCode.Success) {
                missionid = jobj.getInt("missionid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("NewMission", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer,missionid);
    }
}
