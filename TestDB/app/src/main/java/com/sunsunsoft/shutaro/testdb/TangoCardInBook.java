package com.sunsunsoft.shutaro.testdb;

import io.realm.RealmObject;

/**
 * 単語帳に含まれるカード
 */
public class TangoCardInBook extends RealmObject {
    private int bookId;
    private int cardId;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int book_id) {
        this.bookId = book_id;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int card_id) {
        this.cardId = card_id;
    }
}
