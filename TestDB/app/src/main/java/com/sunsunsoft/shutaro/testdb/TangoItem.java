package com.sunsunsoft.shutaro.testdb;

import io.realm.RealmObject;

/**
 * Created by shutaro on 2016/11/22.
 */

interface TangoItem {
    int getId();
    int getPos();
    TangoItemType getItemType();
}
