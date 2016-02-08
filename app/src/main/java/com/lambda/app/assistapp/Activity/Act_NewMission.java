package com.lambda.app.assistapp.Activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lambda.app.assistapp.ConnectionApp.JsonReaderPost;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.Other.Hardware;
import com.lambda.app.assistapp.Other.IsVail;
import com.lambda.app.assistapp.Other.Net;
import com.lambda.app.assistapp.Other.TaskCode;
import com.lambda.app.assistapp.Other.URLs;
import com.lambda.app.assistapp.Picture.BitmapTransformer;
import com.lambda.app.assistapp.Picture.TextImageTransformer;
import com.lambda.app.assistapp.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by v on 2015/12/20.
 */
public class Act_NewMission extends AppCompatActivity {
    //
    private Context ctxt = Act_NewMission.this;
    private MyHttpClient client;
    private Resources res;
    private JsonReaderPost jp;
    private TextImageTransformer titrans;
    private static final int PICK_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    // UI
    private ImageView iv_camera, iv_map, iv_time, iv_send;
    private EditText et_title, et_content;
    // Other
    private List<String> list_bmp_base64;
    // select time dialog
    private Button bt_onlinetime, bt_runtime;
    // For get Lan Let
    private boolean getService = false;     //是否已開啟定位服務
    private LocationManager lms;
    private Location location;
    private String bestProvider = LocationManager.GPS_PROVIDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmission);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void NewMissionTask() {
        if (Net.isNetWork(ctxt)) {
            new NewMissionTask().execute();
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class NewMissionTask extends AsyncTask<String, Integer, Integer> {
        private String title;
        private String content;
        private String lon;
        private String lat;
        private String onlinetime;
        private String runtime;
        private int missionid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            title = et_title.getText().toString();
            content = et_content.getText().toString();
            Location location = getLocation();
            lon = Double.toString(location.getLongitude());
            lat = Double.toString(location.getLatitude());
            if (bt_onlinetime != null && bt_runtime != null) {
                onlinetime = bt_onlinetime.getText().toString();
                runtime = bt_runtime.getText().toString();
            } else {
                onlinetime = getResources().getString(R.string.default_time);
                runtime = getResources().getString(R.string.default_time);
            }
            Log.d("NewMissionTask", "online : " + onlinetime + " runtime : " + runtime);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int result = TaskCode.NoResponse;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("title", title));
            params.add(new BasicNameValuePair("content", content));
            params.add(new BasicNameValuePair("locationID", "1"));
            params.add(new BasicNameValuePair("locationX", lon));
            params.add(new BasicNameValuePair("locationY", lat));
            params.add(new BasicNameValuePair("onlineLimitTime", onlinetime));
            params.add(new BasicNameValuePair("runLimitTime", runtime));

            try {
                JSONObject jobj = jp.Reader(params, URLs.url_New_Mission, client);
                if (jobj == null)
                    return result;
                Log.d("NewMissionTask", jobj.toString());
                result = jobj.getInt("result");
                if (result == TaskCode.Success) {
                    missionid = jobj.getInt("missionid");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("NewMissionTask", e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case TaskCode.Success:
                    if (!list_bmp_base64.isEmpty()) {
                        UpLoadImageTask(missionid);
                    } else {
                        Toast.makeText(ctxt, "成功", Toast.LENGTH_SHORT).show();
                        finish();
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


    private void convertBitmapToBase64() {
        list_bmp_base64.clear();
        // in editText nums , <@@img-1> , <@@img-2> >> [1,2]
        List<Integer> numOfImage = titrans.getNums(et_content);
        // all bitmap from startActivity to now , 1,2,3,4,5,6,7,8
        List<Bitmap> list = titrans.getBmpList();
        for (Integer imgid : numOfImage) {
            Bitmap bmp = list.get(imgid - 1);
            list_bmp_base64.add(BitmapTransformer.BitmapToBase64(bmp));
            //Log.d("convertBitmapToBase64", "ID : " + imgid + " Base64 : " + list_base64.get(list_base64.size() - 1));
        }
    }

    private Location LocationServiceInitial() {
        lms = (LocationManager) ctxt.getSystemService(ctxt.LOCATION_SERVICE); //取得系統定位服務
        // 由Criteria物件判斷提供最準確的資訊
        Criteria criteria = new Criteria();  //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
        Location location = lms.getLastKnownLocation(bestProvider);
        return location;
    }

    private Location LocationSetting() {
        LocationManager status = (LocationManager) (ctxt.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            return LocationServiceInitial();
        } else {
            Toast.makeText(ctxt, "請開啟定位服務", Toast.LENGTH_LONG).show();
            getService = true; //確認開啟定位服務
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
            return null;
        }
    }

    private Location getLocation() {
        return LocationSetting();
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
                String result = IsVail.isVail_TimePick(ctxt, np_hour.getValue(), np_min.getValue(), np_sec.getValue());
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
                final CharSequence[] items = {"相簿", "拍照"};
                AlertDialog dlg = new AlertDialog.Builder(ctxt).setTitle("選擇照片").setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent iPickPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(iPickPicture, PICK_PICTURE);
                                } else {
                                    Intent iTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (Hardware.isIntentAvailable(ctxt, iTakePicture)) {
                                        startActivityForResult(iTakePicture, TAKE_PICTURE);
                                    } else {
                                        Toast.makeText(ctxt, "沒有拍攝裝置", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        }).create();
                dlg.show();
            }
        });
        iv_map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Hardware.closeKeyBoard(ctxt, v);
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
                if (IsVail.isVail_New_Mission(ctxt, title, content)) {
                    convertBitmapToBase64();
                    NewMissionTask();
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
        titrans = new TextImageTransformer(ctxt);
        client = MyHttpClient.getMyHttpClient();
        list_bmp_base64 = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_PICTURE:
                    Uri uri = data.getData();
                    String[] columns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, columns, null, null, null);
                    if (cursor.moveToFirst()) {
                        String imagePath = cursor.getString(0);
                        cursor.close();
                        Bitmap mBitmap = BitmapFactory.decodeFile(imagePath);
                        et_content.setText(titrans.putBitmapToText(mBitmap, et_content));
                    }
                    break;
                case TAKE_PICTURE:
                    Bitmap iBitmap = (Bitmap) data.getExtras().get("data");
                    et_content.setText(titrans.putBitmapToText(iBitmap, et_content));
                    break;
            }
        }
    }
}
