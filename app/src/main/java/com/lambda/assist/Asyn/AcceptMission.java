package com.lambda.assist.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/3/2.
 */
public class AcceptMission extends AsyncTask<String, String, Integer> {
    public interface OnAcceptMissionListener {
        void finish(Integer result);
    }

    private final OnAcceptMissionListener mListener;
    private String missionid;

    public AcceptMission(OnAcceptMissionListener mListener) {
        this.mListener = mListener;
    }

    protected Integer doInBackground(String... datas) {
        Integer result = TaskCode.NoResponse;
        missionid = datas[0];

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("missionID[]", missionid));
        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_accept_mission, MyHttpClient.getMyHttpClient());
            if (jobj == null)
                return result;
            result = jobj.getInt("result");
            if (result == TaskCode.Success) {

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("AcceptMission", e.toString());
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer);
    }
}
