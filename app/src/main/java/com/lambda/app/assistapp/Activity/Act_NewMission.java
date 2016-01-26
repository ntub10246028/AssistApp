package com.lambda.app.assistapp.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.apple.assistapp.R;
import com.lambda.app.assistapp.ConnectionApp.JsonReaderPost;
import com.lambda.app.assistapp.ConnectionApp.MyHttpClient;
import com.lambda.app.assistapp.Other.IsVail;
import com.lambda.app.assistapp.Other.Net;
import com.lambda.app.assistapp.Other.TaskCode;
import com.lambda.app.assistapp.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
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
    private static final int BYCAMERA = 1;
    private static final int BYPHOTO = 0;
    //
    private Button bt_issue;
    private EditText et_title, et_content;
    //
    private boolean[] imageIsEmpty = {true, true, true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuearticle);
        InitialSomething();
        InitialUI();
        InitialAction();
        if (client != null) {
            Toast.makeText(ctxt, "Y", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ctxt, "N", Toast.LENGTH_SHORT).show();
        }
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
            params.add(new BasicNameValuePair("locationID", "360.0"));
            try {
                JSONObject jobj = jp.Reader(params, URLs.url_New_Mission, client);
                if (jobj == null) return result;
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
                    Toast.makeText(ctxt, "Success", Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.New_Mission_Fail:
                    Toast.makeText(ctxt, "Fail", Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.New_Mission_LackData:
                    Toast.makeText(ctxt, "LackData", Toast.LENGTH_SHORT).show();
                    break;
                case TaskCode.NoResponse:
                    Toast.makeText(ctxt, "NoResponse", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void InitialSomething() {
        res = getResources();
        jp = new JsonReaderPost(ctxt);
        client = MyHttpClient.getMyHttpClient();
    }


    private void InitialUI() {
        bt_issue = (Button) findViewById(R.id.bt_ia_issue);
        et_title = (EditText) findViewById(R.id.et_ia_title);
        et_content = (EditText) findViewById(R.id.et_ia_content);
    }

    private void InitialAction() {
        bt_issue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();
                if (IsVail.isVail_New_Mission(ctxt, title, content)) {
                    NewMissionTask();
                }
            }
        });
    }

    View.OnClickListener ImageClick = new View.OnClickListener() {
        public void onClick(View v) {
            //img_upload_selected = (ImageView) v;
            final CharSequence[] items = {"相簿", "拍照"};
            AlertDialog dlg = new AlertDialog.Builder(ctxt).setTitle("選擇照片").setItems(items,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 1) {
                                Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                                startActivityForResult(getImageByCamera, BYCAMERA);
                            } else {
//                                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
//                                getImage.addCategory(Intent.CATEGORY_OPENABLE);
//                                getImage.setType("image/*");
//                                startActivityForResult(getImage, BYPHOTO);
                                Intent getImage = new Intent(Intent.ACTION_PICK);
                                getImage.setType("image/*");
                                Intent destIntent = Intent.createChooser(getImage, "選擇檔案");
                                startActivityForResult(destIntent, BYPHOTO);
                            }

                        }
                    }).create();
            dlg.show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver contentResolver = getContentResolver();
        Bitmap myBitmap = null;
        byte[] mContent;
        if (requestCode == BYPHOTO) {
            //方式一
            try {
                //獲得圖片的uri
                Uri orginalUri = data.getData();
                //將圖片?容解析成字節數組
                mContent = readStream(contentResolver.openInputStream(Uri.parse(orginalUri.toString())));
                //將字節數組轉換為ImageView可調用的Bitmap對象
                myBitmap = getPicFromBytes(mContent, null);
                ////把得到的圖片?定在控件上顯示
                //img_upload_selected.setImageBitmap(myBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            //方式二
//            try {
//                Uri selectedImage = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                Cursor cursor = getContentResolver().query(selectedImage,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String picturePath = cursor.getString(columnIndex);
//                cursor.close();
//                img.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else if (requestCode == BYCAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    Bundle extras = data.getExtras();
                    myBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    mContent = baos.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //img_upload_selected.setImageBitmap(myBitmap);
            }
        }

    }

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }


    public static byte[] readStream(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        while ((len = in.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        in.close();
        return data;
    }
}
