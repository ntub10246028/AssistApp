package com.lambda.assist.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.lambda.assist.Fragment.AroundFragment;
import com.lambda.assist.Fragment.MainBaseFragment;
import com.lambda.assist.Fragment.ProcessingFragment;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Other.ActivityCode;
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
    private MyFragmentAdapter fragmentAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private List<MainBaseFragment> fragments;
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
    private List<Mission> list_historymission_tmp;
    //


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitialSomething();
        InitialWindowInfo();
        InitialDrawerLayout();
        InitialToolBar();
        InitialUI();
        InitialAction();
    }

    private void InitialSomething() {
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
        et_drawer_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence key, int start, int before, int count) {
                List<Mission> newlist = new ArrayList<>();
                for(Mission mission:list_historymission_tmp){
                    if(mission.getTitle().contains(key))
                        newlist.add(mission);
                }
                list_historymission.clear();
                list_historymission.addAll(newlist);
                refreshHistory();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                            if(list_historymission_tmp==null){
                                list_historymission_tmp = new ArrayList<>();
                            }else{
                                list_historymission_tmp.clear();
                            }
                            list_historymission_tmp.addAll(list);

                            list_historymission.clear();
                            list_historymission.addAll(list_historymission_tmp);
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

    private void InitialUI() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtab);
    }

    private void InitialAction() {

        fragments = getFragments();
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(fragmentAdapter);
        mSlidingTabLayout.setCustomTabView(R.layout.tabview, R.id.tv_tab_icon,
                R.id.tv_tab_title);

        mSlidingTabLayout.setViewPager(mViewPager);
        int trans = getResources().getColor(R.color.trans);
        mSlidingTabLayout.setSelectedIndicatorColors(trans);
    }

    private List<MainBaseFragment> getFragments() {
        int indicatorColor = Color.TRANSPARENT;
        int dividerColor = Color.TRANSPARENT;
        List<MainBaseFragment> list = new ArrayList<>();
        list.add(AroundFragment.newInstance("附近任務", R.drawable.tab_image_neartask_selector, indicatorColor, dividerColor));
        list.add(ProcessingFragment.newInstance("進行中", R.drawable.tab_image_processing_selector, indicatorColor, dividerColor));
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ActivityCode.NewMission) {
                if(fragments!=null && fragments.size() == 2 ){
                    ProcessingFragment processingFragment = (ProcessingFragment) fragments.get(1);
                    processingFragment.refresh();
                }
            }
        }
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
