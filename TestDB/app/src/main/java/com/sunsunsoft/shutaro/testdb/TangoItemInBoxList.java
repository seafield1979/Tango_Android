package com.sunsunsoft.shutaro.testdb;

/**
 * TangoItemInBox のListViewに表示する項目
 */

public class TangoItemInBoxList {

    private String name;          // 名前 カードならWordA、単語帳ならname
    private int itemId;
    private TangoItemType type;   // アイテムの種類
    private boolean isChecked;      // ListViewのチェック状態

    /**
     * Get/Set
     */
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public TangoItemType getType() {
        return type;
    }

    public void setType(TangoItemType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    /**
     * Constructor
     */
    public TangoItemInBoxList(TangoItemType type, int id, String name) {
        this.type = type;
        this.itemId = id;
        this.name = name;
    }
}
