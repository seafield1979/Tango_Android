package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * 単語カードのアイコン
 */

public class IconCard extends UIcon{
    /**
     * Consts
     */

    private static final int ICON_W = 150;
    private static final int ICON_H = 100;
    private static final int DISP_TITLE_LEN = 6;
    private static final int TEXT_PAD_X = 10;
    private static final int TEXT_PAD_Y = 10;

    /**
     * Member Variables
     */
    protected TangoCard card;
    protected String title;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */

    public IconCard(TangoCard card, UIconWindow parent, UIconCallbacks iconCallbacks) {
        this(card, parent, iconCallbacks, 0, 0);
    }

    public IconCard(TangoCard card, UIconWindow parent, UIconCallbacks iconCallbacks, int x, int y) {
        super(parent, iconCallbacks, IconType.Card,
                x, y, ICON_W, ICON_H);

        color = Color.rgb(0,255,255);
        this.card = card;
        this.title = card.getWordA().substring(0, DISP_TITLE_LEN);
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
        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);

        if (isLongTouched) {
            paint.setColor(longPressedColor);
        }
        else if (isTouched) {
            paint.setColor(touchedColor);
        } else if (isDroping) {
            // 外枠のみ
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

        // x,yが円を囲む矩形の左上にくるように座標を調整
        PointF drawPos = null;
        if (offset != null) {
            drawPos = new PointF(pos.x + offset.x, pos.y + offset.y);
        } else {
            drawPos = pos;
        }

        canvas.drawRect( drawPos.x, drawPos.y,
                drawPos.x + ICON_W, drawPos.y + ICON_H, paint);
        canvas.drawText( title, drawPos.x + TEXT_PAD_X, drawPos.y + TEXT_PAD_Y, paint);
    }
}
