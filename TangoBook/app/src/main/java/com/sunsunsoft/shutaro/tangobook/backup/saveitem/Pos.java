package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

/**
 * Created by shutaro on 2017/06/14.
 */


/**
 * TangoItemPos保存用
 */
public class Pos {
    private int parentType;  // parentType
    private int parentId;    // parentId
    private int pos;
    private int itemType;  // itemType
    private int itemId;    // itemId

    /**
     * Get/Set
     */
    public int getParentType() {
        return parentType;
    }

    public int getParentId() {
        return parentId;
    }

    public int getPos() {
        return pos;
    }

    public int getItemType() {
        return itemType;
    }

    public int getItemId() {
        return itemId;
    }

    public Pos(){}
    public Pos(int parentType, int parentId, int pos, int itemType, int itemId) {
        this.parentType = parentType;
        this.parentId = parentId;
        this.pos = pos;
        this.itemType = itemType;
        this.itemId = itemId;
    }
}
