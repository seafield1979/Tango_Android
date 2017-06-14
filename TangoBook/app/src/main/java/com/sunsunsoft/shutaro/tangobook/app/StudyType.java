package com.sunsunsoft.shutaro.tangobook.app;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

/**
 * Created by shutaro on 2017/06/14.
 */

public enum StudyType {
    EtoJ(R.string.study_type_1),
    JtoE(R.string.study_type_2)
    ;
    private final int strId;

    StudyType(int strId) {
        this.strId = strId;
    }

    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyType toEnum(int value) {
        if (value < StudyType.values().length) {
            return StudyType.values()[value];
        }
        return StudyType.EtoJ;
    }
}
