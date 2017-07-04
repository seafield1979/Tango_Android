package com.sunsunsoft.shutaro.tangobook.database;

import com.sunsunsoft.shutaro.tangobook.backup.saveitem.BHistory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by shutaro on 2016/12/04.
 */

public class TangoBookHistoryDao {
    /**
     * Constants
     */
    public static final String TAG = "TangoBookHistoryDao";

    /**
     * Member variables
     */
    private Realm mRealm;

    /**
     * Constructor
     * @param realm
     */
    public TangoBookHistoryDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 要素数を取得
     */
    public int getNum() {
        List<TangoBookHistory> list = selectAll(false);
        return list.size();
    }

    /**
     * 全て選択
     * @param reverse  並び順を逆順にする
     * @return
     */
    public List<TangoBookHistory> selectAll(boolean reverse) {
        Sort sort = reverse ? Sort.DESCENDING : Sort.ASCENDING;
        RealmResults<TangoBookHistory> results = mRealm.where(TangoBookHistory.class)
                .findAllSorted("studiedDateTime", sort);
        return results;
    }

    public List<TangoBookHistory> selectAllWithLimit(boolean reverse, int limit) {
        Sort sort = reverse ? Sort.DESCENDING : Sort.ASCENDING;
        RealmResults<TangoBookHistory> results = mRealm.where(TangoBookHistory.class)
                .findAllSorted("studiedDateTime", sort);

        if (results.size() > limit) {
            LinkedList<TangoBookHistory> list = new LinkedList<>();
            int limitCount = 0;
            for (TangoBookHistory history : results) {
                list.add(history);
                limitCount++;
                if ( limitCount >= limit) {
                    break;
                }
            }
            return list;
        }

        return results;
    }


    /**
     * Book情報から選択
     * @param book
     * @return
     */
    List<TangoBookHistory> selectByBook(TangoBook book) {
        RealmResults<TangoBookHistory> results = mRealm.where(TangoBookHistory.class)
                .equalTo("bookId", book.getId())
                .findAll();
        return results;
    }

    /**
     * 指定のBookの最後の学習日を取得
     * @param bookId
     * @return
     */
    public Date selectMaxDateByBook(int bookId) {
        Date date = mRealm.where(TangoBookHistory.class)
                .equalTo("bookId", bookId)
                .maximumDate("studiedDateTime");
        return date;
    }

    /**
     * Add
     */
    /**
     * レコードを１つ追加
     * @param bookId
     * @param okNum
     * @param ngNum
     * @return 作成したレコードのid
     */
    public int addOne(int bookId, int okNum, int ngNum)
    {
        TangoBookHistory history = new TangoBookHistory();
        int id = getNextId();
        history.setId(id);
        history.setBookId(bookId);
        history.setOkNum(okNum);
        history.setNgNum(ngNum);
        history.setStudiedDateTime(new Date());

        mRealm.beginTransaction();
        mRealm.insert(history);
        mRealm.commitTransaction();

        return id;
    }

    /**
     * Delete
     */
    /**
     * 配下の学習カード履歴も含めてすべて削除
     */
    public void deleteAll() {
        RealmResults<TangoBookHistory> results = mRealm.where(TangoBookHistory.class)
                .findAll();

        // 学習カード履歴削除
        for (TangoBookHistory history : results) {
            RealmManager.getStudiedCardDao().deleteByHistoryId(history.getId());
        }

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    public boolean deleteByBookId(int bookId) {
        RealmResults<TangoBookHistory> results = mRealm.where(TangoBookHistory.class)
                .equalTo("bookId", bookId).findAll();
        if (results == null) return false;

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();

        return true;
    }

    /**
     * Update
     */

    /**
     * かぶらないプライマリIDを取得する
     * @return
     */
    public int getNextId() {
        // 初期化
        int nextId = 1;
        // userIdの最大値を取得
        Number maxId = mRealm.where(TangoBookHistory.class).max("id");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxId != null) {
            nextId = maxId.intValue() + 1;
        }
        return nextId;
    }


    /**
     * XMLファイルから読み込んだデータを追加する
     */
    public void addXmlBook(List<BHistory> bookHistory, boolean transaction) {
        if (bookHistory == null || bookHistory.size() == 0) {
            return;
        }
        if (transaction) {
            mRealm.beginTransaction();
        }
        for (BHistory _history : bookHistory) {
            TangoBookHistory history = new TangoBookHistory();
            history.setId( _history.getId());
            history.setBookId( _history.getBookId());
            history.setOkNum( _history.getOkNum());
            history.setNgNum( _history.getNgNum());
            history.setStudiedDateTime( _history.getStudiedDateTime());
            mRealm.insert(history);
        }
        if (transaction) {
            mRealm.commitTransaction();
        }
    }
}
