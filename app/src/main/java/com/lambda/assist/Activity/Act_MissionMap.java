package com.lambda.assist.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lambda.assist.R;

/**
 * Created by asus on 2016/3/16.
 */
public class Act_MissionMap extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Marker currentMarker;
    //
    private double mission_lat;
    private double mission_lng;
    private static final String missionPosition = "任務位置";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missionmap);
        Intent data = getIntent();
        mission_lat = data.getDoubleExtra("lat", 0.0);
        mission_lng = data.getDoubleExtra("lng", 0.0);
        setUpMap();
    }

    private void setUpMap() {
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fm_mission_map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setBuildingsEnabled(true);
        map.setMyLocationEnabled(true);
        if (mission_lat != 0.0 && mission_lng != 0.0) {
            LatLng itemPlace = new LatLng(mission_lat, mission_lng);
            addMarker(itemPlace);
            moveMap(itemPlace);
        }
    }

    private void addMarker(LatLng place) {
//        BitmapDescriptor icon =
//                BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place).title(missionPosition);
        // 加入並設定記事儲存的位置標記
        currentMarker = map.addMarker(markerOptions);
        currentMarker.showInfoWindow();
    }

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .build();
        // 使用動畫的效果移動地圖
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void finishActivity() {
        this.finish();
    }
}
