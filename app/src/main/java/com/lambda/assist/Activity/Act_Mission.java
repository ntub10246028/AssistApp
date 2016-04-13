package com.lambda.assist.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lambda.assist.Adapter.MissionFragmentAdapter;
import com.lambda.assist.Asyn.AcceptMission;
import com.lambda.assist.Asyn.CancelMission;
import com.lambda.assist.Asyn.LoadMissions;
import com.lambda.assist.Asyn.LoadingMessage;
import com.lambda.assist.Fragment.ChatFragment;
import com.lambda.assist.Fragment.ContentFragment;
import com.lambda.assist.Fragment.LimitFragment;
import com.lambda.assist.Fragment.MessageFragment;
import com.lambda.assist.Fragment.MissionBaseFragment;
import com.lambda.assist.Model.MessageItem;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Other.Code;
import com.lambda.assist.Other.MyDialog;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;
import com.lambda.assist.UI.MissionSlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v on 2016/2/8.
 */
public class Act_Mission extends AppCompatActivity {
    //
    private Context ctxt = Act_Mission.this;
    // UI
    private TextView tv_title;
    private ViewPager mViewPager;
    private MissionSlidingTabLayout mSlidingTabLayout;
    private Button bt_gps, bt_accept;
    // Adapter
    private MissionFragmentAdapter fragmentAdapter;
    // get Extras
    private String fromType;
    private int me;
    private int missionid;
    private String title;
    //
    private List<String> list_Titles;
    private Mission mMission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        getExtras();
        InitialSomething();
        InitialUI();
        InitialAction();
        LoadMission();

        InitialToolBar();

        //InitialTabView();
    }

    private void LoadMission() {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadMissions task = new LoadMissions(new LoadMissions.OnLoadMissionsListener() {
                public void finish(Integer result, List<Mission> list) {
                    pd.dismiss();
                    switch (result) {
                        case TaskCode.Empty:
                            Toast.makeText(ctxt, "空", Toast.LENGTH_SHORT).show();
                            break;
                        case TaskCode.Success:
                            if (list != null && !list.isEmpty()) {
                                mMission = list.get(0);
                                if (mMission != null) {
                                    LoadMessage(mMission.getMissionid() + "");
                                }
                            }
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            List<Integer> id = new ArrayList<>();
            id.add(missionid);
            task.execute(id);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadMessage(String missionid) {
        if (Net.isNetWork(ctxt)) {
            LoadingMessage task = new LoadingMessage(new LoadingMessage.OnLoadingMessageListener() {
                public void finish(Integer result, List<MessageItem> list) {
                    switch (result) {
                        case TaskCode.Success:
                        case TaskCode.Empty:
                            mMission.setMessages(list);
                            InitialTabView();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            });
            task.execute(missionid);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void AcceptMission(final String missionid) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            AcceptMission task = new AcceptMission(new AcceptMission.OnAcceptMissionListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case TaskCode.Success:
                            Toast.makeText(ctxt, "接受成功", Toast.LENGTH_SHORT).show();
                            break;
                        case TaskCode.Accept_fail:
                            Toast.makeText(ctxt, "接取失敗(" + result + ")", Toast.LENGTH_SHORT).show();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
            task.execute(missionid);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void CancelMission(final String missionid) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            CancelMission task = new CancelMission(new CancelMission.OnCancelMissionListener() {
                public void finish(Integer result) {
                    Log.d("LAG", "B");
                    pd.dismiss();
                    Log.d("CancelMission",result+"**");
                    switch (result) {
                        case TaskCode.Success:
                            Toast.makeText(ctxt, "取消/放棄成功", Toast.LENGTH_SHORT).show();
                            break;
                        case TaskCode.GiveUp_fail:
                            Toast.makeText(ctxt, "取消/放棄失敗(" + result + ")", Toast.LENGTH_SHORT).show();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            });
            task.execute(missionid);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void InitialToolBar() {
        Toolbar tb_top = (Toolbar) findViewById(R.id.toolbar_mission_top);
//        setSupportActionBar(tb_top);
        View v = getLayoutInflater().inflate(R.layout.toolbar_mission, null);
        tv_title = (TextView) v.findViewById(R.id.tv_toolbar_mission_title);
        tv_title.setText(title);
        tb_top.addView(v);
    }

    private void InitialTabView() {
        // adapter
        final List<MissionBaseFragment> fragments = getFragments();
        fragmentAdapter = new MissionFragmentAdapter(getSupportFragmentManager(), fragments);
        // pager
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setOffscreenPageLimit(fromProcessing() ? 4 : 3);
        // tabs
        mSlidingTabLayout.setCustomTabColorizer(new MissionSlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return fragments.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return fragments.get(position).getDividerColor();
            }
        });
        mSlidingTabLayout.setCustomTabView(R.layout.tabview_mission, R.id.tv_tabview_title);
        mSlidingTabLayout.setBackgroundResource(R.color.gray800);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private List<MissionBaseFragment> getFragments() {
        int indicatorColor = Color.TRANSPARENT;
        int dividerColor = Color.TRANSPARENT;

        List<MissionBaseFragment> list = new ArrayList<>();
        list.add(ContentFragment.newInstance(getResources().getString(R.string.tab_mission_content), indicatorColor, dividerColor));
        list.add(LimitFragment.newInstance(getResources().getString(R.string.tab_mission_limit), indicatorColor, dividerColor));
        list.add(MessageFragment.newInstance(getResources().getString(R.string.tab_mission_message), indicatorColor, dividerColor));
        if (fromProcessing()) {
            list.add(ChatFragment.newInstance(getResources().getString(R.string.tab_mission_chat), indicatorColor, dividerColor));
        }
        return list;
    }

    private void InitialSomething() {

    }

    private void InitialUI() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_mission);
        mSlidingTabLayout = (MissionSlidingTabLayout) findViewById(R.id.slidingtab_mission);

        bt_gps = (Button) findViewById(R.id.bt_mission_gps);
        bt_accept = (Button) findViewById(R.id.bt_mission_accept);
    }

    private void InitialAction() {
        bt_gps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent iMissionMap = new Intent(ctxt, Act_MissionMap.class);
                iMissionMap.putExtra("lng", mMission.getLocationx());
                iMissionMap.putExtra("lat", mMission.getLocationy());
                startActivity(iMissionMap);
            }
        });
        bt_accept.setText(fromProcessing() ? me != -1 ? me == 1 ? "取消" : "放棄" : "錯誤" : "接受");
        bt_accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (fromProcessing()) {
                    CancelMission(Integer.toString(missionid));
                } else {
                    AcceptMission(Integer.toString(missionid));
                }
            }
        });

    }

    private void getExtras() {
        Intent it = getIntent();
        fromType = it.getStringExtra("fromType");
        me = fromProcessing() ? it.getIntExtra("me", -1) : -1;
        missionid = it.getIntExtra("missionid", 0);
        title = it.getStringExtra("title");
    }

    public Mission getMissionData() {
        return mMission;
    }

    private boolean fromProcessing() {
        return fromType.equals(Code.FromType_Processing);
    }
}
