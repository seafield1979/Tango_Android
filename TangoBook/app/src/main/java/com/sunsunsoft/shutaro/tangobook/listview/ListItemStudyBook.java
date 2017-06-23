package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.util.ConvDateMode;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoItemPosDao;
import com.sunsunsoft.shutaro.tangobook.database.TangoParentType;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;

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

    private static final int TEXT_SIZE = 17;
    private static final int TEXT_SIZE2 = 14;
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int ICON_W = 45;

    private static final int MARGIN_H = 17;
    private static final int MARGIN_V = 5;

    private static final int FRAME_WIDTH = 1;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private String mTextName;
    private String mStudiedDate;
    private String mCardCount;
    private TangoBook mBook;
    private Bitmap mIcon;

    // Dpi計算結果
    private int itemH;

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
        super(listItemCallbacks, true, 0, width, UDpi.toPixel(TEXT_SIZE) * 3 + UDpi.toPixel(MARGIN_V) * 4, color, FRAME_WIDTH, FRAME_COLOR);
        mBook = book;
        itemH = UDpi.toPixel(TEXT_SIZE) * 3 + UDpi.toPixel(MARGIN_V) * 4;

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

        super.draw(canvas, paint, _pos);

        float x = _pos.x + UDpi.toPixel(MARGIN_H);
        float y = _pos.y + UDpi.toPixel(MARGIN_V);
        int margin = UDpi.toPixel(TEXT_SIZE + MARGIN_V);

        // Icon image
        UDraw.drawBitmap(canvas, paint, mIcon, x,
                _pos.y + (itemH - UDpi.toPixel(ICON_W)) / 2,
                UDpi.toPixel(ICON_W), UDpi.toPixel(ICON_W) );
        x += UDpi.toPixel(ICON_W + MARGIN_H);
        // Book名
        UDraw.drawTextOneLine(canvas, paint, mTextName, UAlignment.None, UDpi.toPixel(TEXT_SIZE), x, y, Color
                .rgb(50,150,50));
        y += margin;
        // 学習日時
        UDraw.drawTextOneLine(canvas, paint, mStudiedDate, UAlignment.None, UDpi.toPixel(TEXT_SIZE2), x, y,
                TEXT_COLOR);
        y += margin;

        // カード数
        UDraw.drawTextOneLine(canvas, paint, mCardCount , UAlignment.None, UDpi.toPixel(TEXT_SIZE2),
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
