package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.util.ConvDateMode;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoBookHistory;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;

/**
 * Created by shutaro on 2016/12/14.
 *
 * 単語帳の学習履歴ListView(ListViewStudyHistory)に表示する項目
 */


public class ListItemStudiedBook extends UListItem {
    /**
     * Enums
     */

    /**
     * Constants
     */
    public static final String TAG = "ListItemStudiedBook";

    private static final int TEXT_SIZE = 50;
    private static final int TEXT_SIZE2 = 44;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 15;
    private static final int ITEM_HISTORY_H = TEXT_SIZE * 3 + MARGIN_V * 4;
    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private ListItemStudiedBookType mType;
    private String mTextDate;
    private String mTextName;
    private String mTextInfo;
    private TangoBookHistory mBookHistory;

    /**
     * Get/Set
     */
    public ListItemStudiedBookType getType() {
        return mType;
    }

    public TangoBookHistory getBookHistory() {
        return mBookHistory;
    }

    /**
     * Constructor
     */
    public ListItemStudiedBook(UListItemCallbacks listItemCallbacks,
                               ListItemStudiedBookType type, boolean isTouchable,
                               TangoBookHistory history,
                          float x, int width, int textColor, int color) {
        super(listItemCallbacks, isTouchable, x, width, 0, color);
        mType = type;
        mBookHistory = history;

    }

    // ListItemResultType.OKのインスタンスを生成する
    public static ListItemStudiedBook createHistory(TangoBookHistory history,
                                          int width, int textColor,int bgColor) {
        ListItemStudiedBook instance = new ListItemStudiedBook(null,
                ListItemStudiedBookType.History, true, history,
                0, width, textColor, bgColor);

        TangoBook book = RealmManager.getBookDao().selectById(history.getBookId());
        if (book == null) {
            // 削除されるなどして存在しない場合は表示しない
            return null;
        }

        instance.mTextDate = String.format("学習日時: %s",
                UUtil.convDateFormat(history.getStudiedDateTime(), ConvDateMode.DateTime));
        instance.mTextName = UResourceManager.getStringById(R.string.book) + ": " + book
                .getName();
        instance.mTextInfo = String.format("OK:%d  NG:%d", history.getOkNum(), history
                .getNgNum());

        instance.size.height = ITEM_HISTORY_H;


        return instance;
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

        int _color = UColor.WHITE;
        int _textColor = UColor.BLACK;

        if (isTouching) {
            _color = UColor.addBrightness(_color, -0.2f);
        }

        UDraw.drawRectFill(canvas, paint,
                new Rect((int) _pos.x, (int) _pos.y, (int) _pos.x + size.width, (int) _pos.y + size.height),
                _color, FRAME_WIDTH, FRAME_COLOR);

        float x = _pos.x + MARGIN_H;
        float y = _pos.y + MARGIN_V;

        // Book名
        UDraw.drawTextOneLine(canvas, paint, mTextName, UAlignment.None,
                TEXT_SIZE, x, y, _textColor);
        y += TEXT_SIZE + MARGIN_V;

        // 学習日時
        UDraw.drawTextOneLine(canvas, paint, mTextDate, UAlignment.None,
                TEXT_SIZE2 , x, y, _textColor);
        y += TEXT_SIZE + MARGIN_V;

        // OK/NG数 正解率
        UDraw.drawTextOneLine(canvas, paint, mTextInfo, UAlignment.None,
                TEXT_SIZE2, x, y, _textColor);
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
     * 高さを返す
     */
    public int getHeight() {
        return size.height;
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        return false;
    }
}