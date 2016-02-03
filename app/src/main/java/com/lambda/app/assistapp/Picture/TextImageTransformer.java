package com.lambda.app.assistapp.Picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by asus on 2016/2/3.
 */
public class TextImageTransformer {
    private final Context context;

    private final String TAG = "<@@img-";
    private List<Bitmap> mBmpList;

    public TextImageTransformer(Context context) {
        this.context = context;
        mBmpList = new ArrayList<>();
    }

    public SpannableString putBitmapToText(Bitmap bmp, EditText et_content) {
        // let tag add num
        String num = Integer.toString(mBmpList.size() + 1);
        StringBuilder bTag = new StringBuilder("<@@img->");
        bTag.insert(bTag.length() - 1, num);
        // set Map
        //Log.d("TextImage", bTag.toString());
        mBmpList.add(bmp);
        // origan text
        StringBuilder ori_text_image = new StringBuilder(et_content.getText().toString());
        // get edittext float index
        int startIndex = et_content.getSelectionStart();
        ori_text_image.insert(startIndex, bTag);
        SpannableString new_text = new SpannableString(ori_text_image);
        int count = 0;
        while (count < ori_text_image.length()) {
            int index = ori_text_image.indexOf("<@@img-", count);
            if (index == -1)
                break;
            StringBuilder img_format = new StringBuilder(ori_text_image.substring(index, index + 8 + 1));
            if (img_format.length() == 9) {
                try {
                    // let tag add num
                    StringBuilder tag = new StringBuilder("<@@img->");
                    int numOfbmp = Integer.valueOf(img_format.substring(7, 8));
                    tag.insert(tag.length() - 1, img_format.substring(7, 8));
                    // get bmp
                    Bitmap bmpOfnum = mBmpList.get(numOfbmp - 1);
                    Log.d("TextImage", bmpOfnum != null ? "Y" : "N");
                    // set bmp in imageSpan
                    ImageSpan imageSpan = new ImageSpan(context, bmpOfnum, ImageSpan.ALIGN_BASELINE);
                    // setting ready
                    new_text.setSpan(imageSpan, index, index + tag.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
                    Log.d("TextImage", "set " + tag.toString() + index + "~" + (index + tag.length()));
                } catch (NumberFormatException ex) {
                }
            }
            count = index + 1;
        }
        return new_text;
    }
    public List<Integer> getNums(EditText et_content){
        // begin in 1 2 3 , get need -1
        List<Integer> result = new ArrayList<>();
        // origan text
        StringBuilder ori_text_image = new StringBuilder(et_content.getText().toString());
        // get edittext float index
        int count = 0;
        while (count < ori_text_image.length()) {
            int index = ori_text_image.indexOf("<@@img-", count);
            if (index == -1)
                break;
            StringBuilder img_format = new StringBuilder(ori_text_image.substring(index, index + 8 + 1));
            if (img_format.length() == 9) {
                try {
                    // let tag add num
                    StringBuilder tag = new StringBuilder("<@@img->");
                    int numOfbmp = Integer.valueOf(img_format.substring(7, 8));
                    result.add(numOfbmp);
                    Log.d("getNums", "img" + numOfbmp);
                } catch (NumberFormatException ex) {
                }
            }
            count = index + 1;
        }
        return result;
    }

    public List<Bitmap> getBmpList() {
        return mBmpList;
    }
}
