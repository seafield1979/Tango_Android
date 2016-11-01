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
     * @param book_id
     * @return
     */
    public List<TangoCardBook> selectByBookId(int book_id) {
        RealmResults<TangoCardBook> results =
                mRealm.where(TangoCardBook.class)
                        .equalTo("id", book_id).
                        findAll();
        return results;
    }



    /**
     * 指定単語帳のカードを削除
     * @param book_id   単語帳ID
     * @param card_ids  削除するカードのID
     */
    public void deleteByCardIds(int book_id, int[] card_ids) {
        if (card_ids.length <= 0) return;

        mRealm.beginTransaction();

        // Build the query looking at all users:
        RealmQuery<TangoCardBook> query = mRealm.where(TangoCardBook.class);

        // Add query conditions:
        boolean isFirst = true;
        query.equalTo("book_id", book_id);

        for (int id : card_ids) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("card_id", id);
            } else {
                query.or().equalTo("card_id", id);
            }
        }
        // Execute the query:
        RealmResults<TangoCardBook> results = query.findAll();

//        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }
}
