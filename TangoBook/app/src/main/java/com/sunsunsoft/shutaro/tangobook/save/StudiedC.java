package com.sunsunsoft.shutaro.tangobook.save;

/**
 * Created by shutaro on 2017/06/14.
 */


/**
 * TangoStudiedCard保存用
 */
public class StudiedC {
    private int bId;        // bookHistoryId
    private int cId;        // cardId
    private boolean ok;     // okFlag

    /**
     * Get/Set
     */
    public int getBookHistoryId() {
        return bId;
    }

    public int getCardId() {
        return cId;
    }

    public boolean isOkFlag() {
        return ok;
    }

    public StudiedC(){}
    public StudiedC(int bookHistoryId, int cardId, boolean okFlag) {
        this.bId = bookHistoryId;
        this.cId = cardId;
        this.ok = okFlag;
    }
}