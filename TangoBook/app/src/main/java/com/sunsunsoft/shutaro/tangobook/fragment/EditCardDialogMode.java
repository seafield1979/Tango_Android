package com.sunsunsoft.shutaro.tangobook.fragment;

/**
 * Created by shutaro on 2017/06/14.
 */

public enum EditCardDialogMode {
    Create,     // 新しくアイコンを作成する
    Edit        // 既存のアイコンを編集する
    ;

    public static EditCardDialogMode toEnum(int value) {
        for (EditCardDialogMode id : values()) {
            if (id.ordinal() == value) {
                return id;
            }
        }
        return EditCardDialogMode.Create;
    }
}
