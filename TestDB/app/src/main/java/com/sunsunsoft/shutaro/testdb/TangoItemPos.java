package com.sunsunsoft.shutaro.testdb;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * 単語帳のアイテムの場所を特定する情報
 *
 * 特定のアイテム以下にあるアイテムを検索するのに使用する
 * 例: ホーム以下
 *      指定の単語帳以下
 *      指定のボックス以下
 *      ゴミ箱以下
 * posは自分が所属するグループ内での配置位置
 */
public class TangoItemPos extends RealmObject {

    // 親の種類 (0:ホーム / 1:単語帳 / 2:ボックス / 3:ゴミ箱) TangoParentType
    private int parentType;

    // 親のID
    private int parentId;

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
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentType() {
        return parentType;
    }

    public void setParentType(int parentType) {
        this.parentType = parentType;
    }

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
