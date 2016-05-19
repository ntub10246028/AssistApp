package com.lambda.assist.Activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lambda.assist.R;

/**
 * Created by asus on 2016/3/9.
 */
public class Act_SelectMap extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private Button bt_ok, bt_cancel;
    private GoogleMap map;

    // 顯示目前與儲存位置的標記物件
    private Marker currentMarker;
    // other
    private double final_lat;
    private double final_lng;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectmap);
        Intent data = getIntent();
        final_lat = data.getDoubleExtra("lat", 0.0);
        final_lng = data.getDoubleExtra("lng", 0.0);
        setUpMap();
        initUI();
        initAction();
    }

    private void setUpMap() {
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fm_select_map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setBuildingsEnabled(true);
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                final_lat = point.latitude;
                final_lng = point.longitude;
                addMarker(point);
                moveMap(point);
            }
        });

        if (final_lat != 0.0 && final_lng != 0.0) {
            LatLng itemPlace = new LatLng(final_lat, final_lng);
            addMarker(itemPlace);
            moveMap(itemPlace);
        }

    }

    private void addMarker(LatLng place) {
//        BitmapDescriptor icon =
//                BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
        if (currentMarker != null)
            currentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place);
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

    private void initAction() {
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent();
                back.putExtra("lat", final_lat);
                back.putExtra("lng", final_lng);
                setResult(RESULT_OK, back);
                Log.d("back send", final_lat + " " + final_lng);
                finishActivity();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finishActivity();
            }
        });
    }

    private void initUI() {
        bt_ok = (Button) findViewById(R.id.bt_select_map_ok);
        bt_cancel = (Button) findViewById(R.id.bt_select_map_cancel);
    }

    private void finishActivity() {
        this.finish();
    }
}
