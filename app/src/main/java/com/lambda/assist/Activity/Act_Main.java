package com.lambda.assist.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.lambda.assist.Adapter.HistoryRVAdapter;
import com.lambda.assist.Adapter.MyFragmentAdapter;
import com.lambda.assist.Adapter.SettingsListAdapter;
import com.lambda.assist.Asyn.LoadHistory;
import com.lambda.assist.Asyn.LoadMissions;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Other.Code;
import com.lambda.assist.Other.MyDialog;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.R;
import com.lambda.assist.UI.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Arrays;
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
    private ListView lv_drawer_setting;
    // Left Drawer
    private EditText et_drawer_input;
    private ImageButton bt_search;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecycleview;
    private HistoryRVAdapter historyRVAdapter;
    private List<Mission> list_historymission;
    //


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
        list_historymission = new ArrayList<>();
    }

    private void InitialWindowInfo() {
        window_width = getResources().getDisplayMetrics().widthPixels;
        window_height = getResources().getDisplayMetrics().heightPixels;
        draweropen_offset_left = (int) (window_width * perfectRate_left);
    }

    private boolean F = true;

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
                if (F) {
                    if (v.getId() == R.id.drawer_left_layout) {
                        // refresh
                        LoadHistory();

                    }
                    F = !F;
                }
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
        // InitialUI
        View v = getLayoutInflater().inflate(R.layout.drawer_left, null);
        et_drawer_input = (EditText) v.findViewById(R.id.et_drawer_input);
        bt_search = (ImageButton) v.findViewById(R.id.imgbt_drawer_search);
        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.srfl_history);
        mRecycleview = (RecyclerView) v.findViewById(R.id.rv_history);
        //
        // SwipeRefreshLayout Setting
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //Loading
                LoadHistory();
                mSwipeLayout.setRefreshing(false);
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.black);
        //  RecyclerView Setting
        mRecycleview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeLayout.setEnabled(topRowVerticalPosition >= 0);
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
        //ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(10);
        //mRecycleview.addItemDecoration(itemDecoration);
        // 3. create an adapter
        historyRVAdapter = new HistoryRVAdapter(ctxt, list_historymission);
        // 4. set adapter
        mRecycleview.setAdapter(historyRVAdapter);
        // 5. set item animator to DefaultAnimator
        mRecycleview.setItemAnimator(new DefaultItemAnimator());
        //
        bt_search.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String text = et_drawer_input.getText().toString();
                Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
            }
        });


        return v;
    }

    private void refreshHistory() {
        if (historyRVAdapter != null) {
            historyRVAdapter.notifyDataSetChanged();
        }
    }

    private void LoadHistory() {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadHistory task = new LoadHistory(new LoadHistory.OnLoadHistoryListener() {
                public void finish(Integer result, List<Integer> list) {
                    pd.dismiss();
                    Log.d("LoadHistory", result + "");
                    switch (result) {
                        case TaskCode.Success:
                            if (!list.isEmpty()) {
                                LoadingMissionID(list);
                            }

                            break;
                        case TaskCode.Empty:
                            Toast.makeText(ctxt, "History is empty", Toast.LENGTH_SHORT).show();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute();
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadingMissionID(List<Integer> list) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadMissions task = new LoadMissions(new LoadMissions.OnLoadMissionsListener() {
                public void finish(Integer result, List<Mission> list) {
                    pd.dismiss();
                    switch (result) {
                        case TaskCode.Empty:
                        case TaskCode.Success:
                            list_historymission.clear();
                            list_historymission.addAll(list);
                            refreshHistory();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute(list);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private View getRightDrawerLayout() {
        View v = getLayoutInflater().inflate(R.layout.drawer_right, null);
        lv_drawer_setting = (ListView) v.findViewById(R.id.lv_drawer_setting);
        List<String> settings = Arrays.asList(getResources().getStringArray(R.array.settings));
        SettingsListAdapter adapter = new SettingsListAdapter(ctxt, settings);
        lv_drawer_setting.setAdapter(adapter);

        lv_drawer_setting.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                switch (position) {
                    case Code.SETTING:
                        break;
                }
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
        Titles = new ArrayList<>();
        Titles.add("附近任務");
        Titles.add("進行中");
        Icons = new ArrayList<>();
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
        int trans = getResources().getColor(R.color.trans);
        mSlidingTabLayout.setSelectedIndicatorColors(trans);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static long lastPressTime = 0;

    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT) || mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawers();
            return;
        }


        if (System.currentTimeMillis() - lastPressTime < 2000) {
            super.onBackPressed();
        } else {
            lastPressTime = System.currentTimeMillis();
            Toast.makeText(ctxt, getResources().getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
        }
    }
}
