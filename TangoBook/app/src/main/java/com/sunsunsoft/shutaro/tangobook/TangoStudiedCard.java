package com.sunsunsoft.shutaro.tangobook;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by shutaro on 2016/12/15.
 *
 * TangoBookHistoryの学習で学習したカードを記録したテーブル
 * 学習したカード１枚につき１つ記録する
 */

public class TangoStudiedCard extends RealmObject {
    @Index
    private int bookHistoryId;

    private int cardId;

    private boolean okFlag;

    /**
     * Get/Set
     */
    public boolean isOkFlag() {
        return okFlag;
    }

    public void setOkFlag(boolean okFlag) {
        this.okFlag = okFlag;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getBookHistoryId() {
        return bookHistoryId;
    }

    public void setBookHistoryId(int bookHistoryId) {
        this.bookHistoryId = bookHistoryId;
    }
}
