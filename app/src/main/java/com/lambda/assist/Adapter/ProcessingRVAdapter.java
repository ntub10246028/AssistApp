package com.lambda.assist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.lambda.assist.Activity.Act_Main;
import com.lambda.assist.Activity.Act_Mission;
import com.lambda.assist.Helper.BitmapHelp;
import com.lambda.assist.Helper.BitmapTransform;
import com.lambda.assist.Helper.ImgurHelper;
import com.lambda.assist.Listener.OnLoadMoreListener;
import com.lambda.assist.Model.Mission;
import com.lambda.assist.Other.ActivityCode;
import com.lambda.assist.Other.Code;
import com.lambda.assist.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by v on 2015/12/19.
 */
public class ProcessingRVAdapter extends SampleRecyclerViewAdapter {
    private final Act_Main activity;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<Mission> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    public ProcessingRVAdapter(Context context, List<Mission> list, RecyclerView mRecyclerView) {
        super(context);
        this.activity = (Act_Main) context;
        this.list = list;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_processing, parent, false);
            return new MissionViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MissionViewHolder) {
            final Mission item = list.get(position);
            MissionViewHolder missionViewHolder = (MissionViewHolder) holder;
            missionViewHolder.text.setText(item.getTitle());
            String url = ImgurHelper.checkUrl(item.getContent());
            if (url != null) {
                Picasso.with(getContext()).cancelRequest(missionViewHolder.image);

                Picasso.with(getContext())
                        .load(url)
                        .fit()
                        .centerCrop()
                        .into(missionViewHolder.image);
            }
            missionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent it = new Intent(getContext(), Act_Mission.class);
                    it.putExtra("fromType", Code.FromType_Processing);
                    it.putExtra("msessionid", item.getMsessionid());
                    it.putExtra("me", item.getMe());
                    it.putExtra("missionid", item.getMissionid());
                    it.putExtra("title", item.getTitle());
                    activity.startActivityForResult(it, ActivityCode.Mission);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    // inner class to hold a reference to each item of RecyclerView
    static class MissionViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public ImageView image;

        public MissionViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            text = (TextView) itemLayoutView.findViewById(R.id.tv_item_processing_title);
            image = (ImageView) itemLayoutView.findViewById(R.id.tv_item_processing_image);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
}