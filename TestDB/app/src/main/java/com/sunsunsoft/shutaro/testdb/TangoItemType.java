package com.sunsunsoft.shutaro.testdb;

/**
 * 単語帳のアイテムの種類
 */

public enum TangoItemType {
    Card,       // カード
    Book,       // 単語帳
    Box,         // 箱
    ;

    // int を enumに変換する
    public static TangoItemType toEnum(int val) {
        if (val < values().length) {
            return TangoItemType.values()[val];
        }
        return Card;
    }
}
