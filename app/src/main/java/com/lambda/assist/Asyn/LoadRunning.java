package com.lambda.assist.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/3/8.
 */
public class LoadRunning extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadRunningListener {
        void finish(Integer result, List<Integer> ids);
    }

    private final OnLoadRunningListener mListener;
    private List<Integer> list;

    public LoadRunning(OnLoadRunningListener mListener) {
        this.mListener = mListener;
        list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... datas) {
        int result = TaskCode.NoResponse;
        List<NameValuePair> params = new ArrayList<>();
        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_running, MyHttpClient.getMyHttpClient());
            if (jobj != null) {
                Log.d("LoadRunning" , jobj.toString());
                result = jobj.getInt("result");
                if (result == TaskCode.Success) {
                    JSONArray array = jobj.getJSONArray("runing");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject ajobj = array.getJSONObject(i);
                            list.add(ajobj.getInt("missionid"));
                        }
                    } else {
                        Log.d("LoadRunning", "Array null");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoadRunning", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer, list);
    }
}
