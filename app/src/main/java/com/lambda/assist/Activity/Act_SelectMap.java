package com.lambda.assist.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.lambda.assist.R;

/**
 * Created by asus on 2016/3/9.
 */
public class Act_SelectMap extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    // other
    private double final_lat;
    private double final_lng;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectmap);
        setUpMap();
    }

    private void setUpMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fm_select_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void InitialExtras() {
        Intent data = getIntent();
        final_lat = data.getDoubleExtra("lat", 25.042385);
        final_lng = data.getDoubleExtra("lng", 121.525241);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setBuildingsEnabled(true);
        map.setMyLocationEnabled(true);
    }
}
