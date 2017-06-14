package com.sunsunsoft.shutaro.tangobook.database;

import java.util.Date;

/**
 * 単語帳のアイテムを同じListに入れるためのインターフェース
 * ※RealmObjectを親クラスにしたベースクラスを作ろうとするとRealmでエラーが起きるので
 * やむなくインターフェースで実装
 */

public interface TangoItem {
    int getId();
    int getPos();
    void setPos(int pos);

    String getTitle();
    TangoItemType getItemType();

    void setItemPos(TangoItemPos itemPos);
    TangoItemPos getItemPos();

    Date getCreateTime();
    Date getUpdateTime();
    Date getLastStudiedTime();
}
