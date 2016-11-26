package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

// スクロールバーの配置場所
enum ScrollBarType {
    Top,
    Bottom,
    Left,
    Right
}

// スクロールバーの配置場所2
enum ScrollBarInOut {
    In,
    Out
}

/**
 * 自前で描画するスクロールバー
 * タッチ操作あり
 *
 * 機能
 *  外部の値に連動してスクロール位置を画面に表示
 *  ドラッグしてスクロール
 *  バー以外の領域をタップしてスクロール
 *  指定のViewに張り付くように配置
 */
public class UScrollBar {

    /**
     * Constants
     */
    public static final String TAG = "UScrollBar";

    /**
     * Member variables
     */
    private ScrollBarType type;
    private ScrollBarInOut inOut;

    protected boolean isShow;
    private PointF pos = new PointF();
    private int contentLen;       // コンテンツ領域のサイズ
    private int viewLen;          // 表示画面のサイズ
    private float topPos;         // スクロールの現在の位置
    private boolean isDraging;
    private PointF parentPos;

    private int bgLength, bgWidth;

    private float barPos;        // バーの座標（縦ならy,横ならx)
    private int barLength;       // バーの長さ(縦バーなら高さ、横バーなら幅)
    private int bgColor, barColor;

    /**
     * Get/Set
     */

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    // 縦のスクロールバーか
    private boolean isVertical() {
        return (type == ScrollBarType.Left || type == ScrollBarType.Right);
    }
    // 横のスクロールバーか
    private boolean isHorizontal() {
        return (type == ScrollBarType.Top || type == ScrollBarType.Bottom);
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    public float getTopPos() {
        return topPos;
    }

    private void updateBarLength() {
        ULog.print(TAG, "viewLen:" + viewLen + " contentLen:" + contentLen);
        if (viewLen >= contentLen) {
            // 表示領域よりコンテンツの領域が小さいので表示不要
            barLength = 0;
            topPos = 0;
        } else {
            barLength = (int) (this.bgLength * ((float) viewLen / (float) contentLen));
        }
    }

    public int getBgWidth() {
        return bgWidth;
    }



    /**
     * Constructor
     */
    /**
     * コンストラクタ
     * 指定のViewに張り付くタイプのスクロールバーを作成
     * @param type
     * @param viewWidth
     * @param viewHeight
     * @param width
     * @param contentLen
     */
    public UScrollBar(ScrollBarType type, ScrollBarInOut inOut, PointF parentPos, int viewWidth, int viewHeight, int width, int contentLen ) {
        this.type = type;
        this.inOut = inOut;
        this.parentPos = parentPos;
        topPos = 0;
        barPos = 0;
        this.bgWidth = width;
        this.contentLen = contentLen;
        if (isVertical()) {
            viewLen = viewHeight;
        } else {
            viewLen = viewWidth;
        }

        updateBarLength();

        bgColor = Color.argb(128,255,255,255);
        barColor = Color.argb(255, 255,128,0);

        updateSize(viewWidth, viewHeight);
    }

    /**
     * スクロールバーを表示する先のViewのサイズが変更された時の処理
     * @param viewW
     * @param viewH
     */
    public void updateSize(int viewW, int viewH) {
        if (isVertical()) {
            viewLen = viewH;
        } else {
            viewLen = viewW;
        }

        switch (type) {
            case Top:
                pos.x = 0;
                bgLength = viewW;
                if (inOut == ScrollBarInOut.In) {
                    pos.y = 0;
                } else {
                    pos.y = -bgWidth;
                }
                break;
            case Bottom:
                pos.x = 0;
                bgLength = viewW;
                if (inOut == ScrollBarInOut.In) {
                    pos.y = viewH - bgWidth;
                } else {
                    pos.y = viewH;
                }
                break;
            case Left:
                pos.y = 0;
                bgLength = viewH;
                if (inOut == ScrollBarInOut.In) {
                    pos.x = 0;
                } else {
                    pos.x = -bgWidth;
                }
                break;
            case Right:
                pos.y = 0;
                bgLength = viewH;
                if (inOut == ScrollBarInOut.In) {
                    pos.x = viewW - bgWidth;
                } else {
                    pos.x = viewW;
                }
                break;
        }
        updateBarLength();
        if (barPos + barLength > bgLength) {
            barPos = bgLength - barLength;
        }
    }


    /**
     * 色を設定
     * @param bgColor  背景色
     * @param barColor バーの色
     */
    public void setColor(int bgColor, int barColor) {
        this.bgColor = bgColor;
        this.barColor = barColor;
    }

    /**
     * 領域がスクロールした時の処理
     * ※外部のスクロールを反映させる
     * @param topPos
     */
    public void updateScroll(PointF topPos) {
        float _pos = isVertical() ? topPos.y : topPos.x;
        barPos = (_pos / (float)contentLen) * bgLength;
        this.topPos = _pos;
    }

    public void updateScroll(float topPos) {
        barPos = (topPos / (float)contentLen) * bgLength;
        this.topPos = topPos;
    }

    /**
     * バーの座標からスクロール量を求める
     * updateScrollの逆バージョン
     */
    public void updateScrollByBarPos() {
        topPos = (barPos / viewLen) * contentLen;
    }

    /**
     * コンテンツやViewのサイズが変更された時の処理
     */
    public float updateContent(Size contentSize) {
        if (isVertical()) {
            this.contentLen = contentSize.height;
        } else {
            this.contentLen = contentSize.width;
        }

        updateBarLength();
        return topPos;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!isShow) return;

        if (barLength == 0) return;

        paint.setStyle(Paint.Style.FILL);

        RectF bgRect = new RectF();
        RectF barRect = new RectF();

        float baseX = pos.x + parentPos.x;
        float baseY = pos.y + parentPos.y;

        if (isHorizontal()) {
            bgRect.left = baseX;
            bgRect.right = baseX + bgLength;
            bgRect.top = baseY;
            bgRect.bottom = baseY + bgWidth;
            barRect.left = baseX + barPos;
            barRect.top = baseY + 10;
            barRect.right = baseX + barPos + barLength;
            barRect.bottom = baseY + bgWidth - 10;
        } else {
            bgRect.left = baseX;
            bgRect.top = baseY;
            bgRect.right = baseX + bgWidth;
            bgRect.bottom = baseY + bgLength;
            barRect.left = baseX + 10;
            barRect.top = baseY + barPos;
            barRect.right = baseX + bgWidth - 10;
            barRect.bottom =baseY + barPos + barLength;
        }

        // 背景
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bgColor);
        canvas.drawRect(bgRect.left,
                bgRect.top,
                bgRect.right,
                bgRect.bottom,
                paint);

