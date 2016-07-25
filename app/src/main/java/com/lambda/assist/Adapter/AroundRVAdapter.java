package com.lambda.assist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.Helper.BitmapHelp;
import com.lambda.assist.Helper.BitmapTransform;
import com.lambda.assist.Helper.GPSHelper;
import com.lambda.assist.Helper.ImgurHelper;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Other.Code;
import com.lambda.assist.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by v on 2015/12/19.
 */
public class AroundRVAdapter extends SampleRecyclerViewAdapter {

    private List<Mission> list;
    private double my_lon;
    private double my_lat;

    public AroundRVAdapter(Context context, List<Mission> list) {
        super(context);
        this.list = list;
        this.my_lon = -1;
        this.my_lat = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_around, null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, int position) {
        // Get Your Holder
        ViewHolder holder = (ViewHolder) vholder;
        final Mission item = list.get(position);
        // set
        holder.title.setText(item.getTitle());

        Log.d("AroundRVAdapter", my_lon + " " + my_lat);
        if (my_lon != -1 && my_lat != -1) {
            String distance = GPSHelper.gps2m(my_lat, my_lon, item.getLocationy(), item.getLocationx());
            holder.distances.setText(distance);
        }
        String url = ImgurHelper.checkUrl(item.getContent());
        if (url != null) {
            Picasso.with(getContext())
                    .load(url)
                    .transform(new BitmapTransform(BitmapHelp.maxW, BitmapHelp.maxH))
                    .resize(BitmapHelp.size(), BitmapHelp.size())
                    .centerInside()
                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(getContext(), Act_Mission.class);
                it.putExtra("fromType", Code.FromType_Around);
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

    public void setPosition(double lon, double lat) {
        my_lon = lon;
        my_lat = lat;
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, distances;
        public ImageView image;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            title = (TextView) itemLayoutView.findViewById(R.id.tv_item_around_title);
            distances = (TextView) itemLayoutView.findViewById(R.id.tv_item_around_distance);
            image = (ImageView) itemLayoutView.findViewById(R.id.tv_item_around_image);
        }
    }
}