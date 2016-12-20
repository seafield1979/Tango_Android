package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;
import java.util.Random;

/**
 *　カスタマイズしたColorクラス
 */

public class UColor extends Color {
    public static final String TAG = "UColor";
    public static final int Orange = 0xFFFFA500;
    public static final int Aqua = 0xFF00FFFF;
    public static final int AquaMarine = 0xFF7FFFD4;
    public static final int Beige = 0xFFF5F5DC;
    public static final int Blue = 0xFF0000FF;
    public static final int Brown = 0xFFA52A2A;
    public static final int Chocolate = 0xFFD2691E;
    public static final int Coral = 0xFFFF7F50;
    public static final int Cyan = 0xFF00FFFF;
    public static final int DarkBlue = 0xFF00008B;
    public static final int Darkcyan = 0xFF008B8B;
    public static final int DarkGray = 0xFFA9A9A9;
    public static final int DarkGreen = 0xFF006400;
    public static final int DarkOrange = 0xFFFF8C00;
    public static final int DarkRed = 0xFF8B0000;
    public static final int DarkYellow = 0xFF8B0000;
    public static final int DarkViolet = 0xFF9400D3;
    public static final int DeepPink = 0xFF1493;
    public static final int Gold = 0xFFFFD700;
    public static final int Gray = 0xFF808080;
    public static final int Green = 0xFF008000;
    public static final int GreenYellow = 0xFF746508;
    public static final int HotPink = 0xFFFF69B4;
    public static final int Indigo = 0xFF4B0082;
    public static final int LightBlue = 0xFFADD8E6;
    public static final int LightCyan = 0xFFE0FFFF;
    public static final int LightGreen = 0xFF90EE90;
    public static final int LightGray = 0xFFD3D3D3;
    public static final int LightPink = 0xFFFFB6C1;
    public static final int LightRed = 0xFFEE9090;
    public static final int LightSalmon = 0xFFFFA07A;
    public static final int LightSkyBlue = 0xFF87CEFA;
    public static final int LightYellow = 0xFFFFFFE0;
    public static final int Lime = 0xFF00FF00;
    public static final int LimeGreen = 0xFF32CD32;
    public static final int Magenta = 0xFFFF00FF;
    public static final int Maroon = 0xFF800000;
    public static final int MidnightBlue = 0xFF191970;
    public static final int Navy = 0xFF000080;
    public static final int Olive = 0xFF808000;
    public static final int OrangeRed = 0xFFFF4500;
    public static final int Purple = 0xFF800080;
    public static final int Red = 0xFFFF0000;
    public static final int Salmon = 0xFFFA8072;
    public static final int SeaGreen = 0xFF2E8B57;
    public static final int SeaShell = 0xFFFFF5EE;
    public static final int Silver = 0xFFC0C0C0;
    public static final int SkyBlue = 0xFF87CEEB;
    public static final int Snow = 0xFFFFFAFA;
    public static final int Tomato = 0xFFFF6347;
    public static final int Violet = 0xFFEE82EE;
    public static final int Wheat = 0xFFF5DEB3;
    public static final int White = 0xFFFFFFFF;
    public static final int Yellow = 0xFFFFFF00;
    public static final int YellowGreen = 0xFF9ACD32;


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

    /**
     * 2つの色を指定の割合で合成する
     * @param color1
     * @param color2
     * @param ratio  合成比率 0.0 : color1=100%, colo2=0%
     *                      1.0 : color1=0%, color2=100%
     * @return
     */
    public static int mixRGBColor(int color1, int color2, float ratio) {
        int a = (int)(((color1 & 0xff000000) >> 24) * (1.0f - ratio) +
                ((color2 & 0xff000000) >> 24) * ratio);
        if (a > 255) a = 255;
        int r = (int)(((color1 & 0xff0000) >> 16) * (1.0f - ratio) +
                ((color2 & 0xff0000) >> 16) * ratio);
        if (r > 255) r = 255;
        int g = (int)(((color1 & 0xff00) >> 8) * (1.0f - ratio) +
                ((color2 & 0xff00) >> 8) * ratio);
        if (g > 255) g = 255;
        int b = (int)((color1 & 0xff) * (1.0f - ratio) +
                (color2 & 0xff) * ratio);
        if (b > 255) b = 255;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
