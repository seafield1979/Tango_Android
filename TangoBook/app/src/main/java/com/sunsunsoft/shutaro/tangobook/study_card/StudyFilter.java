package com.sunsunsoft.shutaro.tangobook.study_card;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

/**
 * Created by shutaro on 2017/06/14.
 * 出題絞り込み
 */

public enum StudyFilter {
    All(R.string.study_filter_1),                // すべて出題
    NotLearned(R.string.study_filter_2),         // 未収得カードのみ
    ;

    private final int strId;
    StudyFilter(int strId) {
        this.strId = strId;
    }
    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyFilter toEnum(int value) {
        if (value < StudyFilter.values().length) {
            return StudyFilter.values()[value];
        }
        return StudyFilter.All;
    }
}
