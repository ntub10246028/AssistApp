package com.lambda.assist.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.Item.MissionData;
import com.lambda.assist.R;

/**
 * Created by asus on 2016/2/27.
 */
public class LimitFragment extends MissionBaseFragment {

    private Context ctxt;
    //
    private TextView tv_onlinelimit;
    private TextView tv_runlimit;
    //
    private MissionData nMissionData;

    public static LimitFragment newInstance(String title, int indicatorColor, int dividerColor) {
        LimitFragment fragment = new LimitFragment();
        fragment.setTitle(title);
        fragment.setIndicatorColor(indicatorColor);
        fragment.setDividerColor(dividerColor);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        this.activity = activity;
        this.ctxt = activity;
        nMissionData = ((Act_Mission) activity).getMissionData();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        InitialSomething();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_limit, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitialUI(getView());
        InitialAction();
    }

    private void InitialUI(View v) {
        tv_onlinelimit = (TextView) v.findViewById(R.id.tv_mission_limit_onlinelimit);
        tv_runlimit = (TextView) v.findViewById(R.id.tv_mission_limit_runlimit);
    }

    private void InitialAction() {
        tv_onlinelimit.setText(nMissionData.getOnlinelimittime());
        tv_runlimit.setText(nMissionData.getRunlimittime());
    }
}

