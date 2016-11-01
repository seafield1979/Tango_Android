package com.sunsunsoft.shutaro.testdb;

import android.content.Context;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by shutaro on 2016/10/28.
 */

public class TangoBookDao {
    private Realm mRealm;

    public TangoBookDao(Context context) {
        mRealm = MyRealmManager.getRealm();
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

        return list;
    }

    /**
     * 指定のIDの要素を取得
     * @param ids
     * @return
     */
    public List<TangoBook>selectByIds(int[] ids) {
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
     * 要素を追加 TangoBookオブジェクトをそのまま追加
     * @param book
     */
    public void addOne(TangoBook book) {
        book.setId(getNextUserId(mRealm));

        mRealm.beginTransaction();
        mRealm.copyToRealm(book);
        mRealm.commitTransaction();
    }

    /**
     * ダミーのデータを一件追加
     */
    public void addDummy() {
        int newId = getNextUserId(mRealm);
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
     * @param id
     * @param book
     */
    public void updateOne(Integer id, TangoBook book) {

        TangoBook newBook = mRealm.where(TangoBook.class).equalTo("id", id).findFirst();
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
     * @param realm
     * @return
     */
    public int getNextUserId(Realm realm) {
        // 初期化
        int nextId = 1;
        // userIdの最大値を取得
        Number maxUserId = realm.where(TangoBook.class).max("id");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxUserId != null) {
            nextId = maxUserId.intValue() + 1;
        }
        return nextId;
    }
}