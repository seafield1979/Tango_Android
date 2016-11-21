package com.sunsunsoft.shutaro.testdb;

import io.realm.RealmObject;


/**
 * ボックスに含まれるカード、単語帳
 */
public class TangoItemInBox extends RealmObject {
    private int itemType;  // TangoItemType
    private int itemId;
    private int boxId;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }
}
