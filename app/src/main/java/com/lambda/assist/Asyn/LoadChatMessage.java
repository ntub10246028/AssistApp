package com.lambda.assist.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Model.ChatMessage;
import com.lambda.assist.Model.MessageItem;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/4/18.
 */
public class LoadChatMessage extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadChatMessageListener {
        void finish(Integer result, List<ChatMessage> list);
    }

    private final OnLoadChatMessageListener mListener;
    private List<ChatMessage> list;

    public LoadChatMessage(OnLoadChatMessageListener mListener) {
        this.mListener = mListener;
        this.list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... datas) {
        int result = TaskCode.NoResponse;
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("sessionID", datas[0]));
        params.add(new BasicNameValuePair("oldtime", datas[1]));

        try {
            JSONObject jobj = new JsonReaderPost().Reader(params, URLs.url_loadchatmessage, MyHttpClient.getMyHttpClient());
            if (jobj != null) {
                Log.d("LoadChatMessage", jobj.toString());
                result = jobj.getInt("result");
                if (result == TaskCode.Success) {
                    JSONArray array = jobj.getJSONArray("reply");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jItem = array.getJSONObject(i);
                            ChatMessage item = new ChatMessage();
                            item.setIspostuser(jItem.getInt("ispostuser"));
                            item.setMe(jItem.getInt("me"));
                            item.setReply(jItem.getString("reply"));
                            item.setReplytime(jItem.getString("replytime"));
                            list.add(item);
                        }
                    } else {
                        Log.d("LoadChatMessage", "Array null");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoadChatMessage", e.toString());
        }

        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer, list);
    }
}
