

package com.lambda.app.assistapp.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lambda.app.assistapp.Activity.Act_IssueArticle;
import com.lambda.app.assistapp.Adapter.RVListAdapter;
import com.lambda.app.assistapp.Other.ActivityCode;
import com.lambda.app.assistapp.Other.Item;
import com.example.apple.assistapp.R;

import java.util.ArrayList;
import java.util.List;


public class Frg_Processing extends Fragment {

    private Context ctxt;
    private int position;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView rv;
    private ImageButton imgbt_add;
    // Adapter
    private RVListAdapter adapter_rv;

    public static Frg_Processing newInstance(int pos) {
        Frg_Processing fragment = new Frg_Processing();
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        fragment.setArguments(b);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt("pos") : 2;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ctxt = getActivity();
        View v = inflater.inflate(R.layout.fragment_processing, container, false);
        laySwipe = (SwipeRefreshLayout) v.findViewById(R.id.laySwipe_list);
        rv = (RecyclerView) v.findViewById(R.id.rv_list);
        imgbt_add = (ImageButton) v.findViewById(R.id.imgbt_add);

        // SwipeRefreshLayout Setting
        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        // ImageButton Setting
        imgbt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(ctxt, Act_IssueArticle.class);
                getActivity().startActivityForResult(it, ActivityCode.ADD);
            }
        });
        //  RecyclerView Setting
        List<Item> list = new ArrayList<Item>();
        for (int i = 0; i < 20; i++) {
            Item item = new Item();
            item.setText("Text" + i);
            list.add(item);
        }
        // 2. set layoutManger
        GridLayoutManager manager = new GridLayoutManager(ctxt, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                return 2;
            }
        });
        rv.setLayoutManager(manager);
        // 3. create an adapter
        adapter_rv = new RVListAdapter(list);
        // 4. set adapter
        rv.setAdapter(adapter_rv);
        // 5. set item animator to DefaultAnimator
        rv.setItemAnimator(new DefaultItemAnimator());
        return v;
    }

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        public void onRefresh() {
            Toast.makeText(ctxt, "Refresh", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    laySwipe.setRefreshing(false);
                }
            }, 1000);
        }
    };
}