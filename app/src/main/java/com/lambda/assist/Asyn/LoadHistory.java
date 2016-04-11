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
 * Created by asus on 2016/2/25.
 */
public class LoadHistory extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadHistoryListener {
        void finish(Integer result, List<Integer> list);
    }

    private final OnLoadHistoryListener mListener;
    private List<Integer> list;

    public LoadHistory(OnLoadHistoryListener mListener) {
        this.mListener = mListener;
        list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        int result = TaskCode.NoResponse;
        List<NameValuePair> params = new ArrayList<>();
        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_history, MyHttpClient.getMyHttpClient());
            if (jobj != null) {
                Log.d("LoadHistory", jobj.toString());
                result = jobj.getInt("result");
                if (result == TaskCode.Success) {
                    JSONArray array = jobj.getJSONArray("history");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject ajobj = array.getJSONObject(i);
                            list.add(ajobj.getInt("missionid"));
                        }
                    }else{
                        Log.d("LoadHistory", "Array null");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoadHistory", e.toString());
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer, list);
    }
}
