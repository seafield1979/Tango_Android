package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

import java.util.List;

/**
 * 単語帳アイコン
 */
public class IconBook extends IconContainer {

    /**
     * Constant
     */
    public static final String TAG = "UIconRect";
    private static final int ICON_W = 150;
    private static final int ICON_H = 100;

    // アイコンの上のテキストのPadding
    private static final int TEXT_PAD_X = 10;
    private static final int TEXT_PAD_Y = 10;

    private static final int DISP_TITLE_LEN = 6;
    private static final int TEXT_SIZE = 40;
    private static final int ICON_COLOR = Color.rgb(100,200,100);

    /**
     * Member variable
     */
    protected TangoBook book;

    /**
     * Get/Set
     */
    public TangoItem getTangoItem() {
        return book;
    }

    public TangoParentType getParentType() {
        return TangoParentType.Book;
    }

    /**
     * Constructor
     */
    public IconBook(TangoBook book, View parentView, UIconWindow parent, UIconCallbacks iconCallbacks) {
        super(parent, iconCallbacks, IconType.Book, 0, 0, ICON_W, ICON_H);

        this.book = book;
        updateTitle();
        setColor(ICON_COLOR);

        UIconWindows windows = parentWindow.getWindows();
        subWindow = windows.getSubWindow();

        // Bookに表示するCardのアイコンを管理する
        mIconManager = UIconManager.createInstance(parentView, subWindow, iconCallbacks);

        // データベースから配下のCardを読み込む
        List<TangoCard> cards = RealmManager.getItemPosDao().selectCardsByBookId(book.getId());
        if (cards != null) {
            for (TangoItem item : cards) {
                mIconManager.addIcon(item, AddPos.Tail);
            }
        }
    }

    public void drawIcon(Canvas canvas,Paint paint, PointF offset) {

        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);
        // 色
        if (isLongTouched) {
            paint.setColor(longPressedColor);
        }
        else if (isTouched) {
            paint.setColor(touchedColor);
        }
        else if (isDroping) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
        } else if (isAnimating) {
            double v1 = ((double)animeFrame / (double)animeFrameMax) * 180;
            int alpha = (int)((1.0 -  Math.sin(v1 * RAD)) * 255);
            paint.setColor((alpha << 24) | (color & 0xffffff));
        } else {
            paint.setColor(color);
        }

        Rect drawRect = null;
        if (offset != null) {
            drawRect = new Rect(rect.left + (int)offset.x,
                    rect.top + (int)offset.y,
                    rect.right + (int)offset.x,
                    rect.bottom + (int)offset.y);
        } else {
            drawRect = rect;
        }
        canvas.drawRect(drawRect, paint);

        // Text
        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE);
        canvas.drawText( title, drawRect.left + TEXT_PAD_X, drawRect.top + TEXT_SIZE + TEXT_PAD_Y, paint);

        // 穴
        paint.setColor(Color.BLACK);
        float cx = drawRect.left + ICON_W - 30;
        float cy = drawRect.centerY();
        canvas.drawCircle( cx, cy, 15, paint);


    }

    /**
     * タイトルを更新する
     */
    public void updateTitle() {
        int len = (book.getName().length() < DISP_TITLE_LEN) ? book.getName().length() :
                DISP_TITLE_LEN;
        this.title = book.getName().substring(0, len);
    }

    @Override
    public void click() {
        super.click();
    }

    @Override
    public void longClick() {
        super.longClick();
    }

    @Override
    public void moving() {
        super.moving();
    }
}
