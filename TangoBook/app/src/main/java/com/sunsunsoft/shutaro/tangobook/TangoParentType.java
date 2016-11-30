package com.sunsunsoft.shutaro.tangobook;

/**
 * 単語帳を保持する親の種類
 */
public enum TangoParentType {
    Home,           // ホーム画面
    Book,           // 単語帳
    Trash,          // ゴミ箱
    ;

    // int を enumに変換する
    public static TangoParentType toEnum(int val) {
        if (val < values().length) {
            return TangoParentType.values()[val];
        }
        return Home;
    }
}
