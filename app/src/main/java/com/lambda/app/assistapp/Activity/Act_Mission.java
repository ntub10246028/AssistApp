package com.lambda.app.assistapp.Activity;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lambda.app.assistapp.ConnectionApp.JsonReaderPost;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.Item.MissionData;
import com.lambda.app.assistapp.Other.Net;
import com.lambda.app.assistapp.Other.TaskCode;
import com.lambda.app.assistapp.Other.URLs;
import com.lambda.app.assistapp.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by v on 2016/2/8.
 */
public class Act_Mission extends AppCompatActivity {
    //
    private Context ctxt = Act_Mission.this;
    private MissionData iData;
    //
    private TextView tv_title, tv_content, tv_posttime, tv_onlinetime, tv_runtime;
    private Button bt_accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        InitialSomething();
        InitialUI();
        InitialAction();
        getExtrasAndLoadMission();
    }

    private void LoadingMission(int id) {
        if (Net.isNetWork(ctxt)) {
            new LoadingMission().execute(id);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class LoadingMission extends AsyncTask<Integer, Integer, Integer> {
        private int missionid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... datas) {
            int result = TaskCode.NoResponse;
            missionid = datas[0];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("missionID[]", Integer.toString(missionid)));
            Log.d("LoadingMission", "MissionID = " + missionid);
            try {
                JsonReaderPost jp = new JsonReaderPost();
                JSONObject jobj = jp.Reader(params, URLs.url_get_mission_data, MyHttpClient.getMyHttpClient());
                if (jobj == null)
                    return result;
                result = jobj.getInt("result");
                if (result == TaskCode.Success) {
                    JSONArray jarray = jobj.getJSONArray("missiondata");
                    JSONObject item = jarray.getJSONObject(0);
                    iData.setMissionid(item.getInt("missionid"));
                    iData.setPosttime(item.getString("posttime"));
                    iData.setOnlinelimittime(item.getString("onlinelimittime"));
                    iData.setRunlimittime(item.getString("runlimittime"));
                    iData.setLocationx(item.getDouble("locationx"));
                    iData.setLocationy(item.getDouble("locationy"));
                    iData.setLocationtypeid(item.getInt("locationtypeid"));
                    iData.setTitle(item.getString("title"));
                    iData.setContent(item.getString("content"));
                    iData.setImage(item.getString("image"));
                    iData.setGettime(item.getString("gettime"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("LoadingMission", e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case TaskCode.Success:
                    RefreshToUI();
                    break;
                case TaskCode.New_Mission_Fail:
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_new_mission_fail), Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.New_Mission_LackData:
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_new_mission_lackdata), Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.NoResponse:
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void RefreshToUI() {
        tv_title.setText(iData.getTitle());
        tv_content.setText(iData.getContent());
        tv_posttime.setText(iData.getPosttime());
        tv_onlinetime.setText(iData.getOnlinelimittime());
        tv_runtime.setText(iData.getRunlimittime());
    }

    private void MillSecondsToDate(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        
    }


    private void InitialSomething() {
        iData = new MissionData();
    }

    private void InitialUI() {
        tv_title = (TextView) findViewById(R.id.tv_mission_title);
        tv_content = (TextView) findViewById(R.id.tv_mission_content);
        tv_posttime = (TextView) findViewById(R.id.tv_mission_posttime);
        tv_onlinetime = (TextView) findViewById(R.id.tv_mission_online);
        tv_runtime = (TextView) findViewById(R.id.tv_mission_runtime);
        bt_accept = (Button) findViewById(R.id.bt_mission_accept);
    }

    private void InitialAction() {

    }

    private void getExtrasAndLoadMission() {
        int missionid = getIntent().getIntExtra("missionid", 0);
        LoadingMission(missionid);
    }
}
