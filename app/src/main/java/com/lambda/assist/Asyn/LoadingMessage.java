package com.lambda.assist.Asyn;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Item.MessageItem;
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
public class LoadingMessage extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadingMessageListener {
        void finish(Integer result, List<MessageItem> list);
    }

    private final OnLoadingMessageListener mListener;
    private List<MessageItem> list;

    public LoadingMessage(OnLoadingMessageListener mListener) {
        this.mListener = mListener;
        this.list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... datas) {
        int result = TaskCode.NoResponse;
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("missionID", datas[0]));
        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_loadmessage, MyHttpClient.getMyHttpClient());
            if (jobj != null) {
                result = jobj.getInt("result");

                if (result == TaskCode.Success) {
                    JSONArray array = jobj.getJSONArray("message");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jItem = array.getJSONObject(i);
                            MessageItem item = new MessageItem();
                            item.setMe(jItem.getInt("me"));
                            item.setMessage(jItem.getString("message"));
                            list.add(item);
                        }
                    } else {
                        Log.d("LoadingMessage", "Array null");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoadingMessage", e.toString());
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer, list);
    }
}
