package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/18.
 *
 * ListViewに表示する単語カードアイテム
 */

public class ListItemCard extends UListItem {
    /**
     * Enums
     */
    /**
     * Constants
     */
    public static final String TAG = "ListItemCard";

    private static final int TEXT_SIZE = 40;
    private static final int TEXT_SIZE2 = 42;
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BG_COLOR = Color.WHITE;
    private static final int ICON_W = 100;

    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 15;
    private static final int ITEM_H = TEXT_SIZE * 3 + MARGIN_V * 4;

    private static final int FRAME_WIDTH = 4;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private PresetCard mPresetCard;

    /**
     * Get/Set
     */


    /**
     * Constructor
     */
    public ListItemCard(UListItemCallbacks listItemCallbacks,
                             PresetCard card, int width)
    {
        super(listItemCallbacks, true, 0, width, ITEM_H, BG_COLOR);
        mPresetCard = card;
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
        // Icon image
        UDraw.drawBitmap(canvas, paint, UResourceManager.getBitmapById(R.drawable.card2), x,
                _pos.y + (ITEM_H - ICON_W) / 2,
                ICON_W, ICON_W );
        x += ICON_W + MARGIN_H;

        // WordA
        UDraw.drawTextOneLine(canvas, paint,
                UResourceManager.getStringById(R.string.word_a) + ": " + mPresetCard.mWordA,
                UAlignment.None, TEXT_SIZE,
                x, y, TEXT_COLOR);
        y += TEXT_SIZE + MARGIN_V;

        // WordB
        UDraw.drawTextOneLine(canvas, paint,
                UResourceManager.getStringById(R.string.word_b) + ": " + mPresetCard.mWordB,
                UAlignment.None, TEXT_SIZE,
                x, y, TEXT_COLOR);
        y += TEXT_SIZE + MARGIN_V;

        // Comment
        UDraw.drawTextOneLine(canvas, paint,
                UResourceManager.getStringById(R.string.comment) + ": " + mPresetCard.mComment,
                UAlignment.None, TEXT_SIZE,
                x, y, TEXT_COLOR);
    }
}