package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by shutaro on 2016/10/24.
 */

public class IconCard extends IconBase {
    public IconCard(int x, int y, int width, int height) {
        super(IconType.RECT, x,y,width,height);

        color = Color.rgb(0,255,255);
    }

    public void draw(Canvas canvas, Paint paint) {
        // 線の種類
        paint.setStyle(Paint.Style.STROKE);
        // 線の太さ
        paint.setStrokeWidth(1);
        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // 色
        paint.setColor(color);

        canvas.drawRect((float)x,
                (float)y,
                (float)(x + width),
                (float)(y + height),
                paint);

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
