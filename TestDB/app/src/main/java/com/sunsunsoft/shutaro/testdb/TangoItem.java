package com.sunsunsoft.shutaro.testdb;

import io.realm.RealmObject;

/**
 * 単語帳のアイテムを同じListに入れるためのインターフェース
 * ※RealmObjectを親クラスにしたベースクラスを作ろうとするとRealmでエラーが起きるので
 * やむなくインターフェースで実装
 */

interface TangoItem {
    int getId();
    int getPos();
    TangoItemType getItemType();
}
