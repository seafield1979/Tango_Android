package com.sunsunsoft.shutaro.tangobook;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
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
     * Select
     */
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
