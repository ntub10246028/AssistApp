package com.lambda.assist.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by asus on 2016/3/26.
 */
public class ImageClickableSpan extends ClickableSpan {
    private String url;

    public ImageClickableSpan(String url) {
        this.url = url;
    }

    @Override

    public void onClick(View widget) {
        TextView tv = (TextView) widget;
        Picasso.with(tv.getContext()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public void updateDrawState(TextPaint ds) {
        // Customize your Text Look if required
        ds.setColor(Color.YELLOW);
        ds.setFakeBoldText(true);
        ds.setStrikeThruText(true);
        ds.setTypeface(Typeface.SERIF);
        ds.setUnderlineText(true);
        ds.setShadowLayer(10, 1, 1, Color.WHITE);
        ds.setTextSize(15);
    }
}
