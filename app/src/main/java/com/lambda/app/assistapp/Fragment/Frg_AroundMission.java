package com.lambda.app.assistapp.Fragment;

import android.app.Activity;
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

import com.lambda.app.assistapp.ConnectionApp.JsonReaderPost;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.Item.ReadyMission;
import com.lambda.app.assistapp.Item.MissionData;
import com.lambda.app.assistapp.Listener.OnRcvScrollListener;
import com.lambda.app.assistapp.Adapter.AroundRVAdapter;
import com.lambda.app.assistapp.Other.Net;
import com.lambda.app.assistapp.Other.TaskCode;
import com.lambda.app.assistapp.Other.URLs;
import com.lambda.app.assistapp.R;
import com.lambda.app.assistapp.UI.ItemOffsetDecoration;

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
    private MyHttpClient client;
    // UI
    private SwipeRefreshLayout laySwipe;
    private RecyclerView mRecycleview;
    // Adapter
    private AroundRVAdapter adapter_rv;
    // Other
    private int position;
    private List<ReadyMission> list_readmission;
    private List<MissionData> list_missiondata;
    // For get Lan Let
    private boolean getService = false;     //是否已開啟定位服務
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;

    public static Frg_AroundMission newInstance(int pos) {
        Frg_AroundMission fragment = new Frg_AroundMission();
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
        position = getArguments() != null ? getArguments().getInt("pos") : 1;
        client = MyHttpClient.getMyHttpClient();
        list_readmission = new ArrayList<>();
        list_missiondata = new ArrayList<>();
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
            list_readmission.clear();
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
                if (jobj == null)
                    return result;
                Log.d("LoadingAroundMission", jobj.toString());
                result = jobj.getInt("result");
                if (result == TaskCode.Success) {
                    JSONArray jarray = jobj.getJSONArray("around");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject item = jarray.getJSONObject(i);
                        ReadyMission aitem = new ReadyMission();
                        aitem.setMission(item.getInt("missionid"));
                        aitem.setLocationx(item.getDouble("locationx"));
                        aitem.setLocationy(item.getDouble("locationy"));
                        list_readmission.add(aitem);
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
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_warning_around_empty), Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.Success:
                    //Toast.makeText(ctxt, "Success", Toast.LENGTH_SHORT).show();
                    //RefreshRecyclerView();
                    LoadingMission(getMissions());
                    break;
                case TaskCode.NoResponse:
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<Integer> getMissions() {
        List<Integer> result = new ArrayList<>();
        for (ReadyMission item : list_readmission) {
            result.add(item.getMission());
        }
        return result;
    }

    ///
    private void LoadingMission(List<Integer> datas) {
        if (Net.isNetWork(ctxt)) {
            new LoadingMission().execute(datas);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class LoadingMission extends AsyncTask<List<Integer>, Integer, Integer> {
        private List<Integer> missions;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_missiondata.clear();
        }

        @Override
        protected Integer doInBackground(List<Integer>... datas) {
            Integer result = TaskCode.NoResponse;
            missions = datas[0];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Integer id : missions) {
                params.add(new BasicNameValuePair("missionID[]", Integer.toString(id)));
                //params.add(new BasicNameValuePair("missionID[]", 27 + ""));
            }
            try {
                JsonReaderPost jp = new JsonReaderPost();
                JSONObject jobj = jp.Reader(params, URLs.url_get_mission_data, client);
                if (jobj == null)
                    return result;
                Log.d("LoadingMission", jobj.toString());
                result = jobj.getInt("result");
                if (result == TaskCode.Success) {
                    JSONArray jarray = jobj.getJSONArray("missiondata");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject item = jarray.getJSONObject(i);
                        MissionData idata = new MissionData();
                        idata.setMissionid(item.getInt("missionid"));
                        idata.setPosttime(item.getString("posttime"));
                        idata.setOnlinelimittime(item.getString("onlinelimittime"));
                        idata.setRunlimittime(item.getString("runlimittime"));
                        idata.setLocationx(item.getDouble("locationx"));
                        idata.setLocationy(item.getDouble("locationy"));
                        idata.setLocationtypeid(item.getInt("locationtypeid"));
                        idata.setTitle(item.getString("title"));
                        idata.setContent(item.getString("content"));
                        idata.setImage(item.getString("image"));
                        idata.setGettime(item.getString("gettime"));
                        list_missiondata.add(idata);
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

