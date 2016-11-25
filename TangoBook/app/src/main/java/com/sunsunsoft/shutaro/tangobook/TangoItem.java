package com.sunsunsoft.shutaro.tangobook;

/**
 * 単語帳のアイテムを同じListに入れるためのインターフェース
 * ※RealmObjectを親クラスにしたベースクラスを作ろうとするとRealmでエラーが起きるので
 * やむなくインターフェースで実装
 */

interface TangoItem {
    int getId();
    int getPos();
    void setPos(int pos);
    TangoItemType getItemType();

    void setItemPos(TangoItemPos itemPos);
    TangoItemPos getItemPos();
}
