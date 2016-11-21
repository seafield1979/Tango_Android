package com.sunsunsoft.shutaro.testdb;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * ボックスの中のアイテム(カード,単語帳)のDAO
 */

public class TangoItemInBoxDao {
    private Realm mRealm;

    public TangoItemInBoxDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<TangoItemInBox> selectAll() {
        RealmResults<TangoItemInBox> results = mRealm.where(TangoItemInBox.class).findAll();
        LinkedList<TangoItemInBox> list = new LinkedList<>();
        for (TangoItemInBox obj : results) {
            list.add(obj);
        }

        return list;
    }

    /**
     * 指定の単語帳に含まれるカードを取得
     * @param boxId
     * @return
     */
    public List<TangoItemInBox> selectByBoxId(int boxId) {
        RealmResults<TangoItemInBox> results =
                mRealm.where(TangoItemInBox.class)
                        .equalTo("boxId", boxId).
                        findAll();
        return results;
    }

    /**
     * 指定のボックスに含まれるアイテムのIDリストを取得
     */
    public List<TangoItemInBox> selecteByBoxId(int boxId) {
        List<TangoItemInBox> list = selectByBoxId(boxId);

        return list;
    }


    /**
     * 指定の単語帳にカードを追加する
     */
    public void addCards(int boxId, Integer[] cardIds) {
        addItems(boxId, cardIds, null);
    }
    public void addBooks(int boxId, Integer[] bookIds) {
        addItems(boxId, null, bookIds);
    }

    public void addItems(int boxId, Integer[] cardIds, Integer[] bookIds) {
        if (cardIds == null && bookIds == null) return;

        mRealm.beginTransaction();

        // 単語
        if (cardIds != null) {
            for (int cardId : cardIds) {
                TangoItemInBox c2b = new TangoItemInBox();
                c2b.setBoxId(boxId);
                c2b.setItemType(TangoItemType.Card.ordinal());
                c2b.setItemId(cardId);
                mRealm.copyToRealm(c2b);
            }
        }
        // 単語帳
        if (bookIds != null) {
            for (int bookId : bookIds) {
                TangoItemInBox c2b = new TangoItemInBox();
                c2b.setBoxId(boxId);
                c2b.setItemType(TangoItemType.Book.ordinal());
                c2b.setItemId(bookId);
                mRealm.copyToRealm(c2b);
            }
        }
        mRealm.commitTransaction();
    }

    /**
     * 指定ボックスのアイテムを削除
     * @param boxId   ボックスID
     * @param cardIds  削除するカードのID
     * @param bookIds  削除する単語帳のID
     */
    public void deleteByItemIds(int boxId, Integer[] cardIds, Integer[] bookIds) {
        if (cardIds.length <= 0) return;

        // Build the query looking at all users:
        RealmQuery<TangoItemInBox> query = mRealm.where(TangoItemInBox.class);

        // Add query conditions:
        boolean isFirst = true;
        query.equalTo("boxId", boxId);

        for (int id : cardIds) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("itemId", id)
                        .equalTo("itemType", id);
            } else {
                query.or().equalTo("itemId", id)
                    .equalTo("itemType", id);
            }
        }
        mRealm.beginTransaction();

        // Execute the query:
        RealmResults<TangoItemInBox> results = query.findAll();
        results.deleteAllFromRealm();

        mRealm.commitTransaction();
    }

    public void deleteByItems(int boxId, TangoItemInBoxList[] deleteList) {
        if (deleteList.length <= 0) return;

        RealmQuery<TangoItemInBox> query = mRealm.where(TangoItemInBox.class);

        boolean isFirst = true;
        query.equalTo("boxId", boxId);

        for (TangoItemInBoxList item : deleteList) {
            if (isFirst) {
                isFirst = false;
            } else {
                query.or();
            }
            query.equalTo("itemId", item.getItemId())
                    .equalTo("itemType", item.getType().ordinal());
        }
        mRealm.beginTransaction();

        // Execute the query:
        RealmResults<TangoItemInBox> results = query.findAll();
        results.deleteAllFromRealm();

        mRealm.commitTransaction();
    }
}
