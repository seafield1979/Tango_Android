package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by shutaro on 2016/10/24.
 */

public class IconCard extends IconBase {
    protected int radius;

    public IconCard(int x, int y, int width) {
        super(IconType.CIRCLE, x,y,width,width);

        color = Color.rgb(0,255,255);
        this.radius = width / 2;
    }

    public void draw(Canvas canvas, Paint paint) {
        // 線の種類
        paint.setStyle(Paint.Style.STROKE);
        // 線の太さ
        paint.setStrokeWidth(10);
        // 色
        paint.setColor(color);

        // 塗りつぶし
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // x,yが円を囲む矩形の左上にくるように座標を調整
        canvas.drawCircle(x+radius, y+radius, radius, paint);

        drawId(canvas, paint);
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
