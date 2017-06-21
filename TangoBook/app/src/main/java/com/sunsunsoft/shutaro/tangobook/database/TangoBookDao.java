package com.sunsunsoft.shutaro.tangobook.database;

import android.graphics.Color;
import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.save.Book;
import com.sunsunsoft.shutaro.tangobook.util.UDebug;

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

    public static final int NGBookId = 100000;

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

        // for Debug
        if (UDebug.debugDAO) {
            Log.d(TAG, "TangoBook selectAll");
            for (TangoBook book : results) {
                Log.d(TAG, "id:" + book.getId() + " name:" + book.getName());
            }
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
     * @param itemPoses
     * @return
     */
    public List<TangoBook>selectByIds(List<TangoItemPos> itemPoses, boolean changeable) {
        if (itemPoses.size() <= 0) return null;

        // Build the query looking at all users:
        RealmQuery<TangoBook> query = mRealm.where(TangoBook.class);

        // Add query conditions:
        boolean isFirst = true;
        for (TangoItemPos itemPos : itemPoses) {
            if (isFirst) {
                isFirst = false;
            } else {
                query.or();
            }
            query.equalTo("id", itemPos.getItemId());
        }
        // Execute the query:
        RealmResults<TangoBook> results = query.findAll();

        if (results != null && changeable) {
            return toChangeable(results);
        }

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
    public List<TangoBook> selectByExceptIds(Iterable<Integer> ids, boolean changeable) {

        RealmQuery<TangoBook> query = mRealm.where(TangoBook.class);

        for (int id : ids) {
            query.notEqualTo("id", id);
        }
        RealmResults<TangoBook> results = query.findAll();

        if (results != null && changeable) {
            return toChangeable(results);
        }

        return results;
    }

    /**
     * 要素を追加 TangoBookオブジェクトをそのまま追加
     * @param book
     */
    public void addOne(TangoBook book, int addPos) {
        book.setId(getNextId());

        mRealm.beginTransaction();
        mRealm.copyToRealm(book);
        mRealm.commitTransaction();

        // 位置情報を追加（単語帳はホームにしか作れないので作成場所にホームを指定）
        TangoItemPos itemPos = RealmManager.getItemPosDao().addOne(book, TangoParentType
                .Home, 0, addPos);
        book.setItemPos(itemPos);

    }

    /**
     * NGカード用の単語帳を作成する
     */
    public void addNgBook() {
        TangoBook book = TangoBook.createBook();
        book.setName("NG Cards");
        book.setId(NGBookId);
        book.setColor(Color.RED);

        mRealm.beginTransaction();
        mRealm.copyToRealm(book);
        mRealm.commitTransaction();

        // 位置情報を追加（単語帳はホームにしか作れないので作成場所にホームを指定）
        TangoItemPos itemPos = RealmManager.getItemPosDao().addOne(book, TangoParentType.Home, 0,
                -1);
        book.setItemPos(itemPos);
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
        newBook.setLastStudiedTime(book.getLastStudiedTime());

        mRealm.commitTransaction();
    }

    /**
     * IDのリストに一致する項目を全て削除する
     */
    public void deleteIds(List<Integer> ids, boolean transaction) {
        if (ids.size() <= 0) return;

        RealmQuery<TangoBook> query = mRealm.where(TangoBook.class);

        boolean isFirst = true;
        for (int id : ids) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("id", id);
            } else {
                query.or().equalTo("id", id);
            }
        }
        RealmResults<TangoBook> results = query.findAll();

        if (transaction) {
            mRealm.beginTransaction();
            results.deleteAllFromRealm();
            mRealm.commitTransaction();
        } else {
            results.deleteAllFromRealm();
        }
    }

    /**
     * 全要素削除
     *
     * @return
     */
    public boolean deleteAll() {
        RealmResults<TangoBook> results = mRealm.where(TangoBook.class).findAll();
        mRealm.beginTransaction();
        boolean ret = results.deleteAllFromRealm();
        mRealm.commitTransaction();
        return ret;
    }

    /**
     * １件削除する
     * @param id
     * @return
     */
    public boolean deleteById(int id) {
        TangoBook result = mRealm.where(TangoBook.class)
                .equalTo("id", id)
                .findFirst();
        if (result == null) return false;

        mRealm.beginTransaction();
        result.deleteFromRealm();
        mRealm.commitTransaction();

        return true;
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

    /**
     * NEWフラグを変更する
     */
    public void updateNewFlag(TangoBook book, boolean newFlag) {
        TangoBook updateBook =
                mRealm.where(TangoBook.class)
                        .equalTo("id", book.getId())
                        .findFirst();

        mRealm.beginTransaction();
        updateBook.setNewFlag(newFlag);
        mRealm.commitTransaction();
    }

    /**
     * XMLファイルから読み込んだBookを追加する
     * @param books
     */
    public void addXmlBooks(List<Book> books, boolean transaction) {
        if (transaction) {
            mRealm.beginTransaction();
        }
        for (Book _book : books) {
            TangoBook book = new TangoBook();
            book.setId( _book.getId());
            book.setName( _book.getName());
            book.setComment( _book.getComment());
            book.setColor( _book.getColor());
            book.setCreateTime( _book.getCreateTime());
            book.setNewFlag( _book.isNewFlag());

            mRealm.copyToRealm(book);
        }
        if (transaction) {
            mRealm.commitTransaction();
        }
    }
}