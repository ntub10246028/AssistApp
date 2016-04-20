package com.lambda.assist.Fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;

/**
 * Created by asus on 2016/2/27.
 */
public class MainBaseFragment extends Fragment {

    private String title = "";
    private int icon;
    private int indicatorColor = Color.BLUE;
    private int dividerColor = Color.GRAY;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

}