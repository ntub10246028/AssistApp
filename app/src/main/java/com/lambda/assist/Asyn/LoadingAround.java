package com.lambda.assist.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Model.ReadyAroundMission;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/3/8.
 */
public class LoadingAround extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadingAroundMissionIDListener {
        void finish(Integer result, List<ReadyAroundMission> readyMissionss);
    }

    private final OnLoadingAroundMissionIDListener mListener;
    private List<ReadyAroundMission> list_readymissions;

    public LoadingAround(OnLoadingAroundMissionIDListener mListener) {
        this.mListener = mListener;
        list_readymissions = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = TaskCode.NoResponse;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("lon", datas[0]));
        params.add(new BasicNameValuePair("lat", datas[1]));
        try {
            Log.d("LoadingAroundMission", "Start");
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_around_Mission, MyHttpClient.getMyHttpClient());
            if (jobj == null)
                return result;
            Log.d("LoadingAroundMission", jobj.toString());
            result = jobj.getInt("result");
            if (result == TaskCode.Success) {
                JSONArray jarray = jobj.getJSONArray("around");
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject item = jarray.getJSONObject(i);
                    ReadyAroundMission aitem = new ReadyAroundMission();
                    aitem.setMissionid(item.getInt("missionid"));
                    aitem.setLocationx(item.getDouble("locationx"));
                    aitem.setLocationy(item.getDouble("locationy"));
                    list_readymissions.add(aitem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoadingAroundMission", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer, list_readymissions);
    }
}
