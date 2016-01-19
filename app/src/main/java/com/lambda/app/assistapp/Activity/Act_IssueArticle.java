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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.apple.assistapp.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by v on 2015/12/20.
 */
public class Act_IssueArticle extends AppCompatActivity {

    //
    private Context ctxt = Act_IssueArticle.this;
    private Resources res;
    private static final int BYCAMERA = 1;
    private static final int BYPHOTO = 0;
    //
    private Button bt_issue;
    private EditText et_title, et_content;
    private ImageView img_upload_1, img_upload_2, img_upload_3, img_upload_4, img_upload_selected;
    //
    private boolean[] imageIsEmpty = {true, true, true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuearticle);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void InitialSomething() {
        res = getResources();
    }


    private void InitialUI() {
        bt_issue = (Button) findViewById(R.id.bt_ia_issue);
        et_title = (EditText) findViewById(R.id.et_ia_title);
        et_content = (EditText) findViewById(R.id.et_ia_content);
        img_upload_1 = (ImageView) findViewById(R.id.img_upload_1);
        img_upload_2 = (ImageView) findViewById(R.id.img_upload_2);
        img_upload_3 = (ImageView) findViewById(R.id.img_upload_3);
        img_upload_4 = (ImageView) findViewById(R.id.img_upload_4);
        img_upload_selected = img_upload_1;
    }

    private void InitialAction() {
        bt_issue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        img_upload_1.setOnClickListener(ImageClick);
        img_upload_2.setOnClickListener(ImageClick);
        img_upload_3.setOnClickListener(ImageClick);
        img_upload_4.setOnClickListener(ImageClick);

        img_upload_1.setOnLongClickListener(ImageLongClick);
        img_upload_2.setOnLongClickListener(ImageLongClick);
        img_upload_3.setOnLongClickListener(ImageLongClick);
        img_upload_4.setOnLongClickListener(ImageLongClick);
    }

    View.OnClickListener ImageClick = new View.OnClickListener() {
        public void onClick(View v) {
            img_upload_selected = (ImageView) v;
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
    View.OnLongClickListener ImageLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View v) {
            img_upload_selected = (ImageView) v;
            // if no image
            if (img_upload_selected.getDrawable() == res.getDrawable(R.drawable.non_selected_background)) {
                return true;
            }
            new AlertDialog.Builder(ctxt).setMessage(res.getString(R.string.warning_removeimage)).setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    img_upload_selected.setImageResource(R.drawable.non_selected_background);
                }
            }).setNegativeButton("取消", null).show();

            return true;
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
                img_upload_selected.setImageBitmap(myBitmap);
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
                img_upload_selected.setImageBitmap(myBitmap);
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
