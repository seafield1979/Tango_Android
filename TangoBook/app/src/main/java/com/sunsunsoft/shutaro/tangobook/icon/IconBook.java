package com.sunsunsoft.shutaro.tangobook.icon;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.database.TangoItem;
import com.sunsunsoft.shutaro.tangobook.database.TangoParentType;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

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

    public List<TangoCard> getItems() {
        List<TangoCard> list = RealmManager.getItemPosDao().selectCardsByBookId(book.getId());
        return list;
    }

    /**
     * Constructor
     */
    public IconBook(TangoBook book, UIconWindow parentWindow, UIconCallbacks
            iconCallbacks) {
        super(parentWindow, iconCallbacks, IconType.Book, 0, 0, ICON_W, ICON_H);

        this.book = book;
        updateTitle();
        setColor(ICON_COLOR);

        UIconWindows windows = parentWindow.getWindows();
        subWindow = windows.getSubWindow();

        image = UResourceManager.getBitmapWithColor(R.drawable.cards, book.getColor());
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

        if (isLongTouched || isTouched || isDroped) {
            // 長押し、タッチ、ドロップ中はBGを表示
            UDraw.drawRoundRectFill(canvas, paint,
                    new RectF(drawPos.x, drawPos.y, drawPos.x + ICON_W, drawPos.y + ICON_H),
                    10, touchedColor, 0, 0);
        } else if (isAnimating) {
            // 点滅
            double v1 = ((double)animeFrame / (double)animeFrameMax) * 180;
            int alpha = (int)((1.0 -  Math.sin(v1 * UDrawable.RAD)) * 255);
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
                drawPos.x + ICON_W / 2, drawPos.y + ICON_H + UIcon.TEXT_MARGIN, Color.BLACK);

        // New!
        if (book.isNewFlag()) {
            if (UIcon.newTextView == null) {
                createNewBadge(canvas);
            }
            UIcon.newTextView.draw(canvas, paint,
                    new PointF(drawPos.x + ICON_W / 2, drawPos.y + ICON_H - UIcon.NEW_TEXT_SIZE));
        }
    }

    /**
     * タイトルを更新する
     */
    public void updateTitle() {
        int len = (book.getName().length() < UIcon.DISP_TITLE_LEN) ? book.getName().length() :
                UIcon.DISP_TITLE_LEN;
        this.title = book.getName().substring(0, len);
    }

    /**
     * Newフラグ設定
     */
    public void setNewFlag(boolean newFlag) {
        if (book.isNewFlag() != newFlag) {
            book.setNewFlag(newFlag);
            RealmManager.getBookDao().updateNewFlag(book, newFlag);
        }
    }


    /**
     * ドロップ可能かどうか
     * ドラッグ中のアイコンを他のアイコンの上に重ねたときにドロップ可能かを判定してアイコンの色を変えたりする
     * @param dstIcon
     * @return
     */
    public boolean canDrop(UIcon dstIcon, float dropX, float dropY) {
        // ドロップ先のアイコンがサブWindowの中なら不可能
        if (dstIcon.getParentWindow().getType() == UIconWindow.WindowType.Sub) {
            return false;
        }
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
