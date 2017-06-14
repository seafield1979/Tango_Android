package com.sunsunsoft.shutaro.tangobook.icon;

/**
 * Created by shutaro on 2017/06/14.
 * 単語帳ソートの種類
 */

public enum IconSortMode {
    None,
    TitleAsc,
    TitleDesc,
    CreateTimeAsc,
    CreateTimeDesc,
    StudiedTimeAsc,
    StudiedTimeDesc
    ;

    public static com.sunsunsoft.shutaro.tangobook.icon.IconSortMode toEnum(int val) {
        if (val > com.sunsunsoft.shutaro.tangobook.icon.IconSortMode.values().length) {
            return TitleAsc;
        }
        return com.sunsunsoft.shutaro.tangobook.icon.IconSortMode.values()[val];
    }
}
