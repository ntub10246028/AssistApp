package com.example.apple.assistapp.Fragment;

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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.assistapp.Adapter.DataGridAdapter;
import com.example.apple.assistapp.R;

import java.util.ArrayList;
import java.util.List;


public class Fragment1 extends Fragment {
    //
    private Context ctxt;
    // UI
    private SwipeRefreshLayout laySwipe;
    private GridView gv_datas;
    private DataGridAdapter dataAdapter;
    //
    // Other
    private int position;

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
        gv_datas = (GridView) v.findViewById(R.id.gv_fgm_datas);

        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("Text");
        }
        dataAdapter = new DataGridAdapter(ctxt, datas);
        // 更新Adapter
        //adapter.notifyDataSetChanged();

        gv_datas.setAdapter(dataAdapter);

        return v;
    }

    private OnRefreshListener onSwipeToRefresh = new OnRefreshListener() {
        public void onRefresh() {
//            if (listIsAtTop()) {
//                laySwipe.setRefreshing(true);
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        laySwipe.setRefreshing(false);
//                        Toast.makeText(getActivity(), "Refresh done!",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }, 1000);
//
//            }
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

