package com.sunsunsoft.shutaro.tangobook;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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
     * Select
     */
    public List<TangoBookHistory> selectAll() {
        RealmResults<TangoBookHistory> results = mRealm.where(TangoBookHistory.class).findAll();
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
     * Add
     */
    public boolean addOne(int bookId, boolean learned, int okNum, int ngNum) {
        TangoBookHistory history = new TangoBookHistory();
        history.setBookId(bookId);
        history.setLearned(learned);
        history.setOkNum(okNum);
        history.setNgNum(ngNum);
        history.setCorrectRatio((float)okNum / (float)(okNum + ngNum));
        history.setStudiedDateTime(new Date());

        mRealm.beginTransaction();
        mRealm.insert(history);
        mRealm.commitTransaction();

        return true;
    }

    /**
     * Delete
     */
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
    // Unnecessary
}
