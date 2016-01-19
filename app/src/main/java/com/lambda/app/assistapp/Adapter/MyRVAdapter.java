package com.lambda.app.assistapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lambda.app.assistapp.Other.Item;
import com.example.apple.assistapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by v on 2015/12/19.
 */
public class MyRVAdapter extends RecyclerView.Adapter {
    private final float NORMAL = 16f;
    private final float SMALL = 14f;
    private List<Item> list;

    public MyRVAdapter(List<Item> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_test, null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get Your Holder
        MyRVAdapter.ViewHolder mholder = (MyRVAdapter.ViewHolder) holder;

        // setTextSize
        mholder.text.setTextSize(((position + 1) % 3 == 0) ? NORMAL : SMALL);
        mholder.text.setSelected(true);

        // Loading Image use URL
        Picasso.with(mholder.img.getContext()).cancelRequest(mholder.img);
        Picasso.with(mholder.img.getContext()).load(list.get(position).getImgurl()).placeholder(R.drawable.loading).into(mholder.img);
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
            Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}