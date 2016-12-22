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
enum ConvDateMode {
    Date,
    DateTime
}

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
     * 単色Bitmap画像の色を変更する
     */
    public static Bitmap convBitmapColor(Bitmap bmp, int newColor) {
        // グレースケール変換
        int height = bmp.getHeight();
        int width  = bmp.getWidth();
        int size   = height * width;
        int pix[]  = new int[size];
        int pos = 0;
        int _newColor = newColor & 0xffffff;

        bmp.getPixels(pix, 0, width, 0, 0, width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pix[pos];
                int alpha = pixel & 0xff000000;
                // 白はそのまま
                if ((pixel & 0xffffff) == 0xffffff) {
                    pix[pos] = pixel;
                } else {
                    pix[pos] = alpha | _newColor;
                }
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
    public static String convDateFormat(Date date, ConvDateMode mode) {
        if (date == null) return null;

        final DateFormat df;

        if (mode == ConvDateMode.Date) {
            df = new SimpleDateFormat(UResourceManager.getStringById(R
                    .string.date_format2));
        } else {
            df = new SimpleDateFormat(UResourceManager.getStringById(R
                    .string.datetime_format2));
        }
        return df.format(date);
    }

    /**
     * 表示するためのテキストに変換（改行なし、最大文字数制限）
     * @param text
     * @return
     */
    public static String convString(String text, boolean cutNewLine, int maxLength) {
        // 改行を除去
        String _text = text;
        if (cutNewLine) {
            _text = text.replace("\n", " ");
        }

        // 最大文字数制限
        if (maxLength > 0 && _text.length() > maxLength) {
            return _text.substring(0, maxLength - 1);
        }
        return _text;
    }
}
