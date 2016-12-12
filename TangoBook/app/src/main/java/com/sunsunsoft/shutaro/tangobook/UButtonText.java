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
    public static final String TAG = "UButtonText";

    /**
     * Member Variables
     */
    private String text;
    private int textColor;
    private int mTextSize;

    /**
     * Get/Set
     */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }


    /**
     * Constructor
     */
    public UButtonText(UButtonCallbacks callbacks, UButtonType type, int id,
                       int priority, String text,
                       float x, float y, int width, int height,
                       int textSize, int textColor, int color)
    {
        super(callbacks, type, id, priority, x, y, width, height, color);
        this.text = text;
        this.textColor = textColor;
        mTextSize = textSize;
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

        if (type == UButtonType.BGColor) {
            // 押したら色が変わるボタン
            if (isPressed) {
                _color = pressedColor;
            }
        }
        else {
            // 押したら凹むボタン
            if (isPressed || pressedOn) {
                _pos.y += PRESS_Y;
            } else {
                // ボタンの影用に下に矩形を描画
                int height = PRESS_Y + 20;
                UDraw.drawRoundRectFill(canvas, paint,
                        new RectF(_pos.x, _pos.y + size.height - height,
                                _pos.x + size.width, _pos.y + size.height),
                        BUTTON_RADIUS, pressedColor, 0, 0);
            }
            _height -= PRESS_Y;

        }
        UDraw.drawRoundRectFill(canvas, paint,
                new RectF(_pos.x, _pos.y, _pos.x + size.width, _pos.y + _height),
                BUTTON_RADIUS, _color, 0, 0);

        // テキスト
        if (text != null) {
            UDraw.drawText(canvas, text, UAlignment.Center, mTextSize,
                    _pos.x + size.width / 2,
                    _pos.y + size.height / 2, textColor);
        }
    }
}
