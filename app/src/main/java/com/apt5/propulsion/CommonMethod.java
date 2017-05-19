package com.apt5.propulsion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Van Quyen on 5/15/2017.
 */

public class CommonMethod {
    public static String convertToDate(long time) {
        Date date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
    public static final byte[] BitmaptoByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bm = bitmap;
        bitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);
        byte[] arrayBytes = byteArrayOutputStream.toByteArray();
        return arrayBytes;
    }
    public static final Bitmap ByteArraytoBimap(byte[] bytes)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bitmap;
    }

}
