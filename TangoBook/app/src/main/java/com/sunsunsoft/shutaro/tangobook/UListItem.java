package com.sunsunsoft.shutaro.tangobook;


import android.graphics.PointF;

/**
 * Created by shutaro on 2016/12/09.
 *
 * UListViewに表示する項目
 */
interface UListItemCallbacks {
    /**
     * 項目がクリックされた
     * @param item
     */
    void ListItemClicked(UListItem item);
}

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

    /**
     * Get/Set
     */

    public int getIndex() {
        return mIndex;
    }
    public void setIndex(int index) {
        mIndex = index;
    }

    public void setListItemCallbacks(UListItemCallbacks mListItemCallbacks) {
        this.mListItemCallbacks = mListItemCallbacks;
    }

    /**
     * Constructor
     */
    public UListItem(UListItemCallbacks listItemCallbacks, boolean isTouchable,
                     float x, int width, int height, int bgColor)
    {
        super(0, x, 0, width, height);      // yはリスト追加時に更新されるので0
        mListItemCallbacks = listItemCallbacks;
        color = bgColor;
        this.isTouchable = isTouchable;

        if (isTouchable) {
            // 押された時の色（少し明るくする)
            pressedColor = UColor.addBrightness(color, 0.3f);
        }
    }

    /**
     * タッチ処理
     * @param vt
     * @return true:再描画あり
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        boolean isDraw = false;

        if (vt.isTouchUp()) {
            isTouching = false;
            ULog.print(TAG, "isTouching = false");
            isDraw = true;
        }

        switch(vt.type) {
            case Touch:
                if (isTouchable) {
                    if (rect.contains((int) (vt.touchX() - offset.x),
                            (int) (vt.touchY() - offset.y))) {
                        isTouching = true;
                        ULog.print(TAG, "isTouching = true");
                        isDraw = true;
                    }
                }
                break;
            case Click:
                if (rect.contains((int) (vt.touchX() - offset.x),
                        (int) (vt.touchY() - offset.y)))
                {
                    if (mListItemCallbacks != null) {
                        mListItemCallbacks.ListItemClicked(this);
                    }
                    isDraw = true;
                }
                break;
        }
        return isDraw;
    }

    /**
     * 高さを返す
     */
    abstract public int getHeight();
}
