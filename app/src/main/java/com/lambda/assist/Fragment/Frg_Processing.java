

package com.lambda.assist.Fragment;


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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lambda.assist.Activity.Act_NewMission;
import com.lambda.assist.Adapter.ProcessingRVAdapter;
import com.lambda.assist.Asyn.LoadMissions;
import com.lambda.assist.Asyn.LoadRunning;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Listener.OnRcvScrollListener;
import com.lambda.assist.Other.ActivityCode;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;
import com.lambda.assist.UI.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;


public class Frg_Processing extends Fragment {

    private Context ctxt;
    private Activity activity;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView mRecycleview;
    private ProgressBar pb_progressing;
    private Button bt_add;
    // Adapter
    private ProcessingRVAdapter adapter_rv;
    private GridLayoutManager manager;
    //
    // Other
    private List<Mission> list_missiondata;

    public static Frg_Processing newInstance() {
        return new Frg_Processing();
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
        LoadRunning();
    }

    private void LoadRunning() {
        if (Net.isNetWork(ctxt)) {
            ProgressingUI();
            LoadRunning task = new LoadRunning(new LoadRunning.OnLoadRunningListener() {
                public void finish(Integer result, List<Integer> ids) {
                    FinishUI();
                    switch (result) {
                        case TaskCode.Success:
                            LoadingMission(ids);
                            break;
                        case TaskCode.Empty:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_warning_around_empty), Toast.LENGTH_SHORT).show();
                            list_missiondata.clear();
                            RefreshRecyclerView();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute();
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadingMission(List<Integer> datas) {
        if (Net.isNetWork(ctxt)) {
            ProgressingUI();
            LoadMissions task = new LoadMissions(new LoadMissions.OnLoadMissionsListener() {
                public void finish(Integer result, List<Mission> list) {
                    FinishUI();
                    switch (result) {
                        case TaskCode.Empty:
                        case TaskCode.Success:
                            list_missiondata.clear();
                            list_missiondata.addAll(list);
                            RefreshRecyclerView();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute(datas);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void RefreshRecyclerView() {
        if (adapter_rv != null) {
            adapter_rv.notifyDataSetChanged();
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
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(ctxt, Act_NewMission.class);
                activity.startActivityForResult(it, ActivityCode.NewMission);
            }
        });
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
        //  set layoutManger
        manager = new GridLayoutManager(ctxt, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                return 2;
            }
        });
        mRecycleview.setLayoutManager(manager);
        // item between item
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(10);
        mRecycleview.addItemDecoration(itemDecoration);
        // set adapter
        adapter_rv = new ProcessingRVAdapter(ctxt, list_missiondata);
        // set adapter
        mRecycleview.setAdapter(adapter_rv);
        // set item animator to DefaultAnimator
        mRecycleview.setItemAnimator(new DefaultItemAnimator());
    }

    private void InitialUI(View v) {
        laySwipe = (SwipeRefreshLayout) v.findViewById(R.id.laySwipe_list);
        mRecycleview = (RecyclerView) v.findViewById(R.id.rv_list);
        pb_progressing = (ProgressBar) v.findViewById(R.id.pb_running_processing);
        bt_add = (Button) v.findViewById(R.id.bt_processing_add);
    }

    private void InitialSomething() {
        list_missiondata = new ArrayList<>();
    }

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        public void onRefresh() {
            LoadRunning();
            laySwipe.setRefreshing(false);
        }
    };

    private void ProgressingUI() {
        mRecycleview.setVisibility(View.GONE);
        pb_progressing.setVisibility(View.VISIBLE);
    }

    private void FinishUI() {
        mRecycleview.setVisibility(View.VISIBLE);
        pb_progressing.setVisibility(View.GONE);
    }
}
