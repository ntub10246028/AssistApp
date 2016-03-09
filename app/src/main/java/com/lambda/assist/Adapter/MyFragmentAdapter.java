package com.lambda.assist.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.lambda.assist.Fragment.Frg_AroundMission;
import com.lambda.assist.Fragment.Frg_Processing;

import java.util.List;


public class MyFragmentAdapter extends FragmentPagerAdapter {

    private List<String> Titles;
    private List<Integer> Icons;

    public MyFragmentAdapter(FragmentManager fm, List<String> titles, List<Integer> icons) {
        super(fm);
        this.Titles = titles;
        this.Icons = icons;
    }

    public int getCount() {
        return 2;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return Frg_AroundMission.newInstance();
            case 1:
                return Frg_Processing.newInstance();
            default:
                return null;
        }
    }

    public CharSequence getPageTitle(int position) {
        return Titles.get(position);
    }

    public int getPageIcon(int position) {
        return Icons.get(position);
    }
}
