package com.sunsunsoft.shutaro.tangobook.uview.button;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;

/**
 * 閉じるボタン
 */

public class UButtonClose extends UButton {

    /**
     * Consts
     */
    private static final int X_LINE_WIDTH = 5;
    private static final int RADIUS = 17;

    /**
     * Member Variables
     */
    private int radius;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UButtonClose(UButtonCallbacks callbacks, UButtonType type, int id, int priority,
                        float x, float y, int color)
    {
        super(callbacks, type, id, priority, x, y, UDpi.toPixel(RADIUS) * 2, UDpi.toPixel(RADIUS) * 2, color);

        this.buttonCallback = callbacks;
        this.type = type;
        this.id = id;
        this.color = color;
        this.radius = UDpi.toPixel(RADIUS);
    }

    /**
     * Methods
     */

    @Override
    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);

        // 色
        // 押されていたら明るくする
        int _color = color;

        paint.setColor(_color);

        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }

        // 押したら色が変わるボタン
        if (isPressed) {
            _color = pressedColor;
        }
        UDraw.drawCircleFill(canvas, paint,
                new PointF(_pos.x, _pos.y),
                radius, _color);

        // x
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth( UDpi.toPixel(X_LINE_WIDTH) );
        float x = (float)Math.cos(45 * UDrawable.RAD) * radius * 0.8f;
        float y = (float)Math.sin(45 * RAD) * radius * 0.8f;
        canvas.drawLine(_pos.x - x, _pos.y - y,
                _pos.x + x, _pos.y + y, paint);
        canvas.drawLine(_pos.x - x, _pos.y + y,
                _pos.x + x, _pos.y - y, paint);
    }

    /**
     * タッチ処理
     * @param vt
     * @param offset
     * @return
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        boolean done = false;
        if (offset == null) {
            offset = new PointF();
        }
        if (vt.isTouchUp()) {
            if (isPressed) {
                isPressed = false;
                done = true;
            }
        }

        switch(vt.type) {
            case None:
                break;
            case Touch:
            case Moving:
                if (contains((int)vt.touchX(-offset.x), (int)vt.touchY(-offset.y))) {
                    isPressed = true;
                    done = true;
                }
                break;
            case Click:
            case LongClick:
                isPressed = false;
                if (contains((int)vt.touchX(-offset.x), (int)vt.touchY(-offset.y))) {
                    buttonCallback.UButtonClicked(id, false);
                    done = true;
                }
                break;
            case MoveEnd:

                break;
        }
        return done;
    }

    /**
     * 指定の座標がボタンの円の中に含まれるかをチェック
     * @param x
     * @param y
     * @return
     */
    private boolean contains(float x, float y) {
        // 中心からの距離で判定
        float dx = x - pos.x;
        float dy = y - pos.y;

        if (radius * radius >= dx * dx + dy * dy) {
            return true;
        }
        return false;
    }
}
