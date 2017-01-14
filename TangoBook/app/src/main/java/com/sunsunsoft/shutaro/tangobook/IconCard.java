package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 単語カードのアイコン
 */

public class IconCard extends UIcon{
    /**
     * Consts
     */
    private static final int ICON_W = 120;
    private static final int ICON_H = 120;
    private static final int ICON_COLOR = Color.rgb(100,100,200);

    /**
     * Member Variables
     */
    protected TangoCard card;

    /**
     * Get/Set
     */

    public TangoItem getTangoItem() {
        return card;
    }

    /**
     * Constructor
     */

    public IconCard(TangoCard card, UIconWindow parentWindow, UIconCallbacks
            iconCallbacks)
    {
        this(card, parentWindow, iconCallbacks, 0, 0);

    }

    public IconCard(TangoCard card, UIconWindow parentWindow, UIconCallbacks
            iconCallbacks, int x, int y)
    {
        super(parentWindow, iconCallbacks, IconType.Card,
                x, y, ICON_W, ICON_H);

        this.card = card;
        updateTitle();
        setColor(card.getColor());

        // アイコン画像の読み込み
        image = UResourceManager.getBitmapWithColor(R.drawable.card, card.getColor());
    }

    /**
     * Methods
     */

    /**
     * カードアイコンを描画
     * 長方形の中に単語のテキストを最大 DISP_TITLE_LEN 文字表示
     * @param canvas
     * @param paint
     * @param offset
     */
    public void drawIcon(Canvas canvas, Paint paint, PointF offset) {
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
        UDraw.drawTextOneLine(canvas, paint, title, UAlignment.Center, TEXT_SIZE,
                drawPos.x + ICON_W / 2, drawPos.y + ICON_H + TEXT_MARGIN, Color.BLACK);
        // New!
        if (card.isNewFlag()) {
            if (newTextView == null) {
                createNewBadge(canvas);
            }
            newTextView.draw(canvas, paint,
                    new PointF(drawPos.x + ICON_W / 2, drawPos.y + ICON_W / 2));
        }
    }

    /**
     * タイトルに表示する文字列を更新
     */
    public void updateTitle() {
        // 改行ありなら１行目のみ切り出す
        if (card.getWordA() == null) return;
        String[] strs = card.getWordA().split("\n");

        if (strs[0].length() < DISP_TITLE_LEN) {
            this.title = strs[0];
        } else {
            this.title = strs[0].substring(0, DISP_TITLE_LEN);
        }
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
        if (dstIcon.getType() != IconType.Card) {
            if (dstIcon.checkDrop(dropX, dropY)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ドロップ時の処理
     * @param dstIcon
     * @return 何かしら処理をした（再描画あり）
     */
    public boolean droped(UIcon dstIcon, float dropX, float dropY) {
        // 全面的にドロップはできない
        if (!canDrop(dstIcon, dropX, dropY)) return false;

        return true;
    }

    /**
     * Newフラグ設定
     */
    public void setNewFlag(boolean newFlag) {
        if (card.isNewFlag() != newFlag) {
            card.setNewFlag(newFlag);
            RealmManager.getCardDao().updateNewFlag(card, newFlag);
        }
    }
}
