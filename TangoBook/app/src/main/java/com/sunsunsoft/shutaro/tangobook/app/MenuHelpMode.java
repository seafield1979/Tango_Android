package com.sunsunsoft.shutaro.tangobook.app;

/**
 * Created by shutaro on 2017/06/14.
 * メニューのヘルプ表示
 */

public enum MenuHelpMode {
    None,       // 非表示
    Name,       // メニュー名を表示
    Help        // メニューヘルプを表示
    ;

    public static MenuHelpMode toEnum(int value) {
        if (value < values().length) {
            return values()[value];
        }
        return None;
    }
}
