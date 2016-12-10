package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

// スクロールバーの配置場所
enum ScrollBarLocation {
    Bottom,
    Right
}

// スクロールバーの表示タイプ
enum ScrollBarShowType {
    Show,           // 必要なら表示
    Show2,          // 必要なら表示（自動で非表示になる）
    ShowAllways     // 常に表示
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

    // colors
    private static final int SHOW2_BG_COLOR = Color.argb(160, 255,128,0);
    private static final int SHOW_BAR_COLOR = Color.argb(255, 255,128,0);
    private static final int SHOW_BG_COLOR = Color.argb(128,255,255,255);

    /**
     * Membar Variables
     */
    private ScrollBarLocation location;
    private ScrollBarShowType showType;
    private boolean isShow;

    public PointF pos = new PointF();
    public PointF parentPos;
    public int bgColor, barColor;
    private boolean isDraging;

    // スクリーン座標系
    public int bgLength, bgWidth;
    public float barPos;        // バーの座標（縦ならy,横ならx)
    public int barLength;       // バーの長さ(縦バーなら高さ、横バーなら幅)

    // コンテンツ座標系
    public long contentLen;       // コンテンツ領域のサイズ
    public long pageLen;          // 表示画面のサイズ
    public long topPos;         // スクロールの現在の位置

    // 縦のスクロールバーか
    private boolean isVertical() {
        return (location == ScrollBarLocation.Right);
    }
    // 横のスクロールバーか
    private boolean isHorizontal() {
        return (location == ScrollBarLocation.Bottom);
    }

    /**
     * Get/Set
     */
    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    public long getTopPos() {
        return topPos;
    }

    private void updateBarLength() {
        if (pageLen >= contentLen) {
            // 表示領域よりコンテンツの領域が小さいので表示不要
            barLength = 0;
            topPos = 0;
            if (showType != ScrollBarShowType.ShowAllways) {
                isShow = false;
            }
        } else {
            barLength = (int) (this.bgLength * ((float) pageLen / (float) contentLen));
            isShow = true;
        }
    }

    public int getBgWidth() {
        return bgWidth;
    }

    public void setPageLen(long pageLen) {
        this.pageLen = pageLen;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        this.isShow = show;
    }

    /**
     * コンストラクタ
     * 指定のViewに張り付くタイプのスクロールバーを作成
     *
     * @param location
     * @param showType
     * @param parentPos
     * @param viewWidth
     * @param viewHeight
     * @param bgWidth
     * @param pageLen   1ページ分のコンテンツの長さ
     * @param contentLen  全体のコンテンツの長さ
     */
    public UScrollBar(ScrollBarLocation location, ScrollBarShowType showType,
                      PointF parentPos, int viewWidth, int viewHeight, int bgWidth,
                      long pageLen, long contentLen ) {
        this.location = location;
        this.showType = showType;
        if (showType == ScrollBarShowType.ShowAllways) {
            isShow = true;
        }
        this.parentPos = parentPos;
        topPos = 0;
        barPos = 0;
        this.bgWidth = bgWidth;
        this.contentLen = contentLen;
        this.pageLen = pageLen;

        updateBarLength();

        if (showType == ScrollBarShowType.Show2) {
            bgColor = 0;
            barColor = SHOW2_BG_COLOR;
        } else {
            bgColor = SHOW_BAR_COLOR;
            barColor = SHOW_BG_COLOR;
        }

        updateSize(viewWidth, viewHeight, true);
    }

    /**
     * スクロールバーを表示する先のViewのサイズが変更された時の処理
     * @param viewW
     * @param viewH
     */
    public void updateSize(int viewW, int viewH, boolean init) {
        switch (location) {
            case Bottom:
                pos.x = 0;
                bgLength = viewW;
                if (init) {
                    pos.y = viewH - bgWidth;
                }
                break;
            case Right:
                pos.y = 0;
                bgLength = viewH;
                if (init) {
                    pos.x = viewW - bgWidth;
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
    public void updateScroll(PointL topPos) {
        long _pos = isVertical() ? topPos.y : topPos.x;
        barPos = (_pos / (float)contentLen) * bgLength;
        this.topPos = _pos;
    }

    public void updateScroll(long pos) {
        barPos = (pos / (float)contentLen) * bgLength;
        this.topPos = pos;
    }

    public void updateBarPos() {
        barPos = (topPos / (float)contentLen) * bgLength;
    }

    /**
     * バーの座標からスクロール量を求める
     * updateScrollの逆バージョン
     */
    private void updateScrollByBarPos() {
        topPos = (long)((barPos / bgLength) * contentLen);
    }

    /**
     * コンテンツやViewのサイズが変更された時の処理
     */
    public long updateContent(SizeL contentSize) {
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

        paint.setStyle(Paint.Style.FILL);

        RectF bgRect = new RectF();
        RectF barRect = new RectF();

        float baseX = pos.x + parentPos.x;
        float baseY = pos.y + parentPos.y;

        float _barLength = barLength;
        float _barPos = barPos;
        if (showType == ScrollBarShowType.ShowAllways) {
            _barLength = bgLength - 30;
            _barPos = 15;
        }
        if (isHorizontal()) {
            if (bgColor != 0) {
                bgRect.left = baseX;
                bgRect.right = baseX + bgLength;
                bgRect.top = baseY;
                bgRect.bottom = baseY + bgWidth;
            }

            barRect.left = baseX + _barPos;
            barRect.top = baseY + 10;
            barRect.right = baseX + _barPos + _barLength;
            barRect.bottom = baseY + bgWidth - 10;
        } else {
            if (bgColor != 0) {
                bgRect.left = baseX;
                bgRect.top = baseY;
                bgRect.right = baseX + bgWidth;
                bgRect.bottom = baseY + bgLength;
            }

            barRect.left = baseX + 10;
            barRect.top = baseY + _barPos;
            barRect.right = baseX + bgWidth - 10;
            barRect.bottom =baseY + _barPos + _barLength;
        }

        // 背景
        if (bgColor != 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(bgColor);
            canvas.drawRect(bgRect.left,
                    bgRect.top,
                    bgRect.right,
                    bgRect.bottom,
                    paint);
        }

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
        topPos -= pageLen;
        if (topPos < 0) {
            topPos = 0;
        }
        updateBarPos();
    }

    /**
     * １画面分下（先）にスクロール
     */
    public void scrollDown() {
        topPos += pageLen;
        if (topPos + pageLen > contentLen) {
            topPos = contentLen - pageLen;
        }
        updateBarPos();
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
