package com.example.apple.assistapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.apple.assistapp.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by asus on 2015/12/16.
 */
public class DataGridAdapter extends BaseAdapter {
    private Context ctxt;
    private LayoutInflater inflater;
    private List<String> list;

    public DataGridAdapter(Context ctxt, List<String> list) {
        this.ctxt = ctxt;
        this.inflater = LayoutInflater.from(ctxt);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int pos, View v, ViewGroup parent) {
        Tag tag = null;
        if (v == null) {
            tag = new Tag();
            v = inflater.inflate(R.layout.item_grid_data, null);
            tag.text = (TextView) v.findViewById(R.id.tv_data_grid_text);
            v.setTag(tag);
        } else {
            tag = (Tag) v.getTag();
        }
        tag.text.setText(getItem(pos));
        return v;
    }

    class Tag {
        TextView text;
    }
}
