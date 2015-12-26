

package com.example.apple.assistapp.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apple.assistapp.Activity.Act_IssueArticle;
import com.example.apple.assistapp.Adapter.RVListAdapter;
import com.example.apple.assistapp.Other.ActivityCode;
import com.example.apple.assistapp.Other.Item;
import com.example.apple.assistapp.R;
import com.example.apple.assistapp.UI.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class Fragment2 extends Fragment {

    private Context ctxt;
    private int position;
    // UI
    private SwipeRefreshLayout laySwipe;
    private FloatingActionButton fab;
    private RecyclerView rv;
    // Adapter
    private RVListAdapter adapter_rv;

    public static Fragment2 newInstance(int pos) {
        Fragment2 fragment = new Fragment2();
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
        View v = inflater.inflate(R.layout.fragment_2, container, false);
        laySwipe = (SwipeRefreshLayout) v.findViewById(R.id.laySwipe_list);
        rv = (RecyclerView) v.findViewById(R.id.rv_list);
        fab = (FloatingActionButton) v.findViewById(R.id.fab_list);
        // Fab Setting
        fab.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent it = new Intent(ctxt, Act_IssueArticle.class);
                getActivity().startActivityForResult(it, ActivityCode.ADD);
            }
        });
        // SwipeRefreshLayout Setting
        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
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
//        // 6. set Divider
//        rv.addItemDecoration(new DividerItemDecoration(ctxt, DividerItemDecoration.VERTICAL_LIST));

        return v;
    }

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        public void onRefresh() {
            Toast.makeText(ctxt, "Refresh", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    laySwipe.setRefreshing(false);
                }
            }, 2000);
        }
    };
}
