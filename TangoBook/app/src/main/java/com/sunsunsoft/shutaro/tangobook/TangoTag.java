package com.sunsunsoft.shutaro.tangobook;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by shutaro on 2016/12/02.
 *
 * どの単語アイテムがどのタグを持っているかの情報をもつテーブル
 */

public class TangoTag extends RealmObject {
    @Index
    private int itemType;   // タグつけられたアイテムの種類(TangoItemType)

    @Index
    private int itemId;     // タグつけられたアイテムのID

    @Index
    private int tagId;      // TangoTagInfo のID

    /**
     * Get/Set
     */
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

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
