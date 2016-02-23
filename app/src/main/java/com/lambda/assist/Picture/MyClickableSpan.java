package com.lambda.assist.Picture;

import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

/**
 * Created by v on 2016/2/11.
 */
public abstract class MyClickableSpan extends ClickableSpan {
    private int missionid;
    private int num;

    public MyClickableSpan(int num, int missionid) {
        this.num = num;
        this.missionid = missionid;
    }

    public abstract void onClick(View widget) ;
}
