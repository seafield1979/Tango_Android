package com.sunsunsoft.shutaro.tangobook.save;

/**
 * Created by shutaro on 2017/06/14.
 */


/**
 * TangoItemPos保存用
 */
public class Pos {
    private int pType;  // parentType
    private int pId;    // parentId
    private int pos;
    private int iType;  // itemType
    private int iId;    // itemId

    /**
     * Get/Set
     */
    public int getParentType() {
        return pType;
    }

    public int getParentId() {
        return pId;
    }

    public int getPos() {
        return pos;
    }

    public int getItemType() {
        return iType;
    }

    public int getItemId() {
        return iId;
    }

    public Pos(){}
    public Pos(int parentType, int parentId, int pos, int itemType, int itemId) {
        this.pType = parentType;
        this.pId = parentId;
        this.pos = pos;
        this.iType = itemType;
        this.iId = itemId;
    }
}
