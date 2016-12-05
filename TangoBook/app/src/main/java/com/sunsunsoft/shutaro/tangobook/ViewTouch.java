package com.sunsunsoft.shutaro.tangobook;

import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

enum TouchType {
    None,
    Touch,        // タッチ開始
    LongPress,    // 長押し
    Click,        // ただのクリック（タップ)
    LongClick,    // 長クリック
    Moving,       // 移動
    MoveEnd,      // 移動終了
    MoveCancel    // 移動キャンセル
}

/**
 * ViewTouchで長押しされた時のコールバック
 */

interface ViewTouchCallbacks {

    /**
     * 長押しされたときに呼ばれる
     */
    void longPressed();
}


/**
 * View上のタッチ処理を判定する
 *
 */
public class ViewTouch {
    public static final String TAG = "ViewTouch";

    // クリック判定するためのタッチ座標誤差
    public static final int CLICK_DISTANCE = 30;

    // ロングクリックの時間(ms)
    public static final int LONG_CLICK_TIME = 300;

    // 移動前の待機時間(ms)
    public static final int MOVE_START_TIME = 100;

    // 長押しまでの時間(ms)
    public static final int LONG_TOUCH_TIME = 700;


    private ViewTouchCallbacks callbacks;
    public TouchType type;          // 外部用のタイプ(変化があった時に有効な値を返す)
    private TouchType innerType;    // 内部用のタイプ
    private Timer timer;

    private boolean isTouchUp;      // タッチアップしたフレームだけtrueになる
    private boolean isTouching;
    private boolean isLongTouch;

    // タッチ開始した座標
    private float touchX, touchY;

    protected float x, y;       // スクリーン座標
    float moveX, moveY;
    private boolean isMoveStart;

    // タッチ開始した時間
    long touchTime;

    /**
     * Get/Set
     */
    public float getX() { return x; }
    public float getY() { return y; }
    public float getX(float offset) { return x + offset; }
    public float getY(float offset) { return y + offset; }
    public float touchX() {return this.touchX;}
    public float touchY() {return this.touchY;}
    public float touchX(float offset) {return this.touchX + offset;}
    public float touchY(float offset) {return this.touchY + offset;}
    public boolean isMoveStart() { return isMoveStart; }
    public boolean isTouchUp() {
        return isTouchUp;
    }
    public void setTouchUp(boolean touchUp) {
        isTouchUp = touchUp;
    }

    public void setTouching(boolean touching) {
        isTouching = touching;
    }

    public ViewTouch() {
        this(null);
        innerType = TouchType.None;
    }
    public ViewTouch(ViewTouchCallbacks callback) {
        this.callbacks = callback;
    }

    /**
     * ロングタッチがあったかどうかを取得する
     * このメソッドを呼ぶと内部のフラグをクリア
     * @return true:ロングタッチ
     */
    public boolean checkLongTouch() {
        // ロングタッチが検出済みならそれを返す
        if (isLongTouch) {
            ULog.print(TAG, "Long Touch");
            isLongTouch = false;
            return true;
        }
        return false;
    }

    public TouchType checkTouchType(MotionEvent e) {
        isTouchUp = false;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            {
                ULog.print(TAG, "Touch Down");

                isTouching = true;
                touchX = e.getX();
                touchY = e.getY();
                type = innerType = TouchType.Touch;
                touchTime = System.currentTimeMillis();
                startLongTouchTimer();
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                ULog.print(TAG, "Up");

                timer.cancel();

                isTouchUp = true;
                if (isTouching) {
                    if (innerType == TouchType.Moving) {
                        ULog.print(TAG, "MoveEnd");
                        type = innerType = TouchType.MoveEnd;
                        return type;
                    } else {
                        float x = (e.getX() - touchX);
                        float y = (e.getY() - touchY);
                        float dist = (float) Math.sqrt(x * x + y * y);

                        if (dist <= CLICK_DISTANCE) {
                            long time = System.currentTimeMillis() - touchTime;

                            if (time <= LONG_CLICK_TIME) {
                                type = TouchType.Click;
                                ULog.print(TAG, "SingleClick");
                            } else {
                                type = TouchType.LongClick;
                                ULog.print(TAG, "LongClick");
                            }
                        } else {
                            type = TouchType.None;
                        }
                    }
                } else {
                    type = TouchType.None;
                }
                isTouching = false;
            }
            break;
            case MotionEvent.ACTION_MOVE:
                isMoveStart = false;

                // 長押し時は何もしない
                if (innerType == TouchType.LongPress) {
                    type = TouchType.None;
                    break;
                }

                // クリックが判定できるようにタッチ時間が一定時間以上、かつ移動距離が一定時間以上で移動判定される
                else if ( innerType != TouchType.Moving) {
                    float dx = (e.getX() - touchX);
                    float dy = (e.getY() - touchY);
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    if (dist >= CLICK_DISTANCE) {
                        long time = System.currentTimeMillis() - touchTime;
                        if (time >= MOVE_START_TIME) {
                            type = innerType = TouchType.Moving;
                            isMoveStart = true;
                            x = touchX;
                            y = touchY;
                        }
                    }
                }
                if ( innerType == TouchType.Moving) {
                    moveX = e.getX() - x;
                    moveY = e.getY() - y;
                } else {
                    innerType = type = TouchType.None;
                }
                x = e.getX();
                y = e.getY();

                break;
//            case MotionEvent.ACTION_CANCEL:
//                ULog.print(TAG, "Cancel");
//                if (type == TouchType.Moving) {
//                    type = TouchType.None;
//                    return TouchType.MoveCancel;
//                }
//                break;
        }

        return type;
    }

    /**
     * ２点間の距離が指定の距離内に収まっているかどうかを調べる
     * @return true:距離内 / false:距離外
     */
    public boolean checkInsideCircle(float vx, float vy, float x, float y, float length) {
        if ((vx - x) * (vx - x) + (vy - y) * (vy - y) <= length * length) {
            return true;
        }
        return false;
    }

    /**
     * ロングタッチ検出用のタイマーを開始
     */
    private void startLongTouchTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                timer.cancel();
                if (isTouching && type != TouchType.Moving) {
                    // ロングタッチを検出する
                    isLongTouch = true;
                    isTouching = false;
                    innerType = type = TouchType.LongPress;
                    // ロングタッチイベント開始はonTouchから取れないので親に通知する
                    if (callbacks != null) {
                        callbacks.longPressed();
                    }
                    ULog.print(TAG, "timer Long Touch");
                }
            }
        }, LONG_TOUCH_TIME, 1000);
    }
}
