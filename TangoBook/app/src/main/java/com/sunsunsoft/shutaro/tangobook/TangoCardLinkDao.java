package com.sunsunsoft.shutaro.tangobook;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by shutaro on 2016/12/04.
 */

public class TangoCardLinkDao {

    /**
     * Constants
     */
    public static final String TAG = "TangoCardLinkDao";

    /**
     * Member variables
     */
    private Realm mRealm;

    /**
     * Constructor
     * @param realm
     */
    public TangoCardLinkDao(Realm realm) {
        mRealm = realm;
    }


    /**
     * Select
     */
    public List<TangoCardLink> selectAll() {
        RealmResults<TangoCardLink> results = mRealm.where(TangoCardLink.class).findAll();
        return results;
    }

    public List<TangoCardLink> selectByCardId(int cardId) {
        RealmResults<TangoCardLink> results = mRealm.where(TangoCardLink.class)
                .equalTo("srcId", cardId)
                .findAll();
        return results;
    }

    /**
     * Add
     */
    public boolean addOne(int cardId, int dstCardId) {
        TangoCardLink link = new TangoCardLink();

        link.setSrcId(cardId);
        link.setDstId(dstCardId);

        mRealm.beginTransaction();
        mRealm.insert(link);
        mRealm.commitTransaction();
        return true;
    }

    /**
     * Delete
     */
    /**
     * リンクを１つ削除する
     * @param cardId リンク元のCard ID
     * @param dstId リンク先のCard ID
     * @return
     */
    public boolean deleteOne(int cardId, int dstId) {
        TangoCardLink result = mRealm.where(TangoCardLink.class)
                .equalTo("srcId", cardId)
                .equalTo("dstId", dstId)
                .findFirst();
        if (result == null) return false;

        mRealm.beginTransaction();
        result.deleteFromRealm();
        mRealm.commitTransaction();
        return true;
    }

    /**
     * 指定のCardIDのリンクを全て削除する
     * @param cardId
     * @return
     */
    public boolean deleteByCardId(int cardId) {
        RealmResults<TangoCardLink> results = mRealm.where(TangoCardLink.class)
                .equalTo("srcId", cardId)
                .findAll();
        if (results == null) return false;

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
        return true;
    }

    /**
     * Update
     */
}
