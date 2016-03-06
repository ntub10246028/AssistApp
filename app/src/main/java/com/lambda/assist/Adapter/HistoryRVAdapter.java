package com.lambda.assist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.Item.HistoryMission;
import com.lambda.assist.R;

import java.util.List;

/**
 * Created by asus on 2016/3/4.
 */
public class HistoryRVAdapter extends SampleRecyclerViewAdapter {
    private List<HistoryMission> list;


    public HistoryRVAdapter(Context context, List<HistoryMission> list) {
        super(context);
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // get holder
        ViewHolder mHolder = (ViewHolder) holder;
        // get item
        final HistoryMission item = list.get(position);
        // set value
        mHolder.title.setText(item.getTitle());
        mHolder.datetime.setText(item.getPosttime());
//        int statusID = item.get
        //mHolder.status.setBackgroundResource();
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), Act_Mission.class);
                it.putExtra("missionid", item.getMissionid());
                it.putExtra("title",item.getTitle());
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
            title = (TextView) itemView.findViewById(R.id.tv_item_leftlist_title);
            datetime = (TextView) itemView.findViewById(R.id.tv_item_leftlist_datetime);
            status = (ImageView) itemView.findViewById(R.id.img_item_leftlist_status);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
