package com.example.apple.assistapp;

import android.support.v7.app.ActionBarActivity;


/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.assist.R;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    private Context ctxt = MainActivity.this;
    private MyFragmentAdapter fragmentAdapter;
    private List<String> Titles;
    private List<Integer> Icons;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    // window
    private int draweropen_offset;
    private float perfectRate = 0.6875f;
    private int window_width;
    private int window_height;

    // Title Views
    private ImageView img_left;
    private ImageView img_right;
    // Main Views
    private DrawerLayout mDrawerLayout;
    // Drawer Views
    private LinearLayout main_layout;
    private LinearLayout drawer_left_layout;
    private LinearLayout drawer_right_layout;
    // Right Drawer
    private ImageView img_drawer_icon;
    private TextView tv_drawer_id;
    private ListView lv_drawer_setting;
    // Left Drawer
    private EditText et_drawer_input;
    private ImageButton imgbt_drawer_search;
    private ListView lv_drawer_datas;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitialWindowInfo();
        InitialDrawerLayout();
        InitialToolBar();
        InitialTabView();
        InitialViews();
    }

    private void InitialWindowInfo() {
        window_width = getResources().getDisplayMetrics().widthPixels;
        window_height = getResources().getDisplayMetrics().heightPixels;

        draweropen_offset = (int) (window_width * perfectRate);

    }

    private void InitialDrawerLayout() {
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        drawer_left_layout = (LinearLayout) findViewById(R.id.drawer_left_layout);
        drawer_right_layout = (LinearLayout) findViewById(R.id.drawer_right_layout);

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawer_left_layout
                .getLayoutParams();
        params.width = draweropen_offset;
        drawer_left_layout.setLayoutParams(params);

        // drawer_left_layout.setId(0);
        // drawer_right_layout.setId(1);
        //
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new DrawerListener() {
            public void onDrawerClosed(View v) {
            }

            public void onDrawerOpened(View v) {
            }

            public void onDrawerSlide(View v, final float f) {
                int id = v.getId();
                if (id == R.id.drawer_left_layout) { // left
                    main_layout.setX(draweropen_offset * f);
                }
            }

            public void onDrawerStateChanged(int arg0) {

            }
        });

        // Left Drawer
        drawer_left_layout.addView(getLeftDrawerLayout());
        // Right Drawer
        drawer_right_layout.addView(getRightDrawerLayout());
        //
        mDrawerLayout.setScrimColor(getResources().getColor(R.color.trans));
        mDrawerLayout.setDrawerShadow(getResources().getDrawable(R.drawable.drawer_shadow), Gravity.RIGHT);

    }

    private View getLeftDrawerLayout() {
        View v = getLayoutInflater().inflate(R.layout.drawer_left, null);
        et_drawer_input = (EditText) v.findViewById(R.id.et_drawer_input);
        imgbt_drawer_search = (ImageButton) v
                .findViewById(R.id.imgbt_drawer_search);
        lv_drawer_datas = (ListView) v.findViewById(R.id.lv_drawer_datas);
        String[] datas = { "Data1", "Data2", "Data3", "Data4", "Data5",
                "Data6", "Data7", "Data8", "Data9", "Data10", "Data11",
                "Data12", "Data13", "Data14", "Data15" };
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, datas);
        lv_drawer_datas.setAdapter(adapter2);

        imgbt_drawer_search.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String text = et_drawer_input.getText().toString();
                Toast.makeText(ctxt, text,  Toast.LENGTH_SHORT).show();
            }
        });
        lv_drawer_datas.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String text = ((TextView) v).getText().toString();
                Toast.makeText(ctxt, text,  Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private View getRightDrawerLayout() {
        View v = getLayoutInflater().inflate(R.layout.drawer_right, null);
        img_drawer_icon = (ImageView) v.findViewById(R.id.img_drawer_icon);
        tv_drawer_id = (TextView) v.findViewById(R.id.tv_drawer_id);
        lv_drawer_setting = (ListView) v.findViewById(R.id.lv_drawer_setting);
        String[] settings = { "Section1", "Section2", "Section3", "Section4",
                "Section5", "Section6", "Section7", "Section8", "Section9",
                "Section10", "Section11", "Section12", "Section13",
                "Section14", "Section15" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, settings);
        lv_drawer_setting.setAdapter(adapter);

        lv_drawer_setting.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String text = ((TextView) v).getText().toString();
                Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void InitialToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View tView = getLayoutInflater().inflate(R.layout.toolbar, null);
        img_left = (ImageView) tView.findViewById(R.id.img_left);
        img_right = (ImageView) tView.findViewById(R.id.img_right);
        img_left.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        img_right.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
        });
        toolbar.addView(tView);
    }

    private void InitialTabView() {
        Titles = new ArrayList<String>();
        Titles.add("AAA");
        Titles.add("BBB");
        Icons = new ArrayList<Integer>();
        Icons.add(android.R.drawable.ic_menu_sort_by_size);
        Icons.add(android.R.drawable.ic_menu_sort_by_size);
    }

    private void InitialViews() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),
                Titles, Icons);
        mViewPager.setAdapter(fragmentAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtab);
        mSlidingTabLayout.setCustomTabView(R.layout.tabview, R.id.tv_tab_icon,
                R.id.tv_tab_title);

        mSlidingTabLayout.setViewPager(mViewPager);
        int dark = getResources().getColor(R.color.dark);
        int white = getResources().getColor(R.color.white);
        mSlidingTabLayout.setDividerColors(dark);
        // mSlidingTabLayout.setBackgroundColor(dark);
        mSlidingTabLayout.setSelectedIndicatorColors(white);
    }

}
