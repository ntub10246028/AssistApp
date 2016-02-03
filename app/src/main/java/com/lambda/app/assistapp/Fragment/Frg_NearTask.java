package com.lambda.app.assistapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import com.lambda.app.assistapp.ConnectionApp.JsonReaderPost;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.Item.AroundItem;
import com.lambda.app.assistapp.Listener.OnRcvScrollListener;
import com.lambda.app.assistapp.Adapter.AroundRVAdapter;
import com.lambda.app.assistapp.Other.Net;
import com.lambda.app.assistapp.Other.TaskCode;
import com.lambda.app.assistapp.Other.URLs;
import com.lambda.app.assistapp.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Frg_NearTask extends Fragment implements LocationListener {
    //
    private Context ctxt;
    private MyHttpClient client;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView mRecycleview;
    // Adapter
    private AroundRVAdapter adapter_rv;
    // Other
    private int position;
    private List<AroundItem> list_around;
    // For get Lan Let
    private boolean getService = false;     //是否已開啟定位服務
    private LocationManager lms;
    private Location location;
    private String bestProvider = LocationManager.GPS_PROVIDER;

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
        client = MyHttpClient.getMyHttpClient();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ctxt = getActivity();
        InitialSomething();
        View v = inflater.inflate(R.layout.fragment_neartask, container, false);
        InitialUI(v);
        InitialAction();
        GetCurrentPositionAndLoading();
        return v;
    }

    private void GetCurrentPositionAndLoading() {
        Location location = getLocation();
        if (location != null) {
            Double lon = location.getLongitude();
            Double lat = location.getLatitude();
            Log.d("Frg_NearTask", "lon = " + lon + " lat = " + lat);
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
        // 3. create an adapter
        adapter_rv = new AroundRVAdapter(list_around);
        // 4. set adapter
        mRecycleview.setAdapter(adapter_rv);
        // 5. set item animator to DefaultAnimator
        mRecycleview.setItemAnimator(new DefaultItemAnimator());
    }

    private void getData() {

    }

    private void InitialUI(View v) {
        laySwipe = (SwipeRefreshLayout) v.findViewById(R.id.laySwipe);
        mRecycleview = (RecyclerView) v.findViewById(R.id.recycleview);
    }

    private void InitialSomething() {
        list_around = new ArrayList<>();
    }

    private OnRefreshListener onSwipeToRefresh = new OnRefreshListener() {
        public void onRefresh() {
            GetCurrentPositionAndLoading();
            laySwipe.setRefreshing(false);
        }
    };

    private void LoadingAroundMission(String... datas) {
        if (Net.isNetWork(ctxt)) {
            new LoadingAroundMission().execute(datas);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class LoadingAroundMission extends AsyncTask<String, Integer, Integer> {
        private String longitude, latitude;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_around.clear();
        }

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = TaskCode.NoResponse;
            longitude = datas[0];
            latitude = datas[1];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lon", longitude));
            params.add(new BasicNameValuePair("lat", latitude));
            try {
                JsonReaderPost jp = new JsonReaderPost();
                JSONObject jobj = jp.Reader(params, URLs.url_around_Mission, client);
                if (jobj == null) return result;
                Log.d("LoadingAroundMission", jobj.toString());
                result = jobj.getInt("result");
                if (result == TaskCode.Success) {
                    JSONArray jarray = jobj.getJSONArray("around");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject item = jarray.getJSONObject(i);
                        AroundItem aitem = new AroundItem();
                        aitem.setMission(item.getInt("missionid"));
                        aitem.setLocationx(item.getDouble("locationx"));
                        aitem.setLocationy(item.getDouble("locationy"));
                        list_around.add(aitem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("LoadingAroundMission", e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case TaskCode.Empty:
                case TaskCode.Success:
                    Toast.makeText(ctxt, "success", Toast.LENGTH_SHORT).show();
                    RefreshRecyclerView();
                    break;
                case TaskCode.NoResponse:
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ctxt, "error : " + result, Toast.LENGTH_SHORT).show();
            }
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

