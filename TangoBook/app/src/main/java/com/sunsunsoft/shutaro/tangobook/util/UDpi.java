package com.sunsunsoft.shutaro.tangobook.util;

/**
 * Created by shutaro on 2017/06/22.
 */

import android.content.Context;

import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;

/**
 * Created by shutaro on 2017/06/22.
 *
 * 端末ごとのDPI(Dot per Inchi)を計算するクラス
 * 端末毎に1pixあたりのサイズ(DPI)が異なるので、全ての端末で同じピクセルで
 * 描画を行うと、高解像度端末では小さく、程解像度では大きく表示されてしまう。
 * この表示のズレを補正するために、描画前に補正値をかけてあげる。
 */

public class UDpi {
    /**
     * Enums
     */
    public enum Scale{
        None(0.0f),
        S50(0.5f),
        S67(0.67f),
        S75(0.75f),
        S80(0.80f),
        S90(0.90f),
        S100(1.00f),
        S110(1.10f),
        S125(1.25f),
        S150(1.50f),
        S175(1.75f),
        S200(2.00f),
        S250(2.50f),
        S300(3.00f);

        private final float scale;

        Scale(final float scale) {
            this.scale = scale;
        }
        // floatのスケールを返す
        public float getScale() {
            return this.scale;
        }
        // スケールアップ
        public Scale scaleUp() {
            if (ordinal() < values().length - 1) {
                return toEnum(ordinal() + 1);
            }
            return this;
        }

        // スケールダウン
        public Scale scaleDown() {
            if (ordinal() > Scale.S50.ordinal()) {
                return toEnum(ordinal() - 1);
            }
            return this;
        }

        // int を enumに変換する
        public static Scale toEnum(int val) {
            if (val >= values().length) return S100;
            return values()[val];
        }
    }


    /**
     * Constants
     */
    public static final float BASE_DPI = 160.f;       // ベースのdpiは mdpi(160dpi)

    /**
     * Variables
     */
    // dpi補正値
    // プログラムないの座標にこの補正値をかけて求めた座標に描画を行う
    public static float dpiToPixel;
    public static float dpiToPixelBase;
    public static Scale mScale;        // スケール種類

    /**
     * Methods
     */
    public static void init(Context context) {
        // リソースから取得する (要 Context)
        float dpi = context.getResources().getDisplayMetrics().densityDpi;
//        if (dpi <= 240) {
//            dpi = 240;
//        }

        dpiToPixelBase = dpi / BASE_DPI;    // 例: 480 / 160 = 3.0f

        // スケールが設定されていたら読み込む
        int scaleInt = MySharedPref.getInstance().readInt(MySharedPref.ScaleKey);
        if (scaleInt != 0) {
            mScale = Scale.toEnum(scaleInt);
            dpiToPixel = dpiToPixelBase * mScale.getScale();
        } else {
            mScale = Scale.S100;
        }
    }

    /**
     * スケールを変更
     * @param scale
     */
    public static void setScale(Scale scale) {
        if (!scale.equals(mScale)) {
            mScale = scale;

            // DPI補正値を再計算
            dpiToPixel = dpiToPixelBase * mScale.getScale();
            // 保存
            MySharedPref.getInstance().writeInt(MySharedPref.ScaleKey, scale.ordinal());
        }
    }

    public static void scaleUp() {
        setScale(mScale.scaleUp());
    }

    public static void scaleDown() {
        setScale(mScale.scaleDown());
    }

    public static String getScaleText() {
        return String.format("Zoom %03d", (int)(mScale.getScale() * 100)) + "%";
    }

    /**
     * DPI座標をピクセル座標に変換する
     * @param dpi
     * @return
     */
    public static int toPixel(int dpi) {
        return (int)(dpi * dpiToPixel);
    }
}
