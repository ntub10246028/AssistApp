package com.lambda.assist.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

/**
 * Created by asus on 2016/2/3.
 */
public class ImageTextHelp {

    public static void convertUrlToButton(String content, LinearLayout ll) {
        if (ll == null)
            return;
        ll.removeAllViews();
        Context context = ll.getContext();
        String start = ImgurHelper.start;
        String end = ImgurHelper.end;
        int count = 0;
        while (count < content.length()) {
            int iStart = content.indexOf(start, count);
            int iEnd = content.indexOf(end, count);
            if (iStart != -1 && iEnd != -1) {
                ll.addView(getTextView(context, content.substring(count, iStart)));
                ll.addView(getLoadImageView(context, content.substring(iStart + start.length(), iEnd)));
                count = iEnd + 1;
            } else {
                ll.addView(getTextView(context, content.substring(count)));
                break;
            }
        }
    }

    private static TextView getTextView(Context context, String text) {
        TextView tv = new TextView(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setTextSize(20);
        return tv;
    }

    private static LinearLayout getLoadImageView(final Context context, final String url) {
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        final Button bt = new Button(context);
        bt.setLayoutParams(params);
        final ImageView iv = new ImageView(context);
        iv.setLayoutParams(params);
        bt.setText("載入圖片");
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Picasso.with(context)
                        .load(url)
                        .transform(new BitmapTransform(BitmapHelp.maxW, BitmapHelp.maxH))
                        .resize(BitmapHelp.size(), BitmapHelp.size())
                        .centerInside()
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                if (bitmap != null) {
                                    iv.setTag(bitmap);
                                    iv.setImageBitmap(bitmap);
                                    bt.setVisibility(View.GONE);
                                    iv.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                bt.setText("載入失敗:(");
                                bt.setEnabled(true);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                bt.setText("載入中...");
                                bt.setEnabled(false);
                            }
                        });
            }
        });
        layout.addView(bt);
        layout.addView(iv);
        return layout;
    }
}
