package com.sunsunsoft.shutaro.tangobook;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;

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

    // 親の種類 TangoParentType(0:ホーム / 1:単語帳 / 2:ゴミ箱)
    @Index
    private int parentType;

    // 親のID
    @Index
    private int parentId;

    // 表示場所 0...
    @Index
    private int pos;

    // アイテムの種類 TangoItemType( 0:カード / 1:単語帳 / 2:ボックス)
    @Index
    private int itemType;

    // 各アイテムのID
    @Index
    private int itemId;

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

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int id) {
        this.itemId = id;
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
        this.itemId = id;
    }
}
