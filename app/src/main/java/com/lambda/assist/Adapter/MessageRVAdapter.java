package com.lambda.assist.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lambda.assist.Item.MessageItem;
import com.lambda.assist.R;

import java.util.List;

/**
 * Created by asus on 2016/3/8.
 */
public class MessageRVAdapter extends SampleRecyclerViewAdapter {

    private List<MessageItem> list;

    public MessageRVAdapter(Context context, List<MessageItem> list) {
        super(context);
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_message, null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, int position) {
        // Get Holder
        ViewHolder holder = (ViewHolder) vholder;
        final MessageItem item = list.get(position);
        // set
        int me = item.getMe();
        holder.content.setText(item.getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView content;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            content = (TextView) itemLayoutView.findViewById(R.id.tv_item_message_content);
        }
    }
}
