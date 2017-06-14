package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.page.OptionItems;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;

/**
 * Created by shutaro on 2017/01/27.
 * オプションページのListViewに表示する項目
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
    private OptionItems mItemType;
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
                          OptionItems itemType, String title, boolean isTitle, int color, int
                                  bgColor,
                          float x, int width) {
        super(listItemCallbacks, !isTitle, x, width, TITLE_H, bgColor);
        this.mItemType = itemType;
        this.mTitle = title;
        this.mColor = color;
        this.mBgColor = bgColor;

        switch(mItemType) {
            case ColorBook:
            case ColorCard:
                size.height = 150;
                break;
            case CardTitle:
            case DefaultNameBook:
            case DefaultNameCard:
            case AddNgCard:
            case StudyMode4:
                size.height = 200;
                break;
        }

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

        // BG　タッチ中は色を変更
        int _color = mBgColor;
        if (isTouchable && isTouching) {
            _color = pressedColor;
        }
        UDraw.drawRectFill(canvas, paint,
                new Rect((int) _pos.x, (int) _pos.y,
                        (int) _pos.x + size.width, (int) _pos.y + size.height),
                _color, FRAME_WIDTH, FRAME_COLOR);

        UDraw.drawText(canvas, mTitle, UAlignment.Center, TEXT_SIZE,
                _pos.x + size.width / 2, _pos.y + size.height / 2, mColor);

        switch(mItemType) {
            case ColorBook:
            case ColorCard: {
                int color = MySharedPref.readInt(
                        (mItemType == OptionItems.ColorBook) ?
                                MySharedPref.DefaultColorBookKey :
                                MySharedPref.DefaultColorCardKey);
                if (color != 0) {
                    _pos.x += size.width - 150;
                    _pos.y += 20;
                    UDraw.drawRectFill(canvas, paint,
                            new Rect((int) _pos.x, (int) _pos.y, (int) _pos.x + 100, (int) _pos.y +
                                    size.height - 40),
                            color, 0, 0);
                }
            }
                break;
        }
    }

    /**
     * 高さを返す
     */
    public int getHeight() {
        return size.height;
    }
}
