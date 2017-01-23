package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by shutaro on 2016/12/18.
 * <p>
 * プリセット単語帳ListViewのアイテム
 */

public class ListItemPresetBook extends UListItem implements UButtonCallbacks {
    /**
     * Enums
     */
    /**
     * Constants
     */
    public static final String TAG = "ListItemResult";

    public static final int ButtonIdAdd = 100100;
    private static final int ITEM_H = 200;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 15;
    private static final int ICON_W = 100;

    private static final int TEXT_SIZE = 50;
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BG_COLOR = Color.WHITE;

    private static final int STAR_ICON_W = 100;

    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private PresetBook mBook;
    private UButtonImage mAddButton;

    /**
     * Get/Set
     */
    public PresetBook getBook() {
        return mBook;
    }

    /**
     * Constructor
     */
    public ListItemPresetBook(UListItemCallbacks listItemCallbacks,
                          PresetBook book, int width)
    {
        super(listItemCallbacks, true, 0, width, ITEM_H, BG_COLOR);
        mBook = book;

        // Add Button
        Bitmap image = UResourceManager.getBitmapWithColor(R.drawable.add, UColor.Green);
        mAddButton = UButtonImage.createButton(this, ButtonIdAdd, 0,
                size.width - 150, (size.height - STAR_ICON_W) / 2,
                STAR_ICON_W, STAR_ICON_W, image, null);
        mAddButton.scaleRect(2.0f, 1.5f);

    }

    /**
     * Methods
     */
    /**
     * 描画処理
     *
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
        int _color = color;
        if (isTouchable && isTouching) {
            _color = pressedColor;
        }
        UDraw.drawRectFill(canvas, paint,
                new Rect((int) _pos.x, (int) _pos.y,
                        (int) _pos.x + size.width, (int) _pos.y + size.height),
                _color, FRAME_WIDTH, FRAME_COLOR);

        float x = _pos.x + MARGIN_H;
        float y = _pos.y + MARGIN_V;
        // Icon image
        UDraw.drawBitmap(canvas, paint, UResourceManager.getBitmapById(R.drawable.cards), x,
                _pos.y + (ITEM_H - ICON_W) / 2,
                ICON_W, ICON_W );
        x += ICON_W + MARGIN_H;

        // Name
        UDraw.drawTextOneLine(canvas, paint, mBook.mName, UAlignment.None, TEXT_SIZE,
                x, y, TEXT_COLOR);
        y += TEXT_SIZE + MARGIN_V;

        // Comment
        UDraw.drawTextOneLine(canvas, paint, mBook.mComment, UAlignment.None, TEXT_SIZE,
                x, y, TEXT_COLOR);

        // Add Button
        if (mAddButton != null) {
            mAddButton.draw(canvas, paint, _pos);
        }
    }

    /**
     * 毎フレーム呼ばれる処理
     * @return
     */
    public DoActionRet doAction() {
        if (mAddButton != null ) {
            return mAddButton.doAction();
        }
        return DoActionRet.None;
    }

    /**
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (mAddButton != null) {
            PointF offset2 = new PointF(pos.x + offset.x, pos.y + offset.y);
            if (mAddButton.touchEvent(vt, offset2)) {
                return true;
            }
        }
        if (super.touchEvent(vt, offset)) {
            return true;
        }
        return false;
    }

    /**
     * 高さを返す
     */
    public int getHeight() {
        return size.height;
    }


    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        if (mListItemCallbacks != null) {
            mListItemCallbacks.ListItemButtonClicked(this, id);
            return true;
        }
        return false;
    }
}