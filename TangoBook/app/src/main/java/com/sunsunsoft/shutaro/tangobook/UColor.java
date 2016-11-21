package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;
import java.util.Random;

/**
 *　カスタマイズしたColorクラス
 */

public class UColor extends Color {
    public static final String TAG = "UColor";

    /**
     * ランダムな色を取得
     * @return
     */
    public static int getRandomColor() {
        Random rand = new Random();
        return (0xff << 24) | (rand.nextInt(255) << 16) | (rand.nextInt(255) << 8) | (rand.nextInt(255));
    }

    /**
     * RGB -> YUV に変換
     */
    public static int RGBtoYUV(int rgb) {
        float R = (float)Color.red(rgb);
        float G = (float)Color.green(rgb);
        float B = (float)Color.blue(rgb);

        int Y  = (int)(0.257 * R + 0.504 * G + 0.098 * B + 16);
        int Cb = (int)(-0.148 * R - 0.291 * G + 0.439 * B + 128);
        int Cr = (int)(0.439 * R - 0.368 * G - 0.071 * B + 128);

        if (Y > 255) Y = 255;
        if (Cb > 255) Cb = 255;
        if (Cr > 255) Cr = 255;

        return Y << 16 | Cb << 8 | Cr;
    }

    /**
     * YUV -> RGB
     */
    public static int YUVtoRGB(int yuv) {
        float Y = (float)((yuv & 0xff0000) >> 16);
        float Cb = (float)((yuv & 0x00ff00) >> 8);
        float Cr = (float)(yuv & 0xff);

        int R = (int)(1.164 * (Y-16)                 + 1.596 * (Cr-128));
        int G = (int)(1.164 * (Y-16) - 0.391 * (Cb-128) - 0.813 * (Cr-128));
        int B = (int)(1.164 * (Y-16) + 2.018 * (Cb-128));

        if (R > 255) R = 255;
        if (R < 0) R = 0;
        if (G > 255) G = 255;
        if (G < 0) G = 0;
        if (B > 255) B = 255;
        if (B < 0) B = 0;

        return R << 16 | G << 8 | B;
    }

    /**
     * RGBの輝度を上げる
     * @param argb
     * @param addY  輝度 100% = 1.0 / 50% = 0.5
     * @return
     */
    public static int addBrightness(int argb, float addY) {
        ULog.print(TAG, String.format("RGB:%06x", argb));

        int yuv = RGBtoYUV(argb);

        ULog.print(TAG, String.format("YUV:%06x", yuv));

        int Y = yuv >> 16;
        int Y2 = Y + (int)(addY * 255);
        if (Y2 > 255) Y2 = 255;
        else if (Y2 < 0) Y2 = 0;

        int _argb = (argb & 0xff000000) | YUVtoRGB( (Y2 << 16) | (yuv & 0x00ffff));

        ULog.print(TAG, String.format("RGB2:%06x", _argb));

        return _argb;
    }
}