        // バー
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(barColor);
        canvas.drawRect(barRect.left,
                barRect.top,
                barRect.right,
                barRect.bottom,
                paint);
    }


    /**
     * １画面分上（前）にスクロール
     */
    public void scrollUp() {
        topPos -= viewLen;
        if (topPos < 0) {
            topPos = 0;
        }
        updateScroll(topPos);
    }

    /**
     * １画面分下（先）にスクロール
     */
    public void scrollDown() {
        topPos += viewLen;
        if (topPos + viewLen > contentLen) {
            topPos = contentLen - viewLen;
        }
        updateScroll(topPos);
    }

    /**
     * バーを移動
     * @param move 移動量
     */
    public void barMove(float move) {
        barPos += move;
        if (barPos < 0) {
            barPos = 0;
        }
        else if (barPos + barLength > bgLength) {
            barPos = bgLength - barLength;
        }

        updateScrollByBarPos();
    }

    /**
     * タッチ系の処理
     * @param tv
     * @return
     */
    public boolean touchEvent(ViewTouch tv) {
        switch(tv.type) {
            case Touch:
                if (touchDown(tv)) {
                    return true;
                }
                break;
            case Moving:
                if (touchMove(tv)) {
                    return true;
                }
                break;
            case MoveEnd:
                touchUp();
                break;
        }
        return false;
    }

    /**
     * スクロールバーのタッチ処理
     * @param vt
     * @return true:バーがスクロールした
     */
    private boolean touchDown(ViewTouch vt) {
        // スペース部分をタッチしたら１画面分スクロール
        float ex = vt.touchX() - parentPos.x;
        float ey = vt.touchY() - parentPos.y;

        if (isVertical()) {
            if (pos.x <= ex && ex < pos.x + bgWidth &&
                    pos.y <= ey && ey < pos.y + bgLength)
            {
                if (ey < barPos) {
                    // 上にスクロール
                    ULog.print(TAG, "Scroll Up");
                    scrollUp();
                    return true;
                } else if (ey > pos.y + barPos + barLength) {
                    // 下にスクロール
                    ULog.print(TAG, "Scroll Down");
                    scrollDown();
                    return true;
                } else {
                    // バー
                    ULog.print(TAG, "Drag Start");
                    isDraging = true;
                    return true;
                }
            }
        } else {
            if (pos.x <= ex && ex < pos.x + bgLength &&
                    pos.y <= ey && ey < pos.y + bgWidth)
            {
                if (ex < barPos) {
                    // 上にスクロール
                    ULog.print(TAG, "Scroll Up");
                    scrollUp();
                    return true;
                } else if (ex > pos.x + barPos + barLength) {
                    // 下にスクロール
                    ULog.print(TAG, "Scroll Down");
                    scrollDown();
                    return true;
                } else {
                    // バー
                    ULog.print(TAG, "Drag Start");
                    isDraging = true;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean touchUp() {
        ULog.print(TAG, "touchUp");
        isDraging = false;

        return false;
    }

    private boolean touchMove(ViewTouch vt) {
        if (isDraging) {
            float move = isVertical() ? vt.moveY : vt.moveX;
            barMove(move);
            return true;
        }
        return false;
    }
}
