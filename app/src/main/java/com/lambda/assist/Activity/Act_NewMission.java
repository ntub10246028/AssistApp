package com.lambda.assist.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.lambda.assist.Asyn.NewMission;
import com.lambda.assist.ConnectionApp.JsonReaderPost;
import com.lambda.assist.ConnectionApp.MyHttpClient;
import com.lambda.assist.ForImgur.ImageResponse;
import com.lambda.assist.Helper.BitmapHelp;
import com.lambda.assist.Helper.DocumentHelper;
import com.lambda.assist.Model.UploadImage;
import com.lambda.assist.Other.Hardware;
import com.lambda.assist.Helper.IntentHelper;
import com.lambda.assist.Other.IsVaild;
import com.lambda.assist.Other.MyDialog;
import com.lambda.assist.Other.Net;
import com.lambda.assist.Other.TaskCode;
import com.lambda.assist.Other.URLs;
import com.lambda.assist.Presenter.UploadImagePresenter;
import com.lambda.assist.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by v on 2015/12/20.
 */
public class Act_NewMission extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //
    private Context ctxt = Act_NewMission.this;
    private Activity activity = Act_NewMission.this;
    private MyHttpClient client;
    private Resources res;
    private JsonReaderPost jp;
    private static final int PICK_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    private static final int OPEN_MAP = 2;
    // UI
    private ImageView iv_camera, iv_map, iv_time, iv_send;
    private EditText et_title, et_content;
    // Other
    private List<String> list_bmp_base64;
    // select time dialog
    private Button bt_onlinetime, bt_runtime;
    // Google API用戶端物件
    private GoogleApiClient googleApiClient;
    // Location請求物件
    private LocationRequest locationRequest;
    // 記錄目前最新的位置
    private Location currentLocation;
    // 顯示目前與儲存位置的標記物件
    private Marker currentMarker, itemMarker;
    private double final_lat = 0.0;
    private double final_lng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmission);
        InitialSomething();
        InitialUI();
        InitialAction();

        // 建立Google API用戶端物件
        configGoogleApiClient();

        // 建立Location請求物件
        configLocationRequest();

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    private void NewMissionTask(String title, String content, String lon, String lat, String onlinetime, String runtime) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            NewMission task = new NewMission(new NewMission.OnNewMissionListener() {
                public void finish(Integer result, Integer missionid) {
                    pd.dismiss();
                    switch (result) {
                        case TaskCode.Success:
                            if (!list_bmp_base64.isEmpty()) {
                                UpLoadImageTask(missionid);
                            } else {
                                Toast.makeText(ctxt, "成功", Toast.LENGTH_SHORT).show();
                                finishActivity();
                            }
                            break;
                        case TaskCode.New_Mission_Fail:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_new_mission_fail), Toast.LENGTH_SHORT).show();
                            break;
                        case TaskCode.New_Mission_LackData:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_new_mission_lackdata), Toast.LENGTH_SHORT).show();
                            break;
                        case TaskCode.NoResponse:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });


            task.execute(title, content, lon, lat, onlinetime, runtime);
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void UpLoadImageTask(int missionid) {
        if (Net.isNetWork(ctxt)) {
            new UpLoadImageTask().execute(missionid);
        } else {
            Toast.makeText(Act_NewMission.this, res.getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }


    class UpLoadImageTask extends AsyncTask<Integer, Integer, Integer> {
        private int missionid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... datas) {
            int result = TaskCode.NoResponse;
            missionid = datas[0];
            Log.d("UpLoadImageTask", "Size = " + list_bmp_base64.size());
            for (String bmp64 : list_bmp_base64) {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("missionID", Integer.toString(missionid)));
                params.add(new BasicNameValuePair("imageString", bmp64));
                Log.d("UpLoadImageTask", missionid + "");
                Log.d("UpLoadImageTask", bmp64);
                try {
                    JSONObject jobj = jp.Reader(params, URLs.url_upload_image, client);
                    if (jobj == null)
                        return result;
                    result = jobj.getInt("result");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("UpLoadImageTask", e.toString());
                }
            }

            return result;
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case TaskCode.Success:
                    Toast.makeText(ctxt, "成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case TaskCode.Upload_image_NoThisMan:
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_upload_image_nothismane), Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.Upload_image_NoReplaceImage:
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_upload_image_noreplaceimage), Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.NoResponse:
                    Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private NumberPicker np_hour, np_min, np_sec;
    private int ol_hour = 24, ol_min = 0, ol_sec = 0;
    private int r_hour = 24, r_min = 0, r_sec = 0;
    private final int ONLINE = 0;
    private final int RUN = 1;

    private void TimeDialog(final View bt, final int mode) {
        AlertDialog.Builder b = new AlertDialog.Builder(ctxt, AlertDialog.THEME_HOLO_LIGHT);
        View v = getLayoutInflater().inflate(R.layout.ui_timepicker, null);
        np_hour = (NumberPicker) v.findViewById(R.id.np_hour);
        np_min = (NumberPicker) v.findViewById(R.id.np_min);
        np_sec = (NumberPicker) v.findViewById(R.id.np_sec);
        np_hour.setMaxValue(24);
        np_min.setMaxValue(59);
        np_sec.setMaxValue(59);
        np_hour.setValue(mode == ONLINE ? ol_hour : r_hour);
        np_min.setValue(mode == ONLINE ? ol_min : r_min);
        np_sec.setValue(mode == ONLINE ? ol_sec : r_sec);
        np_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldV, int newV) {
                // when hour equals 24 , min and sec change to 0 ;
                if (newV == 24) {
                    np_min.setValue(0);
                    np_sec.setValue(0);
                    if (mode == ONLINE) {
                        ol_min = 0;
                        ol_sec = 0;
                    } else {
                        r_min = 0;
                        r_sec = 0;
                    }
                }
                if (mode == ONLINE) {
                    ol_hour = newV;
                } else {
                    r_hour = newV;
                }
            }
        });
        np_min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldV, int newV) {
                if (mode == ONLINE) {
                    ol_min = newV;
                } else {
                    r_min = newV;
                }
            }
        });
        np_sec.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldV, int newV) {
                if (mode == ONLINE) {
                    ol_sec = newV;
                } else {
                    r_sec = newV;
                }
            }
        });
        b.setPositiveButton(getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String result = IsVaild.isVail_TimePick(ctxt, np_hour.getValue(), np_min.getValue(), np_sec.getValue());
                if (result.equals(getResources().getString(R.string.default_time))) {
                    if (mode == ONLINE) {
                        ol_hour = 24;
                        ol_min = 0;
                        ol_sec = 0;
                    } else {
                        r_hour = 24;
                        r_min = 0;
                        r_sec = 0;
                    }
                }

                ((Button) bt).setText(result);
            }
        });
        b.setView(v);
        b.show();
    }

    private void PickTimeDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(ctxt, AlertDialog.THEME_HOLO_LIGHT);
        View v = getLayoutInflater().inflate(R.layout.dialog_picktime, null);
        bt_onlinetime = (Button) v.findViewById(R.id.bt_picktime_onlinetime);
        bt_runtime = (Button) v.findViewById(R.id.bt_picktime_runtime);
        bt_onlinetime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimeDialog(v, ONLINE);
            }
        });
        bt_runtime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimeDialog(v, RUN);
            }
        });
        b.setPositiveButton(getResources().getString(R.string.button_ok), null);
        b.setView(v);
        b.show();
    }


    private void InitialAction() {
        iv_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Hardware.closeKeyBoard(ctxt, v);
//                final CharSequence[] items = {"相簿", "拍照"};
//                AlertDialog dlg = new AlertDialog.Builder(ctxt).setTitle("選擇照片").setItems(items,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == 0) {
//                                    Intent iPickPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                    startActivityForResult(iPickPicture, PICK_PICTURE);
//                                } else {
//                                    Intent iTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                    if (Hardware.isIntentAvailable(ctxt, iTakePicture)) {
//                                        startActivityForResult(iTakePicture, TAKE_PICTURE);
//                                    } else {
//                                        Toast.makeText(ctxt, "沒有拍攝裝置", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                            }
//                        }).create();
//                dlg.show();
                IntentHelper.chooseFileIntent(activity);
            }
        });
        iv_map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Hardware.closeKeyBoard(ctxt, v);
                Intent it = new Intent(ctxt, Act_SelectMap.class);
                it.putExtra("lat", final_lat);
                it.putExtra("lng", final_lng);
                Log.d("select", final_lat + " " + final_lng);
                startActivityForResult(it, OPEN_MAP);
            }
        });
        iv_time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Hardware.closeKeyBoard(ctxt, v);
                PickTimeDialog();
            }
        });

        iv_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Hardware.closeKeyBoard(ctxt, v);
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();
                String lon = Double.toString(final_lng);
                String lat = Double.toString(final_lat);
                String onlinetime;
                String runtime;
                if (bt_onlinetime != null && bt_runtime != null) {
                    onlinetime = bt_onlinetime.getText().toString();
                    runtime = bt_runtime.getText().toString();
                } else {
                    onlinetime = getResources().getString(R.string.default_time);
                    runtime = getResources().getString(R.string.default_time);
                }
                if (IsVaild.isVail_New_Mission(ctxt, title, content, lon, lat, onlinetime, runtime)) {
                    NewMissionTask(title, content, lon, lat, onlinetime, runtime);
                }
            }
        });
    }

    private void InitialUI() {
        iv_camera = (ImageView) findViewById(R.id.iv_newmission_camera);
        iv_map = (ImageView) findViewById(R.id.iv_newmission_map);
        iv_time = (ImageView) findViewById(R.id.iv_newmission_time);
        iv_send = (ImageView) findViewById(R.id.iv_newmission_send);

        et_title = (EditText) findViewById(R.id.et_newmission_title);
        et_content = (EditText) findViewById(R.id.et_newmission_content);
    }

    private void InitialSomething() {
        res = getResources();
        jp = new JsonReaderPost();
        client = MyHttpClient.getMyHttpClient();
        list_bmp_base64 = new ArrayList<>();
    }

    private UploadImage upload; // Upload object containging image and meta data
    private File chosenFile; //chosen file from intent

    private void createUpload(File image, Bitmap bitmap) {
        upload = new UploadImage();
        upload.image = image;
        upload.bitmap = bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IntentHelper.FILE_PICK:
                    try {
                        Uri returnUri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), returnUri);
                        Bitmap okBitmap = BitmapHelp.resize(bitmap);
                        String filePath = DocumentHelper.getPath(this, returnUri);
                        if (filePath == null || filePath.isEmpty() || okBitmap == null) return;
                        chosenFile = new File(filePath);
                        createUpload(chosenFile, okBitmap);
                        new UploadImagePresenter(this).execute(upload, new UiCallback());
                    } catch (IOException e) {
                    }
                    break;
