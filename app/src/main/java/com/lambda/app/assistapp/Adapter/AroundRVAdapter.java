package com.lambda.app.assistapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lambda.app.assistapp.Item.AroundItem;
import com.lambda.app.assistapp.R;

import java.util.List;

/**
 * Created by v on 2015/12/19.
 */
public class AroundRVAdapter extends RecyclerView.Adapter {
    private List<AroundItem> list;

    public AroundRVAdapter(List<AroundItem> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_around, null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get Your Holder
        AroundRVAdapter.ViewHolder mholder = (AroundRVAdapter.ViewHolder) holder;

        // setTextSize
        mholder.text.setSelected(true);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text;
        public ImageView img;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            text = (TextView) itemLayoutView.findViewById(R.id.tv_item_rv_test);
            img = (ImageView) itemLayoutView.findViewById(R.id.img_item_rv_test);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}