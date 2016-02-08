

package com.lambda.app.assistapp.Fragment;


import android.app.Activity;
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

import com.lambda.app.assistapp.Activity.Act_NewMission;
import com.lambda.app.assistapp.Adapter.ProcessingRVAdapter;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.Item.ProcessingItem;
import com.lambda.app.assistapp.Listener.OnRcvScrollListener;
import com.lambda.app.assistapp.Other.ActivityCode;
import com.lambda.app.assistapp.Other.Item;
import com.lambda.app.assistapp.R;
import com.lambda.app.assistapp.UI.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;


public class Frg_Processing extends Fragment {

    private Context ctxt;
    private Activity activity;
    private MyHttpClient client;
    private int position;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView rv;
    private ImageButton imgbt_add;
    // Adapter
    private ProcessingRVAdapter adapter_rv;
    private GridLayoutManager manager;
    //
    private List<ProcessingItem> list_processing;

    public static Frg_Processing newInstance(int pos) {
        Frg_Processing fragment = new Frg_Processing();
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        this.ctxt = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitialSomething();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_processing, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitialUI(getView());
        InitialAction();


        List<Item> list = new ArrayList<Item>();
        for (int i = 0; i < 20; i++) {
            Item item = new Item();
            item.setText("Text" + i);
            list.add(item);
        }
    }

    private void InitialAction() {
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
                Intent it = new Intent(ctxt, Act_NewMission.class);
                activity.startActivityForResult(it, ActivityCode.NewMission);
            }
        });
        //  RecyclerView Setting
        rv.setOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                laySwipe.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        //  set layoutManger
        manager = new GridLayoutManager(ctxt, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                return 2;
            }
        });
        rv.setLayoutManager(manager);
        // item between item
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(10);
        rv.addItemDecoration(itemDecoration);
        // set adapter
        list_processing = getData();
        adapter_rv = new ProcessingRVAdapter(list_processing);
        // set adapter
        rv.setAdapter(adapter_rv);
        // set item animator to DefaultAnimator
        rv.setItemAnimator(new DefaultItemAnimator());
    }

    private List<ProcessingItem> getData() {
        List<ProcessingItem> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ProcessingItem item = new ProcessingItem();
            item.setText("Text" + i);
            list.add(item);
        }
        return list;
    }

    private void InitialUI(View v) {
        laySwipe = (SwipeRefreshLayout) v.findViewById(R.id.laySwipe_list);
        rv = (RecyclerView) v.findViewById(R.id.rv_list);
        imgbt_add = (ImageButton) v.findViewById(R.id.imgbt_add);
    }

    private void InitialSomething() {
        position = getArguments() != null ? getArguments().getInt("pos") : 2;
        client = MyHttpClient.getMyHttpClient();
        list_processing = new ArrayList<>();
    }

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        public void onRefresh() {

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    laySwipe.setRefreshing(false);
                }
            }, 1000);
        }
    };
}
