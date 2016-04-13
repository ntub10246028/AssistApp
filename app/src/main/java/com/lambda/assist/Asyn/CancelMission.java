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
 * Created by asus on 2016/4/14.
 */
public class CancelMission extends AsyncTask<String, String, Integer> {

    public interface OnCancelMissionListener {
        void finish(Integer result);
    }

    private final OnCancelMissionListener mListener;
    private String missionid;

    public CancelMission(OnCancelMissionListener mListener) {
        this.mListener = mListener;
    }


    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = TaskCode.NoResponse;
        missionid = datas[0];

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("missionID", missionid));
        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_giveup_mission, MyHttpClient.getMyHttpClient());
            if (jobj == null)
                return result;
            result = jobj.getInt("result");
        } catch (Exception e) {
            Log.d("CancelMission", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer);
    }
}
