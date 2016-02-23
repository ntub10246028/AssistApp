package com.lambda.assist.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lambda.assist.R;

public class MapsActivity extends FragmentActivity {
    private Context ctxt = MapsActivity.this;
    private GoogleMap mMap;
    // For get Lan Let
    private boolean getService = false;     //是否已開啟定位服務
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;
    // other
    private double final_lat;
    private double final_lng;
    private static final int MOVE_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        InitialExtras();
        setUpMapIfNeeded();
        OnMapListeners();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                mMap.setBuildingsEnabled(true);
                mMap.setMyLocationEnabled(true);
                AddCurrentPosition();
            }
        } else {
            Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void OnMapListeners() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng latLng) {
                final_lat = latLng.latitude;
                final_lng = latLng.longitude;
                mMap.clear();
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.anchor(0.5f, 1.0f);
                options.draggable(true);
                mMap.addMarker(options);
                playAnimateCamera(latLng);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {

                new AlertDialog.Builder(ctxt).setMessage("確定這邊 ? \n緯度:" + final_lat + "\n經度:" + final_lng).setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent result = new Intent();
                        result.putExtra("lng", final_lng);
                        result.putExtra("lat", final_lat);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                }).setNegativeButton("繼續選", null).show();
                return false;
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latlng = marker.getPosition();
                final_lat = latlng.latitude;
                final_lng = latlng.longitude;
                playAnimateCamera(latlng);
            }
        });
    }

    private void AddCurrentPosition() {

        if (final_lat == -1 && final_lng == -1) {
            Location location = getLocation();
            if (location != null) {
                final_lat = location.getLatitude();
                final_lng = location.getLongitude();
            } else {
                Toast.makeText(ctxt, "無法取得位置", Toast.LENGTH_SHORT).show();
                final_lat = 0;
                final_lng = 0;
            }
        }
        Log.d("ActivityMap", "lon = " + final_lng + " lat = " + final_lat);
        LatLng thisWay = new LatLng(final_lat, final_lng);
        drawMarker(thisWay);

    }

    private Location getLocation() {
        return LocationSetting();
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

    private void playAnimateCamera(LatLng latlng) {
        CameraPosition cameraPos = new CameraPosition.Builder().target(latlng).zoom(17.0f).build();
        CameraUpdate cameraUpt = CameraUpdateFactory.newCameraPosition(cameraPos);
        mMap.animateCamera(cameraUpt, MOVE_TIME, null);
    }

    private void drawMarker(LatLng latlng) {
        MarkerOptions options = new MarkerOptions();
        options.position(latlng);
        options.anchor(0.5f, 1.0f);
        options.draggable(true);
        mMap.addMarker(options);
        playAnimateCamera(latlng);
    }

    private void InitialExtras() {
        Intent data = getIntent();
        final_lat = data.getDoubleExtra("lat", -1);
        final_lng = data.getDoubleExtra("lng", -1);
    }
}
