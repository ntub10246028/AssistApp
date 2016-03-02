package com.lambda.assist.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lambda.assist.R;

/**
 * Created by asus on 2016/2/27.
 */
public class MessageFragment extends MissionBaseFragment {

    private Context ctxt;
    //
    private TextView tv_test;

    public static MessageFragment newInstance(String title , int indicatorColor , int dividerColor) {
        MessageFragment fragment = new MessageFragment();
        fragment.setTitle(title);
        fragment.setIndicatorColor(indicatorColor);
        fragment.setDividerColor(dividerColor);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.ctxt = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        InitialSomething();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitialUI(getView());
        InitialAction();
    }
    private void InitialUI(View v) {
        //tv_test = (TextView) v.findViewById(R.id.tv_mission_message_test);
    }

    private void InitialAction() {
        //tv_test.setText("Test123456");
    }
}
