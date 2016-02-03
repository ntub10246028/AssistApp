package com.lambda.app.assistapp.Activity;

import android.app.AlertDialog;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lambda.app.assistapp.ConnectionApp.JsonReaderPost;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by v on 2015/12/20.
 */
public class Act_NewMission extends AppCompatActivity {

    private Context ctxt = Act_NewMission.this;
    private MyHttpClient client;
    private Resources res;
    private JsonReaderPost jp;
    private TextImageTransformer titrans;
    private static final int PICK_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    //
    private ImageView iv_camera, iv_map, iv_time, iv_send;
    private EditText et_title, et_content;
    //
    private List<String> list_bmp_base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmission);
        InitialSomething();
        InitialUI();
        InitialAction();
//        if (client != null) {
//            Toast.makeText(ctxt, "Y", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(ctxt, "N", Toast.LENGTH_SHORT).show();
//        }
    }

    private void NewMissionTask() {
        if (Net.isNetWork(ctxt)) {
            new NewMissionTask().execute();
        } else {
            Toast.makeText(Act_NewMission.this, res.getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    class NewMissionTask extends AsyncTask<String, Integer, Integer> {
        private String title;
        private String content;
        private int missionid;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            title = et_title.getText().toString();
            content = et_content.getText().toString();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int result = TaskCode.NoResponse;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("title", title));
            params.add(new BasicNameValuePair("content", content));
            params.add(new BasicNameValuePair("locationID", "1"));
            params.add(new BasicNameValuePair("locationX", "121.5256661"));
            params.add(new BasicNameValuePair("locationY", "25.0421488"));
            params.add(new BasicNameValuePair("onlineLimitTime", "10:00:00"));
            params.add(new BasicNameValuePair("runLimitTime", "2:00:00"));

            try {
                JSONObject jobj = jp.Reader(params, URLs.url_New_Mission, client);
                if (jobj == null) return result;
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
                    UpLoadImageTask(missionid);
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
                params.add(new BasicNameValuePair("missionid", Integer.toString(missionid)));
                params.add(new BasicNameValuePair("imageString", bmp64));
                try {
                    JSONObject jobj = jp.Reader(params, URLs.url_upload_image, client);
                    if (jobj == null) return result;
                    result = jobj.getInt("result");
                    if (result == TaskCode.Success) {
                        missionid = jobj.getInt("missionid");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("UpLoadImageTask", e.toString());
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case TaskCode.Success:
                    UpLoadImageTask(missionid);
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


    private boolean isIntentAvailable(Intent intent) {
        PackageManager manager = getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    private void InitialAction() {
        iv_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final CharSequence[] items = {"相簿", "拍照"};
                AlertDialog dlg = new AlertDialog.Builder(ctxt).setTitle("選擇照片").setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent iPickPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(iPickPicture, PICK_PICTURE);
                                } else {
                                    Intent iTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (isIntentAvailable(iTakePicture)) {
                                        startActivityForResult(iTakePicture, TAKE_PICTURE);
                                    } else {
                                        Toast.makeText(ctxt, "No Device", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        }).create();
                dlg.show();
            }
        });
        iv_map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        iv_time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        iv_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();
                if (IsVail.isVail_New_Mission(ctxt, title, content)) {
                    convertBitmapToBase64();
                    NewMissionTask();
                }
                //Toast.makeText(ctxt, content, Toast.LENGTH_SHORT).show();
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
