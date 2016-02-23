package com.lambda.assist.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by v on 2016/2/6.
 */
public abstract class SampleRecyclerViewAdapter extends RecyclerView.Adapter {
    private final Context context;

    public SampleRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    public abstract int getItemCount();
}
