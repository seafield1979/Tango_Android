package com.sunsunsoft.shutaro.tangobook.uview;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;


abstract public class UListItem extends UDrawable {

    /**
     * Constants
     */
    public static final String TAG = "UListItem";

    /**
     * Member variables
     */
    protected UListItemCallbacks mListItemCallbacks;
    protected int mIndex;
    protected boolean isTouchable;
    protected boolean isTouching;
    protected int pressedColor;
    protected int mFrameW;          // 枠の幅
    protected int mFrameColor;      // 枠の色
    protected Rect mRect;            // 矩形領域

    /**
     * Get/Set
     */

    public int getIndex() {
        return mIndex;
    }
    public void setIndex(int index) {
        mIndex = index;
    }
    public int getMIndex(){ return mIndex; }
    public void setListItemCallbacks(UListItemCallbacks mListItemCallbacks) {
        this.mListItemCallbacks = mListItemCallbacks;
    }


    /**
     * Constructor
     */
    public UListItem(UListItemCallbacks listItemCallbacks, boolean isTouchable,
                     float x, int width, int height, int bgColor,
                     int frameW, int frameColor)
    {
        super(0, x, 0, width, height);      // yはリスト追加時に更新されるので0
        mListItemCallbacks = listItemCallbacks;
        color = bgColor;
        this.isTouchable = isTouchable;
        mFrameW = frameW;
        mFrameColor = frameColor;

        mRect = new Rect(0, 0, width, height);

        if (isTouchable) {
            // 押された時の色（暗くする)
            pressedColor = UColor.addBrightness(color, -0.2f);
        }
    }


    public boolean touchUpEvent(ViewTouch vt) {
        if (vt.isTouchUp()) {
            isTouching = false;
        }
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return true:再描画あり
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        boolean isDraw = false;

        switch(vt.type) {
            case Touch:
                if (isTouchable) {
                    if (rect.contains((int) (vt.touchX() - offset.x),
                            (int) (vt.touchY() - offset.y))) {
                        isTouching = true;
                        isDraw = true;
                    }
                }
                break;
            case Click:
                if (isTouchable) {
                    if (rect.contains((int) (vt.touchX() - offset.x),
                            (int) (vt.touchY() - offset.y))) {
                        if (mListItemCallbacks != null) {
                            mListItemCallbacks.ListItemClicked(this);
                        }
                        isDraw = true;
                    }
                }
                break;
        }
        return isDraw;
    }

    @Override
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // BG　タッチ中は色を変更
        int _color = color;

        if (isTouchable && isTouching) {
            _color = pressedColor;
        }

        Rect rect = new Rect((int)offset.x, (int)offset.y, (int)offset.x + size.width, (int)offset.y + size.height);

        if (mFrameW > 0 && color != 0) {
            UDraw.drawRectFill(canvas, paint, rect, _color, mFrameW, mFrameColor );
        }
        else if (color != 0) {
            UDraw.drawRectFill(canvas, paint, rect, _color);
        }
        else if (mFrameW > 0) {
            UDraw.drawRect(canvas, paint, rect, mFrameW, mFrameColor);
        }
    }

}
