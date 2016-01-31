package com.lambda.app.assistapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lambda.app.assistapp.Adapter.LeftListAdapter;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.Other.ActivityCode;
import com.lambda.app.assistapp.Other.Item_History;
import com.lambda.app.assistapp.R;
import com.lambda.app.assistapp.UI.SlidingTabLayout;
import com.lambda.app.assistapp.Adapter.MyFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class Act_Main extends AppCompatActivity {

    private Context ctxt = Act_Main.this;
    private MyHttpClient client;
    private MyFragmentAdapter fragmentAdapter;
    private List<String> Titles;
    private List<Integer> Icons;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    // window
    private int draweropen_offset_left;
    private int draweropen_offset_right;
    private float perfectRate_left = 0.8f;
    private float perfectRate_right = 0.6875f;
    private int window_width;
    private int window_height;

    // Title Views
    private ImageView img_left;
    private ImageView img_right;
    private TextView tv_title;
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
        InitialSomething();
        InitialWindowInfo();
        InitialDrawerLayout();
        InitialToolBar();
        InitialTabView();
        InitialUI();
        InitialAction();
    }

    private void InitialSomething() {
        client = MyHttpClient.getMyHttpClient();
        if (client != null) {
            Toast.makeText(ctxt, "YA", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ctxt, "NO", Toast.LENGTH_SHORT).show();
        }
    }

    private void InitialWindowInfo() {
        window_width = getResources().getDisplayMetrics().widthPixels;
        window_height = getResources().getDisplayMetrics().heightPixels;
        draweropen_offset_left = (int) (window_width * perfectRate_left);
    }

    private void InitialDrawerLayout() {
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        drawer_left_layout = (LinearLayout) findViewById(R.id.drawer_left_layout);
        drawer_right_layout = (LinearLayout) findViewById(R.id.drawer_right_layout);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawer_left_layout
                .getLayoutParams();
        params.width = draweropen_offset_left;
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
                    main_layout.setX(draweropen_offset_left * f);
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
        List<Item_History> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Item_History item = new Item_History();
            item.setTitle("Title" + i);
            item.setDatetime("01/01");
            item.setStatus(i % 2 == 0 ? "0" : "1");
            list.add(item);
        }

        LeftListAdapter adapter_left = new LeftListAdapter(ctxt, list);
        lv_drawer_datas.setAdapter(adapter_left);

        imgbt_drawer_search.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String text = et_drawer_input.getText().toString();
                Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
            }
        });
        lv_drawer_datas.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

            }
        });
        return v;
    }

    private View getRightDrawerLayout() {
        View v = getLayoutInflater().inflate(R.layout.drawer_right, null);
        img_drawer_icon = (ImageView) v.findViewById(R.id.img_drawer_icon);
        tv_drawer_id = (TextView) v.findViewById(R.id.tv_drawer_id);
        lv_drawer_setting = (ListView) v.findViewById(R.id.lv_drawer_setting);

        String[] settings = {"Section1", "Section2", "Section3", "Section4",
                "Section5", "Section6", "Section7", "Section8", "Section9",
                "Section10", "Section11", "Section12", "Section13",
                "Section14", "Section15"};
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
        View v = getLayoutInflater().inflate(R.layout.toolbar, null);
        tv_title = (TextView) v.findViewById(R.id.tv_title);
        img_left = (ImageView) v.findViewById(R.id.img_left);
        img_right = (ImageView) v.findViewById(R.id.img_right);
        tv_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/JKG-L_3.ttf"));
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
        toolbar.addView(v);
    }

    private void InitialTabView() {
        Titles = new ArrayList<String>();
        Titles.add("附近任務");
        Titles.add("進行中");
        Icons = new ArrayList<Integer>();
        Icons.add(R.drawable.tab_image_neartask_selector);
        Icons.add(R.drawable.tab_image_processing_selector);
    }

    private void InitialUI() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtab);

    }

    private void InitialAction() {
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),
                Titles, Icons);
        mViewPager.setAdapter(fragmentAdapter);
        mSlidingTabLayout.setCustomTabView(R.layout.tabview, R.id.tv_tab_icon,
                R.id.tv_tab_title);

        mSlidingTabLayout.setViewPager(mViewPager);
        int red = getResources().getColor(R.color.red);
        int green = getResources().getColor(R.color.green);
        int trans = getResources().getColor(R.color.trans);
        //mSlidingTabLayout.setDividerColors(dark);
        //mSlidingTabLayout.setSelectedIndicatorColors(white,dark);
        mSlidingTabLayout.setSelectedIndicatorColors(trans);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivityCode.ADD:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("test");
                    Toast.makeText(ctxt, result, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private static long lastPressTime = 0;

    public void onBackPressed() {

        if (System.currentTimeMillis() - lastPressTime < 2000) {
            super.onBackPressed();
        } else {
            lastPressTime = System.currentTimeMillis();
            Toast.makeText(ctxt, getResources().getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
        }
    }
}
