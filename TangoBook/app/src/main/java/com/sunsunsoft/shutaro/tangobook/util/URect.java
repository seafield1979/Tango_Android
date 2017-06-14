package com.sunsunsoft.shutaro.tangobook.util;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 自前のRectクラス
 */

public class URect {
    /**
     * 重なっているかチェック
     * @param rect1
     * @param rect2
     * @return true:一部分でも重なっている / false:全く重なっていない
     */
    public static boolean intersect(Rect rect1, Rect rect2) {
        if (rect1.right < rect2.left || rect1.left > rect2.right ||
                rect1.bottom < rect2.top || rect1.top > rect2.bottom )
        {
            return false;
        }
        return true;
    }
}
