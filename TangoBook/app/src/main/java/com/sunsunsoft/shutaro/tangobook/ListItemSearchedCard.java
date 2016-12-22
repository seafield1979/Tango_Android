package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/22.
 *
 * 検索結果ListViewのアイテム
 */

public class ListItemSearchedCard extends UListItem {
    /**
     * Constants
     */
    public static final String TAG = "ListItemSearchedCard";

    private static final int MAX_TEXT_LEN = 20;

    private static final int TEXT_SIZE = 50;
    private static final int TEXT_SIZE2 = 42;
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int ICON_W = 100;

    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 15;
    private static final int ITEM_H = TEXT_SIZE * 3 + MARGIN_V * 4;

    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private TangoCard mCard;
    private String mWordA;
    private String mWordB;
    private TangoBook mParentBook;

    /**
     * Get/Set
     */
    public TangoCard getCard() {
        return mCard;
    }

    /**
     * Constructor
     */
    public ListItemSearchedCard(UListItemCallbacks listItemCallbacks,
                             TangoCard card, int width, int color)
    {
        super(listItemCallbacks, true, 0, width, ITEM_H, color);
        mCard = card;

        mWordA = UResourceManager.getStringById(R.string.word_a) + " : " +
                UUtil.convString(card.getWordA(), true, MAX_TEXT_LEN) +
                " (id:" + card.getId() + ")";
        mWordB = UResourceManager.getStringById(R.string.word_b) + " : " +
                UUtil.convString(card.getWordB(), true, MAX_TEXT_LEN);

        TangoItemPos itemPos = RealmManager.getItemPosDao().selectCardParent(card.getId());
        if (itemPos != null) {
            mParentBook = RealmManager.getBookDao().selectById(itemPos.getParentId());
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
        int _color = color;
        if (isTouchable && isTouching) {
            _color = pressedColor;
        }
        UDraw.drawRectFill(canvas, paint,
                new Rect((int) _pos.x, (int) _pos.y, (int) _pos.x + size.width, (int) _pos.y + size.height),
                _color, FRAME_WIDTH, FRAME_COLOR);

        float x = _pos.x + MARGIN_H;
        float y = _pos.y + MARGIN_V;

        // WordA
        UDraw.drawTextOneLine(canvas, paint, mWordA, UAlignment.None, TEXT_SIZE2, x, y, TEXT_COLOR);
        y += TEXT_SIZE + MARGIN_V;

        // WordB
        UDraw.drawTextOneLine(canvas, paint, mWordB, UAlignment.None, TEXT_SIZE2, x, y, TEXT_COLOR);
        y += TEXT_SIZE + MARGIN_V;

        // parent book
        if (mParentBook != null) {
            String bookName = UResourceManager.getStringById(R.string.book_name) +
                    " : " + mParentBook.getName() +
                    " (id:" + mParentBook.getId() + ")";
            UDraw.drawTextOneLine(canvas, paint, bookName, UAlignment.None, TEXT_SIZE2, x,
                    y, TEXT_COLOR);
        }
    }

    /**
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (super.touchEvent(vt, offset)) {
            return true;
        }
        return false;
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        return false;
    }
}
