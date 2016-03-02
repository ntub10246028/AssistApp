package com.lambda.assist.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Item.MissionData;
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
public class LoadMissions extends AsyncTask<List<Integer>, Integer, Integer> {
    public interface OnLoadMissionsListener {
        void finish(Integer result, List<MissionData> list);
    }

    private final OnLoadMissionsListener mListener;
    private List<Integer> missions;
    private List<MissionData> list;

    public LoadMissions(OnLoadMissionsListener mListener) {
        this.mListener = mListener;
        list = new ArrayList<>();
    }


    @Override
    protected Integer doInBackground(List<Integer>... lists) {
        Integer result = TaskCode.NoResponse;
        missions = lists[0];
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Integer id : missions) {
            params.add(new BasicNameValuePair("missionID[]", Integer.toString(id)));
        }
        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_get_mission_data, MyHttpClient.getMyHttpClient());
            if (jobj == null)
                return result;
            Log.d("LoadMissions", jobj.toString());
            result = jobj.getInt("result");
            if (result == TaskCode.Success) {
                JSONArray jarray = jobj.getJSONArray("missiondata");
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject item = jarray.getJSONObject(i);
                    MissionData idata = new MissionData();
                    idata.setMissionid(item.getInt("missionid"));
                    idata.setPosttime(item.getString("posttime"));
                    idata.setOnlinelimittime(item.getString("onlinelimittime"));
                    idata.setRunlimittime(item.getString("runlimittime"));
                    idata.setLocationx(item.getDouble("locationx"));
                    idata.setLocationy(item.getDouble("locationy"));
                    idata.setLocationtypeid(item.getInt("locationtypeid"));
                    idata.setTitle(item.getString("title"));
                    idata.setContent(item.getString("content"));
                    idata.setImage(item.getString("image"));
                    idata.setGettime(item.getString("gettime"));
                    list.add(idata);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoadMissions", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer, list);
    }
}
