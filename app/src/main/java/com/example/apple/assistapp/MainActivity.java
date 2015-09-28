package com.example.apple.assistapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
    private static final int START_CAMERA = 0;
    private static final int START_RECORD = 1;
    private static final int START_LOCATION = 2;
    private static final int START_ALARM = 3;
    private static final int START_COLOR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickFunction(View view) {
        Intent intentMap = new Intent(this, MapsActivity.class);
        // 設定儲存的座標
        /*
        intentMap.putExtra("lat", 25.0);
        intentMap.putExtra("lng", 121.0);
        intentMap.putExtra("title", "title");
        intentMap.putExtra("datetime", "some data");
        */

        // 啟動地圖元件
        startActivityForResult(intentMap, START_LOCATION);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case START_LOCATION:
                    // 讀取與設定座標
                    double lat = data.getDoubleExtra("lat", 0.0);
                    double lng = data.getDoubleExtra("lng", 0.0);
                    Log.d(String.valueOf(lat),String.valueOf(lng));
                    break;
            }
        }
    }
}
