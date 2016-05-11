package com.lambda.assist.Helper;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by asus on 2016/3/28.
 */
public class BitmapHelp {
    public static final int maxW = 1280;
    public static final int maxH = 800;

    public static int size() {
        return (int) Math.ceil(Math.sqrt(maxW * maxH));
    }

    public static Bitmap resize(Bitmap bmp) {
        if (bmp == null)
            return null;
        // old w , h
        int oldWidth = bmp.getWidth();
        int oldHeight = bmp.getHeight();
        // conform upload format
        if (oldWidth <= maxW && oldHeight <= maxH)
            return bmp;
        // new format
        int newWidth = oldWidth;
        int newHeight = oldHeight;
        do {
            newWidth *= 0.9;
            newHeight *= 0.9;
        } while (newWidth > maxW || newHeight > maxH);
        // scale %
        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;
        // scale matrix params
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bmp, 0, 0, oldWidth, oldHeight, matrix, true);
        return newbmp;
    }

    public static Bitmap resizeBig(Bitmap bmp) {
        if (bmp == null)
            return null;
        // old w , h
        int oldWidth = bmp.getWidth();
        int oldHeight = bmp.getHeight();
        // conform upload format
        if (oldWidth >= maxW && oldHeight >= maxH)
            return bmp;
        // new format
        int newWidth = oldWidth;
        int newHeight = oldHeight;
        while (newWidth < maxW || newHeight < maxH) {
            newWidth *= 1.05;
            newHeight *= 1.05;
        }
        // scale %
        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;
        // scale matrix params
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bmp, 0, 0, oldWidth, oldHeight, matrix, true);
        return newbmp;
    }
}
