package com.sunsunsoft.shutaro.testdb;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * 単語帳(TangoBook)のDAO
 */
public class TangoBookDao {
    /**
     * Constract
     */
    public static final String TAG = "TangoBookDao";

    /**
     * Member variables
     */
    private Realm mRealm;


    /**
     * Csontructor
     */
    public TangoBookDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<TangoBook> selectAll() {
        RealmResults<TangoBook> results = mRealm.where(TangoBook.class).findAll();
        LinkedList<TangoBook> list = new LinkedList<>();
        for (TangoBook book : results) {
            list.add(book);
        }

        for (TangoBook book : results) {
            Log.d(TAG, "id:" + book.getId() + " name:" + book.getName());
        }

        return list;
    }

    /**
     * 変更不可なRealmのオブジェクトを変更可能なリストに変換する
     * @param list
     * @return
     */
    public List<TangoBook> toChangeable(List<TangoBook> list) {
        LinkedList<TangoBook> newList = new LinkedList<>();
        for (TangoBook book : list) {
            newList.add(mRealm.copyFromRealm(book));
        }
        return newList;
    }

    /**
     * 指定のIDの要素を取得
     * @param ids
     * @return
     */
    public List<TangoBook>selectByIds(Iterable<Integer> ids) {
        // Build the query looking at all users:
        RealmQuery<TangoBook> query = mRealm.where(TangoBook.class);

        // Add query conditions:
        boolean isFirst = true;
        for (int id : ids) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("id", id);
            } else {
                query.or().equalTo("id", id);
            }
        }
        // Execute the query:
        RealmResults<TangoBook> results = query.findAll();

        return results;
    }

    /**
     * 指定のIDの要素を取得(1つ)
     */
    public TangoBook selectById(int id) {
        TangoBook book =
                mRealm.where(TangoBook.class)
                        .equalTo("id", id).
                        findFirst();

        if (book == null) return null;
        TangoBook newBook = mRealm.copyFromRealm(book);

        return newBook;
    }


    /**
     * 指定の単語帳に追加されていない単語を取得
     * @return
     */
    public List<TangoBook> selectByExceptIds(Iterable<Integer> ids) {

        RealmQuery<TangoBook> query = mRealm.where(TangoBook.class);

        for (int id : ids) {
            query.notEqualTo("id", id);
        }
        RealmResults<TangoBook> results = query.findAll();

        return results;
    }

    /**
     * 要素を追加 TangoBookオブジェクトをそのまま追加
     * @param book
     */
    public void addOne(TangoBook book) {
        book.setId(getNextId());

        mRealm.beginTransaction();
        mRealm.copyToRealm(book);
        mRealm.commitTransaction();
    }

    /**
     * ダミーのデータを一件追加
     */
    public void addDummy() {
        int newId = getNextId();
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoBook book = new TangoBook();
        book.setId(newId);
        book.setName("book" + randVal);
        book.setColor(0xffffff);
        book.setComment("comment:" + randVal);
        byte[] history = new byte[3];
        for (int i=0; i<history.length; i++) {
            history[i] = 1;
        }

        Date now = new Date();
        book.setCreateTime(now);
        book.setUpdateTime(now);

        mRealm.beginTransaction();
        mRealm.copyToRealm(book);
        mRealm.commitTransaction();
    }

    /**
     * 一件更新  ユーザーが設定するデータ全て
     * @param book
     */
    public void updateOne(TangoBook book) {

        TangoBook newBook = mRealm.where(TangoBook.class).equalTo("id", book.getId()).findFirst();
        if (newBook == null) return;

        mRealm.beginTransaction();

        newBook.setName(book.getName());
        newBook.setColor(book.getColor());
        newBook.setComment(book.getComment());
        newBook.setUpdateTime(new Date());

        mRealm.commitTransaction();
    }

    /**
     * IDのリストに一致する項目を全て削除する
     */
    public void deleteIds(Integer[] ids) {
        if (ids.length <= 0) return;

        mRealm.beginTransaction();

        // Build the query looking at all users:
        RealmQuery<TangoBook> query = mRealm.where(TangoBook.class);

        // Add query conditions:
        boolean isFirst = true;
        for (int id : ids) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("id", id);
            } else {
                query.or().equalTo("id", id);
            }
        }
        // Execute the query:
        RealmResults<TangoBook> results = query.findAll();

        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * 学習日付を更新する
     */
    private void updateStudyTime(Integer id) {
        TangoBook newBook = mRealm.where(TangoBook.class).equalTo("id", id).findFirst();
        if (newBook == null) return;

        mRealm.beginTransaction();
        newBook.setStudyTime(new Date());
        mRealm.commitTransaction();
    }

    /**
     * かぶらないプライマリIDを取得する
     * @return
     */
    public int getNextId() {
        int nextId = 1;
        Number maxUserId = mRealm.where(TangoBook.class).max("id");
        if(maxUserId != null) {
            nextId = maxUserId.intValue() + 1;
        }
        return nextId;
    }
}