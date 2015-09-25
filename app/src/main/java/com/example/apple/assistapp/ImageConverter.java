package com.example.apple.assistapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lambda.tw on 2015/9/25.
 */
public class ImageConverter {
    private String convertSting;

    public ImageConverter(String convertSting){
        this.setConvertSting(convertSting);
    }

    public Bitmap stringToBitmap(){
        byte[] decodedString = Base64.decode(this.getConvertSting(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    public String pathToString() {
        try {
            InputStream inputStream = new FileInputStream(this.convertSting);//You can get an inputStream using any IO API
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
            return encodedString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getConvertSting() {
        return convertSting;
    }

    public void setConvertSting(String convertSting) {
        this.convertSting = convertSting;
    }
}
