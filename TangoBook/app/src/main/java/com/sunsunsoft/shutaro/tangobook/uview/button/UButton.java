package com.sunsunsoft.shutaro.tangobook.uview.button;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;


/**
 * クリックでイベントが発生するボタン
 *
 * 生成後ViewのonDraw内で draw メソッドを呼ぶと表示される
 * ボタンが押されたときの動作はtypeで指定できる
 *   BGColor ボタンの背景色が変わる
 *   Press   ボタンがへこむ
 *   Press2  ボタンがへこむ。へこんだ状態が維持される
 */

abstract public class UButton extends UDrawable {
    /**
     * Consts
     */
    public static final String TAG = "UButton";
    public static final int PRESS_Y = 6;
    public static final int BUTTON_RADIUS = 6;
    public static final int DISABLED_COLOR = Color.rgb(160,160,160);
    public static final int DEFAULT_BG_COLOR = Color.LTGRAY;

    /**
     * Member Variables
     */
    protected int id;
    protected UButtonType type;
    protected UButtonCallbacks buttonCallback;
    protected boolean enabled;          // falseならdisableでボタンが押せなくなる
    protected boolean checked;          // チェックアイコンを表示する
    protected boolean isPressed;
    protected boolean isClicked;        // クリックされた(クリックイベントを遅延発生させるために使用)
    protected int pressedColor;
    protected int disabledColor;        // enabled == false のときの色
    protected int disabledColor2;       // eanbled == false のときの濃い色
    protected boolean pressedOn;        // Press2タイプの時のOn状態
    protected boolean pullDownIcon;     // プルダウンのアイコン▼を表示

    /**
     * Get/Set
     */
    public boolean getEnabled() { return enabled; }
    public int getId() {
        return id;
    }

    public boolean isPressedOn() {
        return pressedOn;
    }

    public void setPressedOn(boolean pressedOn) {
        this.pressedOn = pressedOn;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPullDownIcon(boolean pullDown) {
        pullDownIcon = pullDown;
    }

    public boolean isPressButton() {
        return (type != UButtonType.BGColor);
    }

    /**
     * Constructor
     */
    public UButton(UButtonCallbacks callbacks, UButtonType type, int id, int priority,
                   float x, float y, int width, int height, int color)
    {
        super(priority, x, y, width, height);
        this.id = id;
        this.enabled = true;
        this.buttonCallback = callbacks;
        this.type = type;
        this.color = color;
        if (color != 0) {
            if (type == UButtonType.BGColor) {
                this.pressedColor = UColor.addBrightness(color, 0.2f);
            } else {
                this.pressedColor = UColor.addBrightness(color, -0.2f);
            }
        }
        disabledColor = DISABLED_COLOR;
        disabledColor2 = UColor.addBrightness(disabledColor, -0.2f);
    }

    /**
     * Get/Set
     */
    public void setId(int id) {
        this.id = id;
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
    public abstract void draw(Canvas canvas, Paint paint, PointF offset);

    /**
     * 毎フレームの処理
     * サブクラスでオーバーライドして使用する
     * @return true:処理中 / false:処理完了
     */
    public DoActionRet doAction(){
        if (isClicked) {
            isClicked = false;
            click();
            return DoActionRet.Done;
        }
        return DoActionRet.None;
    }

    /**
     * UDrawable Interface
     */
    /**
     * タッチアップイベント
     */
    public boolean touchUpEvent(ViewTouch vt) {
        if (vt.isTouchUp()) {
            if (isPressed) {
                isPressed = false;
                return true;
            }
        }
        return false;
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
        if (!enabled) return false;

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
            case LongPress:
                isPressed = false;
                if (rect.contains((int)vt.touchX(-offset.x), (int)vt.touchY(-offset.y))) {
                    if (type == UButtonType.Press3) {
                        // Off -> On に切り替わる一回目だけイベント発生
                        if (pressedOn == false) {
                            isClicked = true;
                            pressedOn = true;
                            done = true;
                        }
                    } else {
                        isClicked = true;
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
