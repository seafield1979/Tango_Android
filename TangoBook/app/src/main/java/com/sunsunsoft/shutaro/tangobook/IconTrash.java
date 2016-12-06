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

/**
 * Created by shutaro on 2016/11/21.
 *
 * ゴミ箱アイコン
 * このアイコンにドラッグするとゴミ箱に入る
 */

public class IconTrash extends IconContainer {
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
    protected Bitmap image;
    protected Bitmap image2;

    /**
     * Get/Set
     */

    public TangoItem getTangoItem() {
        return card;
    }
    public TangoParentType getParentType() {
        return TangoParentType.Trash;
    }
    public void updateTitle(){}

    /**
     * Constructor
     */
    public IconTrash(View parentView, UIconWindow parentWindow, UIconCallbacks iconCallbacks) {
        // 自動整列するので座標は設定しない
        super(parentView, parentWindow, iconCallbacks, IconType.Trash,
                0, 0, ICON_W, ICON_H);

        title = parentView.getContext().getString(R.string.trash);
        setColor(ICON_COLOR);

        // 中のアイコンを表示するためのSubWindow
        UIconWindows windows = parentWindow.getWindows();
        subWindow = windows.getSubWindow();

        image = image2 = BitmapFactory.decodeResource(mParentView.getResources(), R.drawable.trash);
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
        UDraw.drawTextOneLine(canvas, paint, title, UDraw.UAlignment.CenterX, TEXT_SIZE,
                drawPos.x + ICON_W / 2, drawPos.y + ICON_H + TEXT_MARGIN, Color.BLACK);
    }

    /**
     * ドロップ可能かどうか
     * ドラッグ中のアイコンを他のアイコンの上に重ねたときにドロップ可能かを判定してアイコンの色を変えたりする
     * @param dstIcon
     * @return
     */
    public boolean canDrop(UIcon dstIcon, float dropX, float dropY) {
        return false;
    }

    /**
     * アイコンの中に入れることができるか
     * @return
     */
    public boolean canDropIn(UIcon dstIcon, float dropX, float dropY) {
        return false;
    }

    /**
     * ドロップ時の処理
     * @param dstIcon
     * @return 何かしら処理をした（再描画あり）
     */
    public boolean droped(UIcon dstIcon, float dropX, float dropY) {
        if (!canDrop(dstIcon, dropX, dropY)) return false;

        return true;
    }
}
