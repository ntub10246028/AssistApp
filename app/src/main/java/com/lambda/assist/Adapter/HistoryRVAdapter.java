package com.lambda.assist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Other.Code;
import com.lambda.assist.Other.MyTime;
import com.lambda.assist.R;

import java.util.List;

/**
 * Created by asus on 2016/3/4.
 */
public class HistoryRVAdapter extends SampleRecyclerViewAdapter {
    private List<Mission> list;


    public HistoryRVAdapter(Context context, List<Mission> list) {
        super(context);
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_history, null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder mHolder = (ViewHolder) holder;
        final Mission item = list.get(position);
        // set value
        mHolder.title.setText(item.getTitle());
        mHolder.datetime.setText(item.getPosttime() != null ? MyTime.convertTime_History(item.getPosttime()) : "");
        mHolder.status.setImageDrawable(getContext().getResources().getDrawable(item.getIsdone() == Mission.DO ? R.drawable.m_history_completed : R.drawable.m_history_iscancel));


        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), Act_Mission.class);
                it.putExtra("fromType", Code.FromType_History);
                it.putExtra("missionid", item.getMissionid());
                it.putExtra("title", item.getTitle());
                getContext().startActivity(it);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView datetime;
        public ImageView status;


        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_item_history_title);
            datetime = (TextView) itemView.findViewById(R.id.tv_item_history_datetime);
            status = (ImageView) itemView.findViewById(R.id.img_item_history_status);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
