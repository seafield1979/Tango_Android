package com.sunsunsoft.shutaro.tangobook.uview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by shutaro on 2016/12/21.
 *
 * 色を選択するためのView
 * クリックすると自分の色情報(mColor)を返す
 */

interface ColorViewCallbacks {
    /**
     * クリックされた
     * @param color
     */
    void ColorViewClicked(int color);
}

public class ColorView extends View {
    /**
     * Constants
     */
    public static final String TAG = "ColorView";

    /**
     * Member varialbes
     */
    private Context mContext;
    private Paint paint = new Paint();
    private int mColor;
    private ColorViewCallbacks mCallbacks;

    /**
     * Get/Set
     */
    public int getColor() {
        return mColor;
    }
    public void setColor(int color) {
        mColor = color;
    }

    /**
     * Constructor
     */
    public ColorView(Context context) {
        this(context, null);
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        // 色を取得
        for(int i = 0; i < attrs.getAttributeCount(); i++) {
            if (attrs.getAttributeName(i).equals("bg_color")) {
                String colorStr = attrs.getAttributeValue(i);
                mColor = Color.parseColor(colorStr) | 0xff000000;
                break;
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        // 背景塗りつぶし
        canvas.drawColor(mColor);
    }
}
