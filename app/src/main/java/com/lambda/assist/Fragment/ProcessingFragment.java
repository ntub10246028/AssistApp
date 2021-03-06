

package com.lambda.assist.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lambda.assist.Activity.Act_Main;
import com.lambda.assist.Activity.Act_NewMission;
import com.lambda.assist.Adapter.ProcessingRVAdapter;
import com.lambda.assist.Asyn.LoadMissions;
import com.lambda.assist.Asyn.LoadRunning;
import com.lambda.assist.Listener.OnLoadMoreListener;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Model.ReadyProcessingMission;
import com.lambda.assist.Other.ActivityCode;
import com.lambda.assist.Other.ListUtil;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;
import com.lambda.assist.UI.ItemOffsetDecoration;
import com.lambda.assist.UI.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;


public class ProcessingFragment extends MainBaseFragment {

    private Context ctxt;
    private Act_Main activity;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView mRecycleview;
    private ProgressBar pb_progressing;
    private Button bt_add;
    // Adapter
    private ProcessingRVAdapter adapter_rv;
    //
    // Other
    private List<Mission> list_missiondata;
    private List<ReadyProcessingMission> allRPM;
    private int lastMissionPosition, totalMissionPosition, countOfOnceLoad = 10;

    public static ProcessingFragment newInstance(String title, int icon, int indicatorColor, int dividerColor) {
        ProcessingFragment fragment = new ProcessingFragment();
        fragment.setTitle(title);
        fragment.setIcon(icon);
        fragment.setIndicatorColor(indicatorColor);
        fragment.setDividerColor(dividerColor);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (Act_Main) activity;
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
                public void finish(Integer result, List<ReadyProcessingMission> datas) {
                    FinishUI();
                    switch (result) {
                        case TaskCode.Success:
                            allRPM.clear();
                            allRPM.addAll(datas);
                            list_missiondata.clear();
                            lastMissionPosition = 0;
                            totalMissionPosition = allRPM.size() - 1;
                            if (stillCanLoading()) {
                                LoadingMission(canLoadMissionIds());
                            }
                            break;
                        case TaskCode.Empty:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_warning_processing_empty), Toast.LENGTH_SHORT).show();
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

    private void LoadingMission(List<ReadyProcessingMission> datas) {
        if (Net.isNetWork(ctxt)) {
            list_missiondata.add(null);
            adapter_rv.notifyItemInserted(list_missiondata.size() - 1);
            LoadMissions task = new LoadMissions(new LoadMissions.OnLoadMissionsListener() {
                public void finish(Integer result, List<Mission> list) {
                    //FinishUI();
                    switch (result) {
                        case TaskCode.Empty:
                        case TaskCode.Success:
                            //Remove loading item
                            list_missiondata.remove(list_missiondata.size() - 1);
                            adapter_rv.notifyItemRemoved(list_missiondata.size());
                            ListUtil.append(list_missiondata, list);
                            matchMissionByME();
                            adapter_rv.notifyDataSetChanged();
                            adapter_rv.setLoaded();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
            List<Integer> ids = new ArrayList<>();
            for (ReadyProcessingMission m : datas) {
                ids.add(m.getMission());
            }
            task.execute(ids);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void matchMissionByME() {
        for (Mission m : list_missiondata) {
            for (ReadyProcessingMission rpm : allRPM) {
                if (m.getMissionid() == rpm.getMission()) {
                    m.setMe(rpm.getMe());
                    break;
                }
            }
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
        mRecycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                laySwipe.setEnabled(topRowVerticalPosition >= 0);
            }
        });

        //  set layoutManger
        mRecycleview.setLayoutManager(new WrapContentLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        // item between item
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(10);
        mRecycleview.addItemDecoration(itemDecoration);
        // set adapter
        adapter_rv = new ProcessingRVAdapter(activity, list_missiondata, mRecycleview);

        adapter_rv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (stillCanLoading()) {
                    LoadingMission(canLoadMissionIds());
                }
            }
        });
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
        allRPM = new ArrayList<>();
    }

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        public void onRefresh() {
            refresh();
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

    private boolean stillCanLoading() {
        Log.d("LLL", "T " + lastMissionPosition + " " + totalMissionPosition);
        return lastMissionPosition <= totalMissionPosition;
    }

    private List<ReadyProcessingMission> canLoadMissionIds() {
        List<ReadyProcessingMission> result = new ArrayList<>();
        int start = lastMissionPosition;
        int end = start + countOfOnceLoad > totalMissionPosition ? totalMissionPosition : start + countOfOnceLoad;
        lastMissionPosition = end + 1;
        Log.d("LLL", "R " + start + " " + end);
        for (int i = start; i <= end; i++) {
            result.add(allRPM.get(i));
        }
        return result;
    }

    public void refresh(){
        LoadRunning();
        laySwipe.setRefreshing(false);
    }
}
