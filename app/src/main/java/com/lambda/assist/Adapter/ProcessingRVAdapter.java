package com.lambda.assist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.R;

import java.util.List;

/**
 * Created by v on 2015/12/19.
 */
public class ProcessingRVAdapter extends SampleRecyclerViewAdapter {
    private List<Mission> list;

    public ProcessingRVAdapter(Context context, List<Mission> list) {
        super(context);
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_processing, null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get Your Holder
        ViewHolder mholder = (ViewHolder) holder;
        final Mission item = list.get(position);
        mholder.text.setText(item.getTitle());

        mholder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(getContext(), Act_Mission.class);
                it.putExtra("missionid", item.getMissionid());
                it.putExtra("title", item.getTitle());
                getContext().startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            text = (TextView) itemLayoutView.findViewById(R.id.tv_item_processing_title);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}