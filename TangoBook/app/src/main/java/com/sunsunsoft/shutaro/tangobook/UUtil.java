package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.FloatMath;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shutaro on 2016/12/07.
 *
 * 便利関数
 */

public class UUtil {
    public static final double RAD = 3.1415 / 180.0;

    /**
     * sinテーブルの0->90度の 0.0~1.0 の値を取得する
     *
     * @param ratio  0.0 ~ 1.0
     * @return 0.0 ~ 1.0
     */
    public static float toAccel(float ratio) {
        return (float)Math.sin(ratio * 90 * RAD);
    }

    /**
     * 1.0 - cosテーブルの0->90度 の0.0~1.0の値を取得する
     * @param ratio
     * @return
     */
    public static float toDecel(float ratio) {
        return (float)(1.0 - Math.cos(ratio * 90 * RAD));
    }

    /**
     * Bitmapをグレースケール（灰色）に変換する
     * @param bmp
     * @return
     */
    public static Bitmap convToGrayBitmap(Bitmap bmp) {
        // グレースケール変換
        int height = bmp.getHeight();
        int width  = bmp.getWidth();
        int size   = height * width;
        int pix[]  = new int[size];
        int pos = 0;
        bmp.getPixels(pix, 0, width, 0, 0, width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pix[pos];
                int red   = (pixel & 0x00ff0000) >> 16;
                int green = (pixel & 0x0000ff00) >> 8;
                int blue  = (pixel & 0x000000ff);
                int alpha = (pixel & 0xff000000) >> 24;
                int gray  = (red + green + blue) / 3;
                pix[pos] = Color.argb(alpha, gray, gray, gray);
                pos++;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(pix, 0, width, width, height,
                Bitmap.Config.ARGB_8888);

        return newBmp;
    }

    /**
     * 日付(Date)のフォーマット変換
     * @param date
     * @return
     */
    public static String convDateFormat(Date date) {
        if (date == null) return null;
        final DateFormat df = new SimpleDateFormat(UResourceManager.getStringById(R
                .string.date_format2));
        return df.format(date);
    }
}
