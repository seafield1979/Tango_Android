package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * テキストを表示するボタン
 */

public class UButtonText extends UButton {

    /**
     * Consts
     */

    /**
     * Member Variables
     */
    private String text;
    private int textColor;


    /**
     * Get/Set
     */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }


    /**
     * Constructor
     */
    public UButtonText(UButtonCallbacks callbacks, UButtonType type, int id,
                       int priority, String text,
                       float x, float y, int width, int height, int textColor, int color)
    {
        super(callbacks, type, id, priority, x, y, width, height, color);
        this.text = text;
        this.textColor = textColor;
    }

    /**
     * Methods
     */

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

        int _height = size.height;

        if (type == UButtonType.Press) {
            // 押したら凹むボタン
            if (isPressed) {
                _pos.y += PRESS_Y;
            } else {
                // ボタンの影用に下に矩形を描画
                UDraw.drawRoundRectFill(canvas, paint,
                        new RectF(_pos.x, _pos.y, _pos.x + size.width, _pos.y + size.height), BUTTON_RADIUS, pressedColor);
            }
            _height -= PRESS_Y;

        } else {
            // 押したら色が変わるボタン
            if (isPressed) {
                _color = pressedColor;
            }
        }
        UDraw.drawRoundRectFill(canvas, paint,
                new RectF(_pos.x, _pos.y, _pos.x + size.width, _pos.y + _height),
                BUTTON_RADIUS, _color);

        // テキスト
        if (text != null) {
            Rect bound = new Rect();
            paint.setTextSize(50);
            paint.setColor(textColor);

            // センタリング
            paint.getTextBounds(text, 0, text.length(), bound);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float baseY = _pos.y + _height / 2 - (fontMetrics.ascent + fontMetrics
                    .descent) / 2;

            canvas.drawText(text, _pos.x + (size.width - bound.width()) / 2, baseY, paint);
        }
    }
}
