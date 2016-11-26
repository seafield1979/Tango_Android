package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

interface UButtonCallbacks {
    void UButtonClick(int id);
    void UButtonLongClick(int id);
}


/**
 * クリックでイベントが発生するボタン
 *
 * 生成後ViewのonDraw内で draw メソッドを呼ぶと表示される
 * ボタンが押されたときの動作はtypeで指定できる
 *   BGColor ボタンの背景色が変わる
 *   Press   ボタンがへこむ
 */
enum UButtonType {
    BGColor,    // color changing
    Press       // pressed down
}

public class UButton extends UDrawable {
    /**
     * Consts
     */
    public static final String TAG = "UButton";
    protected static final int PRESS_Y = 12;
    protected static final int BUTTON_RADIUS = 16;

    /**
     * Member Variables
     */
    protected int id;
    protected UButtonType type;
    protected UButtonCallbacks buttonCallback;
    protected boolean isPressed;
    protected int pressedColor;



    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }


    /**
     * Constructor
     */
    public UButton(UButtonCallbacks callbacks, UButtonType type, int id, int priority,
                   float x, float y, int width, int height, int color)
    {
        super(priority, x, y, width, height);
        this.id = id;
        this.buttonCallback = callbacks;
        this.type = type;
        this.color = color;
        if (type == UButtonType.BGColor) {
            this.pressedColor = UColor.addBrightness(color, 0.4f);
        } else {
            this.pressedColor = UColor.addBrightness(color, -0.3f);
        }
    }

    /**
     * Methods
     */
    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        // 親Windowの座標とスクロール量を取得
        return null;
    }

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
    }

    /**
     * Touchable Interface
     */

    /**
     * タッチイベント
     * @param vt
     * @return true:イベントを処理した(再描画が必要)
     */
    public boolean touchEvent(ViewTouch vt) {
        return touchEvent(vt, null);
    }

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
                if (rect.contains((int)vt.touchX(-offset.x), (int)vt.touchY(-offset.y))) {
                    isPressed = true;
                    done = true;
                }
                break;
            case Click:
            case LongClick:
                isPressed = false;
                if (rect.contains((int)vt.touchX(-offset.x), (int)vt.touchY(-offset.y))) {
                    click();
                    done = true;
                }
                break;
            case MoveEnd:

                break;
        }
        return done;
    }

    public void click() {
        Log.v(TAG, "click");
        if (buttonCallback != null) {
            buttonCallback.UButtonClick(id);
        }
    }

}
