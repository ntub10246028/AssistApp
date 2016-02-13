package com.lambda.app.assistapp.Picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lambda.app.assistapp.ConnectionApp.JsonReaderPost;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.Other.Net;
import com.lambda.app.assistapp.Other.TaskCode;
import com.lambda.app.assistapp.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

    public List<Integer> getNums(EditText et_content) {
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

    public void ConvertImage(final Context ctxt, final TextView tv, final int missionid, final String images) {
        if (!images.isEmpty()) {
            String[] sArray = images.split("[,;\\\\s]+");
            Integer[] iArray = new Integer[sArray.length];
            for (int i = 0; i < iArray.length; i++) {
                iArray[i] = Integer.valueOf(sArray[i]);
            }
            List<Integer> imageIDs = Arrays.asList(iArray);
            LoadImagesTask(ctxt, tv, missionid, imageIDs);
        } else {
        }

    }

    private void LoadImagesTask(Context ctxt, TextView tv, int missionid, List<Integer> imageIDs) {
        if (Net.isNetWork(ctxt)) {
            new LoadImagesTask(ctxt, tv, imageIDs, missionid).execute();
        }
    }

    class LoadImagesTask extends AsyncTask<String, Integer, String> {
        private final Context ctxt;
        private final WeakReference<TextView> textViewWeakReference;
        private List<Integer> imageIDs;
        private List<HashMap<String, Object>> imagesBase64;
        private int missionid;

        public LoadImagesTask(Context ctxt, TextView tv, List<Integer> imageIDs, int missionid) {
            this.ctxt = ctxt;
            this.textViewWeakReference = new WeakReference<>(tv);
            this.imageIDs = imageIDs;
            this.missionid = missionid;
            this.imagesBase64 = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagesBase64.clear();
        }

        protected String doInBackground(String... datas) {

            for (Integer imageid : imageIDs) {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("missionID", Integer.toString(missionid)));
                params.add(new BasicNameValuePair("imageFileID", Integer.toString(imageid)));
                try {
                    JsonReaderPost jp = new JsonReaderPost();
                    JSONObject jobj = jp.Reader(params, URLs.url_download_image, MyHttpClient.getMyHttpClient());
                    if (jobj == null)
                        return null;
                    Log.d("LoadImagesTask", jobj.toString());
                    int result = jobj.getInt("result");
                    if (result == TaskCode.Success) {
                        JSONArray jarray = jobj.getJSONArray("imagefile");
                        StringBuilder base64 = new StringBuilder();
                        for (int i = 0; i < jarray.length(); i++) {
                            base64.append(jarray.getString(i));
                        }
                        Log.d("LoadImagesTask", Integer.toString(imageid));
                        Log.d("LoadImagesTask", base64.toString().substring(0, 100));
                        HashMap<String, Object> h = new HashMap<>();
                        h.put("imageID", imageid);
                        h.put("imageBase64", base64.toString());
                        imagesBase64.add(h);
                    } else if (result == TaskCode.Load_image_NoExist) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("LoadImagesTask", e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (textViewWeakReference != null) {
                TextView tv = textViewWeakReference.get();
                if (tv != null) {
                    if (!imagesBase64.isEmpty()) {
                        StringBuilder sb = new StringBuilder(tv.getText().toString());
                        SpannableString sbs = new SpannableString(sb);
                        int count = 0;
                        while (count < sb.length()) {
                            int index = sb.indexOf("<@@img-", count);
                            if (index == -1) {
                                Log.d("LoadImagesTaskq", "Search stop");
                                break;
                            }

                            StringBuilder img_format = new StringBuilder(sb.substring(index, index + 8 + 1));
                            if (img_format.length() == 9) {
                                try {
                                    Integer num = Integer.valueOf(img_format.substring(7, 8));
                                    Log.d("LoadImagesTaskq", "<@@img-" + num + "> Start");
                                    for (HashMap<String, Object> h : imagesBase64) {
                                        Log.d("LoadImagesTaskq", h.get("imageID") + "");
                                        Log.d("LoadImagesTaskq", h.get("imageBase64").toString().substring(0, 10));
                                        if (num.equals(h.get("imageID"))) {
                                            Bitmap bitmap = BitmapTransformer.Base64ToBitmap(h.get("imageBase64").toString());
                                            if (bitmap != null) {
                                                Log.d("LoadImagesTaskq", "Bitmap not null");
                                                ImageSpan imageSpan = new ImageSpan(ctxt, bitmap, ImageSpan.ALIGN_BASELINE);
                                                Log.d("LoadImagesTaskq", "Set image between " + index + " ~ " + (index + 8 + 1));
                                                sbs.setSpan(imageSpan, index, index + 8 + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                            } else {
                                                Log.d("LoadImagesTaskq", "Bitmap null");
                                            }
                                            break;
                                        } else {
                                            Log.d("LoadImagesTaskq", "No Id");
                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                }
                            }
                            count = index + 1;
                        }
                        tv.setText(sbs);
                    }
                }
            }
        }
    }
}
