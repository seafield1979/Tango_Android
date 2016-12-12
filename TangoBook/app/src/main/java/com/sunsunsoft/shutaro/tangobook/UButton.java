package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

interface UButtonCallbacks {
    /**
     * ボタンがクリックされた時の処理
     * @param id  button id
     * @param pressedOn  押された状態かどうか(On/Off)
     * @return
     */
    boolean UButtonClicked(int id, boolean pressedOn);
}

/**
 * クリックでイベントが発生するボタン
 *
 * 生成後ViewのonDraw内で draw メソッドを呼ぶと表示される
 * ボタンが押されたときの動作はtypeで指定できる
 *   BGColor ボタンの背景色が変わる
 *   Press   ボタンがへこむ
 *   Press2  ボタンがへこむ。へこんだ状態が維持される
 */
enum UButtonType {
    BGColor,    // color changing
    Press,      // pressed down
    Press2,     // pressed down, On/Off swiching
    Press3,     // pressed down, Off -> On only, to change Off call setPressedOn(false)
}

abstract public class UButton extends UDrawable {
    /**
     * Consts
     */
    public static final String TAG = "UButton";
    protected static final int PRESS_Y = 16;
    protected static final int BUTTON_RADIUS = 16;

    /**
     * Member Variables
     */
    protected int id;
    protected UButtonType type;
    protected UButtonCallbacks buttonCallback;
    protected boolean isPressed;
    protected int pressedColor;
    protected boolean pressedOn;        // Press2タイプの時のOn状態

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public boolean isPressedOn() {
        return pressedOn;
    }

    public void setPressedOn(boolean pressedOn) {
        this.pressedOn = pressedOn;
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
    abstract void draw(Canvas canvas, Paint paint, PointF offset);

    /**
     * UDrawable Interface
     */
    /**
     * タッチアップイベント
     */
    public boolean touchUpEvent(ViewTouch vt) {
        boolean done = false;

        if (vt.isTouchUp()) {
            if (isPressed) {
                isPressed = false;
                done = true;
            }
        }
        return done;
    }

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
                    if (type == UButtonType.Press3) {
                        // Off -> On に切り替わる一回目だけイベント発生
                        if (pressedOn == false) {
                            click();
                            pressedOn = true;
                        }
                    } else {
                        click();
                        done = true;
                        if (type == UButtonType.Press2) {
                            pressedOn = !pressedOn;
                        }
                    }
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
            buttonCallback.UButtonClicked(id, pressedOn);
        }
    }
}
