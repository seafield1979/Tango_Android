package com.sunsunsoft.shutaro.testdb;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * TangoCardBookのDAO
 */

public class TangoCardBookDao {

    private Realm mRealm;

    public TangoCardBookDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<TangoCardBook> selectAll() {
        RealmResults<TangoCardBook> results = mRealm.where(TangoCardBook.class).findAll();
        LinkedList<TangoCardBook> list = new LinkedList<>();
        for (TangoCardBook box : results) {
            list.add(box);
        }

        return list;
    }

    /**
     * 指定の単語帳に含まれるカードを取得
     * @param bookId
     * @return
     */
    public List<TangoCardBook> selectByBookId(int bookId) {
        RealmResults<TangoCardBook> results =
                mRealm.where(TangoCardBook.class)
                        .equalTo("bookId", bookId).
                        findAll();
        return results;
    }

    /**
     * 指定の単語帳に含まれるカードのIDリストを取得
     */
    public List<Integer> selecteByBookId(int bookId) {
        List<TangoCardBook> list = selectByBookId(bookId);

        LinkedList<Integer> idsList = new LinkedList<Integer>();
        for (TangoCardBook item : list) {
            idsList.add(item.getCardId());
        }
        return idsList;
    }


    /**
     * 指定の単語帳にカードを追加する
     */
    public void addItems(int bookId, Integer[] cardIds) {
        if (cardIds == null) return;

        mRealm.beginTransaction();

        for (int cardId : cardIds) {
            TangoCardBook c2b = new TangoCardBook();
            c2b.setBookId(bookId);
            c2b.setCardId(cardId);

            mRealm.copyToRealm(c2b);
        }
        mRealm.commitTransaction();
    }

    /**
     * 指定単語帳のカードを削除
     * @param bookId   単語帳ID
     * @param cardIds  削除するカードのID
     */
    public void deleteByCardIds(int bookId, Integer[] cardIds) {
        if (cardIds.length <= 0) return;

        // Build the query looking at all users:
        RealmQuery<TangoCardBook> query = mRealm.where(TangoCardBook.class);

        // Add query conditions:
        boolean isFirst = true;
        query.equalTo("bookId", bookId);

        for (int id : cardIds) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("cardId", id);
            } else {
                query.or().equalTo("cardId", id);
            }
        }
        mRealm.beginTransaction();

        // Execute the query:
        RealmResults<TangoCardBook> results = query.findAll();
        results.deleteAllFromRealm();

        mRealm.commitTransaction();
    }
}
