package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.database.TangoItemPos;
import com.sunsunsoft.shutaro.tangobook.database.TangoParentType;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;

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

    private static final int TEXT_SIZE = 17;
    private static final int TEXT_SIZE2 = 14;
    private static final int TEXT_COLOR = Color.BLACK;

    private static final int MARGIN_H = 17;
    private static final int MARGIN_V = 5;

    private static final int FRAME_WIDTH = 1;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private TangoCard mCard;
    private String mWordA;
    private String mWordB;
    private TangoItemPos mItemPos;
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
        super(listItemCallbacks, true, 0, width, UDpi.toPixel(TEXT_SIZE) * 3 + UDpi.toPixel(MARGIN_V) * 4, color, UDpi.toPixel(FRAME_WIDTH), FRAME_COLOR);
        mCard = card;

        mWordA = UResourceManager.getStringById(R.string.word_a) + " : " +
                UUtil.convString(card.getWordA(), true, 0, MAX_TEXT_LEN);
        mWordB = UResourceManager.getStringById(R.string.word_b) + " : " +
                UUtil.convString(card.getWordB(), true, 0, MAX_TEXT_LEN);

        mItemPos = RealmManager.getItemPosDao().selectCardParent(card.getId());
        if (mItemPos != null) {
            mParentBook = RealmManager.getBookDao().selectById(mItemPos.getParentId());
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

        float x = _pos.x + UDpi.toPixel(MARGIN_H);
        float y = _pos.y + UDpi.toPixel(MARGIN_V);

        // WordA
        UDraw.drawTextOneLine(canvas, paint, mWordA, UAlignment.None, UDpi.toPixel(TEXT_SIZE2), x, y, TEXT_COLOR);
        y += UDpi.toPixel(TEXT_SIZE + MARGIN_V);

        // WordB
        UDraw.drawTextOneLine(canvas, paint, mWordB, UAlignment.None, UDpi.toPixel(TEXT_SIZE2), x, y, TEXT_COLOR);
        y += UDpi.toPixel(TEXT_SIZE + MARGIN_V);

        // parent book
        String location = null;
        if (mParentBook != null) {
            location = UResourceManager.getStringById(R.string.where_card) +
                    " : " + UResourceManager.getStringById(R.string.book) + " " +
                    mParentBook.getName();

        } else if (mItemPos != null)  {
            // ホームかゴミ箱の中
            location = UResourceManager.getStringById(R.string.where_card) +
                    " : " +
                    UResourceManager.getStringById(
                    (mItemPos.getParentType() == TangoParentType.Home.ordinal()) ? R.string.home
                            : R.string.trash);

        }
        UDraw.drawTextOneLine(canvas, paint, location, UAlignment.None, UDpi.toPixel(TEXT_SIZE2), x,
                y, TEXT_COLOR);
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
