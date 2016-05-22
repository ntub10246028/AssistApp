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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lambda.assist.Adapter.MissionFragmentAdapter;
import com.lambda.assist.Asyn.AcceptMission;
import com.lambda.assist.Asyn.AccomplishedMission;
import com.lambda.assist.Asyn.CancelMission;
import com.lambda.assist.Asyn.LoadMissions;
import com.lambda.assist.Fragment.ChatFragment;
import com.lambda.assist.Fragment.ContentFragment;
import com.lambda.assist.Fragment.LimitFragment;
import com.lambda.assist.Fragment.MessageFragment;
import com.lambda.assist.Fragment.MissionBaseFragment;
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
    private LinearLayout ll_bottom_function;
    private Button bt_gps, bt_complete, bt_accept;
    // Adapter
    private MissionFragmentAdapter fragmentAdapter;
    // get Extras
    private int msessionid;
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
                                    InitialTabView();
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

    private void CancelMission(final String missionid, final String msessionid) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            CancelMission task = new CancelMission(new CancelMission.OnCancelMissionListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case TaskCode.Success:
                            Toast.makeText(ctxt, "取消/放棄成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finishActivity();
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
            task.execute(missionid, msessionid);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void AccomplishedMission(final String missionid) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            AccomplishedMission task = new AccomplishedMission(new AccomplishedMission.OnAccomplishedMissionListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case TaskCode.Success:
                            Toast.makeText(ctxt, "成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finishActivity();
                            break;
                        case TaskCode.Accomplished_fail:
                            Toast.makeText(ctxt, "失敗", Toast.LENGTH_SHORT).show();
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
        mViewPager.setOffscreenPageLimit(fromAround() ? 3 : 4);
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
        if ((fromProcessing() && msessionid != 0) || fromHistory()) {
            list.add(ChatFragment.newInstance(getResources().getString(R.string.tab_mission_chat), indicatorColor, dividerColor));
        }
        return list;
    }

    private void InitialSomething() {

    }

    private void InitialUI() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_mission);
        mSlidingTabLayout = (MissionSlidingTabLayout) findViewById(R.id.slidingtab_mission);

        ll_bottom_function = (LinearLayout) findViewById(R.id.ll_mission_bottom_function);
        bt_gps = (Button) findViewById(R.id.bt_mission_gps);
        bt_complete = (Button) findViewById(R.id.bt_mission_complete);
        bt_accept = (Button) findViewById(R.id.bt_mission_accept);
    }

    private void InitialAction() {
        if (fromAround()) {
            ll_bottom_function.setVisibility(View.VISIBLE);
            bt_accept.setText("接受");
        } else if (fromProcessing()) {
            ll_bottom_function.setVisibility(View.VISIBLE);
            if (me != -1) {
                if (me == 1) {
                    bt_accept.setText("取消");
                    if (msessionid != 0)
                        bt_complete.setVisibility(View.VISIBLE);
                } else {
                    bt_accept.setText("放棄");
                }
            } else {
                bt_accept.setText("錯誤");
            }
        } else if (fromHistory()) {
            ll_bottom_function.setVisibility(View.GONE);
        }

        bt_gps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent iMissionMap = new Intent(ctxt, Act_MissionMap.class);
                iMissionMap.putExtra("lng", mMission.getLocationx());
                iMissionMap.putExtra("lat", mMission.getLocationy());
                startActivity(iMissionMap);
            }
        });
        bt_complete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        bt_accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (fromAround()) {
                    AcceptMission(Integer.toString(missionid));
                } else if (fromProcessing()) {
                    CancelMission(Integer.toString(missionid), Integer.toString(msessionid));
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (fromHistory())
                    return;

                if (position == 0 || position == 1) {
                    ll_bottom_function.setVisibility(View.VISIBLE);
                } else {
                    ll_bottom_function.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getExtras() {
        Intent it = getIntent();
        fromType = it.getStringExtra("fromType");
        me = fromProcessing() ? it.getIntExtra("me", -1) : -1;
        missionid = it.getIntExtra("missionid", 0);
        title = it.getStringExtra("title");
        msessionid = fromProcessing() ? it.getIntExtra("msessionid", 0) : 0;
    }

    public Mission getMissionData() {
        return mMission;
    }

    private boolean fromAround() {
        return fromType.equals(Code.FromType_Around);
    }

    private boolean fromProcessing() {
        return fromType.equals(Code.FromType_Processing);
    }

    private boolean fromHistory() {
        return fromType.equals(Code.FromType_History);
    }


    private void finishActivity() {
        this.finish();
    }
}
