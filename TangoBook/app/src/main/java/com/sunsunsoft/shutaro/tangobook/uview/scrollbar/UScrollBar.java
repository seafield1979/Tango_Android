package com.sunsunsoft.shutaro.tangobook.uview.scrollbar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;

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
    private static final int BAR_COLOR = Color.argb(160, 128,128,128);
    private static final int SHOW_BAR_COLOR = Color.argb(255, 255,128,0);
    private static final int SHOW_BG_COLOR = Color.argb(128,255,255,255);

    /**
     * Membar Variables
     */
    private ScrollBarType type;
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

    protected RectF bgRect = new RectF();
    protected RectF barRect = new RectF();

    /**
     * Get/Set
     */

    public void setBgLength(int bgLength) {
        this.bgLength = bgLength;
    }

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
        if (isShow && barLength > 0) {
            return true;
        }
        return false;
    }

    public void setShow(boolean show) {
        this.isShow = show;
    }

    /**
     * コンストラクタ
     * 指定のViewに張り付くタイプのスクロールバーを作成
     *
     * @param pageLen   1ページ分のコンテンツの長さ
     * @param contentLen  全体のコンテンツの長さ
     */
    public UScrollBar(ScrollBarType type, ScrollBarShowType showType,
                      PointF paretnPos, float x, float y,
                      int bgLength, int bgWidth,
                      long pageLen, long contentLen ) {
        this.type = type;
        this.showType = showType;
        if (showType == ScrollBarShowType.ShowAllways) {
            isShow = true;
        }
        pos.x = x;
        pos.y = y;
        topPos = 0;
        barPos = 0;
        this.parentPos = paretnPos;
        this.bgWidth = bgWidth;
        this.bgLength = bgLength;
        this.contentLen = contentLen;
        this.pageLen = pageLen;

        updateBarLength();

        if (showType == ScrollBarShowType.Show2) {
            bgColor = 0;
            barColor = BAR_COLOR;
        } else {
            bgColor = SHOW_BAR_COLOR;
            barColor = SHOW_BG_COLOR;
        }

        updateSize();
    }

    /**
     * スクロールバーを表示する先のViewのサイズが変更された時の処理
     */
    public void updateSize() {
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
     * @param pos
     */
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
    public long updateContent(long contentSize) {
        this.contentLen = contentSize;

        updateBarLength();
        return topPos;
    }

    public void draw(Canvas canvas, Paint paint, PointF offset) {
        if (!isShow) return;

        paint.setStyle(Paint.Style.FILL);

        float baseX = pos.x + parentPos.x;
        float baseY = pos.y + parentPos.y;
        if (offset != null) {
            baseX += offset.x;
            baseY += offset.y;
        }

        float _barLength = barLength;
        float _barPos = barPos;
        if (showType == ScrollBarShowType.ShowAllways) {
            _barLength = bgLength - 30;
            _barPos = 15;
        }
        if (type == ScrollBarType.Horizontal) {
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
    public boolean touchEvent(ViewTouch tv, PointF offset) {
        switch(tv.type) {
            case Touch:
                if (touchDown(tv, offset)) {
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
    private boolean touchDown(ViewTouch vt, PointF offset) {
        // スペース部分をタッチしたら１画面分スクロール
        float ex = vt.touchX() - parentPos.x - offset.x;
        float ey = vt.touchY() - parentPos.y - offset.y;

        if (type == ScrollBarType.Vertical) {
            System.out.println("ex:ey:" + ex + " " + ey + " posx:posy:" + pos.x + " " + pos.y);
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
            float move = (type == ScrollBarType.Vertical) ? vt.getMoveY() : vt.getMoveX();
            barMove(move);
            return true;
        }
        return false;
    }
}