//                case PICK_PICTURE:
//                    Uri uri = data.getData();
//                    String[] columns = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContentResolver().query(uri, columns, null, null, null);
//                    if (cursor.moveToFirst()) {
//                        String imagePath = cursor.getString(0);
//                        cursor.close();
//                        Bitmap mBitmap = BitmapFactory.decodeFile(imagePath);
//                        et_content.setText(titrans.putBitmapToText(mBitmap, et_content));
//                    }
//                    break;
//                case TAKE_PICTURE:
//                    Uri uri2 = data.getData();
//                    break;
                case OPEN_MAP:
                    double lat = data.getDoubleExtra("lat", 25.042385);
                    double lng = data.getDoubleExtra("lng", 121.525241);
                    final_lat = lat;
                    final_lng = lng;
                    Log.d("back get", final_lat + " " + final_lng);
                    break;
            }
        }
    }

    private class UiCallback implements Callback<ImageResponse> {
        // [assist:image=http.../abc.jpg]
        private final String format = "[assist:image=]";

        @Override
        public void success(ImageResponse imageResponse, Response response) {
            if (imageResponse != null) {
                StringBuilder sb = new StringBuilder(format);
                sb.insert(format.length() - 1, imageResponse.data.link);
                SpannableString ss = new SpannableString(sb);
                ss.setSpan(new ImageSpan(upload.bitmap), 0, sb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                et_content.getText().insert(et_content.getSelectionStart(), "\n");
                et_content.getText().insert(et_content.getSelectionStart(), ss);
                et_content.getText().insert(et_content.getSelectionStart(), "\n");
            } else {
                Toast.makeText(ctxt, "error", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void failure(RetrofitError error) {
            //Assume we have no connection, since error is null
            if (error == null) {
                Toast.makeText(ctxt, "No Network", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void finishActivity() {
        this.finish();
    }

    private synchronized void configGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // 建立Location請求物件
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為10秒（10000ms）
        locationRequest.setInterval(60000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(100);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Already connect to google service
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, Act_NewMission.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // google service disconnect , i is fail code
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // google service connect fail ,  connectionResult is fail result
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, "未安裝 Google play",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean F = true;

    @Override
    public void onLocationChanged(Location location) {
        // 位置改變
        // Location參數是目前的位置
        if (F) {
            currentLocation = location;
            final_lng = currentLocation.getLongitude();
            final_lat = currentLocation.getLatitude();
            Log.d("Pos-NewMission", final_lat + " " + final_lng);
            F = !F;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 連線到Google API用戶端
        if (!googleApiClient.isConnected() && currentMarker != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 移除位置請求服務
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 移除Google API用戶端連線
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
}
