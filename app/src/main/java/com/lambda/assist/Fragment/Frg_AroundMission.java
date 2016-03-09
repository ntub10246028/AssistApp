package com.lambda.assist.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lambda.assist.Adapter.AroundRVAdapter;
import com.lambda.assist.Asyn.LoadMissions;
import com.lambda.assist.Asyn.LoadingAroundMissionID;
import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Item.AroundMission;
import com.lambda.assist.Item.Mission;
import com.lambda.assist.Item.ReadyMission;
import com.lambda.assist.Listener.OnRcvScrollListener;
import com.lambda.assist.Other.MyDialog;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;
import com.lambda.assist.R;
import com.lambda.assist.UI.ItemOffsetDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Frg_AroundMission extends Fragment implements LocationListener {
    //
    private Context ctxt;
    private Activity activity;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView mRecycleview;
    // Adapter
    private AroundRVAdapter adapter_rv;
    // Other
    private List<ReadyMission> list_readmission;
    private List<Mission> list_missiondata;
    // For get Lan Let
    private boolean getService = false;     //是否已開啟定位服務
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;

    public static Frg_AroundMission newInstance() {
        return new Frg_AroundMission();
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
        GetCurrentPositionAndLoading();
    }

    private void GetCurrentPositionAndLoading() {
        Location location = getLocation();
        if (location != null) {
            Double lon = location.getLongitude();
            Double lat = location.getLatitude();
            adapter_rv.setPosition(lon, lat);
            Log.d("Frg_AroundMission", "lon = " + lon + " lat = " + lat);
            LoadingAroundMission(Double.toString(lon), Double.toString(lat));
        } else {
            Toast.makeText(ctxt, "無法取得位置", Toast.LENGTH_SHORT).show();
        }
    }

    private void InitialAction() {
        // SwipeRefreshLayout Setting
        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setColorSchemeResources(android.R.color.black);
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
    }

    private void InitialSomething() {
        list_readmission = new ArrayList<>();
        list_missiondata = new ArrayList<>();
    }

    private OnRefreshListener onSwipeToRefresh = new OnRefreshListener() {
        public void onRefresh() {
            GetCurrentPositionAndLoading();
            laySwipe.setRefreshing(false);
        }
    };

    private void LoadingAroundMission(String lon, String lat) {
        if (Net.isNetWork(ctxt)) {
            //final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadingAroundMissionID task = new LoadingAroundMissionID(new LoadingAroundMissionID.OnLoadingAroundMissionIDListener() {
                public void finish(Integer result, List<ReadyMission> readyMissions, List<Integer> ids) {
                    //pd.dismiss();
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
            task.execute(lon, lat);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    ///
    private void LoadingMission(List<Integer> datas) {
        if (Net.isNetWork(ctxt)) {
            //final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadMissions task = new LoadMissions(new LoadMissions.OnLoadMissionsListener() {
                public void finish(Integer result, List<Mission> list) {
                    //pd.dismiss();
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

    private Location LocationSetting() {
        LocationManager status = (LocationManager) (ctxt.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            return LocationServiceInitial();
        } else {
            Toast.makeText(ctxt, "請開啟定位服務", Toast.LENGTH_LONG).show();
            getService = true; //確認開啟定位服務
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
            return null;
        }
    }

    private Location LocationServiceInitial() {
        lms = (LocationManager) ctxt.getSystemService(ctxt.LOCATION_SERVICE); //取得系統定位服務
        // 由Criteria物件判斷提供最準確的資訊
        Criteria criteria = new Criteria();  //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
        Location location = lms.getLastKnownLocation(bestProvider);
        return location;
    }

    private Location getLocation() {
        return LocationSetting();
    }

    @Override
    public void onLocationChanged(Location location) {  //當地點改變時
        //Location location = getLocation();
        if (location != null) {
            Double lon = location.getLongitude();
            Double lat = location.getLatitude();
            StringBuilder builder = new StringBuilder();
            builder.append("lon = " + lon + "\n");
            builder.append("lat = " + lat);
            Toast.makeText(ctxt, builder, Toast.LENGTH_SHORT).show();
            LoadingAroundMission(Double.toString(lon), Double.toString(lat));
        } else {
            Toast.makeText(ctxt, "無法取得位置", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {//當GPS或網路定位功能關閉時
        // TODO 自動產生的方法 Stub
        Toast.makeText(ctxt, "請開啟gps或3G網路", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String arg0) { //當GPS或網路定位功能開啟
        // TODO 自動產生的方法 Stub
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) { //定位狀態改變
        Toast.makeText(ctxt, "status change", Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (getService) {
            lms.requestLocationUpdates(bestProvider, 5000, 1, this);
            //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }
    }

    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (getService) {
            lms.removeUpdates(this);   //離開頁面時停止更新
        }
    }
}

