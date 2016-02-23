package com.lambda.assist.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * Created by asus on 2016/1/19.
 */
public abstract class SampleBaseAdapter extends BaseAdapter {
    private final Context context;
    private final Resources resources;
    private final LayoutInflater inflater;

    public SampleBaseAdapter(Context context) {
        this.context = context;
        this.resources = context.getResources();
        this.inflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return resources;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    @Override
    public abstract int getCount();

    @Override
    public abstract Object getItem(int position);

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
