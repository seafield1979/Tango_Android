package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.page.OptionItems;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
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
    private static final int TITLE_H = 27;
    private static final int TEXT_SIZE = 17;
    private static final int FRAME_WIDTH = 1;
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
        super(listItemCallbacks, !isTitle, x, width, UDpi.toPixel(TITLE_H), bgColor, UDpi.toPixel(FRAME_WIDTH), FRAME_COLOR);
        this.mItemType = itemType;
        this.mTitle = title;
        this.mColor = color;
        this.mBgColor = bgColor;

        switch(mItemType) {
            case ColorBook:
            case ColorCard:
                size.height = UDpi.toPixel(50);
                break;
            case CardTitle:
            case DefaultNameBook:
            case DefaultNameCard:
//            case AddNgCard:
            case StudyMode3:
            case StudyMode4:
                size.height = UDpi.toPixel(67);
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

        super.draw(canvas, paint, _pos);

        UDraw.drawText(canvas, mTitle, UAlignment.Center, UDpi.toPixel(TEXT_SIZE),
                _pos.x + size.width / 2, _pos.y + size.height / 2, mColor);

        switch(mItemType) {
            case ColorBook:
            case ColorCard: {
                int color = MySharedPref.readInt(
                        (mItemType == OptionItems.ColorBook) ?
                                MySharedPref.DefaultColorBookKey :
                                MySharedPref.DefaultColorCardKey);
                if (color != 0) {
                    _pos.x += size.width - UDpi.toPixel(50);
                    _pos.y += UDpi.toPixel(7);
                    UDraw.drawRectFill(canvas, paint,
                            new Rect((int) _pos.x, (int) _pos.y,
                                    (int) _pos.x + UDpi.toPixel(34),
                                    (int) _pos.y + size.height - UDpi.toPixel(13)),
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
