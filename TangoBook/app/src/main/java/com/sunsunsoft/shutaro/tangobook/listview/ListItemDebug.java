package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;

/**
 * Created by shutaro on 2016/12/15.
 *
 * デバッグページのListViewのアイテム
 */

public class ListItemDebug extends UListItem {

    /**
     * Constants
     */
    private static final int FRAME_WIDTH = 5;
    private static final int FRAME_COLOR = Color.BLACK;
    private static final int TEXT_SIZE = 50;
    private static final int ITEM_H = 150;
    private static final int BG_COLOR = Color.WHITE;

    /**
     * Member variables
     */
    private String mText;

    /**
     * Constructor
     */
    public ListItemDebug(UListItemCallbacks listItemCallbacks,
                         String text, boolean isTouchable,
                         float x, int width)
    {
        super(listItemCallbacks, isTouchable, x, width, ITEM_H, BG_COLOR, FRAME_WIDTH, FRAME_COLOR);

        mText = text;
    }

    /**
     * Methods
     */
    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }

        super.draw(canvas, paint, _pos);

        UDraw.drawTextOneLine(canvas, paint, mText, UAlignment.Center, TEXT_SIZE,
                    _pos.x + size.width / 2, _pos.y + size.height / 2, Color.BLACK);

    }

    /**
     *
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (super.touchEvent(vt, offset)) {
            return true;
        }
        return false;
    }

}
