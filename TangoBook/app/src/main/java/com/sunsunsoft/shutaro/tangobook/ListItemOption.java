package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by shutaro on 2017/01/27.
 * オプションページのListView二表示する項目
 */

public class ListItemOption extends UListItem {
    /**
     * Constants
     */
    public static final String TAG = "ListItemOption";
    private static final int TITLE_H = 80;
    private static final int TITLE_H2 = 150;
    private static final int TEXT_SIZE = 50;
    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private String mTitle;
    private int mColor;
    private int mBgColor;

    /**
     * Get/Set
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Constructor
     */
    public ListItemOption(UListItemCallbacks listItemCallbacks,
                          String title, boolean isTitle, int color, int bgColor,
                          float x, int width) {
        super(listItemCallbacks, !isTitle, x, width, isTitle ? TITLE_H : TITLE_H2, bgColor);
        this.mTitle = title;
        this.mColor = color;
        this.mBgColor = bgColor;
    }

    /**
     * Methods
     */
//    public DoActionRet doAction() {
//        return DoActionRet.None;
//    }
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

        // BG　タッチ中は色を変更
        int _color = mBgColor;
        if (isTouchable && isTouching) {
            _color = pressedColor;
        }
        UDraw.drawRectFill(canvas, paint,
                new Rect((int) _pos.x, (int) _pos.y,
                        (int) _pos.x + size.width, (int) _pos.y + size.height),
                _color, FRAME_WIDTH, FRAME_COLOR);

        UDraw.drawTextOneLine(canvas, paint, mTitle, UAlignment.Center, TEXT_SIZE,
                _pos.x + size.width / 2, _pos.y + size.height / 2, mColor);
    }

    /**
     *
     * @param vt
     * @return
     */
//    public boolean touchEvent(ViewTouch vt, PointF offset) {
//        boolean isDraw = false;
//        switch(vt.type) {
//            case Touch:
//                if (isTouchable) {
//                    if (rect.contains((int) (vt.touchX() - offset.x),
//                            (int) (vt.touchY() - offset.y))) {
//                        isTouching = true;
//                        isDraw = true;
//                    }
//                }
//                break;
//        }
//
//        return isDraw;
//    }

    /**
     * 高さを返す
     */
    public int getHeight() {
        return size.height;
    }
}
