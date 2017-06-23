package com.sunsunsoft.shutaro.tangobook.uview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.scrollbar.UScrollWindow;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindowCallbacks;

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
    public static final int MARGIN_V = 7;

    /**
     * Member variables
     */
    protected LinkedList<UListItem> mItems = new LinkedList<>();
    protected UListItemCallbacks mListItemCallbacks;
    protected Rect mClipRect;

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
        super(callbacks, priority, x, y, width, height, color, 0, 0, UDpi.toPixel(10));
        mListItemCallbacks = listItemCallbacks;
        mClipRect = new Rect();
    }

    /**
     * Methods
     */
    public UListItem get(int index) {
        return mItems.get(index);
    }

    public int getItemNum() {return mItems.size(); }

    public void add(UListItem item) {
        item.setPos(0, mBottomY);
        item.setIndex(mItems.size());
        item.setListItemCallbacks(mListItemCallbacks);

        mItems.add(item);

        mBottomY += item.getHeight();
        mBottomY -= item.mFrameW / 2;       // フレーム部分は重なってもOK

        contentSize.setHeight((int)mBottomY);
    }

    public void update(UListItem oldItem, UListItem newItem) {
        int index = mItems.indexOf(oldItem);
        newItem.setPos(oldItem.getX(), oldItem.getY());
        mItems.set(index, newItem);
    }

    public void remove(UListItem item) {
        int index = mItems.indexOf(item);
        removeCore(item, index);
    }

    public void remove(int index) {
        UListItem item = mItems.get(index);
        removeCore(item, index);
    }

    public void clear() {
        mItems.clear();
        mBottomY = 0;
        setContentSize(0,0, true);
    }

    protected void removeCore(UListItem item, int index) {
        if (index == -1 || item == null) return;

        float y = item.getY();
        mItems.remove(index);
        // 削除したアイテム以降のアイテムのIndexと座標を詰める
        for (int i = index; i< mItems.size(); i++) {
            UListItem _item = mItems.get(i);
            _item.setIndex(i);
            _item.setY(y);
            y = _item.getBottom();
        }
        mBottomY = y;
    }

    public DoActionRet doAction() {
        DoActionRet ret = DoActionRet.None;
        for (UListItem item : mItems) {
            DoActionRet _ret = item.doAction();
            switch (_ret) {
                case Done:
                    return DoActionRet.Done;
                case Redraw:
                    ret = _ret;
                    break;
            }
        }
        return ret;
    }

    @Override
    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
        // クリッピング前の状態を保存
        canvas.save();

        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }
        // クリッピングを設定
        mClipRect.left = (int)_pos.x;
        mClipRect.right = (int)_pos.x + clientSize.width;
        mClipRect.top = (int)_pos.y;
        mClipRect.bottom = (int)_pos.y + clientSize.height;

        canvas.clipRect(mClipRect);

        // アイテムを描画
        PointF _offset = new PointF(_pos.x, _pos.y - contentTop.y);
        for (UListItem item : mItems) {
            if (item.getBottom() < contentTop.y) continue;

            item.draw(canvas, paint, _offset);

            if (item.getY() + item.getHeight() > contentTop.y + size.height) {
                // アイテムの下端が画面外にきたので以降のアイテムは表示されない
                break;
            }
        }

        // クリッピングを解除
        canvas.restore();
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (offset == null) {
            offset = new PointF();
        }
        // 領域外なら何もしない
        if (!getClientRect().contains((int)vt.touchX(-pos.x - offset.x),
                (int)vt.touchY(-pos.y - offset.y)))
        {
            return false;
        }

        // アイテムのクリック判定処理
        PointF _offset = new PointF(pos.x + offset.x, pos.y - contentTop.y + offset.y);
        boolean isDraw = false;

        if (super.touchEvent(vt, offset)) {
            return true;
        }

        for (UListItem item : mItems) {
            if (item.getBottom() < contentTop.y) continue;
            if (item.touchEvent(vt, _offset)) {
                isDraw = true;
            }
            if (item.getY() + item.getHeight() > contentTop.y + clientSize.height) {
                // アイテムの下端が画面外にきたので以降のアイテムは表示されない
                break;
            }
        }

        return isDraw;
    }

    public boolean touchUpEvent(ViewTouch vt) {
        boolean isDraw = false;
        if (vt.isTouchUp()) {
            for (UListItem item : mItems) {
                item.touchUpEvent(vt);
                isDraw = true;
            }
        }
        return isDraw;
    }

    /**
     * for Debug
     */
    public void addDummyItems(int count) {

        updateWindow();
    }
}
