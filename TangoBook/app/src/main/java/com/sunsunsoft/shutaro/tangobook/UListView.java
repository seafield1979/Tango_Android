package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/09.
 *
 * ListView
 * UListItemを縦に並べて表示する
 */

public class UListView extends UScrollWindow
{
    /**
     * Enums
     */
    /**
     * Constants
     */

    /**
     * Member variables
     */
    protected LinkedList<UListItem> mItems = new LinkedList<>();
    protected UListItemCallbacks mListItemCallbacks;

    // リストの最後のアイテムの下端の座標
    protected float mBottomY;


    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UListView(UWindowCallbacks callbacks,
                     UListItemCallbacks listItemCallbacks,
                     int priority, float x, float y, int width, int
                             height, int color)
    {
        super(callbacks, priority, x, y, width, height, color);
        mListItemCallbacks = listItemCallbacks;
    }

    /**
     * Methods
     */
    public void add(UListItem item) {
        item.setPos(0, mBottomY);
        item.setIndex(mItems.size());
        mItems.add(item);
        mBottomY += item.size.height;

        contentSize.height = (int)mBottomY;
    }

    public void remove(UListItem item) {
        int index = mItems.indexOf(item);
        removeCore(item, index);
    }

    public void remove(int index) {
        UListItem item = mItems.get(index);
        removeCore(item, index);
    }

    protected void removeCore(UListItem item, int index) {
        if (index == -1 || item == null) return;

        float y = item.pos.y;
        mItems.remove(index);
        // 削除したアイテム以降のアイテムのIndexと座標を詰める
        for (int i = index; i< mItems.size(); i++) {
            UListItem _item = mItems.get(i);
            _item.setIndex(i);
            _item.pos.y = y;
            y = _item.getBottom();
        }
        mBottomY = y;
    }


    public void drawContent(Canvas canvas, Paint paint) {
        // BG
        UDraw.drawRectFill(canvas, paint, rect, Color.LTGRAY, 0, 0);

        // クリッピング前の状態を保存
        canvas.save();

        // クリッピングを設定
        canvas.clipRect(rect);

        // アイテムを描画
        PointF _offset = new PointF(pos.x, pos.y - contentTop.y);
        for (UListItem item : mItems) {
            if (item.getBottom() < contentTop.y) continue;

            item.draw(canvas, paint, _offset);

            if (item.pos.y + item.size.height > contentTop.y + size.height) {
                // アイテムの下端が画面外にきたので以降のアイテムは表示されない
                break;
            }
        }

        // クリッピングを解除
        canvas.restore();
    }

    public boolean touchEvent(ViewTouch vt) {
        // アイテムのクリック判定処理
        PointF offset = new PointF(pos.x, pos.y + contentTop.y);
        for (UListItem item : mItems) {
            if (item.touchEvent(vt, offset)) {
                return true;
            }
        }

        if (super.touchEvent(vt)) {
            return true;
        }
        return false;
    }

    /**
     * for Debug
     */
    public void addDummyItems(int count) {

        updateWindow();
    }
}
