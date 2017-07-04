package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

/**
 * Created by shutaro on 2017/06/14.
 *
 * TangoStudiedCard保存用
 * 単語帳を学習するたびに学習した単語帳の情報を保持するBHistoryレコードが作成され、このレコードの配下に
 * どのカードを学習した情報が入る。ここクラスはその情報を保持する。
 */
public class StudiedC {
    private int bookHistoryId;        // bookHistoryId
    private int cardId;        // cardId
    private boolean okFlag;     // okFlag

    /**
     * Get/Set
     */
    public int getBookHistoryId() {
        return bookHistoryId;
    }

    public int getCardId() {
        return cardId;
    }

    public boolean isOkFlag() {
        return okFlag;
    }

    public StudiedC(){}
    public StudiedC(int bookHistoryId, int cardId, boolean okFlag) {
        this.bookHistoryId = bookHistoryId;
        this.cardId = cardId;
        this.okFlag = okFlag;
    }
}