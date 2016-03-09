package com.lambda.assist.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lambda.assist.Item.Mission;
import com.lambda.assist.Item.ProcessingMission;
import com.lambda.assist.R;

import java.util.List;

/**
 * Created by v on 2015/12/19.
 */
public class ProcessingRVAdapter extends RecyclerView.Adapter {
    private List<Mission> list;

    public ProcessingRVAdapter(List<Mission> list) {
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
        Mission item = list.get(position);
        //mholder.text.setText(item.getText());
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
            text = (TextView) itemLayoutView.findViewById(R.id.tv_item_rv_text);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}