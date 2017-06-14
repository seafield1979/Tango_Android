package com.sunsunsoft.shutaro.tangobook.app;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

/**
 * Created by shutaro on 2017/06/14.
 */

// 学習モード
public enum StudyMode {
    SlideOne(R.string.study_mode_1),
    SlideMulti(R.string.study_mode_2),
    Choice4(R.string.study_mode_3),
    Input(R.string.study_mode_4),
    ;

    private final int strId;

    StudyMode(int strId) {
        this.strId = strId;
    }

    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyMode toEnum(int value) {
        if (value < StudyMode.values().length) {
            return StudyMode.values()[value];
        }
        return StudyMode.SlideOne;
    }
}
