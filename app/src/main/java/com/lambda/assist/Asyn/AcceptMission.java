package com.lambda.assist.Asyn;

import android.os.AsyncTask;

import com.lambda.assist.Other.TaskCode;

/**
 * Created by asus on 2016/3/2.
 */
public class AcceptMission extends AsyncTask<String, String, Integer> {
    public interface OnAcceptMissionListener {
        void finish(Integer result);
    }

    private final OnAcceptMissionListener mListener;

    public AcceptMission(OnAcceptMissionListener mListener) {
        this.mListener = mListener;
    }

    protected Integer doInBackground(String... strings) {
        Integer result = TaskCode.NoResponse;

        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer);
    }
}
