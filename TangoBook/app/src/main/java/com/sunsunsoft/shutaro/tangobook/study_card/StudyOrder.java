package com.sunsunsoft.shutaro.tangobook.study_card;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

/**
 * Created by shutaro on 2017/06/14.
 * 並び順
 */
public enum StudyOrder {
    Normal(R.string.study_order_1),     // 通常（単語帳の並び順）
    Random(R.string.study_order_2)      // ランダムに並び替え
    ;

    private final int strId;
    StudyOrder(int strId) {
        this.strId = strId;
    }
    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyOrder toEnum(int value) {
        if (value < StudyOrder.values().length) {
            return StudyOrder.values()[value];
        }
        return StudyOrder.Normal;
    }
}