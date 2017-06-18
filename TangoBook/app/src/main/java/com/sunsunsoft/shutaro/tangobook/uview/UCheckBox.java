package com.sunsunsoft.shutaro.tangobook.uview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.TouchType;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.Size;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;

/**
 * Created by shutaro on 2016/12/23.
 *
 * チェックボックス
 * タップでON/OFFを切り替えられる
 * 右側にテキストを表示できる
 */

public class UCheckBox extends UDrawable {

    /**
     * Enums
     */

    /**
     * Constants
     */
    public static final String TAG = "UCheckBox";

    private static final int MARGIN_H = 50;
    protected static final int COLLISION_MARGIN = 30;
    protected static final int COLOR_BOX = UColor.LightBlue;

    /**
     * Member variables
     */
    protected UCheckBoxCallbacks mCheckBoxCallbacks;
    protected boolean isChecked;
    protected int mBoxWidth;
    protected String mText;
    protected int mTextSize;
    protected int mTextColor;

    /**
     * Get/Set
     */
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    /**
     * Constructor
     */
    public UCheckBox(UCheckBoxCallbacks callbacks, int drawPriority, float x, float y,
                     int canvasW, int boxWidth, String text,
                     int textSize, int textColor)
    {
        super(drawPriority, x, y, 0, 0);

        mCheckBoxCallbacks = callbacks;
        mBoxWidth = boxWidth;
        mText = text;
        mTextSize = textSize;
        mTextColor = textColor;

        // 描画サイズを計算する
        if (mText != null && mText.length() > 0) {
            Size _size = UDraw.getTextSize(canvasW, text, textSize);
            size.width = boxWidth + MARGIN_H + _size.width;
            size.height = (boxWidth > _size.height) ? boxWidth : _size.height;
        } else {
            size.width = size.height = boxWidth;
        }
    }

    /**
     * Methods
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }

        // Box
        UDraw.drawCheckbox(canvas, paint, isChecked, _pos.x, _pos.y,
                mBoxWidth, COLOR_BOX);

        // Text
        if (mText != null) {
            UDraw.drawTextOneLine(canvas, paint, mText, UAlignment.CenterY, mTextSize,
                    _pos.x + mBoxWidth + MARGIN_H, _pos.y + size.height / 2, mTextColor);
        }
    }

    public boolean touchEvent(ViewTouch vt, PointF offset)
    {
        if (offset == null) {
            offset = new PointF();
        }
        // チェック判定
        // 当たりは実際の見た目より大きく判定する
        // CheckBox部分をクリックしたらチェック状態が変わる
        if (vt.type == TouchType.Click) {
            if (new Rect((int)pos.x - COLLISION_MARGIN, (int)pos.y - COLLISION_MARGIN,
                    (int)pos.x + size.width + COLLISION_MARGIN,
                    (int)pos.y + mBoxWidth + COLLISION_MARGIN).contains(
                    (int)vt.touchX(offset.x), (int)vt.touchY(offset.y)))
            {
                isChecked = !isChecked;
                if (mCheckBoxCallbacks != null) {
                    mCheckBoxCallbacks.UCheckBoxChanged(isChecked);
                }
                return true;
            }
        }
        return false;
    }
}
