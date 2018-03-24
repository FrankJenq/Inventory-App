package com.example.android.inventoryapp.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;

public class DbBitmapUtility {
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * 压缩图片尺寸
     *
     * @param bm 原图片
     * @return 压缩后的新图片
     */
   public static Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;
        while (true) {
            if (width / 2 < REQUIRED_SIZE
                    || height / 2 < REQUIRED_SIZE) {
                break;
            }
            width /= 2;
            height /= 2;
        }
        return Bitmap.createScaledBitmap(bm, width, height, false);
    }
}
