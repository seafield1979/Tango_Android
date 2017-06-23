package com.sunsunsoft.shutaro.tangobook.listview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.preset.PresetCard;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.FontSize;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.UListItemCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

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

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BG_COLOR = Color.WHITE;
    private static final int ICON_W = 35;

    private static final int MARGIN_H = 17;
    private static final int MARGIN_V = 5;

    private static final int FRAME_WIDTH = 2;
    private static final int FRAME_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private PresetCard mPresetCard;

    // Dpi計算済み
    private int itemH, iconW;

    /**
     * Get/Set
     */


    /**
     * Constructor
     */
    public ListItemCard(UListItemCallbacks listItemCallbacks,
                             PresetCard card, int width)
    {
        super(listItemCallbacks, true, 0, width, UDraw.getFontSize(FontSize.M) * 3 + UDpi.toPixel(MARGIN_V) * 4, BG_COLOR, FRAME_WIDTH, FRAME_COLOR);
        mPresetCard = card;
        itemH = UDraw.getFontSize(FontSize.M) * 3 + UDpi.toPixel(MARGIN_V) * 4;
        iconW = UDpi.toPixel(ICON_W);
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

        float x = _pos.x + MARGIN_H;
        float marginV = (itemH - UDraw.getFontSize(FontSize.M) * 2) / 3;
        float y = _pos.y + marginV;
        int fontSize = UDraw.getFontSize(FontSize.M);

        // Icon image
        UDraw.drawBitmap(canvas, paint, UResourceManager.getBitmapById(R.drawable.card), x,
                _pos.y + (itemH - iconW) / 2,
                iconW, iconW );
        x += iconW + UDpi.toPixel(MARGIN_H);

        // WordA
        UDraw.drawTextOneLine(canvas, paint,
                UResourceManager.getStringById(R.string.word_a) + ": " + mPresetCard.mWordA,
                UAlignment.None, fontSize,
                x, y, TEXT_COLOR);
        y += fontSize + marginV;

        // WordB
        UDraw.drawTextOneLine(canvas, paint,
                UResourceManager.getStringById(R.string.word_b) + ": " + mPresetCard.mWordB,
                UAlignment.None, fontSize,
                x, y, TEXT_COLOR);
    }
}