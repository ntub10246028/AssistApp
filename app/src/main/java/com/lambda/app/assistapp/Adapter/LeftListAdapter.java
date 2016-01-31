package com.lambda.app.assistapp.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lambda.app.assistapp.Other.Item_History;
import com.lambda.app.assistapp.R;

import java.util.List;

/**
 * Created by asus on 2016/1/19.
 */
public class LeftListAdapter extends SampleBaseAdapter {
    private List<Item_History> list;

    public LeftListAdapter(Context context, List<Item_History> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHoler tag = null;
        if (v == null) {
            tag = new ViewHoler();
            v = getInflater().inflate(R.layout.item_leftlist, null);
            tag.title = (TextView) v.findViewById(R.id.tv_item_leftlist_title);
            tag.datetime = (TextView) v.findViewById(R.id.tv_item_leftlist_datetime);
            tag.status = (ImageView) v.findViewById(R.id.img_item_leftlist_status);
            v.setTag(tag);
        } else {
            tag = (ViewHoler) v.getTag();
        }
        Item_History item = (Item_History) getItem(position);
        tag.title.setText(item.getTitle());
        tag.datetime.setText(item.getDatetime());
        tag.status.setBackgroundResource(item.getStatus().equals("0") ? R.drawable.m_history_completed : R.drawable.m_history_completedhalt);

        return v;
    }

    class ViewHoler {
        public TextView title;
        public TextView datetime;
        public ImageView status;
    }
}
