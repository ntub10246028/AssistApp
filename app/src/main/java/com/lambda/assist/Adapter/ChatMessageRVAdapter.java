package com.lambda.assist.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lambda.assist.Model.ChatMessage;
import com.lambda.assist.Other.MyTime;
import com.lambda.assist.R;

import java.util.List;

/**
 * Created by asus on 2016/3/8.
 */
public class ChatMessageRVAdapter extends SampleRecyclerViewAdapter {
    private final int VIEW_TYPE_ME = 0;
    private final int VIEW_TYPE_OTHER = 1;

    private List<ChatMessage> list;

    public ChatMessageRVAdapter(Context context, List<ChatMessage> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getMe() == 0 ? VIEW_TYPE_ME : VIEW_TYPE_OTHER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ME) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_chatmessage_me, null);
            return new MeViewHolder(v);
        } else if (viewType == VIEW_TYPE_OTHER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_chatmessage_other, null);
            return new OtherViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ChatMessage item = list.get(position);
        if (holder instanceof MeViewHolder) {
            MeViewHolder mHolder = (MeViewHolder) holder;
            mHolder.content.setText(item.getReply());
            mHolder.time.setText(MyTime.convertTime_Chat(item.getReplytime()));
        } else if (holder instanceof OtherViewHolder) {
            OtherViewHolder mHolder = (OtherViewHolder) holder;
            mHolder.content.setText(item.getReply());
            mHolder.time.setText(MyTime.convertTime_Chat(item.getReplytime()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MeViewHolder extends RecyclerView.ViewHolder {

        public TextView content;
        public TextView time;

        public MeViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            content = (TextView) itemLayoutView.findViewById(R.id.tv_item_chat_me_content);
            time = (TextView) itemLayoutView.findViewById(R.id.tv_item_chat_me_time);
        }
    }

    public static class OtherViewHolder extends RecyclerView.ViewHolder {

        public TextView content;
        public TextView time;

        public OtherViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            content = (TextView) itemLayoutView.findViewById(R.id.tv_item_chat_other_content);
            time = (TextView) itemLayoutView.findViewById(R.id.tv_item_chat_other_time);
        }
    }
}
