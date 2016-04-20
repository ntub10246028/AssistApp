package com.lambda.assist.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.lambda.assist.Fragment.AroundFragment;
import com.lambda.assist.Fragment.MainBaseFragment;
import com.lambda.assist.Fragment.ProcessingFragment;

import java.util.ArrayList;
import java.util.List;


public class MyFragmentAdapter extends FragmentPagerAdapter {

    private  List<MainBaseFragment> fragments;

    public MyFragmentAdapter(FragmentManager fm, List<MainBaseFragment> fragments) {
        super(fm);
        if (fragments == null) {
            this.fragments = new ArrayList<>();
        }else{
            this.fragments = fragments;
        }
    }

    public int getCount() {
        return fragments.size();
    }

    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

    public int getPageIcon(int position) {
        return fragments.get(position).getIcon();
    }
}
