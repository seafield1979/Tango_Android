package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2016/11/21.
 */

public class IconBox extends UIcon{
    /**
     * Consts
     */
    private static final int ICON_W = 120;
    private static final int ICON_H = 120;
    private static final int DISP_TITLE_LEN = 6;
    private static final int TEXT_PAD_X = 10;
    private static final int TEXT_PAD_Y = 10;
    private static final int TEXT_SIZE = 40;
    private static final int ICON_COLOR = Color.rgb(200,100,100);

    /**
     * Member Variables
     */
    protected TangoBox box;
    protected UIconManager mIconManager;

    // ボックスの中身を表示しているウィンドウ
    protected UIconWindow subWindow;

    /**
     * Get/Set
     */
    public TangoItem getTangoItem() {
        return box;
    }

    public UIconManager getIconManager() {
        return mIconManager;
    }
    public List<UIcon> getIcons() {
        return mIconManager.getIcons();
    }

    public UIconWindow getSubWindow() {
        return subWindow;
    }

    /**
     * Constructor
     */
    public IconBox(TangoBox box, View parentView, UIconWindow parent, UIconCallbacks iconCallbacks) {
        super(parent, iconCallbacks, IconType.Box,
                0, 0, ICON_W, ICON_H);

        this.box = box;
        this.title = box.getName().substring(0, DISP_TITLE_LEN);
        setColor(box.getColor());

        // Box以下に表示するアイコンを管理するIconManagerを生成
        mIconManager = UIconManager.createInstance(parentView, subWindow, iconCallbacks);

        // データベースから配下のアイテムをロード
        List<TangoItem> items = RealmManager.getItemPosDao().selectByBoxId(box.getId(), true);
        if (items != null) {
            for (TangoItem item : items) {
                mIconManager.addIcon(item, AddPos.Tail);
            }
        }
    }

    /**
     * Methods
     */

    /**
     * Draw Box Icon
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

        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE);
        canvas.drawText( title, drawPos.x + TEXT_PAD_X, drawPos.y + TEXT_SIZE + TEXT_PAD_Y, paint);

    }
}
