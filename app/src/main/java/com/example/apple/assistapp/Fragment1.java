package com.example.apple.assistapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Fragment1 extends Fragment {
    private Context ctxt;
    private int position;
    private SwipeRefreshLayout laySwipe;
    private ListView lv_aaa_datas;

    public static Fragment1 newInstance(int pos) {
        Fragment1 fragment = new Fragment1();
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        fragment.setArguments(b);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt("pos") : 1;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ctxt = getActivity();
        View v = inflater.inflate(R.layout.fragment_1, container, false);
        laySwipe = (SwipeRefreshLayout) v.findViewById(R.id.laySwipe);
        lv_aaa_datas = (ListView) v.findViewById(R.id.lv_aaa_datas);

        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        String[] datas = { "AAA1", "AAA2", "AAA3", "AAA4", "AAA5", "AAA6",
                "AAA7", "AAA8", "AAA9", "AAA10" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctxt,
                android.R.layout.simple_list_item_1, datas);
        // 更新Adapter
        //adapter.notifyDataSetChanged();

        lv_aaa_datas.setAdapter(adapter);
        lv_aaa_datas.setOnScrollListener(onListScroll);
        lv_aaa_datas.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String text = ((TextView) v).getText().toString();
                Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private boolean listIsAtTop() {
        if (lv_aaa_datas.getChildCount() == 0)
            return true;
        return lv_aaa_datas.getChildAt(0).getTop() == 0;
    }

    private OnRefreshListener onSwipeToRefresh = new OnRefreshListener() {
        public void onRefresh() {
            if (listIsAtTop()) {
                laySwipe.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        laySwipe.setRefreshing(false);
                        Toast.makeText(getActivity(), "Refresh done!",
                                Toast.LENGTH_SHORT).show();
                    }
                }, 1000);

            }
        }
    };
    private OnScrollListener onListScroll = new OnScrollListener() {

        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem == 0) {
                laySwipe.setEnabled(true);
            } else {
                laySwipe.setEnabled(false);
            }
        }
    };
}

