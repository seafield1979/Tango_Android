package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by shutaro on 2016/11/21.
 */

public class IconCard extends UIcon{
    private static final int ICON_W = 150;

    protected int radius;

    public IconCard(UIconWindow parent, UIconCallbacks iconCallbacks) {
        this(parent, iconCallbacks, 0, 0, ICON_W);
    }

    public IconCard(UIconWindow parent, UIconCallbacks iconCallbacks, int x, int y, int width) {
        super(parent, iconCallbacks, IconType.Card, x,y,width,width);

        color = Color.rgb(0,255,255);
        this.radius = width / 2;
    }

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
        canvas.drawCircle(drawPos.x+radius, drawPos.y+radius, radius, paint);
    }
}
