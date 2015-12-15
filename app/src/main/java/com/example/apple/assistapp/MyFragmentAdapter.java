package com.example.apple.assistapp;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.apple.assistapp.Fragment.Fragment1;
import com.example.apple.assistapp.Fragment.Fragment2;

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
                return Fragment1.newInstance(position);
            case 1:
                return Fragment2.newInstance(position);
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
