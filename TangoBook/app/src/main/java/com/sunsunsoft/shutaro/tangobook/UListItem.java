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

    /**
     * Member variables
     */
    protected UListItemCallbacks mListItemCallbacks;
    protected int mIndex;

    /**
     * Get/Set
     */

    public int getIndex() {
        return mIndex;
    }
    public void setIndex(int index) {
        mIndex = index;
    }

    /**
     * Constructor
     */
    public UListItem(UListItemCallbacks listItemCallbacks,
                     float x, int width, int height)
    {
        super(0, x, 0, width, height);      // yはリスト追加時に更新されるので0
        mListItemCallbacks = listItemCallbacks;
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (vt.type == TouchType.Click) {
            if (rect.contains((int) (vt.touchX() + offset.x),
                    (int) (vt.touchY() + offset.y)))
            {
                if (mListItemCallbacks != null) {
                    mListItemCallbacks.ListItemClicked(this);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 高さを返す
     */
    abstract public int getHeight();

}
