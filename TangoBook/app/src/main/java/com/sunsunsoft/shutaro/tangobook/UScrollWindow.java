package com.sunsunsoft.shutaro.tangobook;


import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by shutaro on 2016/12/09.
 *
 * client領域をスワイプでスクロールできるWindow
 */

public class UScrollWindow extends UWindow {
    /**
     * Enums
     */
    /**
     * Consts
     */

    /**
     * Member Variables
     */

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UScrollWindow(UWindowCallbacks callbacks, int priority, float x, float y, int width, int
            height, int color)
    {
        super(callbacks, priority, x, y, width, height, color);
    }

    /**
     * Methods
     */
    public boolean doAction() {
        return false;
    }

    public void drawContent(Canvas canvas, Paint paint) {

    }

    public boolean touchEvent(ViewTouch vt) {
        if (super.touchEvent(vt)) {
            return true;
        }
        // スクロール処理
        boolean isDraw = false;
        if (vt.type == TouchType.Moving) {
            if (contentSize.width > clientSize.width) {
                if (vt.moveX != 0) {
                    contentTop.x -= vt.moveX;
                    if (contentTop.x < 0) contentTop.x = 0;
                    if (contentTop.x + size.width > contentSize.width) {
                        contentTop.x = contentSize.width - size.width;
                    }
                    mScrollBarH.updateScroll((long)contentTop.x);
                    isDraw = true;

                }
            }
            if (contentSize.height > clientSize.height) {
                if (vt.moveY != 0) {
                    contentTop.y -= vt.moveY;
                    if (contentTop.y < 0) contentTop.y = 0;
                    if (contentTop.y + size.height > contentSize.height) {
                        contentTop.y = contentSize.height - size.height;
                    }
                    mScrollBarV.updateScroll((long)contentTop.y);
                    isDraw = true;
                }
            }
        }
        return isDraw;
    }

    /**
     * Callbacks
     */
}