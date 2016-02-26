package com.lambda.assist.Activity;

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

import com.lambda.assist.Asyn.LoadingMission;
import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Item.MissionData;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;
import com.lambda.assist.Picture.TextImageTransformer;
import com.lambda.assist.R;

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
    private TextImageTransformer titrans;
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
            LoadingMission task = new LoadingMission(new LoadingMission.OnLoadingMissionIDListener() {
                public void finish(Integer result, List<MissionData> list) {
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
            });
            List<Integer> list = new ArrayList<>();
            list.add(id);
            task.execute(list);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void RefreshToUI() {
        tv_title.setText(iData.getTitle());
        tv_content.setText(iData.getContent());
        titrans.ConvertImage(ctxt, tv_content, iData.getMissionid(), iData.getImage());
        tv_posttime.setText(iData.getPosttime());
        tv_onlinetime.setText(iData.getOnlinelimittime());
        tv_runtime.setText(iData.getRunlimittime());
    }

    private void InitialSomething() {
        iData = new MissionData();
        titrans = new TextImageTransformer(ctxt);
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