package com.lambda.assist.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.Helper.ImageTextHelp;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.R;

/**
 * Created by asus on 2016/2/27.
 */
public class ContentFragment extends MissionBaseFragment {

    //
    private Context ctxt;
    //
    private LinearLayout ll_content;
    //
    private Mission mMission;

    public static ContentFragment newInstance(String title, int indicatorColor, int dividerColor) {
        ContentFragment fragment = new ContentFragment();
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
        mMission = ((Act_Mission) activity).getMissionData();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        InitialSomething();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitialUI(getView());
        InitialAction();
    }

    private void InitialUI(View v) {
        ll_content = (LinearLayout) v.findViewById(R.id.ll_mission_content_content);
    }

    private void InitialAction() {
        ImageTextHelp.convertUrlToButton(mMission.getContent(), ll_content);
    }
}
