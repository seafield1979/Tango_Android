package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Drawableで矩形を描画する
 * Drawableのテスト用
 */

public class UDrawableRect extends UDrawable {

    /**
     * Constructor
     */
    public UDrawableRect(int priority, float x, float y, int width, int height) {
        super(priority, x, y, width, height);
    }

    public static UDrawableRect createInstance(int priority, float x, float y, int width, int
            height, int color)
    {
        UDrawableRect instance = new UDrawableRect(priority, x, y, width, height);
        instance.color = color;
        return instance;
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    void draw(Canvas canvas, Paint paint, PointF offset) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(pos.x, pos.y, pos.x + size.width, pos.y + size.height, paint);
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return false;
    }

}
