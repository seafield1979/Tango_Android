package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by shutaro on 2016/12/14.
 *
 * 単語帳の学習履歴ListView(ListViewStudyHistory)に表示する項目
 */

public class ListItemStudiedBook extends UListItem{
    /**
     * Enums
     */
    enum ListItemStudiedBookType {
        Title,
        History     // 単語帳の学習履歴
    }

    /**
     * Constants
     */
    public static final String TAG = "ListItemStudiedBook";

    private static final int ButtonIdStar = 100100;
    private static final int TITLE_H = 80;
    private static final int TEXT_SIZE = 50;
    private static final int TEXT_COLOR = Color.WHITE;

    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 15;
    private static final int ITEM_HISTORY_H = TEXT_SIZE * 3 + MARGIN_V * 4;


    private static final int STAR_ICON_W = 100;

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
    private int mTextColor;

    /**
     * Get/Set
     */
    public ListItemStudiedBookType getType() {
        return mType;
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
        mTextColor = textColor;
        mBookHistory = history;

    }

    // ListItemResultType.OKのインスタンスを生成する
    public static ListItemStudiedBook createHistory(TangoBookHistory history,
                                          int width, int textColor,int bgColor) {
        ListItemStudiedBook instance = new ListItemStudiedBook(null,
                ListItemStudiedBookType.History, true, history,
                0, width, textColor, bgColor);

        TangoBook book = RealmManager.getBookDao().selectById(history.getBookId());

        instance.mTextDate = String.format("学習日時: %s",
                UUtil.convDateFormat(history.getStudiedDateTime()));
        instance.mTextName = "単語帳名: " + book.getName();
        instance.mTextInfo = String.format("  OK:%d  NG:%d    OK率:%.3f", history.getOkNum(), history
                .getNgNum(), history.getCorrectRatio());
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

        // Book名
        UDraw.drawTextOneLine(canvas, paint, mTextName, UAlignment.None, TEXT_SIZE, x, y, Color
                .rgb(50,150,50));
        y += TEXT_SIZE + MARGIN_V;
        // 学習日時
        UDraw.drawTextOneLine(canvas, paint, mTextDate, UAlignment.None, TEXT_SIZE, x, y, Color
                .rgb(255,0,255));
        y += TEXT_SIZE + MARGIN_V;
        // OK/NG数 正解率
        UDraw.drawTextOneLine(canvas, paint, mTextInfo, UAlignment.None, TEXT_SIZE - 5, x, y,
                mTextColor);
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
