package com.lambda.assist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.Item.MissionData;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;
import com.lambda.assist.Picture.BitmapTransformer;
import com.lambda.assist.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v on 2015/12/19.
 */
public class AroundRVAdapter extends SampleRecyclerViewAdapter {
    private List<MissionData> list;

    public AroundRVAdapter(Context context, List<MissionData> list) {
        super(context);
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_around, null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, int position) {
        // Get Your Holder
        ViewHolder holder = (ViewHolder) vholder;
        final MissionData item = list.get(position);
        // setTextSize
        holder.title.setText(item.getTitle());
        if (item.getImage() != null) {
            Log.d("AroundRVAdapter", item.getImage());
            if (holder.image != null) {
                LoadImageTask(holder.image, Integer.toString(item.getMissionid()), "1");
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(getContext(), Act_Mission.class);
                it.putExtra("missionid", item.getMissionid());
                getContext().startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView title;
        public ImageView image;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            title = (TextView) itemLayoutView.findViewById(R.id.tv_item_rv_title);
            image = (ImageView) itemLayoutView.findViewById(R.id.img_item_rv_image);
        }

        @Override
        public void onClick(View view) {
        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }

    private void LoadImageTask(ImageView imageView, String missionid, String which) {
        if (Net.isNetWork(getContext())) {
            new LoadImageTask(imageView).execute(missionid, which);
        } else {
            Toast.makeText(getContext(), getContext().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class LoadImageTask extends AsyncTask<String, Integer, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public LoadImageTask(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
        }

        protected Bitmap doInBackground(String... params) {
            return downLoadImage(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
//                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
//                        imageView.setImageDrawable(placeholder);
                    }
                }
            }

        }
    }

    private Bitmap downLoadImage(String missionid, String which) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("missionID", missionid));
        params.add(new BasicNameValuePair("imageFileID", which));
        try {
            JsonReaderPost jp = new JsonReaderPost();
            JSONObject jobj = jp.Reader(params, URLs.url_download_image, MyHttpClient.getMyHttpClient());
            if (jobj == null)
                return null;
            Log.d("LoadImageTask", jobj.toString());
            int result = jobj.getInt("result");
            if (result == TaskCode.Success) {
                JSONArray jarray = jobj.getJSONArray("imagefile");
                StringBuilder base64 = new StringBuilder();
                for (int i = 0; i < jarray.length(); i++) {
                    base64.append(jarray.getString(i));
                }
                Log.d("LoadImageTask", base64.toString());
                return BitmapTransformer.Base64ToBitmap(base64.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoadImageTask", e.toString());
        }
        return null;
    }
}