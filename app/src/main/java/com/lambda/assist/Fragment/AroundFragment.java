package com.lambda.assist.Fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.lambda.assist.Adapter.AroundRVAdapter;
import com.lambda.assist.Asyn.LoadMissions;
import com.lambda.assist.Asyn.LoadAround;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Model.ReadyAroundMission;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;
import com.lambda.assist.UI.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;


public class AroundFragment extends MainBaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    //
    private Context ctxt;
    private Activity activity;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView mRecycleview;
    private ProgressBar pb_progressing;
    // Adapter
    private AroundRVAdapter adapter_rv;
    // Other
    private List<ReadyAroundMission> list_readmission;
    private List<Mission> list_missiondata;
    // Google API用戶端物件
    private GoogleApiClient googleApiClient;
    // Location請求物件
    private LocationRequest locationRequest;
    // 記錄目前最新的位置
    private Location currentLocation;
    // 顯示目前與儲存位置的標記物件
    private Marker currentMarker, itemMarker;
    private double final_lat = 0.0;
    private double final_lng = 0.0;
    private boolean F = true;

    public static AroundFragment newInstance(String title, int icon, int indicatorColor, int dividerColor) {
        AroundFragment fragment = new AroundFragment();
        fragment.setTitle(title);
        fragment.setIcon(icon);
        fragment.setIndicatorColor(indicatorColor);
        fragment.setDividerColor(dividerColor);
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
        return inflater.inflate(R.layout.fragment_aroundmission, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitialUI(getView());
        InitialAction();

        // 建立Google API用戶端物件
        configGoogleApiClient();

        // 建立Location請求物件
        configLocationRequest();

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    private void InitialAction() {
        // SwipeRefreshLayout Setting
        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setColorSchemeResources(android.R.color.black);
        //  RecyclerView Setting
        mRecycleview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                laySwipe.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        // 2. set layoutManger
        GridLayoutManager manager = new GridLayoutManager(ctxt, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                return 2;
            }
        });
        mRecycleview.setLayoutManager(manager);
        // item between item
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(10);
        mRecycleview.addItemDecoration(itemDecoration);
        // 3. create an adapter
        adapter_rv = new AroundRVAdapter(ctxt, list_missiondata);
        // 4. set adapter
        mRecycleview.setAdapter(adapter_rv);
        // 5. set item animator to DefaultAnimator
        mRecycleview.setItemAnimator(new DefaultItemAnimator());

    }

    private void InitialUI(View v) {
        laySwipe = (SwipeRefreshLayout) v.findViewById(R.id.laySwipe);
        mRecycleview = (RecyclerView) v.findViewById(R.id.recycleview);
        pb_progressing = (ProgressBar) v.findViewById(R.id.pb_around_processing);
    }

    private void InitialSomething() {
        list_readmission = new ArrayList<>();
        list_missiondata = new ArrayList<>();
    }

    private OnRefreshListener onSwipeToRefresh = new OnRefreshListener() {
        public void onRefresh() {
            LoadingAroundMission(Double.toString(final_lng), Double.toString(final_lat));
            laySwipe.setRefreshing(false);
        }
    };

    private void LoadingAroundMission(String lon, String lat) {
        if (Net.isNetWork(ctxt)) {
            ProgressingUI();
            LoadAround task = new LoadAround(new LoadAround.OnLoadAroundMissionIDListener() {
                public void finish(Integer result, List<ReadyAroundMission> readyMissions) {
                    FinishUI();
                    switch (result) {
                        case TaskCode.Success:
                            LoadingMission(readyMissions);
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
            task.execute(lon, lat);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    ///
    private void LoadingMission(List<ReadyAroundMission> datas) {
        if (Net.isNetWork(ctxt)) {
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
            List<Integer> ids = new ArrayList<>();
            for (ReadyAroundMission m : datas) {
                ids.add(m.getMissionid());
            }
            task.execute(ids);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void RefreshRecyclerView() {
        if (adapter_rv != null) {
            adapter_rv.setPosition(final_lng, final_lat);
            adapter_rv.notifyDataSetChanged();
        }
    }

    private void ProgressingUI() {
        mRecycleview.setVisibility(View.GONE);
        pb_progressing.setVisibility(View.VISIBLE);
    }

    private void FinishUI() {
        mRecycleview.setVisibility(View.VISIBLE);
        pb_progressing.setVisibility(View.GONE);
    }

    private synchronized void configGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(ctxt)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // 建立Location請求物件
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為60秒（60000ms）
        locationRequest.setInterval(10000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Already connect to google service
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, AroundFragment.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // google service disconnect , i is fail code
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // google service connect fail ,  connectionResult is fail result
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(ctxt, "未安裝 Google play",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // 位置改變
        // Location參數是目前的位置
        currentLocation = location;
        final_lng = currentLocation.getLongitude();
        final_lat = currentLocation.getLatitude();
        Log.d("Pos-AroundMission", final_lat + " " + final_lng);
        if (F) {
            F = !F;
            LoadingAroundMission(Double.toString(final_lng), Double.toString(final_lat));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 連線到Google API用戶端
        if (!googleApiClient.isConnected() && currentMarker != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 移除位置請求服務
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // 移除Google API用戶端連線
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
}

