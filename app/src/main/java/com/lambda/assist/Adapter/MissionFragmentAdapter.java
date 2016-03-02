package com.lambda.assist.Adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lambda.assist.Fragment.MissionBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/2/27.
 */
public class MissionFragmentAdapter extends FragmentPagerAdapter {

    List<MissionBaseFragment> fragments = null;

    public MissionFragmentAdapter(FragmentManager fm, List<MissionBaseFragment> fragments) {
        super(fm);
        if (fragments == null) {
            this.fragments = new ArrayList<>();
        }else{
            this.fragments = fragments;
        }
    }

    @Override
    public MissionBaseFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

}