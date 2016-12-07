package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private static final int ICON_W = 120;
    private static final int ICON_H = 120;

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
    protected Bitmap image;

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
    public IconBook(TangoBook book, View parentView, UIconWindow parentWindow, UIconCallbacks
            iconCallbacks) {
        super(parentView, parentWindow, iconCallbacks, IconType.Book, 0, 0, ICON_W, ICON_H);

        this.book = book;
        updateTitle();
        setColor(ICON_COLOR);

        UIconWindows windows = parentWindow.getWindows();
        subWindow = windows.getSubWindow();

        image = BitmapFactory.decodeResource(mParentView.getResources(), R.drawable.notebook);

        // データベースから配下のCardを読み込む
        List<TangoCard> cards = RealmManager.getItemPosDao().selectCardsByBookId(book.getId());
        if (cards != null) {
            for (TangoItem item : cards) {
                mIconManager.addIcon(item, AddPos.Tail);
            }
        }
    }

    /**
     * アイコンの描画
     * @param canvas
     * @param paint
     * @param offset
     */
    public void drawIcon(Canvas canvas,Paint paint, PointF offset) {
        PointF drawPos;
        if (offset != null) {
            drawPos = new PointF(pos.x + offset.x, pos.y + offset.y);
        } else {
            drawPos = pos;
        }

        if (isLongTouched || isTouched || isDroping) {
            // 長押し、タッチ、ドロップ中はBGを表示
            UDraw.drawRoundRectFill(canvas, paint,
                    new RectF(drawPos.x, drawPos.y, drawPos.x + ICON_W, drawPos.y + ICON_H),
                    10, touchedColor, 0, 0);
        } else if (isAnimating) {
            // 点滅
            double v1 = ((double)animeFrame / (double)animeFrameMax) * 180;
            int alpha = (int)((1.0 -  Math.sin(v1 * RAD)) * 255);
            paint.setColor((alpha << 24) | (color & 0xffffff));
        } else {
            paint.setColor(color);
        }
        // icon
        // 領域の幅に合わせて伸縮
        canvas.drawBitmap(image, new Rect(0,0,image.getWidth(), image.getHeight()),
                new Rect((int)drawPos.x, (int)drawPos.y,
                        (int)drawPos.x + ICON_W,(int)drawPos.y + ICON_H),
                paint);

        // Text
        UDraw.drawTextOneLine(canvas, paint, title, UAlignment.CenterX, TEXT_SIZE,
                drawPos.x + ICON_W / 2, drawPos.y + ICON_H + TEXT_MARGIN, Color.BLACK);
    }

    /**
     * タイトルを更新する
     */
    public void updateTitle() {
        int len = (book.getName().length() < DISP_TITLE_LEN) ? book.getName().length() :
                DISP_TITLE_LEN;
        this.title = book.getName().substring(0, len);
    }

    /**
     * ドロップ可能かどうか
     * ドラッグ中のアイコンを他のアイコンの上に重ねたときにドロップ可能かを判定してアイコンの色を変えたりする
     * @param dstIcon
     * @return
     */
    public boolean canDrop(UIcon dstIcon, float dropX, float dropY) {
        // ドロップ座標がアイコンの中に含まれているかチェック
        if (!dstIcon.checkDrop(dropX, dropY)) return false;

        return true;
    }

    /**
     * アイコンの中に入れることができるか
     * @return
     */
    public boolean canDropIn(UIcon dstIcon, float dropX, float dropY) {
        if (dstIcon.getType() == IconType.Trash) {
            if (dstIcon.checkDrop(dropX, dropY)) {
                return true;
            }
        }
        return false;
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
