package com.lambda.assist.Presenter;

import android.app.ProgressDialog;
import android.content.Context;

import com.lambda.assist.ForImgur.Constants;
import com.lambda.assist.ForImgur.ImageResponse;
import com.lambda.assist.ForImgur.ImgurAPI;
import com.lambda.assist.Model.UploadImage;
import com.lambda.assist.Other.MyDialog;
import com.lambda.assist.Utils.NetworkUtils;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by asus on 2016/3/23.
 */
public class UploadImagePresenter {
    private WeakReference<Context> mContext;

    public UploadImagePresenter(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public void execute(UploadImage upload, Callback<ImageResponse> callback) {
        final Callback<ImageResponse> cb = callback;

        if (!NetworkUtils.isConnected(mContext.get())) {
            //Callback will be called, so we prevent a unnecessary notification
            cb.failure(null);
            return;
        }

        final ProgressDialog pd = MyDialog.getProgressDialog(mContext.get(), "上傳圖片...");

        RestAdapter restAdapter = buildRestAdapter();

        restAdapter.create(ImgurAPI.class).postImage(
                Constants.getClientAuth(),
                "",
                "",
                upload.albumId,
                null,
                new TypedFile("image/*", upload.image),
                new Callback<ImageResponse>() {
                    @Override
                    public void success(ImageResponse imageResponse, Response response) {
                        pd.dismiss();
                        if (cb != null)
                            cb.success(imageResponse, response);
                        if (response == null) {

                        }
                        if (imageResponse.success) {

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        pd.dismiss();
                        if (cb != null) cb.failure(error);
                    }
                });
    }

    private RestAdapter buildRestAdapter() {
        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setEndpoint(ImgurAPI.server)
                .build();

        /*
        Set rest adapter logging if we're already logging
        */
        if (Constants.LOGGING)
            imgurAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        return imgurAdapter;
    }
}
