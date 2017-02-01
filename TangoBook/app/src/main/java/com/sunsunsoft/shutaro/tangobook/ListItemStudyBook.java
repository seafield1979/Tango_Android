package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/16.
 *
 * 学習する単語帳を選択するListViewのアイテム
 */

public class ListItemStudyBook extends UListItem {
    /**
     * Enums
     */

    /**
     * Constants
     */
    public static final String TAG = "ListItemStudiedBook";

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
    private String mTextName;
    private String mStudiedDate;
    private String mCardCount;
    private TangoBook mBook;
    private Bitmap mIcon;

    /**
     * Get/Set
     */
    public TangoBook getBook() {
        return mBook;
    }

    /**
     * Constructor
     */
    public ListItemStudyBook(UListItemCallbacks listItemCallbacks,
                             TangoBook book, int width, int color)
    {
        super(listItemCallbacks, true, 0, width, ITEM_H, color);
        mBook = book;

        // 単語帳名
        mTextName = UResourceManager.getStringById(R.string.book_name2) + " : " + book.getName();

        // 単語帳アイコン(色あり)
        mIcon = UResourceManager.getBitmapWithColor(R.drawable.cards, book.getColor());

        // カード数 & 覚えていないカード数
        int count = RealmManager
                .getItemPosDao().countInParentType(TangoParentType.Book, book.getId());
        int ngCount = RealmManager.getItemPosDao().countCardInBook(book.getId(),
                TangoItemPosDao.BookCountType.NG);

        mCardCount = UResourceManager.getStringById(R.string.card_count) + ": " + count + "  " +
                UResourceManager.getStringById(R.string.count_not_learned) + ": " + ngCount;

        // 最終学習日
        Date date = book.getLastStudiedTime();
        String dateStr = (date == null) ? " --- " : UUtil.convDateFormat(date, ConvDateMode.DateTime);

        mStudiedDate = String.format("学習日時 : %s", dateStr);
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
                new Rect((int) _pos.x, (int) _pos.y,
                        (int) _pos.x + size.width, (int) _pos.y + size.height),
                _color, FRAME_WIDTH, FRAME_COLOR);

        float x = _pos.x + MARGIN_H;
        float y = _pos.y + MARGIN_V;
        // Icon image
        UDraw.drawBitmap(canvas, paint, mIcon, x,
                _pos.y + (ITEM_H - ICON_W) / 2,
                ICON_W, ICON_W );
        x += ICON_W + MARGIN_H;
        // Book名
        UDraw.drawTextOneLine(canvas, paint, mTextName, UAlignment.None, TEXT_SIZE, x, y, Color
                .rgb(50,150,50));
        y += TEXT_SIZE + MARGIN_V;
        // 学習日時
        UDraw.drawTextOneLine(canvas, paint, mStudiedDate, UAlignment.None, TEXT_SIZE2, x, y,
                TEXT_COLOR);
        y += TEXT_SIZE + MARGIN_V;

        // カード数
        UDraw.drawTextOneLine(canvas, paint, mCardCount , UAlignment.None, TEXT_SIZE2,
                x, y, UColor.DarkGray);
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

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        return false;
    }
}
