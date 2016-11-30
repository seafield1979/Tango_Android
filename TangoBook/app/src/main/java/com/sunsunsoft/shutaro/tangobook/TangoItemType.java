package com.sunsunsoft.shutaro.tangobook;

/**
 * 単語帳のアイテムの種類
 */

public enum TangoItemType {
    Card,       // カード
    Book,       // 単語帳
    Trash       // ゴミ箱
    ;

    // int を enumに変換する
    public static TangoItemType toEnum(int val) {
        if (val < values().length) {
            return TangoItemType.values()[val];
        }
        return Card;
    }
}
