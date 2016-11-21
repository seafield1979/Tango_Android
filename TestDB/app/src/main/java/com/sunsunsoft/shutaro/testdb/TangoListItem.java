package com.sunsunsoft.shutaro.testdb;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * ホームに表示するアイテムのリスト
 * 主に表示順を管理する
 */

public class TangoListItem extends RealmObject {

    // 表示順 0...
    private int pos;

    // アイテムの種類 1:カード / 2:単語帳 / 3:ボックス
    private int itemType;

    // 各アイテムのID
    private int id;

    @Ignore
    private boolean isChecked;


    /**
     * Get/Set
     */
    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setParams(int pos, int type, int id) {
        this.pos = pos;
        this.itemType = type;
        this.id = id;
    }
}
