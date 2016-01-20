package com.lambda.app.assistapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lambda.app.assistapp.Listener.OnRcvScrollListener;
import com.lambda.app.assistapp.Other.Item;
import com.lambda.app.assistapp.Adapter.MyRVAdapter;
import com.example.apple.assistapp.R;

import java.util.ArrayList;
import java.util.List;


public class Frg_NearTask extends Fragment {
    //
    private Context ctxt;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView mRecycleview;
    // Adapter
    private MyRVAdapter adapter_rv;
    // Other
    private int position;

    public static Frg_NearTask newInstance(int pos) {
        Frg_NearTask fragment = new Frg_NearTask();
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
        View v = inflater.inflate(R.layout.fragment_neartask, container, false);
        laySwipe = (SwipeRefreshLayout) v.findViewById(R.id.laySwipe);
        mRecycleview = (RecyclerView) v.findViewById(R.id.recycleview);

        // SwipeRefreshLayout Setting
        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        //  RecyclerView Setting
        mRecycleview.setOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                laySwipe.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        List<Item> list = new ArrayList<Item>();
        for (int i = 0; i < 20; i++) {
            Item item = new Item();
            item.setText("Text" + i);
            if ((i + 1) % 3 == 0) {
                item.setImgurl("http://goo.gl/XUBhFS");
            }
            list.add(item);
        }
        // 2. set layoutManger
        GridLayoutManager manager = new GridLayoutManager(ctxt, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                return (position + 1) % 3 == 0 ? 2 : 1;
            }
        });
        mRecycleview.setLayoutManager(manager);
        // 3. create an adapter
        adapter_rv = new MyRVAdapter(list);
        // 4. set adapter
        mRecycleview.setAdapter(adapter_rv);
        // 5. set item animator to DefaultAnimator
        mRecycleview.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    private OnRefreshListener onSwipeToRefresh = new OnRefreshListener() {
        public void onRefresh() {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctxt, "Refresh", Toast.LENGTH_SHORT).show();
                    laySwipe.setRefreshing(false);
                }
            }, 1000);
        }
    };
}

