package com.sunsunsoft.shutaro.tangobook;

import android.util.FloatMath;

/**
 * Created by shutaro on 2016/12/07.
 *
 * 便利関数
 */

public class UUtil {
    public static final double RAD = 3.1415 / 180.0;

    /**
     * sinテーブルの0->90度の 0.0~1.0 の値を取得する
     *
     * @param ratio  0.0 ~ 1.0
     * @return 0.0 ~ 1.0
     */
    public static float toAccel(float ratio) {
        return (float)Math.sin(ratio * 90 * RAD);
    }

    /**
     * 1.0 - cosテーブルの0->90度 の0.0~1.0の値を取得する
     * @param ratio
     * @return
     */
    public static float toDecel(float ratio) {
        return (float)(1.0 - Math.cos(ratio * 90 * RAD));
    }
}
