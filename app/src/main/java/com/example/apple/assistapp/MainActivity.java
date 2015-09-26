package com.example.apple.assistapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Context;
import android.telephony.TelephonyManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    private String image;
    private String session;
    private Context ctx;
    private MyHttpClient client;

    private Handler mHandler = new Handler() {
        // 重寫handleMessage()方法，此方法在UI線程運行
        @Override
        public void handleMessage(Message msg) {

            final ImageView imgView = (ImageView) findViewById(R.id.imgView);
            switch (msg.what) {
                // 如果成功，則顯示從網络獲取到的圖片
                case 0:

                    Log.d("setImage", "start");
                    imgView.setImageBitmap(new ImageConverter((String)msg.obj).stringToBitmap());
                    Log.d("setImage", "over");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TextView t=(TextView)findViewById(R.id.mytextview);

        final Context ctx = this.getApplicationContext();
        final MyHttpClient client = new MyHttpClient(ctx);
        TelephonyManager tM = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        final String imei = tM.getDeviceId();

        this.setCtx(ctx);
        this.setClient(client);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SignatureApp sa = new SignatureApp(ctx, R.raw.sign);

                while (!sa.isSuccess()) {
                    session = sa.postSignature(imei, client);
                }

                try {
                    //HttpClient client =new MyHttpClient(ctx);
                    HttpGet hg = new HttpGet("https://app.lambda.tw/session");
                    hg.setHeader("lack.session", session);
                    Log.d(session, hg.getFirstHeader("lack.session").toString());
                    HttpResponse response = client.execute(hg);
                    HttpEntity entity = response.getEntity();
                    Log.d("xxxxxxxxxxxxxx", EntityUtils.toString(entity));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Log.d("over",imei);
            }
        }).start();
/*
        new Thread(new Runnable(){
            @Override
            public void run() {
                JsonReaderPost j = new JsonReaderPost(getApplicationContext());
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("query","SELECT+AlertId+FROM+Orion.Alerts"));
                    j.Reader(params);
                    Log.d("DEBUG", "reader");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

*/
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void uploadImageToServer(View view) {
        Log.d("now_Image_String", this.getImage());
        final String uploadImage = this.getImage();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("imageString", uploadImage));
                    new JsonReaderPost(ctx).Reader(params, "upload", client);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void downloadImageFromServer(View view) {
        this.setImage("");
        Log.d("reset_Image", this.getImage());
        final StringBuffer s = new StringBuffer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //HttpClient client =new MyHttpClient(ctx);
                    HttpGet hg = new HttpGet("https://app.lambda.tw/download");
                    hg.setHeader("lack.session", session);
                    Log.d(session, hg.getFirstHeader("lack.session").toString());
                    HttpResponse response = client.execute(hg);
                    HttpEntity entity = response.getEntity();
                    String imgString =EntityUtils.toString(entity);
                    Log.d("downloadImage", imgString);
                    mHandler.obtainMessage(0,imgString).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        this.setImage(s.toString());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                Bitmap bm = BitmapFactory.decodeFile(imgDecodableString);
                ImageConverter IC = new ImageConverter(imgDecodableString);
                this.setImage(IC.pathToString());
                //imgView.setImageBitmap(new ImageConverter(IC.pathToString()).stringToBitmap());
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public void setClient(MyHttpClient client) {
        this.client = client;
    }
}

