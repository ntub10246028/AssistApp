package com.lambda.assist.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lambda.assist.R;

import java.util.List;

/**
 * Created by asus on 2016/3/28.
 */
public class SettingsListAdapter extends SampleBaseAdapter {
    private List<String> settings;

    public SettingsListAdapter(Context context, List<String> settings) {
        super(context);
        this.settings = settings;
    }

    @Override
    public int getCount() {
        return settings.size();
    }

    @Override
    public Object getItem(int position) {
        return settings.get(position);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_list_settings, null);
            tag = new ViewHolder();
            tag.item = (TextView) v.findViewById(R.id.tv_item_settings_setting);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }
        final String item = (String) getItem(position);
        tag.item.setText(item);

        return v;
    }

    static class ViewHolder {
        TextView item;
    }
}
